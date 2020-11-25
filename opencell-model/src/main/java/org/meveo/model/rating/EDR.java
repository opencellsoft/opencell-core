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
package org.meveo.model.rating;

import java.math.BigDecimal;
import java.util.Date;

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
import javax.persistence.Transient;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

import org.hibernate.annotations.GenericGenerator;
import org.hibernate.annotations.Parameter;
import org.meveo.model.BaseEntity;
import org.meveo.model.billing.Subscription;

/**
 * Event data record - EDR - information
 * 
 * @author anasseh
 * @lastModifiedVersion 5.1
 */
@Entity
@Table(name = "rating_edr")
@GenericGenerator(name = "ID_GENERATOR", strategy = "org.hibernate.id.enhanced.SequenceStyleGenerator", parameters = { @Parameter(name = "sequence_name", value = "rating_edr_seq"), })
@NamedQueries({
        @NamedQuery(name = "EDR.getEdrsForCache", query = "select CONCAT(case when e.originBatch is null then '' else e.originBatch end ,'_',case when e.originRecord is null then '' else e.originRecord end) as cacheKey from EDR e where e.status='OPEN' ORDER BY e.eventDate DESC"),

        @NamedQuery(name = "EDR.listToRateIds", query = "SELECT e.id from EDR e join e.subscription sub where e.status='OPEN' order by sub.userAccount.id, e.subscription.id, e.id"),
        @NamedQuery(name = "EDR.listToRateIdsLimitByDate", query = "SELECT e.id from EDR e join e.subscription sub where e.status='OPEN' and e.eventDate<:rateUntilDate order by sub.userAccount.id, e.subscription.id, e.id"),
        @NamedQuery(name = "EDR.listToRateIdsLimitByRG", query = "SELECT e.id from EDR e join e.subscription sub where e.status='OPEN' and e.subscription.ratingGroup=:ratingGroup order by sub.userAccount.id, e.subscription.id, e.id"),
        @NamedQuery(name = "EDR.listToRateIdsLimitByDateAndRG", query = "SELECT e.id from EDR e join e.subscription sub where e.status='OPEN' and e.eventDate<:rateUntilDate and e.subscription.ratingGroup=:ratingGroup order by sub.userAccount.id, e.subscription.id, e.id"),

        @NamedQuery(name = "EDR.countNbrEdrByStatus", query = "select e.status, count(e.id) from EDR e group by e.status"),

        @NamedQuery(name = "EDR.updateWalletOperationForSafeDeletion", query = "update WalletOperation wo set wo.edr=NULL where wo.edr in (select e FROM EDR e where e.status<>'OPEN' AND :firstTransactionDate<e.eventDate and e.eventDate<:lastTransactionDate)"),
        @NamedQuery(name = "EDR.updateRatedTransactionForSafeDeletion", query = "update RatedTransaction rt set rt.edr=NULL where rt.edr in (select e FROM EDR e where e.status<>'OPEN' AND :firstTransactionDate<e.eventDate and e.eventDate<:lastTransactionDate)"),
        @NamedQuery(name = "EDR.deleteNotOpenEdrBetweenTwoDate", query = "delete from EDR e where e.status<>'OPEN' AND :firstTransactionDate<e.eventDate and e.eventDate<:lastTransactionDate"),
        @NamedQuery(name = "EDR.updateWalletOperationForSafeDeletionByStatus", query = "update WalletOperation wo set wo.edr=NULL where wo.edr in (select e FROM EDR e where e.status in (:status) AND :firstTransactionDate<=e.eventDate and e.eventDate<=:lastTransactionDate)"),
        @NamedQuery(name = "EDR.updateRatedTransactionForSafeDeletionByStatus", query = "update RatedTransaction rt set rt.edr=NULL where rt.edr in (select e FROM EDR e where e.status in (:status) AND :firstTransactionDate<=e.eventDate and e.eventDate<=:lastTransactionDate)"),
        @NamedQuery(name = "EDR.deleteEdrBetweenTwoDateByStatus", query = "delete from EDR e where e.status in (:status) AND :firstTransactionDate<=e.eventDate and e.eventDate<=:lastTransactionDate"),

        @NamedQuery(name = "EDR.getNotOpenedEdrBetweenTwoDate", query = "SELECT e from EDR e join fetch e.subscription where e.status != 'OPEN' AND :firstTransactionDate<e.eventDate and e.eventDate<:lastTransactionDate and e.id >:lastId order by e.id"),
        @NamedQuery(name = "EDR.getEdrsBetweenTwoDateByStatus", query = "SELECT e from EDR e join fetch e.subscription where e.status in (:status) AND :firstTransactionDate<=e.eventDate and e.eventDate<=:lastTransactionDate and e.id >:lastId order by e.id"),
        @NamedQuery(name = "EDR.updateEdrsToReprocess", query = "update EDR e  set e.status='OPEN',e.rejectReason = NULL, e.timesTried=(case when e.timesTried is null then 1 else (e.timesTried+1) end) where e.id in :ids"),
        @NamedQuery(name = "EDR.reopenByIds", query = "update EDR e  set e.status='OPEN',rejectReason = NULL where e.status='REJECTED' and e.id in :ids") })
