package org.meveo.apiv2.billing;

import java.util.ArrayList;
import java.util.List;

import org.meveo.api.dto.ActionStatus;
import org.meveo.api.dto.ActionStatusEnum;
import org.meveo.apiv2.billing.ProcessCdrListResult.Statistics;

public class DuplicateRTResult {

	private Statistics statistics;
	private ActionStatus actionStatus;
	private List<Long> failIds = new ArrayList<>();
	private List<Long> createdRts = new ArrayList<>();

	
	
	public DuplicateRTResult(Statistics statistics, ActionStatus actionStatus) {
		super();
		this.statistics = statistics;
		this.actionStatus = actionStatus;
	}
	
	public DuplicateRTResult() {
		super();
		actionStatus = new ActionStatus(ActionStatusEnum.SUCCESS, null);
		statistics = new Statistics(0, 0, 0);
	}

	/**
	 * @return the statistics
	 */
	public Statistics getStatistics() {
		return statistics;
	}
	/**
	 * @param statistics the statistics to set
	 */
	public void setStatistics(Statistics statistics) {
		this.statistics = statistics;
	}
	/**
	 * @return the actionStatus
	 */
	public ActionStatus getActionStatus() {
		return actionStatus;
	}
	/**
	 * @param actionStatus the actionStatus to set
	 */
	public void setActionStatus(ActionStatus actionStatus) {
		this.actionStatus = actionStatus;
	}

	/**
	 * @return the failIds
	 */
	public List<Long> getFailIds() {
		return failIds;
	}

	/**
	 * @param failIds the failIds to set
	 */
	public void setFailIds(List<Long> failIds) {
		this.failIds = failIds;
	}

	/**
	 * @return the createdRts
	 */
	public List<Long> getCreatedRts() {
		return createdRts;
	}

	/**
	 * @param createdRts the createdRts to set
	 */
	public void setCreatedRts(List<Long> createdRts) {
		this.createdRts = createdRts;
	}
	
	
	
}
