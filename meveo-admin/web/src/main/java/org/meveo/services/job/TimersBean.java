package org.meveo.services.job;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.ejb.Timer;
import javax.enterprise.context.ConversationScoped;
import javax.enterprise.context.RequestScoped;
import javax.enterprise.inject.Produces;
import javax.inject.Inject;
import javax.inject.Named;

import org.jboss.seam.international.status.Messages;
import org.jboss.seam.international.status.builder.BundleKey;
import org.meveo.admin.action.BaseBean;
import org.meveo.admin.util.pagination.PaginationConfiguration;
import org.meveo.admin.util.pagination.PaginationDataModel;
import org.meveo.model.jobs.JobCategoryEnum;
import org.meveo.model.jobs.TimerEntity;
import org.meveo.model.jobs.TimerInfo;
import org.meveo.service.base.local.IPersistenceService;
import org.meveo.service.job.Job;
import org.meveo.service.job.TimerEntityService;

@Named
@ConversationScoped
public class TimersBean extends BaseBean<TimerEntity> {

	private static final long serialVersionUID = 5578930292531038376L;

	@Inject
	TimerEntityService timerEntityservice;

	@Inject
	private Messages messages;
	

	private int pageSize = 20;
	private PaginationDataModel<TimerEntity> timersDataModel;

	public int getPageSize() {
		return pageSize;
	}

	public void setPageSize(int pageSize) {
		this.pageSize = pageSize;
	}

	

	@Produces
	@RequestScoped
	@Named("timersDataModel")
	public PaginationDataModel<TimerEntity> find() {
		if (timersDataModel == null) {
			timersDataModel = new TimersDataModel();
		}

		timersDataModel.forceRefresh();

		return timersDataModel;
	}

	public Collection<Timer> getEjbTimers() {// FIXME: throws BusinessException
												// {

		return timerEntityservice.getTimers();
	}

	public TimerInfo getTimerInfo(Timer timer) {
		return (TimerInfo) timer.getInfo();
	}
	


	public String getTimerSchedule(Timer timer) {
		String result = "";
		try {
			result = timer.getSchedule().toString();
		} catch (Exception e) {
			log.error(e.getMessage());
		}
		return result;
	}

	public void cancelEjbTimer(Timer timer) {

		try {
			TimerEntity timerEntity = timerEntityservice.getByTimer(timer);

			if (timerEntity != null) {
				timerEntityservice.remove(timerEntity);
			} else {
				timer.cancel();
			}
			messages.info(new BundleKey("messages", "info.ejbTimer.cancelled"));
		} catch (Exception e) {
			log.error(e.getMessage());
			messages.error(new BundleKey("messages",
					"error.ejbTimer.cancellation"));
		}
	}

	public void cancelEjbTimers() {

		try {
			for (Timer timer : timerEntityservice.getTimers()) {
				TimerEntity timerEntity = timerEntityservice.getByTimer(timer);
				if (timerEntity != null) {
					timerEntityservice.remove(timerEntity);
				} else {
					timer.cancel();
				}
			}

			messages.info(new BundleKey("messages", "info.ejbTimers.cancelled"));
		} catch (Exception e) {
			messages.error(new BundleKey("messages",
					"error.ejbTimers.cancellation"));
		}
	}

	/***********************************************************************************/
	/* DATATABLE MODEL */
	/***********************************************************************************/
	class TimersDataModel extends PaginationDataModel<TimerEntity> {

		private static final long serialVersionUID = 1L;

		@Override
		protected int countRecords(PaginationConfiguration paginatingData) {
			int userCount = (int) timerEntityservice.count(paginatingData);
			return userCount;
		}

		@Override
		protected List<TimerEntity> loadData(
				PaginationConfiguration configuration) {
			return timerEntityservice.find(configuration);
		}
	}

	class JBossTimersDataModel extends PaginationDataModel<Timer> {

		private static final long serialVersionUID = 1L;

		@Override
		protected int countRecords(PaginationConfiguration paginatingData) {
			try {
				return timerEntityservice.getTimers().size();
			} catch (Exception e) {
				// TODO Auto-generated catch block
				log.error(e.getMessage());
			}
			return 0;
		}

		@Override
		protected List<Timer> loadData(PaginationConfiguration configuration) {
			try {
				return new ArrayList<Timer>(timerEntityservice.getTimers());
			} catch (Exception e) {
				// TODO Auto-generated catch block
				log.error(e.getMessage());
			}
			return null;
		}
	}

	@Override
	protected IPersistenceService<TimerEntity> getPersistenceService() {
		// TODO Auto-generated method stub
		return timerEntityservice;
	}


	

}
