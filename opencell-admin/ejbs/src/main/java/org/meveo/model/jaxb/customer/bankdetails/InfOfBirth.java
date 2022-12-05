package org.meveo.model.jaxb.customer.bankdetails;

import java.util.Date;

import jakarta.xml.bind.annotation.XmlAccessType;
import jakarta.xml.bind.annotation.XmlAccessorType;
import jakarta.xml.bind.annotation.XmlElement;
import jakarta.xml.bind.annotation.XmlRootElement;
import jakarta.xml.bind.annotation.XmlType;

@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "", propOrder = { "birthDt", "cityOfBirth", "countryOfBirth" })
@XmlRootElement(name = "DtAndPlcOfBirth")
public class InfOfBirth { 
    @XmlElement(name = "BirthDt")
    protected Date birthDt;
    @XmlElement(name = "CityOfBirth")
    protected String cityOfBirth;    
    @XmlElement(name = "CtryOfBirth")
    protected String countryOfBirth;

    public Date getBirthDt() {
        return birthDt;
    }
    public void setBirthDt(Date birthDt) {
        this.birthDt = birthDt;
    }    
    public String getCityOfBirth() {
        return cityOfBirth;
    }
    public void setCityOfBirth(String cityOfBirth) {
        this.cityOfBirth = cityOfBirth;
    }
    public String getCountryOfBirth() {
        return countryOfBirth;
    }
    public void setCountryOfBirth(String countryOfBirth) {
        this.countryOfBirth = countryOfBirth;
    }
}