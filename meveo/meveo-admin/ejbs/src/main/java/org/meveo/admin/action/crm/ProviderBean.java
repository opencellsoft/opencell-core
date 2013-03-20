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
package org.meveo.admin.action.crm;

import java.util.Arrays;
import java.util.List;

import javax.enterprise.context.ConversationScoped;
import javax.enterprise.inject.Produces;
import javax.inject.Inject;
import javax.inject.Named;

import org.meveo.admin.action.BaseBean;
import org.meveo.admin.action.admin.CurrentProvider;
import org.meveo.admin.util.pagination.PaginationDataModel;
import org.meveo.model.admin.User;
import org.meveo.model.crm.Provider;
import org.meveo.service.base.PersistenceService;
import org.meveo.service.base.local.IPersistenceService;
import org.meveo.service.crm.impl.ProviderService;

/**
 * Standard backing bean for {@link Provider} (extends {@link BaseBean} that
 * provides almost all common methods to handle entities filtering/sorting in
 * datatable, their create, edit, view, delete operations). It works with Manaty
 * custom JSF components.
 * 
 * @author Gediminas Ubartas
 * @created 2011-02-28
 * 
 */
@Named
@ConversationScoped
public class ProviderBean extends BaseBean<Provider> {

	private static final long serialVersionUID = 1L;

	/**
	 * Injected @{link Provider} service. Extends {@link PersistenceService}.
	 */
	@Inject
	private ProviderService providerService;

	@Inject
	private User currentUser;

	@Inject
    @CurrentProvider
	private Provider currentProvider;

	/**
	 * Constructor. Invokes super constructor and provides class type of this
	 * bean for {@link BaseBean}.
	 */
	public ProviderBean() {
		super(Provider.class);
	}

	/**
	 * Factory method for entity to edit. If objectId param set load that entity
	 * from database, otherwise create new.
	 * 
	 * @throws IllegalAccessException
	 * @throws InstantiationException
	 */
	/*
	 * TODO: @Begin(nested = true)
	 * 
	 * @Factory("provider")
	 */
	@Produces
	@Named("provider")
	public Provider init() {
		return initEntity();
	}

	/**
	 * Data model of entities for data table in GUI.
	 * 
	 * @return filtered entities.
	 */
	// @Out(value = "providers", required = false)
	@Produces
	@Named("providers")
	protected PaginationDataModel<Provider> getDataModel() {
		return entities;
	}

	// @Out(value = "userProviders", required = false)
	@Produces
	@Named("userProviders")
	public PaginationDataModel<Provider> userProviders() {
		super.list();
		getFilters();
		filters.put("list-users", currentUser);
		return entities;
	}

	/**
	 * Factory method, that is invoked if data model is empty. Invokes
	 * BaseBean.list() method that handles all data model loading. Overriding is
	 * needed only to put factory name on it.
	 * 
	 * @see org.meveo.admin.action.BaseBean#list()
	 */
	/*
	 * TODO: @Begin(join = true)
	 * 
	 * @Factory("providers")
	 */
	@Produces
	@Named("providers")
	public void list() {
		super.list();
	}

	/**
	 * Conversation is ended and user is redirected from edit to his previous
	 * window.
	 * 
	 * @see org.meveo.admin.action.BaseBean#saveOrUpdate(org.meveo.model.IEntity)
	 */
	// @End(beforeRedirect = true, root=false)
	public String saveOrUpdate() {
		return saveOrUpdate(entity);
	}

	/**
	 * @see org.meveo.admin.action.BaseBean#getPersistenceService()
	 */
	@Override
	protected IPersistenceService<Provider> getPersistenceService() {
		return providerService;
	}

	/**
	 * @see org.meveo.admin.action.BaseBean#getFormFieldsToFetch()
	 */
	protected List<String> getFormFieldsToFetch() {
		return Arrays.asList("users");
	}

	/**
	 * @see org.meveo.admin.action.BaseBean#getListFieldsToFetch()
	 */
	protected List<String> getListFieldsToFetch() {
		return Arrays.asList("users");
	}

	/**
	 * if current provider is not null continue action, else require to select
	 * provider in login screen
	 */
	public void checkCurrentProvider() {
		if (currentUser == null || currentProvider == null) {
			Redirect.instance().setViewId("/home");
			Redirect.instance().execute();
		}
	}
}
