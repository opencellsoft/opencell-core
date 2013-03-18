/*
 * (C) Copyright 2009-2013 Manaty SARL (http://manaty.net/) and contributors.
 *
 * Licensed under the GNU Public Licence, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.gnu.org/licenses/gpl-2.0.txt
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.meveo.admin.action.admin;

import javax.enterprise.inject.Produces;
import javax.inject.Named;

import org.meveo.model.crm.Provider;

/**
 * Class used to set current system provider
 * 
 * @author Gediminas Ubartas
 * @created 2011-02-28
 * 
 */
@Named
// TODO: @Scope(ScopeType.CONVERSATION)
public class CurrentProviderBean {

	@SuppressWarnings("unused")
	// TODO: @Out(required = false, scope = ScopeType.SESSION)
	@Produces
	@Named("currentProvider")
	private Provider currentProvider;

	/**
	 * Sets current provider
	 */
	public void setCurrentProvider(Provider provider) {
		currentProvider = provider;
		/*
		 * TODO: Redirect.instance().setViewId("/home");
		 * Redirect.instance().execute();
		 */
	}
}