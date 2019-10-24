package org.meveo.api.dto.billing;

import java.math.BigDecimal;
import java.util.Date;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlRootElement;

import org.meveo.api.dto.BusinessEntityDto;
import org.meveo.model.rating.EDR;
import org.meveo.model.rating.EDRStatusEnum;

/**
 * The Class WalletOperationDto.
 *
 * @author Edward P. Legaspi
 * @author Said Ramli
 * @lastModifiedVersion 5.1
 */
@XmlRootElement(name = "WalletOperation")
@XmlAccessorType(XmlAccessType.FIELD)
public class EDRDto extends BusinessEntityDto {

    private static final long serialVersionUID = -2901068085522589740L;

    /**
     * Matched subscription
     */
    private String subscriptionCode;

    /**
     * the origin batch the EDR comes from (like a CDR file name or EDR table)
     */
    private String originBatch;

    /**
     * The origin record the EDR comes from (like a CDR magic number)
     */
    private String originRecord;

    /**
     * Event date
     */
    private Date eventDate;

    /**
     * Quantity
     */
    private BigDecimal quantity;

    /**
     * Parameter
     */
    private String parameter1;

    /**
     * Parameter
     */
    private String parameter2;

    /**
     * Parameter
     */
    private String parameter3;

    /**
     * Parameter
     */
    private String parameter4;

    /**
     * Parameter
     */
    private String parameter5;

    /**
     * Parameter
     */
    private String parameter6;

    /**
     * Parameter
     */
    private String parameter7;

    /**
     * Parameter
     */
    private String parameter8;

    /**
     * Parameter
     */
    private String parameter9;

    /**
     * Date type parameter
     */
    private Date dateParam1;

    /**
     * Date type parameter
     */
    private Date dateParam2;

    /**
     * Date type parameter
     */
    private Date dateParam3;

    /**
     * Date type parameter
     */
    private Date dateParam4;

    /**
     * Date type parameter
     */
    private Date dateParam5;

    /**
     * Decimal type parameter
     */
    private BigDecimal decimalParam1;

    /**
     * Decimal type parameter
     */
    private BigDecimal decimalParam2;

    /**
     * Decimal type parameter
     */
    private BigDecimal decimalParam3;

    /**
     * Decimal type parameter
     */
    private BigDecimal decimalParam4;

    /**
     * Decimal type parameter
     */
    private BigDecimal decimalParam5;

    /**
     * Processing status
     */
    private EDRStatusEnum status;

    /**
     * Rejection reason
     */
    private String rejectReason;

    /**
     * Record creation timestamp
     */
    private Date created;

    /**
     * Last update timestamp
     */
    private Date lastUpdate;

    /**
     * Access code
     */
    private String accessCode;

    /**
     * Header EDR
     */
    private EDRDto headerEDR;

    /**
     * Parameter
     */
    private String extraParameter;

    public EDRDto() {

    }

    public EDRDto(EDR e) {
        this.subscriptionCode = (e.getSubscription() != null) ? e.getSubscription().getCode() : null;
        this.originBatch = e.getOriginBatch();
        this.originRecord = e.getOriginRecord();
        this.eventDate = e.getEventDate();
        this.quantity = e.getQuantity();
        this.parameter1 = e.getParameter1();
        this.parameter2 = e.getParameter2();
        this.parameter3 = e.getParameter3();
        this.parameter4 = e.getParameter4();
        this.parameter5 = e.getParameter5();
        this.parameter6 = e.getParameter6();
        this.parameter7 = e.getParameter7();
        this.parameter8 = e.getParameter8();
        this.parameter9 = e.getParameter9();
        this.dateParam1 = e.getDateParam1();
        this.dateParam2 = e.getDateParam2();
        this.dateParam3 = e.getDateParam3();
        this.dateParam4 = e.getDateParam4();
        this.dateParam5 = e.getDateParam5();
        this.decimalParam1 = e.getDecimalParam1();
        this.decimalParam2 = e.getDecimalParam2();
        this.decimalParam3 = e.getDecimalParam3();
        this.decimalParam4 = e.getDecimalParam4();
        this.decimalParam5 = e.getDecimalParam5();
        this.status = e.getStatus();
        this.rejectReason = e.getRejectReason();
        this.created = e.getCreated();
        this.lastUpdate = e.getUpdated();
        this.accessCode = e.getAccessCode();
        this.extraParameter = e.getExtraParameter();
    }

    public String getSubscriptionCode() {
        return subscriptionCode;
    }

    public void setSubscriptionCode(String subscription) {
        this.subscriptionCode = subscription;
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

    public String getAccessCode() {
        return accessCode;
    }

    public void setAccessCode(String accessCode) {
        this.accessCode = accessCode;
    }

    public String getExtraParameter() {
        return extraParameter;
    }

    public void setExtraParameter(String extraParameter) {
        this.extraParameter = extraParameter;
    }

    public EDRDto getHeaderEDR() {
        return headerEDR;
    }

    public void setHeaderEDR(EDRDto headerEDR) {
        this.headerEDR = headerEDR;
    }
}