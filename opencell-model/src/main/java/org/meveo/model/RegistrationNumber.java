package org.meveo.model;

import org.hibernate.annotations.GenericGenerator;
import org.hibernate.annotations.Parameter;
import org.meveo.model.admin.Seller;
import org.meveo.model.billing.BillingAccount;
import org.meveo.model.billing.IsoIcd;
import org.meveo.model.billing.UserAccount;
import org.meveo.model.crm.Customer;
import org.meveo.model.payments.CustomerAccount;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.JoinColumn;
import javax.persistence.JoinColumns;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.persistence.Transient;

@Entity
@ObservableEntity
@Table(name = "account_registration_number")
@GenericGenerator(name = "ID_GENERATOR", strategy = "org.hibernate.id.enhanced.SequenceStyleGenerator", parameters = {
		@Parameter(name = "sequence_name", value = "account_registration_number_seq"), })
public class RegistrationNumber extends  AuditableEntity {
	
	@Column(name = "registration_id")
	private String registrationNo;
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "icd_id")
	private IsoIcd isoIcd;
	
	@Transient
	private AccountEntity accountEntity;
	
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "seller_id")
	private Seller seller;
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "customer_id")
	private Customer customer;
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "customer_account_id")
	private CustomerAccount customerAccount;
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "user_account_id")
	private UserAccount userAccount;
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "billing_account_id")
	private BillingAccount billingAccount;
	
	public RegistrationNumber(){ }
	
	public RegistrationNumber(String registrationNo, IsoIcd isoIcd, AccountEntity accountEntity) {
		this.registrationNo = registrationNo;
		this.isoIcd = isoIcd;
		setAccountEntity(accountEntity);
	}
	
	public String getRegistrationNo() {
		return registrationNo;
	}
	
	public RegistrationNumber setRegistrationNo(String registrationNo) {
		this.registrationNo = registrationNo;
		return this;
	}
	
	public IsoIcd getIsoIcd() {
		return isoIcd;
	}
	
	public RegistrationNumber setIsoIcd(IsoIcd isoIcd) {
		this.isoIcd = isoIcd;
		return this;
	}
	
	public AccountEntity getAccountEntity() {
		return accountEntity;
	}
	
	public RegistrationNumber setAccountEntity(AccountEntity accountEntity) {
		this.accountEntity = accountEntity;
		if(accountEntity instanceof  Seller)
			this.seller = (Seller) accountEntity;
		else if(accountEntity instanceof Customer)
			this.customer = (Customer) accountEntity;
		else if(accountEntity instanceof CustomerAccount)
			this.customerAccount = (CustomerAccount) accountEntity;
		else if(accountEntity instanceof  BillingAccount)
			this.billingAccount = (BillingAccount) accountEntity;
		else this.userAccount = (UserAccount) accountEntity;
		return this;
	}
}
