package org.meveo.model.dwh;

import java.io.Serializable;

import javax.persistence.Column;

import org.meveo.model.BaseEntity;
import org.meveo.model.IEntity;

/**
 * Stores the summary of records to be deleted as specified by GDPR
 * configuration.
 * 
 * @author Edward P. Legaspi
 * @lastModifiedVersion 5.2
 */
// @Entity
// @Cacheable
// @Table(name = "dwh_gdpr_erasure_summary")
// @GenericGenerator(name = "ID_GENERATOR", strategy =
// "org.hibernate.id.enhanced.SequenceStyleGenerator", parameters = {
// @Parameter(name = "sequence_name", value = "dwh_gdpr_erasure_summary_seq"),
// })
public class GdprErasureSummary extends BaseEntity implements Serializable, IEntity {

	@Column(name = "subscription_count")
	private int subscriptionCount;

	@Column(name = "order_count")
	private int orderCount;

	@Column(name = "invoice_count")
	private int invoiceCount;

	@Column(name = "account_op_count")
	private int accountOperationCount;

	@Column(name = "unpaid_account_op_count")
	private int unpaidAccountOperationCount;

	@Column(name = "customer_prospect_count")
	private int customerProspectCount;

	@Column(name = "mailing_count")
	private int mailingCount;

	public int getSubscriptionCount() {
		return subscriptionCount;
	}

	public void setSubscriptionCount(int subscriptionCount) {
		this.subscriptionCount = subscriptionCount;
	}

	public int getOrderCount() {
		return orderCount;
	}

	public void setOrderCount(int orderCount) {
		this.orderCount = orderCount;
	}

	public int getInvoiceCount() {
		return invoiceCount;
	}

	public void setInvoiceCount(int invoiceCount) {
		this.invoiceCount = invoiceCount;
	}

	public int getAccountOperationCount() {
		return accountOperationCount;
	}

	public void setAccountOperationCount(int accountOperationCount) {
		this.accountOperationCount = accountOperationCount;
	}

	public int getUnpaidAccountOperationCount() {
		return unpaidAccountOperationCount;
	}

	public void setUnpaidAccountOperationCount(int unpaidAccountOperationCount) {
		this.unpaidAccountOperationCount = unpaidAccountOperationCount;
	}

	public int getCustomerProspectCount() {
		return customerProspectCount;
	}

	public void setCustomerProspectCount(int customerProspectCount) {
		this.customerProspectCount = customerProspectCount;
	}

	public int getMailingCount() {
		return mailingCount;
	}

	public void setMailingCount(int mailingCount) {
		this.mailingCount = mailingCount;
	}

}
