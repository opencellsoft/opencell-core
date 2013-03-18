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
import javax.inject.Inject;
import javax.inject.Named;

import org.meveo.admin.action.BaseBean;
import org.meveo.admin.util.pagination.PaginationDataModel;
import org.meveo.model.admin.MedinaInputHistory;
import org.meveo.service.admin.local.MedinaInputHistoryServiceLocal;
import org.meveo.service.base.PersistenceService;
import org.meveo.service.base.local.IPersistenceService;

/**
 * Standard backing bean for {@link MedinaInputHistory} (extends
 * {@link BaseBean} that provides almost all common methods to handle entities
 * filtering/sorting in datatable, their create, edit, view, delete operations).
 * It works with Manaty custom JSF components.
 * 
 * @author Gediminas Ubartas
 * @created 2010.08.23
 */
@Named
// TODO: @Scope(ScopeType.CONVERSATION)
// TODO: @Restrict("#{s:hasRole('meveo.medina')}")
public class MedinaInputHistoryBean extends BaseBean<MedinaInputHistory> {

	private static final long serialVersionUID = 1L;

	/**
	 * Injected @{link MedinaInputHistory} service. Extends
	 * {@link PersistenceService}.
	 */
	@Inject
	private MedinaInputHistoryServiceLocal medinaInputHistoryService;

	/**
	 * Constructor. Invokes super constructor and provides class type of this
	 * bean for {@link BaseBean}.
	 */
	public MedinaInputHistoryBean() {
		super(MedinaInputHistory.class);
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
	 * @Factory("medinaInputHistory")
	 */
	@Produces
	@Named("medinaInputHistory")
	public MedinaInputHistory init() {
		return initEntity();
	}

	/**
	 * Data model of entities for data table in GUI.
	 * 
	 * @return filtered entities.
	 */
	// TODO: @Out(value = "medinaInputs", required = false)
	@Produces
	@Named("medinaInputs")
	protected PaginationDataModel<MedinaInputHistory> getDataModel() {
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
	 * @Factory("medinaInputs")
	 */
	@Produces
	@Named("medinaInputs")
	public void list() {
		super.list();
	}

	/**
	 * Conversation is ended and user is redirected from edit to his previous
	 * window.
	 * 
	 * @see org.meveo.admin.action.BaseBean#saveOrUpdate(org.meveo.model.IEntity)
	 */
	// @End(beforeRedirect = true, root = false)
	public String saveOrUpdate() {
		return saveOrUpdate(entity);
	}

	/**
	 * Override default list view name. (By default view name is class name
	 * starting lower case + ending 's').
	 * 
	 * @see org.meveo.admin.action.BaseBean#getDefaultViewName()
	 */
	protected String getDefaultViewName() {
		return "medinaInputs";
	}

	/**
	 * @see org.meveo.admin.action.BaseBean#getPersistenceService()
	 */
	@Override
	protected IPersistenceService<MedinaInputHistory> getPersistenceService() {
		return medinaInputHistoryService;
	}

}
