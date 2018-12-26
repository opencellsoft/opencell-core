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
@GenericGenerator(name = "ID_GENERATOR", strategy = "org.hibernate.id.enhanced.SequenceStyleGenerator", parameters = {
        @Parameter(name = "sequence_name", value = "rating_edr_seq"), })
@NamedQueries({
        @NamedQuery(name = "EDR.getEdrsForCache", query = "select CONCAT(case when e.originBatch is null then '' else e.originBatch end ,'_',case when e.originRecord is null then '' else e.originRecord end) as cacheKey from EDR e where e.status= org.meveo.model.rating.EDRStatusEnum.OPEN ORDER BY e.eventDate DESC") })
public class EDR extends BaseEntity {

    private static final long serialVersionUID = 1278336655583933747L;

    public static String EDR_TABLE_ORIGIN = "EDR_TABLE";

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
     * Processing status
     */
    @Enumerated(EnumType.STRING)
    @Column(name = "status")
    private EDRStatusEnum status;

    /**
     * Rejection reason
     */
    @Column(name = "reject_reason", columnDefinition = "text")
    @Size(max = 255)
    private String rejectReason;

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
    private Date lastUpdate;

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

    public EDRStatusEnum getStatus() {
        return status;
    }

    public void setStatus(EDRStatusEnum status) {
        this.status = status;
    }

    public String getRejectReason() {
        return rejectReason;
    }

    public void setRejectReason(String rejectReason) {
        this.rejectReason = rejectReason;
    }

    public Date getCreated() {
        return created;
    }

    public void setCreated(Date created) {
        this.created = created;
    }

    public Date getLastUpdate() {
        return lastUpdate;
    }

    public void setLastUpdate(Date lastUpdate) {
        this.lastUpdate = lastUpdate;
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

    @Override
    public String toString() {
        return "EDR [id=" + id + ", subscription=" + (subscription != null ? subscription.getId() : null) + ", originBatch=" + originBatch + ", originRecord=" + originRecord
                + ", eventDate=" + eventDate + ", quantity=" + quantity + ", access=" + accessCode + ", parameter1=" + parameter1 + ", parameter2=" + parameter2 + ", parameter3="
                + parameter3 + ", parameter4=" + parameter4 + ", parameter5=" + parameter5 + ", parameter6=" + parameter6 + ", parameter7=" + parameter7 + ", parameter8="
                + parameter8 + ", parameter9=" + parameter9 + ", dateParam1=" + dateParam1 + ", dateParam2=" + dateParam2 + ", dateParam3=" + dateParam3 + ", dateParam4="
                + dateParam4 + ", dateParam5=" + dateParam5 + ", decimalParam1=" + decimalParam1 + ", dateParam2=" + dateParam2 + ", decimalParam3=" + decimalParam3
                + ", dateParam4=" + dateParam4 + ", decimalParam5=" + decimalParam5 + ", extraParameter=" + extraParameter + ", headerEDR="
                + ((headerEDR == null) ? "null" : headerEDR.getId()) + ", status=" + status + ", rejectReason=" + rejectReason + ", created=" + created + ", lastUpdate="
                + lastUpdate + "]";
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

}
