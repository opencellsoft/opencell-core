package org.meveo.model.crm;

import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;
import javax.persistence.UniqueConstraint;

import org.meveo.model.AccountEntity;
import org.meveo.model.BusinessEntity;
import org.meveo.model.billing.Subscription;
import org.meveo.model.mediation.Access;

@Entity
@Table(name = "CRM_CUSTOM_FIELD_INST", uniqueConstraints = @UniqueConstraint(columnNames = { "CODE", "SUBSCRIPTION_ID",
		"ACCOUNT_ID", "PROVIDER_ID" }))
@SequenceGenerator(name = "ID_GENERATOR", sequenceName = "CRM_CUSTOM_FIELD_INST_SEQ")
public class CustomFieldInstance extends BusinessEntity {

	private static final long serialVersionUID = 8691447585410651639L;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "ACCOUNT_ID")
	private AccountEntity account;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "SUBSCRIPTION_ID")
	private Subscription subscription;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "ACCESS_ID")
	private Access access;

	@Column(name = "STRING_VALUE")
	private String stringValue;

	@Column(name = "DATE_VALUE")
	private Date dateValue;

	@Column(name = "LONG_VALUE")
	private Long longValue;

	@Column(name = "DOUBLE_VALUE")
	private Double doubleValue;

	public String getStringValue() {
		return stringValue;
	}

	public void setStringValue(String stringValue) {
		this.stringValue = stringValue;
	}

	public Date getDateValue() {
		return dateValue;
	}

	public void setDateValue(Date dateValue) {
		this.dateValue = dateValue;
	}

	public Long getLongValue() {
		return longValue;
	}

	public void setLongValue(Long longValue) {
		this.longValue = longValue;
	}

	public Double getDoubleValue() {
		return doubleValue;
	}

	public void setDoubleValue(Double doubleValue) {
		this.doubleValue = doubleValue;
	}

	public AccountEntity getAccount() {
		return account;
	}

	public void setAccount(AccountEntity account) {
		this.account = account;
	}

	public Subscription getSubscription() {
		return subscription;
	}

	public void setSubscription(Subscription subscription) {
		this.subscription = subscription;
	}

	public Access getAccess() {
		return access;
	}

	public void setAccess(Access access) {
		this.access = access;
	}

	public String toJson() {
		String result = code + ":";

		if (stringValue != null) {
			result += "'" + stringValue + "'";
		} else if (dateValue != null) {
			result += "'" + sdf.format(dateValue) + "'";
		} else if (longValue != null) {
			result += longValue;
		} else if (doubleValue != null) {
			result += doubleValue;
		} else {
			result = "";
		}

		return result;
	}

	@Override
	public String toString() {
		return "CustomFieldInstance [account=" + account + ", subscription=" + subscription + ", access=" + access
				+ ", stringValue=" + stringValue + ", dateValue=" + dateValue + ", longValue=" + longValue
				+ ", doubleValue=" + doubleValue + "]";
	}

}
