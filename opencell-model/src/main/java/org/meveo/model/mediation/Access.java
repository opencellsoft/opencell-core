/*
 * (C) Copyright 2015-2016 Opencell SAS (http://opencellsoft.com/) and contributors.
 * (C) Copyright 2009-2014 Manaty SARL (http://manaty.net/) and contributors.
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Affero General Public License as
 * published by the Free Software Foundation, either version 3 of the
 * License, or (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  
 * This program is not suitable for any direct or indirect application in MILITARY industry
 * See the GNU Affero General Public License for more details.
 *
 * You should have received a copy of the GNU Affero General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
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
@Table(name = "medina_access", uniqueConstraints = { @UniqueConstraint(columnNames = { "acces_user_id", "subscription_id" }) })
@GenericGenerator(name = "ID_GENERATOR", strategy = "org.hibernate.id.enhanced.SequenceStyleGenerator", parameters = {
        @Parameter(name = "sequence_name", value = "medina_access_seq"), })
@NamedQueries({
        @NamedQuery(name = "Access.getAccessesByUserId", query = "SELECT a from Access a left join fetch a.subscription where a.disabled=false and a.accessUserId=:accessUserId", hints = {
                @QueryHint(name = "org.hibernate.cacheable", value = "true") }) })
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
    private String uuid = UUID.randomUUID().toString();

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

    @Override
    public String getUuid() {
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