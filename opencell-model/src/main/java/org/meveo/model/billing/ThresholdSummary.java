/**
 * 
 */
package org.meveo.model.billing;

/**
 * @author M-ELAZZOUZI
 *
 */
public class ThresholdSummary {
	private Long customerId;
	private Long customerAccountId;
	private int count;

	/**
	 * @return the customerId
	 */
	public Long getCustomerId() {
		return customerId;
	}

	/**
	 * @param customerId the customerId to set
	 */
	public void setCustomerId(Long customerId) {
		this.customerId = customerId;
	}

	/**
	 * @return the customerAccountId
	 */
	public Long getCustomerAccountId() {
		return customerAccountId;
	}

	/**
	 * @param customerAccountId the customerAccountId to set
	 */
	public void setCustomerAccountId(Long customerAccountId) {
		this.customerAccountId = customerAccountId;
	}

	/**
	 * @return the count
	 */
	public int getCount() {
		return count;
	}

	/**
	 * @param count the count to set
	 */
	public void setCount(int count) {
		this.count = count;
	}

	/**
	 * @param customerId
	 * @param customerInvoicingThreshold
	 * @param customerAccountId
	 * @param customerAccountInvoicingThreshold
	 * @param baIDs
	 */
	public ThresholdSummary(Long customerId, Long customerAccountId, Long count) {
		this.customerId = customerId;
		this.customerAccountId = customerAccountId;
		this.count = count.intValue();
	}

	/**
	 * @param customerId
	 * @param customerInvoicingThreshold
	 * @param customerAccountId
	 * @param customerAccountInvoicingThreshold
	 * @param baIDs
	 */
	public ThresholdSummary(Long customerId, Long customerAccountId, int count) {
		this.customerId = customerId;
		this.customerAccountId = customerAccountId;
		this.count = count;
	}

	@Override
	public String toString() {
		return "[ThresholdSummary customerId = " + customerId + " customerAccountId = " + customerAccountId
				+ " count = " + count + "]";
	}
}