public class EDR extends BaseEntity {

    private static final long serialVersionUID = 1278336655583933747L;

    public static final String EDR_TABLE_ORIGIN = "EDR_TABLE";

    /**
     * Matched subscription
     */
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "subscription_id")
    @NotNull
    private Subscription subscription;

    /**
     * the origin batch the EDR comes from (like a CDR file name or EDR table)
     */
    @Column(name = "origin_batch", length = 255)
    @Size(max = 255)
    private String originBatch;

    /**
     * The origin record the EDR comes from (like a CDR magic number)
     */
    @Column(name = "origin_record", length = 255)
    @Size(max = 255)
    private String originRecord;

    /**
     * Event date
     */
    @Temporal(TemporalType.TIMESTAMP)
    @Column(name = "event_date")
    private Date eventDate;

    /**
     * Quantity
     */
    @Column(name = "quantity", precision = NB_PRECISION, scale = NB_DECIMALS)
    private BigDecimal quantity;

    /**
     * Parameter
     */
    @Column(name = "parameter_1", length = 255)
    @Size(max = 255)
    private String parameter1;

    /**
     * Parameter
     */
    @Column(name = "parameter_2", length = 255)
    @Size(max = 255)
    private String parameter2;

    /**
     * Parameter
     */
    @Column(name = "parameter_3", length = 255)
    @Size(max = 255)
    private String parameter3;

    /**
     * Parameter
     */
    @Column(name = "parameter_4", length = 255)
    @Size(max = 255)
    private String parameter4;

    /**
     * Parameter
     */
    @Column(name = "parameter_5", length = 255)
    @Size(max = 255)
    private String parameter5;

    /**
     * Parameter
     */
    @Column(name = "parameter_6", length = 255)
    @Size(max = 255)
    private String parameter6;

    /**
     * Parameter
     */
    @Column(name = "parameter_7", length = 255)
    @Size(max = 255)
    private String parameter7;

    @Column(name = "parameter_8", length = 255)
    @Size(max = 255)
    private String parameter8;

    /**
     * Parameter
     */
    @Column(name = "parameter_9", length = 255)
    @Size(max = 255)
    private String parameter9;

    /**
     * Date type parameter
     */
    @Temporal(TemporalType.TIMESTAMP)
    @Column(name = "date_parameter_1")
    private Date dateParam1;

    /**
     * Date type parameter
     */
    @Temporal(TemporalType.TIMESTAMP)
    @Column(name = "date_parameter_2")
    private Date dateParam2;

    /**
     * Date type parameter
     */
    @Temporal(TemporalType.TIMESTAMP)
    @Column(name = "date_parameter_3")
    private Date dateParam3;

    /**
     * Date type parameter
     */
    @Temporal(TemporalType.TIMESTAMP)
    @Column(name = "date_parameter_4")
    private Date dateParam4;

    /**
     * Date type parameter
     */
    @Temporal(TemporalType.TIMESTAMP)
    @Column(name = "date_parameter_5")
    private Date dateParam5;

    /**
     * Decimal type parameter
     */
    @Column(name = "decimal_parameter_1", precision = NB_PRECISION, scale = NB_DECIMALS)
    private BigDecimal decimalParam1;

    /**
     * Decimal type parameter
     */
    @Column(name = "decimal_parameter_2", precision = NB_PRECISION, scale = NB_DECIMALS)
    private BigDecimal decimalParam2;

    /**
     * Decimal type parameter
     */
    @Column(name = "decimal_parameter_3", precision = NB_PRECISION, scale = NB_DECIMALS)
    private BigDecimal decimalParam3;

    /**
     * Decimal type parameter
     */
    @Column(name = "decimal_parameter_4", precision = NB_PRECISION, scale = NB_DECIMALS)
    private BigDecimal decimalParam4;

    /**
     * Decimal type parameter
     */
    @Column(name = "decimal_parameter_5", precision = NB_PRECISION, scale = NB_DECIMALS)
    private BigDecimal decimalParam5;

    /**
     * Record creation timestamp
     */
    @Temporal(TemporalType.TIMESTAMP)
    @Column(name = "created")
    private Date created;

    /**
     * Last update timestamp
     */
    @Temporal(TemporalType.TIMESTAMP)
    @Column(name = "last_updated")
    private Date updated;

    /**
     * Access code
     */
    @Column(name = "access_code", length = 255)
    @Size(max = 255)
    private String accessCode;

    /**
     * Header EDR
     */
    @JoinColumn(name = "header_edr_id")
    @ManyToOne(fetch = FetchType.LAZY)
    private EDR headerEDR;

    /**
     * Parameter
     */
    @Column(name = "EXTRA_PARAMETER", columnDefinition = "TEXT")
    private String extraParameter;

    /**
     * Processing status
     */
    @Enumerated(EnumType.STRING)
    @Column(name = "status")
    private EDRStatusEnum status = EDRStatusEnum.OPEN;

    /**
     * Rejection reason
     */
    @Column(name = "reject_reason", columnDefinition = "text")
    @Size(max = 255)
    private String rejectReason;

    /** The times tried. */
    @Column(name = "times_tried")
    private Integer timesTried = 0;

    @Transient
    private String ratingRejectionReason;

    /**
     * Tracks quantity left to rate. Initialized with quantity field value on the first call.
     */
    @Transient
    private BigDecimal quantityLeftToRate;

    public Subscription getSubscription() {
        return subscription;
    }

    public void setSubscription(Subscription subscription) {
        this.subscription = subscription;
    }

    public String getOriginBatch() {
        return originBatch;
    }

    public void setOriginBatch(String originBatch) {
        this.originBatch = originBatch;
    }

    public String getOriginRecord() {
        return originRecord;
    }

    public void setOriginRecord(String originRecord) {
        this.originRecord = originRecord;
    }

    public Date getEventDate() {
        return eventDate;
    }

    public void setEventDate(Date eventDate) {
        this.eventDate = eventDate;
    }

    public BigDecimal getQuantity() {
        return quantity;
    }

    public void setQuantity(BigDecimal quantity) {
        this.quantity = quantity;
    }

    public String getParameter1() {
        return parameter1;
    }

    public void setParameter1(String parameter1) {
        this.parameter1 = parameter1;
    }

    public String getParameter2() {
        return parameter2;
    }

    public void setParameter2(String parameter2) {
        this.parameter2 = parameter2;
    }

    public String getParameter3() {
        return parameter3;
    }

    public void setParameter3(String parameter3) {
        this.parameter3 = parameter3;
    }

    public String getParameter4() {
        return parameter4;
    }

    public void setParameter4(String parameter4) {
        this.parameter4 = parameter4;
    }

    public Date getCreated() {
        return created;
    }

    public void setCreated(Date created) {
        this.created = created;
    }

    /**
     * @return Last status change date
     */
    public Date getUpdated() {
        return updated;
    }

    /**
     * @param updated Last status change date
     */
    public void setUpdated(Date updated) {
        this.updated = updated;
    }

    public String getParameter5() {
        return parameter5;
    }

    public void setParameter5(String parameter5) {
        this.parameter5 = parameter5;
    }

    public String getParameter6() {
        return parameter6;
    }

    public void setParameter6(String parameter6) {
        this.parameter6 = parameter6;
    }

    public String getParameter7() {
        return parameter7;
    }

    public void setParameter7(String parameter7) {
        this.parameter7 = parameter7;
    }

    public String getParameter8() {
        return parameter8;
    }

    public void setParameter8(String parameter8) {
        this.parameter8 = parameter8;
    }

    public String getParameter9() {
        return parameter9;
    }

    public void setParameter9(String parameter9) {
        this.parameter9 = parameter9;
    }

    public Date getDateParam1() {
        return dateParam1;
    }

    public void setDateParam1(Date dateParam1) {
        this.dateParam1 = dateParam1;
    }

    public Date getDateParam2() {
        return dateParam2;
    }

    public void setDateParam2(Date dateParam2) {
        this.dateParam2 = dateParam2;
    }

    public Date getDateParam3() {
        return dateParam3;
    }

    public void setDateParam3(Date dateParam3) {
        this.dateParam3 = dateParam3;
    }

    public Date getDateParam4() {
        return dateParam4;
    }

    public void setDateParam4(Date dateParam4) {
        this.dateParam4 = dateParam4;
    }

    public Date getDateParam5() {
        return dateParam5;
    }

    public void setDateParam5(Date dateParam5) {
        this.dateParam5 = dateParam5;
    }

    public BigDecimal getDecimalParam1() {
        return decimalParam1;
    }

    public void setDecimalParam1(BigDecimal decimalParam1) {
        this.decimalParam1 = decimalParam1;
    }

    public BigDecimal getDecimalParam2() {
        return decimalParam2;
    }

    public void setDecimalParam2(BigDecimal decimalParam2) {
        this.decimalParam2 = decimalParam2;
    }

    public BigDecimal getDecimalParam3() {
        return decimalParam3;
    }

    public void setDecimalParam3(BigDecimal decimalParam3) {
        this.decimalParam3 = decimalParam3;
    }

    public BigDecimal getDecimalParam4() {
        return decimalParam4;
    }

    public void setDecimalParam4(BigDecimal decimalParam4) {
        this.decimalParam4 = decimalParam4;
    }

    public BigDecimal getDecimalParam5() {
        return decimalParam5;
    }

    public void setDecimalParam5(BigDecimal decimalParam5) {
        this.decimalParam5 = decimalParam5;
    }

    public String getAccessCode() {
        return accessCode;
    }

    public void setAccessCode(String accessCode) {
        this.accessCode = accessCode;
    }

    public EDR getHeaderEDR() {
        return headerEDR;
    }

    public void setHeaderEDR(EDR headerEDR) {
        this.headerEDR = headerEDR;
    }

    public String getExtraParameter() {
        return extraParameter;
    }

    public void setExtraParameter(String extraParameter) {
        this.extraParameter = extraParameter;
    }

    /**
     * @param ratingRejectionReason Rejection reason why EDR was rejected during rating. A transient value. A persisted value is available in procesingStatus.rejectionReason.
     */
    public void setRatingRejectionReason(String ratingRejectionReason) {
        this.ratingRejectionReason = ratingRejectionReason;
    }

    /**
     * @return Rejection reason why EDR was rejected during rating. A transient value. A persisted value is available in procesingStatus.rejectionReason.
     */
    public String getRatingRejectionReason() {
        return ratingRejectionReason;
    }

    @Override
    public String toString() {
        return "EDR [id=" + id + ", subscription=" + (subscription != null ? subscription.getId() : null) + ", originBatch=" + originBatch + ", originRecord=" + originRecord + ", eventDate=" + eventDate + ", quantity="
                + quantity + ", access=" + accessCode + ", parameter1=" + parameter1 + ", parameter2=" + parameter2 + ", parameter3=" + parameter3 + ", parameter4=" + parameter4 + ", parameter5=" + parameter5
                + ", parameter6=" + parameter6 + ", parameter7=" + parameter7 + ", parameter8=" + parameter8 + ", parameter9=" + parameter9 + ", dateParam1=" + dateParam1 + ", dateParam2=" + dateParam2 + ", dateParam3="
                + dateParam3 + ", dateParam4=" + dateParam4 + ", dateParam5=" + dateParam5 + ", decimalParam1=" + decimalParam1 + ", dateParam2=" + dateParam2 + ", decimalParam3=" + decimalParam3 + ", dateParam4="
                + dateParam4 + ", decimalParam5=" + decimalParam5 + ", extraParameter=" + extraParameter + ", headerEDR=" + ((headerEDR == null) ? "null" : headerEDR.getId()) + ", created=" + created + ", lastUpdate="
                + updated + "]";
    }

    @Override
    public boolean equals(Object obj) {

        if (this == obj) {
            return true;
        } else if (obj == null) {
            return false;
        } else if (!(obj instanceof EDR)) {
            return false;
        }

        EDR other = (EDR) obj;

        if (id != null && other.getId() != null && id.equals(other.getId())) {
            return true;
        }

        return this.toString().equals(other.toString());
    }

    /**
     * @return Quantity left to rate. Initialized with quantity field value on the first call.
     */
    public BigDecimal getQuantityLeftToRate() {

        if (quantityLeftToRate == null) {
            quantityLeftToRate = quantity == null ? BigDecimal.ZERO : quantity;
        }
        return quantityLeftToRate;
    }

    /**
     * Deduce quantity left to rate
     *
     * @param quantityToDeduce Amount to deduce by
     * @return A new quantity left to rate value
     */
    public BigDecimal deduceQuantityLeftToRate(BigDecimal quantityToDeduce) {
        quantityLeftToRate = getQuantityLeftToRate().subtract(quantityToDeduce);
        return quantityLeftToRate;
    }

    /**
     * @return Processing status
     */
    public EDRStatusEnum getStatus() {
        return status;
    }

    /**
     * @param status Processing status
     */
    public void setStatus(EDRStatusEnum status) {
        this.status = status;
    }

    /**
     * Change status and update a last updated timestamp
     *
     * @param status Processing status
     */
    public void changeStatus(EDRStatusEnum status) {
        this.status = status;
        this.updated = new Date();
    }

    /**
     * @return Rejection reason
     */
    public String getRejectReason() {
        return rejectReason;
    }

    /**
     * @param rejectReason Rejection reason
     */
    public void setRejectReason(String rejectReason) {
        this.rejectReason = rejectReason;
    }

    /**
     * Gets the times tried.
     *
     * @return the times tried
     */
    public Integer getTimesTried() {
        return timesTried;
    }

    /**
     * Sets the times tried.
     *
     * @param timesTried the new times tried
     */
    public void setTimesTried(Integer timesTried) {
        this.timesTried = timesTried;
    }

    /**
     * Get the last status date - either record creation or updatedate
     * 
     * @return Last status date
     */
    public Date getStatusDate() {
        return updated == null ? created : updated;
    }
}