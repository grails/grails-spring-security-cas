/* Copyright 2006-2013 SpringSource.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

import grails.plugin.springsecurity.SecurityFilterPosition
import grails.plugin.springsecurity.SpringSecurityUtils

import org.jasig.cas.client.proxy.Cas20ProxyRetriever
import org.jasig.cas.client.proxy.ProxyGrantingTicketStorageImpl
import org.jasig.cas.client.session.SingleSignOutFilter
import org.jasig.cas.client.session.SingleSignOutHttpSessionListener
import org.jasig.cas.client.validation.Cas20ServiceTicketValidator
import org.springframework.security.cas.ServiceProperties
import org.springframework.security.cas.authentication.CasAuthenticationProvider
import org.springframework.security.cas.authentication.NullStatelessTicketCache
import org.springframework.security.cas.web.CasAuthenticationEntryPoint
import org.springframework.security.cas.web.CasAuthenticationFilter

class SpringSecurityCasGrailsPlugin {

	String version = '2.0-RC1'
	String grailsVersion = '2.0 > *'
	List pluginExcludes = [
		'docs/**',
		'src/docs/**',
		'scripts/CreateCasTestApps.groovy'
	]
	List loadAfter = ['springSecurityCore']

	String author = 'Burt Beckwith'
	String authorEmail = 'burt@burtbeckwith.com'
	String title = 'Jasig CAS support for the Spring Security plugin.'
	String description = 'Jasig CAS support for the Spring Security plugin.'
	String documentation = 'http://grails-plugins.github.io/grails-spring-security-cas/'

	String license = 'APACHE'
	def organization = [name: 'SpringSource', url: 'http://www.springsource.org/']
	def issueManagement = [system: 'JIRA', url: 'http://jira.grails.org/browse/GPSPRINGSECURITYCAS']
	def scm = [url: 'https://github.com/grails-plugins/grails-spring-security-cas']

	def doWithWebDescriptor = { xml ->

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

		if (!conf.cas.useSingleSignout) {
			return
		}

		// add the filter right after the last context-param
		def contextParam = xml.'context-param'
		contextParam[contextParam.size() - 1] + {
			'filter' {
				'filter-name'('CAS Single Sign Out Filter')
				'filter-class'(SingleSignOutFilter.name)
			}
		}

		// add the filter-mapping right after the last filter
		def mappingLocation = xml.'filter'
		// TODO  this gets in there 2x
		mappingLocation + {
			'filter-mapping'{
				'filter-name'('CAS Single Sign Out Filter')
				'url-pattern'('/*')
			}
		}

		def filterMapping = xml.'filter-mapping'
		filterMapping[filterMapping.size() - 1] + {
			'listener' {
				'listener-class'(SingleSignOutHttpSessionListener.name)
			}
		}
	}

	def doWithSpring = {

		def conf = SpringSecurityUtils.securityConfig
		if (!conf || !conf.active) {
			return
		}

		if (application.warDeployed) {
			// need to load secondary here since web.xml was already built, so
			// doWithWebDescriptor isn't called when deployed as war

			SpringSecurityUtils.loadSecondaryConfig 'DefaultCasSecurityConfig'
			conf = SpringSecurityUtils.securityConfig
		}

		if (!conf.cas.active) {
			return
		}

		boolean printStatusMessages = (conf.printStatusMessages instanceof Boolean) ? conf.printStatusMessages : true

		if (printStatusMessages) {
			println '\nConfiguring Spring Security CAS ...'
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
			filterProcessesUrl = conf.cas.filterProcessesUrl // '/j_spring_cas_security_check'
			continueChainBeforeSuccessfulAuthentication = conf.apf.continueChainBeforeSuccessfulAuthentication // false
			allowSessionCreation = conf.apf.allowSessionCreation // true
			proxyReceptorUrl = conf.cas.proxyReceptorUrl
		}

		casProxyRetriever(Cas20ProxyRetriever, conf.cas.serverUrlPrefix, conf.cas.serverUrlEncoding /*'UTF-8'*/)

		casTicketValidator(Cas20ServiceTicketValidator, conf.cas.serverUrlPrefix) {
			proxyRetriever = ref('casProxyRetriever')
			proxyGrantingTicketStorage = ref('casProxyGrantingTicketStorage')
			proxyCallbackUrl = conf.cas.proxyCallbackUrl
			renew = conf.cas.sendRenew // false
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
	}
}
