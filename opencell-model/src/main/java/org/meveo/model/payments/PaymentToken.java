package org.meveo.model.payments;

import javax.persistence.Column;
import javax.persistence.DiscriminatorColumn;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.Inheritance;
import javax.persistence.InheritanceType;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.validation.constraints.NotNull;

import org.hibernate.annotations.GenericGenerator;
import org.hibernate.annotations.Parameter;
import org.hibernate.annotations.Type;
import org.meveo.model.EnableEntity;

@Entity
@Table(name = "AR_PAYMENT_TOKEN")
@Inheritance(strategy = InheritanceType.SINGLE_TABLE)
@DiscriminatorColumn(name = "TOKEN_TYPE")
@GenericGenerator(name = "ID_GENERATOR", strategy = "org.hibernate.id.enhanced.SequenceStyleGenerator", parameters = {@Parameter(name = "sequence_name", value = "AR_PAYMENT_TOKEN_SEQ"), })
public class PaymentToken extends EnableEntity {

	private static final long serialVersionUID = 8726571628074346184L;
	
	@Column(name = "TOKEN_ID")    
	@NotNull
	private String tokenId;
	
	
	@Column(name = "ALIAS")    
	@NotNull
	private String alias;
		
	@Type(type="numeric_boolean")
	@Column(name = "IS_DEFAULT")
	private boolean isDefault;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "CUSTOMER_ACCOUNT_ID")
	private CustomerAccount customerAccount;
	
	public PaymentToken(){		
	}

	public String getAlias() {
		return alias;
	}

	public void setAlias(String alias) {
		this.alias = alias;
	}

	public String getTokenId() {
		return tokenId;
	}

	public void setTokenId(String tokenId) {
		this.tokenId = tokenId;
	}

	public boolean getIsDefault() {
		return isDefault;
	}

	public void setIsDefault(boolean isDefault) {
		this.isDefault = isDefault;
	}

	public CustomerAccount getCustomerAccount() {
		return customerAccount;
	}

	public void setCustomerAccount(CustomerAccount customerAccount) {
		this.customerAccount = customerAccount;
	}

	public void setDefault(boolean isDefault) {
		this.isDefault = isDefault;
	}

	@Override
	public String toString() {
		return "PaymentToken [tokenId=" + tokenId + ", alias=" + alias + ", isDefault=" + isDefault
				+ ", customerAccount=" + customerAccount + "]";
	}

}
