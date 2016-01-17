/* Copyright 2006-2015 the original author or authors.
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
package grails.plugin.springsecurity.cas.userdetails

import org.jasig.cas.client.authentication.AttributePrincipal
import org.springframework.security.core.GrantedAuthority
import grails.plugin.springsecurity.cas.userdetails.CasUser
import grails.plugin.springsecurity.cas.userdetails.DomainUserMapper

class CasDomainUserMapperService implements DomainUserMapper{

	/**
    * When using cas, the password attribute of the User object means nothing.
    */
	private static final String NON_EXISTENT_PASSWORD_VALUE = "NO_PASSWORD"

	def findUserByUsername(String username){
		/*
		 * This implementation assumes that all user info will come from CAS.
		 * If you want to save users in a database, create a domain class that inherits from
		 * grails.plugin.springsecurity.userdetails.GrailsUser and do something like this:
		 *
		 * Look up user profile in database
		 * def user = myDomainClass.findUserByUsername(username)
		 *
		 * return the new user object
		 */
	}

	def createUser(String username, AttributePrincipal principal, Collection<GrantedAuthority> authorities){

		new CasUser(username, NON_EXISTENT_PASSWORD_VALUE, true, true, true,
			true, authorities, principal.attributes)
	}
}
