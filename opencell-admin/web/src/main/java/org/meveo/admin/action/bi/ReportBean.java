/*
 * (C) Copyright 2015-2020 Opencell SAS (https://opencellsoft.com/) and contributors.
 *
 * This program is free software: you can redistribute it and/or modify it under the terms of the GNU Affero General
 * Public License as published by the Free Software Foundation, either version 3 of the License, or (at your option)
 * any later version.
 *
 * THERE IS NO WARRANTY FOR THE PROGRAM, TO THE EXTENT PERMITTED BY APPLICABLE LAW. EXCEPT WHEN
 * OTHERWISE STATED IN WRITING THE COPYRIGHT HOLDERS AND/OR OTHER PARTIES PROVIDE THE PROGRAM "AS
 * IS" WITHOUT WARRANTY OF ANY KIND, EITHER EXPRESSED OR IMPLIED, INCLUDING, BUT NOT LIMITED TO, THE
 * IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE. THE ENTIRE RISK AS TO
 * THE QUALITY AND PERFORMANCE OF THE PROGRAM IS WITH YOU. SHOULD THE PROGRAM PROVE DEFECTIVE,
 * YOU ASSUME THE COST OF ALL NECESSARY SERVICING, REPAIR OR CORRECTION.
 *
 * For more information on the GNU Affero General Public License, please consult
 * <https://www.gnu.org/licenses/agpl-3.0.en.html>.
 */
package org.meveo.admin.action.bi;

import java.util.Arrays;
import java.util.List;

import jakarta.faces.view.ViewScoped;
import jakarta.inject.Inject;
import jakarta.inject.Named;

import org.meveo.admin.action.BaseBean;
import org.meveo.admin.exception.BusinessException;
import org.meveo.admin.report.ReportExecution;
import org.meveo.model.bi.Report;
import org.meveo.service.base.PersistenceService;
import org.meveo.service.base.local.IPersistenceService;
import org.meveo.service.bi.impl.ReportService;

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
	 * @throws BusinessException General business exception
	 * @return back link
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
