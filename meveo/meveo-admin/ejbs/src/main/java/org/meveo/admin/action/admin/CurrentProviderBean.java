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

import java.io.Serializable;

import javax.enterprise.context.SessionScoped;
import javax.enterprise.inject.Produces;
import javax.inject.Inject;
import javax.inject.Named;

import org.jboss.seam.security.Identity;
import org.meveo.model.crm.Provider;
import org.meveo.security.MeveoUser;

/**
 * Class used to set current system provider
 * 
 * @author Gediminas Ubartas
 * @created 2011-02-28
 * 
 */
@Named
@SessionScoped
public class CurrentProviderBean implements Serializable {

	private static final long serialVersionUID = 2L;

	private Provider currentProvider;

	@Inject
	Identity identity;

	/**
	 * Sets current provider
	 */
	public String setCurrentProvider(Provider provider) {
		currentProvider = provider;

		return "/home.xhtml?faces-redirect=true";
	}

	@Produces
	@Named("currentProvider")
	@CurrentProvider
	@SessionScoped
	public Provider getCurrentProvider() {

		if (((MeveoUser) identity.getUser()).getUser().isOnlyOneProvider()) {
			currentProvider = ((MeveoUser) identity.getUser()).getUser().getProvider();
		} else {
			currentProvider = ((MeveoUser) identity.getUser()).getUser().getProviders().get(0);
		}

		return currentProvider;
	}
}