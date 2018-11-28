package org.meveo.api.dto;

import java.io.Serializable;
import java.util.Date;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlRootElement;

/**
 * The Class BankingDateStatusDto.
 *
 * @author hznibar
 */
@XmlRootElement()
@XmlAccessorType(XmlAccessType.FIELD)
public class BankingDateStatusDto implements Serializable {

    /** The Constant serialVersionUID. */
    private static final long serialVersionUID = -980309137868444523L;
    
    /** The date to check. */
    private Date date;
    
    /** The date status: true: if it's a bank working day, false if it's a weekend or holiday. */
    private Boolean isWorkingDate;
    
    
    
    
    /**
     * Instantiates a new banking date status dto.
     */
    public BankingDateStatusDto() {
        super();
    }
    
    /**
     * Instantiates a new banking date status dto.
     *
     * @param date the date
     * @param isWorkingDate the is working date
     */
    public BankingDateStatusDto(Date date, Boolean isWorkingDate) {
        super();
        this.date = date;
        this.isWorkingDate = isWorkingDate;
    }




    /**
     * Gets the date.
     *
     * @return the date
     */
    public Date getDate() {
        return date;
    }


    /**
     * Sets the date.
     *
     * @param date the new date
     */
    public void setDate(Date date) {
        this.date = date;
    }


    public Boolean isWorkingDate() {
        return isWorkingDate;
    }


    public void setIsWorkingDate(Boolean isWorkingDate) {
        this.isWorkingDate = isWorkingDate;
    }


    /* (non-Javadoc)
     * @see java.lang.Object#toString()
     */
    @Override
    public String toString() {
        return "BankingDateStatusDto [date=" + date + ", isWorkingDate=" + isWorkingDate + "]";
    }
}