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

import grails.plugin.springsecurity.userdetails.GrailsUserDetailsService
import grails.plugin.springsecurity.userdetails.NoStackUsernameNotFoundException
import org.springframework.security.core.userdetails.UserDetails

class NullUserDetailsService implements GrailsUserDetailsService {

   UserDetails loadUserByUsername(String username, boolean loadRoles) throws NoStackUsernameNotFoundException {
      return loadUserByUsername(username)
   }

   // Always throw an exception - we want the authenticationUserDetailsService to be used
   UserDetails loadUserByUsername(String username) throws NoStackUsernameNotFoundException {
	   throw new NoStackUsernameNotFoundException()
   }
}
