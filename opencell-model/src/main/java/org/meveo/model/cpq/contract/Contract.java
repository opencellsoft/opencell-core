package org.meveo.model.cpq.contract;

import java.util.Calendar;
import java.util.Date;
import java.util.Objects;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.FetchType;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import javax.persistence.UniqueConstraint;
import javax.validation.constraints.NotNull;

import org.hibernate.annotations.GenericGenerator;
import org.hibernate.annotations.Parameter;
import org.hibernate.annotations.Type;
import org.meveo.model.BusinessEntity;
import org.meveo.model.admin.Seller;
import org.meveo.model.billing.BillingAccount;
import org.meveo.model.cpq.enums.ProductStatusEnum;
import org.meveo.model.crm.Customer;
import org.meveo.model.payments.CustomerAccount;

/**
 * @author Tarik FAKHOURI
 * @version 10.0
 */
@Entity
@Table(name = "cpq_contract", uniqueConstraints = { @UniqueConstraint(columnNames = {"code"})})
@GenericGenerator(name = "ID_GENERATOR", strategy = "org.hibernate.id.enhanced.SequenceStyleGenerator", parameters = {
        @Parameter(name = "sequence_name", value = "cpq_contract_seq"), })
//@NamedQueries({
	@NamedQuery(name = "Contract.findBillingAccount", query = "select c from Contract c  left join fetch  c.billingAccount cb where cb.code=:codeBillingAccount")
	@NamedQuery(name = "Contract.findCustomerAccount", query = "select c from Contract c left join c.customerAccount cc where cc.code=:codeCustomerAccount")
	@NamedQuery(name = "Contract.findCustomer", query = "select c from Contract c left join c.customer cc where cc.code=:codeCustomer")
//})
public class Contract extends BusinessEntity {

	public Contract() {
		this.status = ProductStatusEnum.DRAFT;
		this.statusDate = Calendar.getInstance().getTime();
	}

	/**
	 * 
	 */
	private static final long serialVersionUID = 5754133117256268128L;

	/**
	 *  seller attached to quotes
	 */
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "seller_id", referencedColumnName = "id", nullable = false)
	private Seller seller;
	
	/**
	 *  billing account attached to this contract
	 */
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "billing_account_id", referencedColumnName = "id")
	private BillingAccount billingAccount;

	/**
	 * customer account attached to this contract
	 */
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "customer_account_id", referencedColumnName = "id")
	private CustomerAccount customerAccount;
	
	/**
	 * customer attached to this contract
	 */
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "customer_id", referencedColumnName = "id")
	private Customer customer;
	
	/**
	 * status of this contract
	 */
	@Column(name = "status", nullable = false)
	@Enumerated(EnumType.ORDINAL)
	@NotNull
	private ProductStatusEnum status;
	
	/**
	 * date of the modification of the status
	 */
	@Column(name = "status_date", nullable = false)
	@Temporal(TemporalType.TIMESTAMP)
	@NotNull
	private Date statusDate;
	
	/**
	 * contract date
	 */
	@Column(name = "contract_date", nullable = false)
	@Temporal(TemporalType.DATE)
	@NotNull
	private Date contractDate;

	/**
	 * begin date of the validation of the contract
	 */
	@Column(name = "begin_date", nullable = false)
	@Temporal(TemporalType.DATE)
	@NotNull
	private Date beginDate;
	
	/**
	 * end date of the validation of the contract
	 */
	@Column(name = "end_date", nullable = false)
	@Temporal(TemporalType.DATE)
	@NotNull
	private Date endDate;
	
	/**
	 * flag if this contract will be renewal
	 */
	@Type(type = "numeric_boolean") 
	@Column(name = "renewal", nullable = false)
	private boolean renewal;
	
	/**
	 * duration of the contract
	 */
	@Column(name = "contract_duration")
	private int contractDuration;

	/**
	 * @return the seller
	 */
	public Seller getSeller() {
		return seller;
	}

	/**
	 * @param seller the seller to set
	 */
	public void setSeller(Seller seller) {
		this.seller = seller;
	}

	/**
	 * @return the billingAccount
	 */
	public BillingAccount getBillingAccount() {
		return billingAccount;
	}

	/**
	 * @param billingAccount the billingAccount to set
	 */
	public void setBillingAccount(BillingAccount billingAccount) {
		this.billingAccount = billingAccount;
	}

	/**
	 * @return the customerAccount
	 */
	public CustomerAccount getCustomerAccount() {
		return customerAccount;
	}

	/**
	 * @param customerAccount the customerAccount to set
	 */
	public void setCustomerAccount(CustomerAccount customerAccount) {
		this.customerAccount = customerAccount;
	}

	/**
	 * @return the customer
	 */
	public Customer getCustomer() {
		return customer;
	}

	/**
	 * @param customer the customer to set
	 */
	public void setCustomer(Customer customer) {
		this.customer = customer;
	}

	/**
	 * @return the status
	 */
	public ProductStatusEnum getStatus() {
		return status;
	}

	/**
	 * @param status the status to set
	 */
	public void setStatus(ProductStatusEnum status) {
		this.status = status;
	}

	/**
	 * @return the statusDate
	 */
	public Date getStatusDate() {
		return statusDate;
	}

	/**
	 * @param statusDate the statusDate to set
	 */
	public void setStatusDate(Date statusDate) {
		this.statusDate = statusDate;
	}

	/**
	 * @return the contractDate
	 */
	public Date getContractDate() {
		return contractDate;
	}

	/**
	 * @param contractDate the contractDate to set
	 */
	public void setContractDate(Date contractDate) {
		this.contractDate = contractDate;
	}

	/**
	 * @return the beginDate
	 */
	public Date getBeginDate() {
		return beginDate;
	}

	/**
	 * @param beginDate the beginDate to set
	 */
	public void setBeginDate(Date beginDate) {
		this.beginDate = beginDate;
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
	 * @return the renewal
	 */
	public boolean isRenewal() {
		return renewal;
	}

	/**
	 * @param renewal the renewal to set
	 */
	public void setRenewal(boolean renewal) {
		this.renewal = renewal;
	}

	/**
	 * @return the contractDuration
	 */
	public int getContractDuration() {
		return contractDuration;
	}

	/**
	 * @param contractDuration the contractDuration to set
	 */
	public void setContractDuration(int contractDuration) {
		this.contractDuration = contractDuration;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = super.hashCode();
		result = prime * result + Objects.hash(beginDate, billingAccount, contractDate, contractDuration, customer,
				customerAccount, endDate, renewal, seller, status, statusDate);
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (!super.equals(obj))
			return false;
		if (getClass() != obj.getClass())
			return false;
		Contract other = (Contract) obj;
		return Objects.equals(beginDate, other.beginDate) && Objects.equals(billingAccount, other.billingAccount)
				&& Objects.equals(contractDate, other.contractDate) && contractDuration == other.contractDuration
				&& Objects.equals(customer, other.customer) && Objects.equals(customerAccount, other.customerAccount)
				&& Objects.equals(endDate, other.endDate) && renewal == other.renewal
				&& Objects.equals(seller, other.seller) && status == other.status
				&& Objects.equals(statusDate, other.statusDate);
	}
	
	
	
}
