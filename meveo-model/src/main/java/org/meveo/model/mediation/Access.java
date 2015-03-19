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
package org.meveo.model.mediation;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.MapKeyColumn;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.OneToMany;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import javax.persistence.UniqueConstraint;

import org.meveo.model.Auditable;
import org.meveo.model.EnableEntity;
import org.meveo.model.ICustomFieldEntity;
import org.meveo.model.IEntity;
import org.meveo.model.billing.Subscription;
import org.meveo.model.crm.CustomFieldInstance;

/**
 * Access linked to Subscription and Zone.
 */
@Entity
@Table(name = "MEDINA_ACCESS", uniqueConstraints = { @UniqueConstraint(columnNames = {
		"ACCES_USER_ID", "SUBSCRIPTION_ID" }) })
@SequenceGenerator(name = "ID_GENERATOR", sequenceName = "MEDINA_ACCESS_SEQ")
@NamedQueries({ @NamedQuery(name = "Access.getAccessesForCache", query = "SELECT a from Access a where a.disabled=false order by a.accessUserId") })
public class Access extends EnableEntity implements ICustomFieldEntity{

	private static final long serialVersionUID = 1L;

	// input
	@Temporal(TemporalType.TIMESTAMP)
	@Column(name = "START_DATE")
	private Date startDate;

	@Temporal(TemporalType.TIMESTAMP)
	@Column(name = "END_DATE")
	private Date endDate;

	@Column(name = "ACCES_USER_ID")
	private String accessUserId;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "SUBSCRIPTION_ID")
	private Subscription subscription;

	@OneToMany(mappedBy = "access", cascade = CascadeType.ALL, fetch = FetchType.LAZY, orphanRemoval = true)
	@MapKeyColumn(name = "code")
	private Map<String, CustomFieldInstance> customFields = new HashMap<String, CustomFieldInstance>();
	
	public Date getStartDate() {
		return startDate;
	}

	public void setStartDate(Date startDate) {
		this.startDate = startDate;
	}

	public Date getEndDate() {
		return endDate;
	}

	public void setEndDate(Date endDate) {
		this.endDate = endDate;
	}

	public String getAccessUserId() {
		return accessUserId;
	}

	public void setAccessUserId(String accessUserId) {
		this.accessUserId = accessUserId;
	}

	public Subscription getSubscription() {
		return subscription;
	}

	public void setSubscription(Subscription subscription) {
		this.subscription = subscription;
	}

	public String getCacheKey(){
		return getProvider().getCode()+"_"+accessUserId;
	}
	
	public Map<String, CustomFieldInstance> getCustomFields() {
		return customFields;
	}

	public void setCustomFields(Map<String, CustomFieldInstance> customFields) {
		this.customFields = customFields;
	}

    private CustomFieldInstance getOrCreateCustomFieldInstance(String code) {
        CustomFieldInstance cfi = null;

		if (customFields.containsKey(code)) {
			cfi = customFields.get(code);
		} else {
			cfi = new CustomFieldInstance();
			Auditable au=new Auditable();
			au.setCreated(new Date());
			if(subscription!=null && subscription.getAuditable()!=null){
				au.setCreator(subscription.getAuditable().getCreator());
			}
			cfi.setAuditable(au);
			cfi.setCode(code);
			cfi.setAccess(this);
			cfi.setProvider(this.getProvider());
			customFields.put(code, cfi);
		}

		return cfi;
	}

	public String getStringCustomValue(String code) {
		String result = null;
		if (customFields.containsKey(code)) {
			result = customFields.get(code).getStringValue();
		}

		return result;
	}

	public void setStringCustomValue(String code, String value) {
	    getOrCreateCustomFieldInstance(code).setStringValue(value);
	}

	public Date getDateCustomValue(String code) {
		Date result = null;
		if (customFields.containsKey(code)) {
			result = customFields.get(code).getDateValue();
		}

		return result;
	}

	public void setDateCustomValue(String code, Date value) {
	    getOrCreateCustomFieldInstance(code).setDateValue(value);
	}

	public Long getLongCustomValue(String code) {
		Long result = null;
		if (customFields.containsKey(code)) {
			result = customFields.get(code).getLongValue();
		}
		return result;
	}

	public void setLongCustomValue(String code, Long value) {
	    getOrCreateCustomFieldInstance(code).setLongValue(value);
	}

	public Double getDoubleCustomValue(String code) {
		Double result = null;

		if (customFields.containsKey(code)) {
			result = customFields.get(code).getDoubleValue();
		}

		return result;
	}

	public void setDoubleCustomValue(String code, Double value) {
	    getOrCreateCustomFieldInstance(code).setDoubleValue(value);
	}

	public String getCustomFieldsAsJson() {
		String result = "";
		String sep = "";

		for (Entry<String, CustomFieldInstance> cf : customFields.entrySet()) {
			result += sep + cf.getValue().toJson();
			sep = ";";
		}

		return result;
	}
	
	
	public String getInheritedCustomStringValue(String code){
		String stringValue=null;
		if (getCustomFields().containsKey(code)&& getCustomFields().get(code).getStringValue()!=null) {
			stringValue=getCustomFields().get(code).getStringValue();
		}else if(subscription!=null){
			stringValue=subscription.getInheritedCustomStringValue(code);
		}
		return stringValue;
		}
	
	public Long getInheritedCustomLongValue(String code){
		Long result=null; 
		if (getCustomFields().containsKey(code)&& getCustomFields().get(code).getLongValue()!=null) {
			result=getCustomFields().get(code).getLongValue();
		}else if(subscription!=null){
			result=subscription.getInheritedCustomLongValue(code);
		}
		return result;
		}
	
	public Date getInheritedCustomDateValue(String code){
		Date result=null; 
		if (getCustomFields().containsKey(code)&& getCustomFields().get(code).getDateValue()!=null) {
			result=getCustomFields().get(code).getDateValue();
		}else if(subscription!=null){
			result=subscription.getInheritedCustomDateValue(code);
		}
		return result;
		}
	

	public Double getInheritedCustomDoubleValue(String code){
		Double result=null; 
		if (getCustomFields().containsKey(code)&& getCustomFields().get(code).getDoubleValue()!=null) {
			result=getCustomFields().get(code).getDoubleValue();
		}else if(subscription!=null){
			result=subscription.getInheritedCustomDoubleValue(code);
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

    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null) {
            return false;
        }

        IEntity other = (IEntity) obj;

        if (getId() != null && other.getId() != null && getId() == other.getId()) {
            return true;
        }

        return false;
    }
}
