package org.meveo.api.dto.cpq.xml;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;

@XmlAccessorType(XmlAccessType.FIELD)
public class Address {
    private String address1;
    private String address2;
    private String address3;
    private String city;
    private String country;
    private String countryName;
    private String state;

    public Address(org.meveo.model.shared.Address address) {
        if(address == null)
            return;
        this.address1 = address.getAddress1();
        this.address2 = address.getAddress2();
        this.address3 = address.getAddress3();
        this.city = address.getCity();
        this.country = address.getCountry().getCountryCode();
        this.countryName = address.getCountry().getDescription();
        this.state = address.getState();
    }

    public String getAddress1() {
        return address1;
    }

    public void setAddress1(String address1) {
        this.address1 = address1;
    }

    public String getAddress2() {
        return address2;
    }

    public void setAddress2(String address2) {
        this.address2 = address2;
    }

    public String getAddress3() {
        return address3;
    }

    public void setAddress3(String address3) {
        this.address3 = address3;
    }

    public String getCity() {
        return city;
    }

    public void setCity(String city) {
        this.city = city;
    }

    public String getCountry() {
        return country;
    }

    public void setCountry(String country) {
        this.country = country;
    }

    public String getCountryName() {
        return countryName;
    }

    public void setCountryName(String countryName) {
        this.countryName = countryName;
    }

    public String getState() {
        return state;
    }

    public void setState(String state) {
        this.state = state;
    }
}
