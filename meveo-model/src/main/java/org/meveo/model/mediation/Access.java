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

import org.meveo.model.CustomFieldEntity;
import org.meveo.model.EnableEntity;
import org.meveo.model.ExportIdentifier;
import org.meveo.model.ICustomFieldEntity;
import org.meveo.model.IEntity;
import org.meveo.model.ObservableEntity;
import org.meveo.model.billing.Subscription;
import org.meveo.model.crm.AccountLevelEnum;
import org.meveo.model.crm.CustomFieldInstance;

/**
 * Access linked to Subscription and Zone.
 */
@Entity
@ObservableEntity
@CustomFieldEntity(accountLevel = AccountLevelEnum.ACC)
@ExportIdentifier({ "accessUserId", "subscription.code", "provider" })
@Table(name = "MEDINA_ACCESS", uniqueConstraints = { @UniqueConstraint(columnNames = { "ACCES_USER_ID", "SUBSCRIPTION_ID" }) })
@SequenceGenerator(name = "ID_GENERATOR", sequenceName = "MEDINA_ACCESS_SEQ")
@NamedQueries({ @NamedQuery(name = "Access.getAccessesForCache", query = "SELECT a from Access a where a.disabled=false order by a.accessUserId") })
public class Access extends EnableEntity implements ICustomFieldEntity {

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

    public String getCacheKey() {
        return getProvider().getCode() + "_" + accessUserId;
    }

    public Map<String, CustomFieldInstance> getCustomFields() {
        return customFields;
    }

    public void setCustomFields(Map<String, CustomFieldInstance> customFields) {
        this.customFields = customFields;
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

    @Override
    public String toString() {
        return String.format("Access [%s, accessUserId=%s, startDate=%s, endDate=%s, subscription=%s, subscription.status=%s]", super.toString(), accessUserId, startDate, endDate,
            subscription != null ? subscription.getId() : null, subscription != null ? subscription.getStatus() : null);
    }

    @Override
    public ICustomFieldEntity getParentCFEntity() {
        return subscription;
    }

    @Override
    public Object getCFValue(String cfCode) {
        if (getCustomFields().containsKey(cfCode)) {
            return getCustomFields().get(cfCode);
        }
        return null;
    }

    @Override
    public Object getCFValue(String cfCode, Date date) {
        if (getCustomFields().containsKey(cfCode)) {
            return getCustomFields().get(cfCode).getValue(date);
        }
        return null;
    }

    @Override
    public Object getInheritedOnlyCFValue(String cfCode) {
        if (getParentCFEntity() != null) {
            return getParentCFEntity().getInheritedOnlyCFValue(cfCode);
        }
        return null;
    }

    @Override
    public Object getInheritedOnlyCFValue(String cfCode, Date date) {

        if (getParentCFEntity() != null) {
            return getParentCFEntity().getInheritedOnlyCFValue(cfCode, date);
        }
        return null;
    }

    @Override
    public Object getInheritedCFValue(String cfCode) {

        if (getCustomFields().containsKey(cfCode)) {
            return getCustomFields().get(cfCode).getValue();

        } else if (getParentCFEntity() != null) {
            return getParentCFEntity().getInheritedCFValue(cfCode);
        }
        return null;
    }

    @Override
    public Object getInheritedCFValue(String cfCode, Date date) {

        Object value = null;

        if (getCustomFields().containsKey(cfCode)) {
            value = getCustomFields().get(cfCode).getValue(date);
        }
        if (value == null && getParentCFEntity() != null) {
            return getParentCFEntity().getInheritedCFValue(cfCode, date);
        }
        return null;
    }
}