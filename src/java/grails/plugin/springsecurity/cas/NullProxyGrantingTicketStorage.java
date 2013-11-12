/* Copyright 2006-2013 the original author or authors.
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
package grails.plugin.springsecurity.cas;

import org.jasig.cas.client.proxy.ProxyGrantingTicketStorage;

/**
 * No-op implementation.
 *
 * @author <a href='mailto:burt@burtbeckwith.com'>Burt Beckwith</a>
 */
public class NullProxyGrantingTicketStorage implements ProxyGrantingTicketStorage {

	/**
	 * {@inheritDoc}
	 * @see org.jasig.cas.client.proxy.ProxyGrantingTicketStorage#cleanUp()
	 */
	public void cleanUp() {
		// do nothing
	}

	/**
	 * {@inheritDoc}
	 * @see org.jasig.cas.client.proxy.ProxyGrantingTicketStorage#retrieve(java.lang.String)
	 */
	public String retrieve(final String proxyGrantingTicketIou) {
		return null;
	}

	/**
	 * {@inheritDoc}
	 * @see org.jasig.cas.client.proxy.ProxyGrantingTicketStorage#save(
	 * 	java.lang.String, java.lang.String)
	 */
	public void save(final String proxyGrantingTicketIou, final String proxyGrantingTicket) {
		// do nothing
	}
}
