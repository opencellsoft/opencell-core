/*
 * (C) Copyright 2009-2014 Manaty SARL (http://manaty.net/) and contributors.
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Affero General Public License as
 * published by the Free Software Foundation, either version 3 of the
 * License, or (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Affero General Public License for more details.
 *
 * You should have received a copy of the GNU Affero General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package org.meveo.admin.action.bi;

import java.util.Arrays;
import java.util.List;

import javax.inject.Inject;
import javax.inject.Named;

import org.meveo.admin.action.BaseBean;
import org.meveo.admin.exception.BusinessException;
import org.meveo.admin.report.ReportExecution;
import org.meveo.model.bi.Report;
import org.meveo.service.base.PersistenceService;
import org.meveo.service.base.local.IPersistenceService;
import org.meveo.service.bi.impl.ReportService;
import org.omnifaces.cdi.ViewScoped;

/**
 * Standard backing bean for {@link Report} (extends {@link BaseBean} that
 * provides almost all common methods to handle entities filtering/sorting in
 * datatable, their create, edit, view, delete operations). It works with Manaty
 * custom JSF components.
 */
@Named
@ViewScoped
public class ReportBean extends BaseBean<Report> {

	private static final long serialVersionUID = 1L;

	/** Injected @{link Report} service. Extends {@link PersistenceService}. */
	@Inject
	private ReportService reportService;

	/** Injected component that generates PDF reports. */
	@Inject
	private ReportExecution reportExecution;

	/**
	 * Constructor. Invokes super constructor and provides class type of this
	 * bean for {@link BaseBean}.
	 */
	public ReportBean() {
		super(Report.class);
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
	 * 
	 * @throws BusinessException
	 */
	public String executeReport() throws BusinessException {
		log.info("executeReport()");
		String save = super.saveOrUpdate(true);
		log.debug("executeReport : after save");
		reportExecution.executeReport(entity);
		log.info("executeReport : result = {}", save);
		return save;
	}

}
