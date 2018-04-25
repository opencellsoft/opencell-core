package org.meveo.api.dto.usage;

import java.util.Date;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

import org.meveo.api.dto.BaseDto;

/**
 * The Class UsageRequestDto.
 * 
 * @author anasseh
 */
@XmlRootElement(name = "UsageRequest")
@XmlAccessorType(XmlAccessType.FIELD)
public class UsageRequestDto extends BaseDto {

    /** The Constant serialVersionUID. */
    private static final long serialVersionUID = 1L;

    /** The user account code. */
    @XmlElement(required = true)
    private String userAccountCode;

    /** The from date. */
    private Date fromDate;

    /** The to date. */
    private Date toDate;

    /**
     * Instantiates a new usage request dto.
     */
    public UsageRequestDto() {

    }

    /**
     * Gets the user account code.
     *
     * @return the userAccountCode
     */
    public String getUserAccountCode() {
        return userAccountCode;
    }

    /**
     * Sets the user account code.
     *
     * @param userAccountCode the userAccountCode to set
     */
    public void setUserAccountCode(String userAccountCode) {
        this.userAccountCode = userAccountCode;
    }

    /**
     * Gets the from date.
     *
     * @return the fromDate
     */
    public Date getFromDate() {
        return fromDate;
    }

    /**
     * Sets the from date.
     *
     * @param fromDate the fromDate to set
     */
    public void setFromDate(Date fromDate) {
        this.fromDate = fromDate;
    }

    /**
     * Gets the to date.
     *
     * @return the toDate
     */
    public Date getToDate() {
        return toDate;
    }

    /**
     * Sets the to date.
     *
     * @param toDate the toDate to set
     */
    public void setToDate(Date toDate) {
        this.toDate = toDate;
    }

    @Override
    public String toString() {
        return "UsageRequestDto [userAccountCode=" + userAccountCode + ", fromDate=" + fromDate + ", toDate=" + toDate + "]";
    }
}