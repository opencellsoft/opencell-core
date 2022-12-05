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

import jakarta.enterprise.context.ConversationScoped;
import jakarta.enterprise.inject.Produces;
import jakarta.inject.Inject;
import jakarta.inject.Named;

import org.meveo.admin.action.BaseBean;
import org.meveo.model.admin.DunningHistory;
import org.meveo.model.bi.JobHistory;
import org.meveo.service.base.PersistenceService;
import org.meveo.service.base.local.IPersistenceService;
import org.meveo.service.bi.impl.JobHistoryService;

/**
 * Standard backing bean for {@link JobHistory} (extends {@link BaseBean} that
 * provides almost all common methods to handle entities filtering/sorting in
 * datatable, their create, edit, view, delete operations). It works with Manaty
 * custom JSF components.
 */
@Named
@ConversationScoped
public class JobHistoryBean extends BaseBean<JobHistory> {

	private static final long serialVersionUID = 1L;

	/** Injected @{link JobHistory} service. Extends {@link PersistenceService}. */
	@Inject
	private JobHistoryService jobHistoryService;

	DunningHistory dunningHistory;

	/**
	 * Constructor. Invokes super constructor and provides class type of this
	 * bean for {@link BaseBean}.
	 */
	public JobHistoryBean() {
		super(JobHistory.class);
	}

	/**
	 * Factory method for entity to edit. If objectId param set load that entity
	 * from database, otherwise create new.
	 * @return job history.
	 * 
	 */
	@Produces
	@Named("jobHistory")
	public JobHistory init() {
		return initEntity();
	}

	/**
	 * @see org.meveo.admin.action.BaseBean#getPersistenceService()
	 */
	@Override
	protected IPersistenceService<JobHistory> getPersistenceService() {
		return jobHistoryService;
	}

	public String displayHistory(Long jobHistoryId) {
		String page = "/pages/reporting/jobHistory/jobHistory.xhtml";
		JobHistory jobHistory = jobHistoryService.findById(jobHistoryId);
		if (jobHistory instanceof DunningHistory) {
			dunningHistory = (DunningHistory) jobHistory;
			page = "/pages/reporting/jobHistory/dunningHistory.xhtml";
		}
		return page;
	}

}
