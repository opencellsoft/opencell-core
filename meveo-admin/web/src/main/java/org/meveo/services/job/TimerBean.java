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
package org.meveo.services.job;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.enterprise.context.Conversation;
import javax.enterprise.context.ConversationScoped;
import javax.enterprise.inject.Instance;
import javax.enterprise.inject.Produces;
import javax.inject.Inject;
import javax.inject.Named;

import org.jboss.seam.international.status.Messages;
import org.jboss.seam.international.status.builder.BundleKey;
import org.jboss.solder.servlet.http.RequestParam;
import org.meveo.admin.action.BaseBean;
import org.meveo.admin.exception.BusinessException;
import org.meveo.admin.util.pagination.PaginationConfiguration;
import org.meveo.model.jobs.JobCategoryEnum;
import org.meveo.model.jobs.JobExecutionResultImpl;
import org.meveo.model.jobs.TimerEntity;
import org.meveo.service.base.local.IPersistenceService;
import org.meveo.service.job.Job;
import org.meveo.service.job.JobExecutionService;
import org.meveo.service.job.TimerEntityService;
import org.omnifaces.cdi.ViewScoped;
import org.primefaces.model.LazyDataModel;
import org.primefaces.model.SortOrder;
import org.slf4j.Logger;

@Named
@ViewScoped
public class TimerBean extends BaseBean<JobExecutionResultImpl> {

	private static final long serialVersionUID = 5578930292531038376L;

	@Inject
	@RequestParam
	private Instance<Long> timerId;

	@Inject
	TimerEntityService timerEntityService;

	@Inject
	private Logger log;

	@Inject
	private Messages messages;

	private TimerEntity timerEntity;

	@Inject
	private JobExecutionService jobExecutionService;

	@Inject
	private Conversation conversation;

	private LazyDataModel<JobExecutionResultImpl> jobResultsDataModel;

	@Produces
	@Named
	@ConversationScoped
	public TimerEntity getTimerEntity() {
		conversation.getId();

		if (timerEntity == null) {
			if (timerId.get() != null) {
				timerEntity = timerEntityService.findById(timerId.get(), Arrays.asList("provider"));
				filters.put("jobName", timerEntity.getJobName());
			} else {
				log.debug("create new timerEntity");

				timerEntity = new TimerEntity();
			}
		}

		return timerEntity;
	}

	public String create() throws BusinessException {
		log.debug("createTimer on job={}", timerEntity.getJobName());
		if (timerEntity.getJobName() == null) {
			messages.error("Veuillez selectionner un job");
		} else if (!getJobNames().contains(timerEntity.getJobName())) {
			messages.error("Veuillez selectionner un job");
		} else {
			timerEntityService.create(timerEntity);
			messages.info(new BundleKey("messages", "save.successful"));
		}

		try {
			conversation.end();
		} catch (Exception e) {
			log.error(e.getMessage());
		}

		return "jobTimers";
	}

	public String updateTimer() {
		try {
			timerEntityService.update(timerEntity);
			messages.info(new BundleKey("messages", "update.successful"));
		} catch (Exception e) {
			messages.error(new BundleKey("messages", "error.user.usernameAlreadyExists"));
			return null;
		}

		return "jobTimers";
	}

	public String deleteTimer() {// FIXME: throws BusinessException {
		timerEntityService.remove(timerEntity);
		messages.info(new BundleKey("messages", "delete.successful"));

		try {
			conversation.end();
		} catch (Exception e) {
			log.error(e.getMessage());
		}

		return "jobTimers";
	}

	public String executeTimer() {
		try {
			timerEntityService.manualExecute(timerEntity);
			messages.info(new BundleKey("messages", "info.entity.executed"), timerEntity.getJobName());

			// if (result.getErrors() != null) {
			// for (String error : result.getErrors()) {
			// messages.error("error:" + error);
			// }
			// }
			//
			// if (result.getWarnings() != null) {
			// for (String warning : result.getWarnings()) {
			// messages.warn("warn:" + warning);
			// }
			// }
		} catch (Exception e) {
			messages.error(new BundleKey("messages", "error.execution"));
			return null;
		}

		return "jobTimers";
	}

	/*
	 * to be used in picklist to select a job
	 */

	public Set<String> getJobNames() {
		HashMap<String, String> jobs = new HashMap<String, String>();
		if (timerEntity.getJobCategoryEnum() != null) {
			jobs = TimerEntityService.jobEntries.get(timerEntity.getJobCategoryEnum());
			return jobs.keySet();
		}
		return null;
	}

	@Override
	protected IPersistenceService<JobExecutionResultImpl> getPersistenceService() {
		return jobExecutionService;
	}

	@Override
	protected String getListViewName() {
		return "timer";
	}

	public List<TimerEntity> getTimerEntityList() {
		return timerEntityService.find(null);
	}

	@Override
	public LazyDataModel<JobExecutionResultImpl> getLazyDataModel() {
		if (jobResultsDataModel == null) {
			jobResultsDataModel = new LazyDataModel<JobExecutionResultImpl>() {
				private static final long serialVersionUID = 1L;

				private Integer rowCount;
				private Integer rowIndex;

				@Override
				public List<JobExecutionResultImpl> load(int first, int pageSize, String sortField,
						SortOrder sortOrder, Map<String, String> loadingFilters) {
					Map<String, Object> copyOfFilters = new HashMap<String, Object>();
					copyOfFilters.putAll(filters);

					if (sortField == null) {
						sortField = "startDate";
						sortOrder = SortOrder.DESCENDING;
					}

					setRowCount((int) jobExecutionService.count(timerEntity.getJobName(), new PaginationConfiguration(
							first, pageSize, copyOfFilters, getListFieldsToFetch(), sortField, sortOrder)));

					if (getRowCount() > 0) {
						copyOfFilters = new HashMap<String, Object>();
						copyOfFilters.putAll(filters);
						return jobExecutionService.find(timerEntity.getJobName(), new PaginationConfiguration(first,
								pageSize, copyOfFilters, getListFieldsToFetch(), sortField, sortOrder));
					} else {
						return null; // no need to load then
					}
				}

				@Override
				public JobExecutionResultImpl getRowData(String rowKey) {
					return getPersistenceService().findById(Long.valueOf(rowKey));
				}

				@Override
				public Object getRowKey(JobExecutionResultImpl object) {
					return object.getId();
				}

				@Override
				public void setRowIndex(int rowIndex) {
					if (rowIndex == -1 || getPageSize() == 0) {
						this.rowIndex = rowIndex;
					} else {
						this.rowIndex = rowIndex % getPageSize();
					}
				}

				@SuppressWarnings("unchecked")
				@Override
				public JobExecutionResultImpl getRowData() {
					return ((List<JobExecutionResultImpl>) getWrappedData()).get(rowIndex);
				}

				@SuppressWarnings({ "unchecked" })
				@Override
				public boolean isRowAvailable() {
					if (getWrappedData() == null) {
						return false;
					}

					return rowIndex >= 0 && rowIndex < ((List<JobExecutionResultImpl>) getWrappedData()).size();
				}

				@Override
				public int getRowIndex() {
					return this.rowIndex;
				}

				@Override
				public void setRowCount(int rowCount) {
					this.rowCount = rowCount;
				}

				@Override
				public int getRowCount() {
					if (rowCount == null) {
						rowCount = (int) getPersistenceService().count();
					}
					return rowCount;
				}
			};
		}

		return jobResultsDataModel;
	}

	public List<JobCategoryEnum> getJobCategoryEnumValues() {
		return Arrays.asList(JobCategoryEnum.values());
	}

}