/*
 * (C) Copyright 2009-2014 Manaty SARL (http://manaty.net/) and contributors.
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Affero General Public License as
 * published by the Free Software Foundation, either version 3 of the
 * License, or (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Affero General Public License for more details.
 *
 * You should have received a copy of the GNU Affero General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package org.meveo.model.billing;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.FetchType;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.MapKeyColumn;
import javax.persistence.OneToMany;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import javax.persistence.UniqueConstraint;
import javax.validation.constraints.NotNull;

import org.meveo.model.BusinessEntity;
import org.meveo.model.ICustomFieldEntity;
import org.meveo.model.catalog.OfferTemplate;
import org.meveo.model.crm.CustomFieldInstance;
import org.meveo.model.mediation.Access;

/**
 * Subscription
 */
@Entity
@Table(name = "BILLING_SUBSCRIPTION", uniqueConstraints = @UniqueConstraint(columnNames = { "CODE", "PROVIDER_ID" }))
@SequenceGenerator(name = "ID_GENERATOR", sequenceName = "BILLING_SUBSCRIPTION_SEQ")
public class Subscription extends BusinessEntity implements ICustomFieldEntity{

	private static final long serialVersionUID = 1L;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "OFFER_ID")
	private OfferTemplate offer;

	@Enumerated(EnumType.STRING)
	@Column(name = "STATUS")
	private SubscriptionStatusEnum status = SubscriptionStatusEnum.CREATED;

	@Temporal(TemporalType.TIMESTAMP)
	@Column(name = "STATUS_DATE")
	private Date statusDate = new Date();;

	@Temporal(TemporalType.TIMESTAMP)
	@Column(name = "SUBSCRIPTION_DATE")
	private Date subscriptionDate = new Date();

	@Temporal(TemporalType.TIMESTAMP)
	@Column(name = "TERMINATION_DATE")
	private Date terminationDate;

	@OneToMany(mappedBy = "subscription", fetch = FetchType.LAZY)
	// TODO : Add orphanRemoval annotation.
	// @Cascade(org.hibernate.annotations.CascadeType.DELETE_ORPHAN)
	private List<ServiceInstance> serviceInstances = new ArrayList<ServiceInstance>();

	@OneToMany(mappedBy = "subscription", fetch = FetchType.LAZY)
	private List<Access> accessPoints = new ArrayList<Access>();

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "USER_ACCOUNT_ID", nullable = false)
	@NotNull
	private UserAccount userAccount;

	@Temporal(TemporalType.TIMESTAMP)
	@Column(name = "END_AGREMENT_DATE")
	private Date endAgrementDate;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "SUB_TERMIN_REASON_ID", nullable = true)
	private SubscriptionTerminationReason subscriptionTerminationReason;

	@Column(name = "DEFAULT_LEVEL")
	private Boolean defaultLevel = true;

	@OneToMany(mappedBy = "subscription", cascade = CascadeType.ALL, fetch = FetchType.LAZY, orphanRemoval = true)
	@MapKeyColumn(name = "code")
	private Map<String, CustomFieldInstance> customFields = new HashMap<String, CustomFieldInstance>();

	public Date getEndAgrementDate() {
		return endAgrementDate;
	}

	public void setEndAgrementDate(Date endAgrementDate) {
		this.endAgrementDate = endAgrementDate;
	}

	public UserAccount getUserAccount() {
		return userAccount;
	}

	public void setUserAccount(UserAccount userAccount) {
		this.userAccount = userAccount;
	}

	public List<ServiceInstance> getServiceInstances() {
		return serviceInstances;
	}

	public void setServiceInstances(List<ServiceInstance> serviceInstances) {
		this.serviceInstances = serviceInstances;
	}

	public OfferTemplate getOffer() {
		return offer;
	}

	public void setOffer(OfferTemplate offer) {
		this.offer = offer;
	}

	public SubscriptionStatusEnum getStatus() {
		return status;
	}

	public void setStatus(SubscriptionStatusEnum status) {
		this.status = status;
		this.statusDate = new Date();
	}

	public Date getStatusDate() {
		return statusDate;
	}

	public void setStatusDate(Date statusDate) {
		this.statusDate = statusDate;
	}

	public Date getSubscriptionDate() {
		return subscriptionDate;
	}

	public void setSubscriptionDate(Date subscriptionDate) {
		this.subscriptionDate = subscriptionDate;
	}

	public Date getTerminationDate() {
		return terminationDate;
	}

	public void setTerminationDate(Date terminationDate) {
		this.terminationDate = terminationDate;
	}

	public SubscriptionTerminationReason getSubscriptionTerminationReason() {
		return subscriptionTerminationReason;
	}

	public void setSubscriptionTerminationReason(SubscriptionTerminationReason subscriptionTerminationReason) {
		this.subscriptionTerminationReason = subscriptionTerminationReason;
	}

	public List<Access> getAccessPoints() {
		return accessPoints;
	}

	public void setAccessPoints(List<Access> accessPoints) {
		this.accessPoints = accessPoints;
	}

	public Boolean getDefaultLevel() {
		return defaultLevel;
	}

	public void setDefaultLevel(Boolean defaultLevel) {
		this.defaultLevel = defaultLevel;
	}

	public Map<String, CustomFieldInstance> getCustomFields() {
		return customFields;
	}

	public void setCustomFields(Map<String, CustomFieldInstance> customFields) {
		this.customFields = customFields;
	}

	public String getStringCustomValue(String code) {
		String result = null;
		if (customFields.containsKey(code)) {
			result = customFields.get(code).getStringValue();
		}

		return result;
	}
	
	public String getInheritedCustomStringValue(String code){
	String result=null; 
	if (getCustomFields().containsKey(code)&& getCustomFields().get(code).getStringValue()!=null) {
		result=getCustomFields().get(code).getStringValue();
	}else if(userAccount!=null){
		result=userAccount.getInheritedCustomStringValue(code);
	}
	return result;
	}
	
	public Long getInheritedCustomLongValue(String code){
		Long result=null; 
		if (getCustomFields().containsKey(code)&& getCustomFields().get(code).getLongValue()!=null) {
			result=getCustomFields().get(code).getLongValue();
		}else if(userAccount!=null){
			result=userAccount.getInheritedCustomLongValue(code);
		}
		return result;
		}
	
	public Date getInheritedCustomDateValue(String code){
		Date result=null; 
		if (getCustomFields().containsKey(code)&& getCustomFields().get(code).getDateValue()!=null) {
			result=getCustomFields().get(code).getDateValue();
		}else if(userAccount!=null){
			result=userAccount.getInheritedCustomDateValue(code);
		}
		return result;
		}
	

	public Double getInheritedCustomDoubleValue(String code){
		Double result=null; 
		if (getCustomFields().containsKey(code)&& getCustomFields().get(code).getDoubleValue()!=null) {
			result=getCustomFields().get(code).getDoubleValue();
		}else if(userAccount!=null){
            result=userAccount.getInheritedCustomDoubleValue(code);
    }
		return result;
		}
	
	public String getICsv(String code){
		return getInheritedCustomStringValue(code);
	}
	
	public Long getIClv(String code){
		return getInheritedCustomLongValue(code);
	}
	
	public Date getICdav(String code){
		return getInheritedCustomDateValue(code);
	}
	
	public Double getICdov(String code){
		return getInheritedCustomDoubleValue(code);
	}

}
