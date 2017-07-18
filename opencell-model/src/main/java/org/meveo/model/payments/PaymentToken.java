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
@Table(name = "ar_payment_token")
@Inheritance(strategy = InheritanceType.SINGLE_TABLE)
@DiscriminatorColumn(name = "token_type")
@GenericGenerator(name = "ID_GENERATOR", strategy = "org.hibernate.id.enhanced.SequenceStyleGenerator", parameters = {@Parameter(name = "sequence_name", value = "ar_payment_token_seq"), })
public class PaymentToken extends EnableEntity {

	private static final long serialVersionUID = 8726571628074346184L;
	
	@Column(name = "token_id")    
	@NotNull
	private String tokenId;
	
	
	@Column(name = "alias")    
	@NotNull
	private String alias;
		
	@Type(type="numeric_boolean")
	@Column(name = "is_default")
	private boolean isDefault;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "customer_account_id")
	private CustomerAccount customerAccount;
	
	@Column(name = "USER_ID")    
	private String userId;
	
	@Column(name = "INFO_1")    
	private String info1;
	
	@Column(name = "INFO_2")    
	private String info2;
	
	@Column(name = "INFO_3")    
	private String info3;
	
	@Column(name = "INFO_4")    
	private String info4;
	
	@Column(name = "INFO_5")    
	private String info5;
	
	
	
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

	public String getUserId() {
		return userId;
	}

	public void setUserId(String userId) {
		this.userId = userId;
	}

	public String getInfo1() {
		return info1;
	}

	public void setInfo1(String info1) {
		this.info1 = info1;
	}

	public String getInfo2() {
		return info2;
	}

	public void setInfo2(String info2) {
		this.info2 = info2;
	}

	public String getInfo3() {
		return info3;
	}

	public void setInfo3(String info3) {
		this.info3 = info3;
	}

	public String getInfo4() {
		return info4;
	}

	public void setInfo4(String info4) {
		this.info4 = info4;
	}

	public String getInfo5() {
		return info5;
	}

	public void setInfo5(String info5) {
		this.info5 = info5;
	}

	@Override
	public String toString() {
		return "PaymentToken [tokenId=" + tokenId + ", alias=" + alias + ", isDefault=" + isDefault
				+ ", customerAccount=" + (customerAccount == null ? null : customerAccount.getCode() ) + ", userId=" + userId + ", info1=" + info1 + ", info2="
				+ info2 + ", info3=" + info3 + ", info4=" + info4 + ", info5=" + info5 + "]";
	}
	
	


}
