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
package org.meveo.admin.action.bi;

import java.util.Arrays;
import java.util.List;

import javax.enterprise.inject.Produces;
import javax.inject.Inject;
import javax.inject.Named;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

import org.meveo.admin.action.BaseBean;
import org.meveo.admin.report.ReportExecution;
import org.meveo.admin.util.pagination.PaginationDataModel;
import org.meveo.model.bi.Report;
import org.meveo.service.base.PersistenceService;
import org.meveo.service.base.local.IPersistenceService;
import org.meveo.service.bi.local.ReportServiceLocal;

/**
 * Standard backing bean for {@link Report} (extends {@link BaseBean} that
 * provides almost all common methods to handle entities filtering/sorting in
 * datatable, their create, edit, view, delete operations). It works with Manaty
 * custom JSF components.
 * 
 */
@Named
// TODO: @Scope(ScopeType.CONVERSATION)
public class ReportBean extends BaseBean<Report> {

	private static final long serialVersionUID = 1L;

	/** Injected @{link Report} service. Extends {@link PersistenceService}. */
	@Inject
	private ReportServiceLocal reportService;

	/** Injected component that generates PDF reports. */
	@Inject
	private ReportExecution reportExecution;

	@PersistenceContext(unitName = "meveoDWHentityManager")
	protected EntityManager dwhEntityManager;

	/**
	 * Constructor. Invokes super constructor and provides class type of this
	 * bean for {@link BaseBean}.
	 */
	public ReportBean() {
		super(Report.class);
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
	 * @Factory("report")
	 */
	@Produces
	@Named("report")
	public Report init() {
		return initEntity();
	}

	/**
	 * Data model of entities for data table in GUI.
	 * 
	 * @return filtered entities.
	 */
	// TODO: @Out(value = "reports", required = false)
	@Produces
	@Named("reports")
	protected PaginationDataModel<Report> getDataModel() {
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
	 * @Factory("reports")
	 */
	@Produces
	@Named("reports")
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
	 * @see org.meveo.admin.action.BaseBean#getPersistenceService()
	 */
	@Override
	protected IPersistenceService<Report> getPersistenceService() {
		return reportService;
	}

	/**
	 * @see org.meveo.admin.action.BaseBean#getFormFieldsToFetch()
	 */
	protected List<String> getFormFieldsToFetch() {
		return Arrays.asList("emails");
	}

	/**
	 * @see org.meveo.admin.action.BaseBean#getListFieldsToFetch()
	 */
	protected List<String> getListFieldsToFetch() {
		return Arrays.asList("emails");
	}

	/**
	 * Creates report.
	 */
	// @End(beforeRedirect = true)
	public String executeReport() {
		log.info("executeReport()");
		String save = saveOrUpdate();
		log.debug("executeReport : after save");
		reportExecution.executeReport(entity);
		log.info("executeReport : result = {0}", save);
		return save;
	}

}
