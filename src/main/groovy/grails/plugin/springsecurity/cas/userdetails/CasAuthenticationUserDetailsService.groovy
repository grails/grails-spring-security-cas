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
package grails.plugin.springsecurity.cas.userdetails

import java.util.List
import org.jasig.cas.client.validation.Assertion
import org.springframework.security.cas.userdetails.AbstractCasAssertionUserDetailsService
import org.springframework.security.cas.userdetails.GrantedAuthorityFromAssertionAttributesUserDetailsService
import org.springframework.security.core.authority.SimpleGrantedAuthority
import org.springframework.security.core.userdetails.UserDetails
import grails.plugin.springsecurity.SpringSecurityUtils

class CasAuthenticationUserDetailsService extends AbstractCasAssertionUserDetailsService {

	/**
	 * When using CAS attributes for GrantedAuthorities, you need to prepend 'ROLE_' for Spring Security
	 */
	private static final String PREFIX = "ROLE_"

	/**
	 * Some Spring Security classes (e.g. RoleHierarchyVoter) expect at least
	 * one role, so we give a user with no granted roles this one which gets
	 * past that restriction but doesn't grant anything.
	 */
	private static final List NO_ROLES = [new SimpleGrantedAuthority("CAS_AUTH")]

	private GrantedAuthorityFromAssertionAttributesUserDetailsService grantedAuthoritiesService
	private String authorityAttribute

	/**
	 * Provide the attribute name/s that will contain role information from cas
	 */
	public void setAuthorityAttribute(authorityAttribute){
		this.authorityAttribute = authorityAttribute
		this.grantedAuthoritiesService = new GrantedAuthorityFromAssertionAttributesUserDetailsService(authorityAttribute)
	}

	/** Dependency injection for creating and finding Users **/
	def userMapper

	@Override
	protected UserDetails loadUserDetails(Assertion casAssert) {

        def casUser = grantedAuthoritiesService.loadUserDetails(casAssert)
		def casAuthorities = casUser.authorities ?: NO_ROLES

		def configRolePrefix = SpringSecurityUtils.securityConfig.cas.rolePrefix

		List roles = []

		if (configRolePrefix) {
			if (configRolePrefix instanceof String) {
				casAuthorities.each() { authority ->
					roles.add(new SimpleGrantedAuthority(configRolePrefix + authority))
				}
			} else {
				casAuthorities.each() { authority ->
					roles.add(new SimpleGrantedAuthority(PREFIX + authority))
				}
			}

		} else {
			casAuthorities.each() { authority ->
				roles.add(new SimpleGrantedAuthority('' + authority))
			}
		}

		def user = userMapper.findUserByUsername(casUser.username)

		if(! user) {
			user = userMapper.createUser(casUser.username, casAssert.principal, roles)
		}

		user
	}

}
