package org.meveo.apiv2.billing;

import java.util.ArrayList;
import java.util.List;

import org.meveo.api.dto.ActionStatus;
import org.meveo.api.dto.ActionStatusEnum;
import org.meveo.apiv2.billing.ProcessCdrListResult.Statistics;

public class ValidateInvoiceResult {

	private Statistics statistics;
	private ActionStatus actionStatus;
	private List<InvoiceNotValidated> invoicesNotValidated = new ArrayList<>();
	private List<Long> invoicesValidated = new ArrayList<>();

	
	public ValidateInvoiceResult(Statistics statistics, ActionStatus actionStatus) {
		super();
		this.statistics = statistics;
		this.actionStatus = actionStatus;
	}
	
	public ValidateInvoiceResult() {
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
	 * @return the invoicesNotValidated
	 */
	public List<InvoiceNotValidated> getInvoicesNotValidated() {
		return invoicesNotValidated;
	}

	/**
	 * @param invoicesNotValidated the invoicesNotValidated to set
	 */
	public void setInvoicesNotValidated(List<InvoiceNotValidated> invoicesNotValidated) {
		this.invoicesNotValidated = invoicesNotValidated;
	}

	/**
	 * @return the invoicesValidated
	 */
	public List<Long> getInvoicesValidated() {
		return invoicesValidated;
	}

	/**
	 * @param invoicesValidated the invoicesValidated to set
	 */
	public void setInvoicesValidated(List<Long> invoicesValidated) {
		this.invoicesValidated = invoicesValidated;
	}

}
