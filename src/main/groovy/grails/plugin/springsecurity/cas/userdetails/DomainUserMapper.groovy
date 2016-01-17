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

/**
 * The User mapper is implemented to abstract out the
 * Domain model instantiation logic from the security model
 */
interface DomainUserMapper {
	/**
	 * Create and save a new domain user when the user has not previously visited the app.
	 * <br>You could choose to throw a UsernameNotFoundException if you would rather the
	 * user not be created.  This would drop the user to the App's login form with an error message
	 */
	Object createUser(String username, AttributePrincipal principal, Collection<GrantedAuthority> authorities)

	/**
	 * Tell the security mechanism where to find your user profiles
	 */
	Object findUserByUsername(String username)
}
