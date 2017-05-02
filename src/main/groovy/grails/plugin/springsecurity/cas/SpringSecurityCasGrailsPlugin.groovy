/* Copyright 2006-2015 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *	  http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package grails.plugin.springsecurity.cas

import org.jasig.cas.client.proxy.Cas20ProxyRetriever
import org.jasig.cas.client.proxy.ProxyGrantingTicketStorageImpl
import org.jasig.cas.client.session.SingleSignOutFilter
import org.jasig.cas.client.session.SingleSignOutHttpSessionListener
import org.jasig.cas.client.validation.Cas20ServiceTicketValidator
import org.jasig.cas.client.validation.Saml11TicketValidator

import org.springframework.boot.context.embedded.FilterRegistrationBean
import org.springframework.boot.context.embedded.ServletListenerRegistrationBean
import org.springframework.core.Ordered
import org.springframework.security.core.userdetails.UserDetailsByNameServiceWrapper
import org.springframework.security.cas.ServiceProperties
import org.springframework.security.cas.authentication.CasAuthenticationProvider
import org.springframework.security.cas.authentication.NullStatelessTicketCache
import org.springframework.security.cas.web.CasAuthenticationEntryPoint
import org.springframework.security.cas.web.CasAuthenticationFilter

import grails.plugin.springsecurity.cas.userdetails.CasDomainUserMapperService
import grails.plugin.springsecurity.cas.userdetails.NullUserDetailsService
import grails.plugin.springsecurity.cas.userdetails.CasAuthenticationUserDetailsService
import grails.plugin.springsecurity.SecurityFilterPosition
import grails.plugin.springsecurity.SpringSecurityUtils
import grails.plugins.Plugin

class SpringSecurityCasGrailsPlugin extends Plugin {

	String grailsVersion = '3.0.0 > *'
	String author = 'Burt Beckwith'
	String authorEmail = 'burt@burtbeckwith.com'
	String title = 'Jasig CAS support for the Spring Security plugin.'
	String description = 'Jasig CAS support for the Spring Security plugin.'
	String documentation = 'http://grails-plugins.github.io/grails-spring-security-cas/'
	String license = 'APACHE'
	List loadAfter = ['springSecurityCore']
	def organization = [name: 'Grails', url: 'http://www.grails.org/']
	def issueManagement = [url: 'https://github.com/grails-plugins/grails-spring-security-cas/issues']
	def scm = [url: 'https://github.com/grails-plugins/grails-spring-security-cas']
	def profiles = ['web']

	Closure doWithSpring() {{ ->

		def conf = SpringSecurityUtils.securityConfig
		if (!conf || !conf.active) {
			return
		}

		SpringSecurityUtils.loadSecondaryConfig 'DefaultCasSecurityConfig'
		// have to get again after overlaying DefaultCasSecurityConfig
		conf = SpringSecurityUtils.securityConfig

		if (!conf.cas.active) {
			return
		}

		boolean printStatusMessages = (conf.printStatusMessages instanceof Boolean) ? conf.printStatusMessages : true

		if (printStatusMessages) {
			println '\nConfiguring Spring Security CAS ...'
		}

		if (conf.cas.useSingleSignout) {

			// session fixation prevention breaks single signout because
			// the service ticket is mapped to the session id which changes
			conf.useSessionFixationPrevention = false

			singleSignOutFilter(SingleSignOutFilter) {
				ignoreInitConfiguration = true
				casServerUrlPrefix = conf.cas.serverUrlPrefix
			}

			singleSignOutFilterRegistrationBean(FilterRegistrationBean) {
				name = 'CAS Single Sign Out Filter'
				filter = ref('singleSignOutFilter')
				order = Ordered.HIGHEST_PRECEDENCE
			}

			singleSignOutHttpSessionListener(ServletListenerRegistrationBean, new SingleSignOutHttpSessionListener())
		}

		SpringSecurityUtils.registerProvider 'casAuthenticationProvider'
		SpringSecurityUtils.registerFilter 'casAuthenticationFilter', SecurityFilterPosition.CAS_FILTER

		// TODO  document NullProxyGrantingTicketStorage
		casProxyGrantingTicketStorage(ProxyGrantingTicketStorageImpl)

		authenticationEntryPoint(CasAuthenticationEntryPoint) {
			serviceProperties = ref('casServiceProperties')
			loginUrl = conf.cas.serverUrlPrefix + conf.cas.loginUri
		}

		casServiceProperties(ServiceProperties) {
			service = conf.cas.serviceUrl
			sendRenew = conf.cas.sendRenew // false
			artifactParameter = conf.cas.artifactParameter // 'ticket'
			serviceParameter = conf.cas.serviceParameter // 'service'
		}

		casAuthenticationFilter(CasAuthenticationFilter) {
			authenticationManager = ref('authenticationManager')
			sessionAuthenticationStrategy = ref('sessionAuthenticationStrategy')
			authenticationSuccessHandler = ref('authenticationSuccessHandler')
			authenticationFailureHandler = ref('authenticationFailureHandler')
			rememberMeServices = ref('rememberMeServices')
			authenticationDetailsSource = ref('authenticationDetailsSource')
			serviceProperties = ref('casServiceProperties')
			proxyGrantingTicketStorage = ref('casProxyGrantingTicketStorage')
			filterProcessesUrl = conf.cas.filterProcessesUrl // '/login/cas'
			continueChainBeforeSuccessfulAuthentication = conf.apf.continueChainBeforeSuccessfulAuthentication // false
			allowSessionCreation = conf.apf.allowSessionCreation // true
			proxyReceptorUrl = conf.cas.proxyReceptorUrl
		}

		casProxyRetriever(Cas20ProxyRetriever, conf.cas.serverUrlPrefix, conf.cas.serverUrlEncoding /*'UTF-8'*/)

		if(conf.cas.useSamlValidator){
			casTicketValidator(Saml11TicketValidator, conf.cas.serverUrlPrefix) {
		    	renew = conf.cas.sendRenew //false
		    	tolerance = conf.cas.driftTolerance //120000 (ms)
		  	}

			domainUserMapperService(CasDomainUserMapperService)

			// Replace userDetailsService with a dummy version - all userdetails will come from CAS
			userDetailsService(NullUserDetailsService)

			// Read user details from SAML attributes
			authenticationUserDetailsService(CasAuthenticationUserDetailsService){
				authorityAttribute = conf.cas.authorityAttribute
				userMapper = ref('domainUserMapperService')
			}

		} else {

			authenticationUserDetailsService(UserDetailsByNameServiceWrapper, ref('userDetailsService'))

			casTicketValidator(Cas20ServiceTicketValidator, conf.cas.serverUrlPrefix) {
				proxyRetriever = ref('casProxyRetriever')
				proxyGrantingTicketStorage = ref('casProxyGrantingTicketStorage')
				proxyCallbackUrl = conf.cas.proxyCallbackUrl
				renew = conf.cas.sendRenew // false
			}
		}

		casStatelessTicketCache(NullStatelessTicketCache)

		casAuthenticationProvider(CasAuthenticationProvider) {
			authenticationUserDetailsService = ref('authenticationUserDetailsService')
			serviceProperties = ref('casServiceProperties')
			ticketValidator = ref('casTicketValidator')
			statelessTicketCache = ref('casStatelessTicketCache')
			key = conf.cas.key // 'grails-spring-security-cas'
		}

		if (printStatusMessages) {
			println '... finished configuring Spring Security CAS\n'
		}
	}}
}
