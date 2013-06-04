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
package org.meveo.services.job;

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
import org.jboss.solder.logging.Logger;
import org.jboss.solder.servlet.http.RequestParam;
import org.meveo.admin.action.BaseBean;
import org.meveo.admin.util.pagination.PaginationConfiguration;
import org.meveo.model.jobs.JobExecutionResult;
import org.meveo.model.jobs.JobExecutionResultImpl;
import org.meveo.model.jobs.TimerEntity;
import org.meveo.service.base.local.IPersistenceService;
import org.primefaces.model.LazyDataModel;
import org.primefaces.model.SortOrder;

@ConversationScoped
@Named
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

	@Inject
	private Conversation conversation;

	private TimerEntity timerEntity;

	@Inject
	private JobExecutionService jobExecutionService;

	private LazyDataModel<JobExecutionResultImpl> jobResultsDataModel;

	@Produces
	@Named
	@ConversationScoped
	public TimerEntity getTimerEntity() {
		conversation.getId();
		if (timerEntity == null) {
			if (timerId.get() != null) {
				timerEntity = timerEntityService.findById(timerId.get());
				timerEntity.setFieldsFromTimerHandler();
				filters.put("jobName", timerEntity.getJobName());
			} else {
				log.debug("create new timerEntity");

				timerEntity = new TimerEntity();
			}
		}
		return timerEntity;
	}

	public String create() {// FIXME: throws BusinessException {
		log.debug("createTimer on job : " + timerEntity.getJobName());
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
			e.printStackTrace();
		}
		return "/administration/job/jobs.xhtml?faces-redirect=true";
	}

	public String updateTimer() {

		try {
			timerEntityService.update(timerEntity);
			messages.info(new BundleKey("messages", "update.successful"));
		} catch (Exception e) {
			messages.error(new BundleKey("messages", "error.user.usernameAlreadyExists"));
			return null;
		}

		return "/administration/job/jobs.xhtml?faces-redirect=true";
	}

	public String deleteTimer() {// FIXME: throws BusinessException {

		timerEntityService.remove(timerEntity);
		messages.info(new BundleKey("messages", "delete.successful"));
		try {
			conversation.end();
		} catch (Exception e) {
			e.printStackTrace();
		}

		return "/administration/job/jobs.xhtml?faces-redirect=true";
	}

	public String executeTimer() {
		try {
			JobExecutionResult result = timerEntityService.manualExecute(timerEntity);
			messages.info(new BundleKey("messages", "info.entity.executed"),
					timerEntity.getJobName());
			if (result.getErrors() != null) {
				for (String error : result.getErrors()) {
					messages.error("error:" + error);
				}
			}
			if (result.getWarnings() != null) {
				for (String warning : result.getWarnings()) {
					messages.warn("warn:" + warning);
				}
			}
		} catch (Exception e) {
			messages.error(new BundleKey("messages", "error.execution"));
			return null;
		}
		return "/administration/job/jobs.xhtml?faces-redirect=true";
	}

	/*
	 * to be used in picklist to select a job
	 */
	public Set<String> getJobNames() {
		return TimerEntityService.jobEntries.keySet();
	}

	@Override
	protected IPersistenceService<JobExecutionResultImpl> getPersistenceService() {
		// TODO Auto-generated method stub
		return jobExecutionService;
	}

	@Override
	protected String getListViewName() {
		return "timer";
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
					setRowCount((int) jobExecutionService.count(timerEntity.getJobName(),
							new PaginationConfiguration(first, pageSize, copyOfFilters,
									getListFieldsToFetch(), sortField, sortOrder)));
					if (getRowCount() > 0) {
						copyOfFilters = new HashMap<String, Object>();
						copyOfFilters.putAll(filters);
						return jobExecutionService.find(timerEntity.getJobName(),
								new PaginationConfiguration(first, pageSize, copyOfFilters,
										getListFieldsToFetch(), sortField, sortOrder));
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

					return rowIndex >= 0
							&& rowIndex < ((List<JobExecutionResultImpl>) getWrappedData()).size();
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

}
