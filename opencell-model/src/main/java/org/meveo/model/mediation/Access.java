/*
 * (C) Copyright 2015-2020 Opencell SAS (https://opencellsoft.com/) and contributors.
 *
 * This program is free software: you can redistribute it and/or modify it under the terms of the GNU Affero General
 * Public License as published by the Free Software Foundation, either version 3 of the License, or (at your option)
 * any later version.
 *
 * THERE IS NO WARRANTY FOR THE PROGRAM, TO THE EXTENT PERMITTED BY APPLICABLE LAW. EXCEPT WHEN
 * OTHERWISE STATED IN WRITING THE COPYRIGHT HOLDERS AND/OR OTHER PARTIES PROVIDE THE PROGRAM "AS
 * IS" WITHOUT WARRANTY OF ANY KIND, EITHER EXPRESSED OR IMPLIED, INCLUDING, BUT NOT LIMITED TO, THE
 * IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE. THE ENTIRE RISK AS TO
 * THE QUALITY AND PERFORMANCE OF THE PROGRAM IS WITH YOU. SHOULD THE PROGRAM PROVE DEFECTIVE,
 * YOU ASSUME THE COST OF ALL NECESSARY SERVICING, REPAIR OR CORRECTION.
 *
 * For more information on the GNU Affero General Public License, please consult
 * <https://www.gnu.org/licenses/agpl-3.0.en.html>.
 */
package org.meveo.model.mediation;

import java.util.Date;
import java.util.UUID;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.PrePersist;
import javax.persistence.QueryHint;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import javax.persistence.UniqueConstraint;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

import org.hibernate.annotations.GenericGenerator;
import org.hibernate.annotations.Parameter;
import org.hibernate.annotations.Type;
import org.meveo.model.CustomFieldEntity;
import org.meveo.model.EnableEntity;
import org.meveo.model.ExportIdentifier;
import org.meveo.model.ICustomFieldEntity;
import org.meveo.model.ObservableEntity;
import org.meveo.model.billing.Subscription;
import org.meveo.model.crm.custom.CustomFieldValues;

/**
 * Access point linked to Subscription
 * @author Abdellatif BARI
 * @lastModifiedVersion 7.0
 */
@Entity
@ObservableEntity
@CustomFieldEntity(cftCodePrefix = "Access", inheritCFValuesFrom = "subscription")
@ExportIdentifier({ "accessUserId", "subscription.code" })
@Table(name = "medina_access", uniqueConstraints = { @UniqueConstraint(columnNames = { "acces_user_id", "subscription_id", "start_date", "end_date" }) })
@GenericGenerator(name = "ID_GENERATOR", strategy = "org.hibernate.id.enhanced.SequenceStyleGenerator", parameters = { @Parameter(name = "sequence_name", value = "medina_access_seq"), })
@NamedQueries({ @NamedQuery(name = "Access.getAccessesByUserId", query = "SELECT a from Access a where a.disabled=false and a.accessUserId=:accessUserId", hints = {
        @QueryHint(name = "org.hibernate.cacheable", value = "true") }), 
    @NamedQuery(name = "Access.getCountByParent", query = "select count(*) from Access a where a.subscription=:parent") })
public class Access extends EnableEntity implements ICustomFieldEntity {

    private static final long serialVersionUID = 1L;

    /**
     * Validity period - start date
     */
    @Temporal(TemporalType.TIMESTAMP)
    @Column(name = "start_date")
    private Date startDate;

    /**
     * Validity period - end date
     */
    @Temporal(TemporalType.TIMESTAMP)
    @Column(name = "end_date")
    private Date endDate;

    /**
     * Access point identifier/number
     */
    @Column(name = "acces_user_id", length = 255)
    @Size(max = 255)
    private String accessUserId;

    /**
     * Parent subscription
     */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "subscription_id")
    private Subscription subscription;

    /**
     * Unique identifier - UUID
     */
    @Column(name = "uuid", nullable = false, updatable = false, length = 60)
    @Size(max = 60)
    @NotNull
    private String uuid;

    /**
     * Custom field values in JSON format
     */
    @Type(type = "cfjson")
    @Column(name = "cf_values", columnDefinition = "text")
    private CustomFieldValues cfValues;

    /**
     * Accumulated custom field values in JSON format
     */
    @Type(type = "cfjson")
    @Column(name = "cf_values_accum", columnDefinition = "text")
    private CustomFieldValues cfAccumulatedValues;

    /**
     * @return Validity start date
     */
    public Date getStartDate() {
        return startDate;
    }

    /**
     * @param startDate Validity start date
     */
    public void setStartDate(Date startDate) {
        this.startDate = startDate;
    }

    /**
     * @return Validity end date
     */
    public Date getEndDate() {
        return endDate;
    }

    /**
     * @param endDate Validity end date
     */
    public void setEndDate(Date endDate) {
        this.endDate = endDate;
    }

    /**
     * @return Access user identifier
     */
    public String getAccessUserId() {
        return accessUserId;
    }

    /**
     * @param accessUserId Access user identifier
     */
    public void setAccessUserId(String accessUserId) {
        this.accessUserId = accessUserId;
    }

    /**
     * @return Subscription it relates to
     */
    public Subscription getSubscription() {
        return subscription;
    }

    /**
     * @param subscription Subscription it relates to
     */
    public void setSubscription(Subscription subscription) {
        this.subscription = subscription;
    }

    public String getCacheKey() {
        return accessUserId;
    }

    /**
     * setting uuid if null
     */
    @PrePersist
    public void setUUIDIfNull() {
    	if (uuid == null) {
    		uuid = UUID.randomUUID().toString();
    	}
    }
    
    @Override
    public String getUuid() {
    	setUUIDIfNull(); // setting uuid if null to be sure that the existing code expecting uuid not null will not be impacted
        return uuid;
    }

    /**
     * @param uuid Unique identifier
     */
    public void setUuid(String uuid) {
        this.uuid = uuid;
    }

    @Override
    public String clearUuid() {
        String oldUuid = uuid;
        uuid = UUID.randomUUID().toString();
        return oldUuid;
    }

    @Override
    public boolean equals(Object obj) {

        if (this == obj) {
            return true;
        } else if (obj == null) {
            return false;
        } else if (!(obj instanceof Access)) {
            return false;
        }

        Access other = (Access) obj;

        if (getId() != null && other.getId() != null && getId().equals(other.getId())) {
            return true;
        }

        return false;
    }

    @Override
    public CustomFieldValues getCfValues() {
        return cfValues;
    }

    @Override
    public void setCfValues(CustomFieldValues cfValues) {
        this.cfValues = cfValues;
    }

    @Override
    public CustomFieldValues getCfAccumulatedValues() {
        return cfAccumulatedValues;
    }

    @Override
    public void setCfAccumulatedValues(CustomFieldValues cfAccumulatedValues) {
        this.cfAccumulatedValues = cfAccumulatedValues;
    }

    @Override
    public String toString() {
        return String.format("Access [%s, accessUserId=%s, startDate=%s, endDate=%s, subscription=%s, subscription.status=%s]", super.toString(), accessUserId, startDate, endDate,
            subscription != null ? subscription.getId() : null, subscription != null ? subscription.getStatus() : null);
    }

    @Override
    public ICustomFieldEntity[] getParentCFEntities() {
        if (subscription != null) {
            return new ICustomFieldEntity[] { subscription };
        }
        return null;
    }
}