package org.meveo.api.dto;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlRootElement;

/**
 * @author Mohamed CHAOUKI
 **/

@XmlRootElement()
@XmlAccessorType(XmlAccessType.FIELD)
public class FilterCDRDto {


    private String startDate;

    private String endDate;

    private Double quantity;

    private String accessCode;

    private String parameter1;

    public FilterCDRDto() {
    }

    public FilterCDRDto(String startDate, String endDate, Double quantity, String accessCode, String parameter1) {
        this.startDate = startDate;
        this.endDate = endDate;
        this.quantity = quantity;
        this.accessCode = accessCode;
        this.parameter1 = parameter1;
    }

    public String getStartDate() {
        return startDate;
    }

    public void setStartDate(String startDate) {
        this.startDate = startDate;
    }

    public String getEndDate() {
        return endDate;
    }

    public void setEndDate(String endDate) {
        this.endDate = endDate;
    }

    public Double getQuantity() {
        return quantity;
    }

    public void setQuantity(Double quantity) {
        this.quantity = quantity;
    }

    public String getAccessCode() {
        return accessCode;
    }

    public void setAccessCode(String accessCode) {
        this.accessCode = accessCode;
    }

    public String getParameter1() {
        return parameter1;
    }

    public void setParameter1(String parameter1) {
        this.parameter1 = parameter1;
    }
}
