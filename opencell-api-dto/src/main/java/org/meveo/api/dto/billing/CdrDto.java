package org.meveo.api.dto.billing;

import java.math.BigDecimal;
import java.util.Date;
import java.util.Objects;

import org.meveo.api.dto.BaseEntityDto;

@SuppressWarnings("serial")
public class CdrDto extends BaseEntityDto  {
    
    private Date eventDate;
    private BigDecimal quantity;
    private String parameter1;
    private String parameter2;
    private String parameter3;
    private String parameter4;
    private String parameter5;
    private String parameter6;
    private String parameter7;
    private String parameter8;
    private String parameter9;
    private Date dateParam1;
    private Date dateParam2;
    private Date dateParam3;
    private Date dateParam4;
    private Date dateParam5;
    private BigDecimal decimalParam1;
    private BigDecimal decimalParam2;
    private BigDecimal decimalParam3;
    private BigDecimal decimalParam4;
    private BigDecimal decimalParam5;
    private String accessCode;
    private Long headerEDRId;
    private String extraParam;
    private String rejectReason;
    
    
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
    public String getAccessCode() {
        return accessCode;
    }
    public void setAccessCode(String accessCode) {
        this.accessCode = accessCode;
    }
    public String getExtraParam() {
        return extraParam;
    }
    public void setExtraParam(String extraParam) {
        this.extraParam = extraParam;
    }
    public String getRejectReason() {
        return rejectReason;
    }
    public void setRejectReason(String rejectReason) {
        this.rejectReason = rejectReason;
    }
    public Long getHeaderEDRId() {
        return headerEDRId;
    }
    public void setHeaderEDRId(Long headerEDRId) {
        this.headerEDRId = headerEDRId;
    }
    @Override
    public String toString() {
        return "CdrDto [eventDate=" + eventDate + ", quantity=" + quantity + ", parameter1=" + parameter1 + ", parameter2=" + parameter2 + ", parameter3=" + parameter3 + ", parameter4=" + parameter4 + ", parameter5="
                + parameter5 + ", parameter6=" + parameter6 + ", parameter7=" + parameter7 + ", parameter8=" + parameter8 + ", parameter9=" + parameter9 + ", dateParam1=" + dateParam1 + ", dateParam2=" + dateParam2
                + ", dateParam3=" + dateParam3 + ", dateParam4=" + dateParam4 + ", dateParam5=" + dateParam5 + ", decimalParam1=" + decimalParam1 + ", decimalParam2=" + decimalParam2 + ", decimalParam3=" + decimalParam3
                + ", decimalParam4=" + decimalParam4 + ", decimalParam5=" + decimalParam5 + ", accessCode=" + accessCode + ", headerEDRId=" + headerEDRId + ", extraParam=" + extraParam + ", rejectReason=" + rejectReason
                + "]";
    }
    @Override
    public int hashCode() {
        return Objects.hash(accessCode, dateParam1, dateParam2, dateParam3, dateParam4, dateParam5, decimalParam1, decimalParam2, decimalParam3, decimalParam4, decimalParam5, eventDate, extraParam, headerEDRId,
            parameter1, parameter2, parameter3, parameter4, parameter5, parameter6, parameter7, parameter8, parameter9, quantity, rejectReason);
    }
    @Override
    public boolean equals(Object obj) {
        if (this == obj)
            return true;
        if (obj == null)
            return false;
        if (getClass() != obj.getClass())
            return false;
        CdrDto other = (CdrDto) obj;
        return Objects.equals(accessCode, other.accessCode) && Objects.equals(dateParam1, other.dateParam1) && Objects.equals(dateParam2, other.dateParam2) && Objects.equals(dateParam3, other.dateParam3)
                && Objects.equals(dateParam4, other.dateParam4) && Objects.equals(dateParam5, other.dateParam5) && Objects.equals(decimalParam1, other.decimalParam1) && Objects.equals(decimalParam2, other.decimalParam2)
                && Objects.equals(decimalParam3, other.decimalParam3) && Objects.equals(decimalParam4, other.decimalParam4) && Objects.equals(decimalParam5, other.decimalParam5)
                && Objects.equals(eventDate, other.eventDate) && Objects.equals(extraParam, other.extraParam) && Objects.equals(headerEDRId, other.headerEDRId) && Objects.equals(parameter1, other.parameter1)
                && Objects.equals(parameter2, other.parameter2) && Objects.equals(parameter3, other.parameter3) && Objects.equals(parameter4, other.parameter4) && Objects.equals(parameter5, other.parameter5)
                && Objects.equals(parameter6, other.parameter6) && Objects.equals(parameter7, other.parameter7) && Objects.equals(parameter8, other.parameter8) && Objects.equals(parameter9, other.parameter9)
                && Objects.equals(quantity, other.quantity) && Objects.equals(rejectReason, other.rejectReason);
    }
    
    
}
