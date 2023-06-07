package org.meveo.admin.job.invoicing;
import java.util.Date;
import org.meveo.model.shared.DateUtils;
public class DiscountPlanSummary {
	private Date startDate;
	private Date endDate;
	private long discountPlanId;
	/**
	 * @param x
	 */
	public DiscountPlanSummary(String x) {
		final String[] split = x.split("|");
		this.discountPlanId = Long.parseLong(split[0]);
		if (split.length > 1) {
			this.startDate = DateUtils.parseDefaultDate(split[1]);
			if (split.length > 2) {
				this.endDate = DateUtils.parseDefaultDate(split[2]);
			}
		}
	}
	/**
	 * @return the startDate
	 */
	public Date getStartDate() {
		return startDate;
	}
	/**
	 * @param startDate the startDate to set
	 */
	public void setStartDate(Date startDate) {
		this.startDate = startDate;
	}
	/**
	 * @return the endDate
	 */
	public Date getEndDate() {
		return endDate;
	}
	/**
	 * @param endDate the endDate to set
	 */
	public void setEndDate(Date endDate) {
		this.endDate = endDate;
	}
	/**
	 * @return the discountPlanId
	 */
	public long getDiscountPlanId() {
		return discountPlanId;
	}
	/**
	 * @param discountPlanId the discountPlanId to set
	 */
	public void setDiscountPlanId(long discountPlanId) {
		this.discountPlanId = discountPlanId;
	}
}