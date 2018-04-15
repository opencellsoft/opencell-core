package org.meveo.api.dto.account;

import java.io.Serializable;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlRootElement;

/**
 * The Class AddressDto.
 *
 * @author Edward P. Legaspi
 * @lastModifiedVersion 5.0
 */
@XmlRootElement(name = "Address")
@XmlAccessorType(XmlAccessType.FIELD)
public class AddressDto implements Serializable {

    /** The Constant serialVersionUID. */
    private static final long serialVersionUID = 3064994876758578132L;

    /** The address 1. */
    protected String address1;
    
    /** The address 2. */
    protected String address2;
    
    /** The address 3. */
    protected String address3;
    
    /** The zip code. */
    protected String zipCode;
    
    /** The city. */
    protected String city;
    
    /** The country. */
    protected String country;
    
    /** The state. */
    protected String state;

    /**
     * Instantiates a new address dto.
     */
    public AddressDto() {

    }

    /**
     * Instantiates a new address dto.
     *
     * @param address the address entity
     */
    public AddressDto(org.meveo.model.shared.Address address) {
        if (address != null) {
            address1 = address.getAddress1();
            address2 = address.getAddress2();
            address3 = address.getAddress3();
            zipCode = address.getZipCode();
            city = address.getCity();
            country = address.getCountry() == null ? null : address.getCountry().getCountryCode();
            state = address.getState();
        }
    }

    /**
     * Gets the address 1.
     *
     * @return the address 1
     */
    public String getAddress1() {
        return address1;
    }

    /**
     * Sets the address 1.
     *
     * @param address1 the new address 1
     */
    public void setAddress1(String address1) {
        this.address1 = address1;
    }

    /**
     * Gets the address 2.
     *
     * @return the address 2
     */
    public String getAddress2() {
        return address2;
    }

    /**
     * Sets the address 2.
     *
     * @param address2 the new address 2
     */
    public void setAddress2(String address2) {
        this.address2 = address2;
    }

    /**
     * Gets the address 3.
     *
     * @return the address 3
     */
    public String getAddress3() {
        return address3;
    }

    /**
     * Sets the address 3.
     *
     * @param address3 the new address 3
     */
    public void setAddress3(String address3) {
        this.address3 = address3;
    }

    /**
     * Gets the zip code.
     *
     * @return the zip code
     */
    public String getZipCode() {
        return zipCode;
    }

    /**
     * Sets the zip code.
     *
     * @param zipCode the new zip code
     */
    public void setZipCode(String zipCode) {
        this.zipCode = zipCode;
    }

    /**
     * Gets the city.
     *
     * @return the city
     */
    public String getCity() {
        return city;
    }

    /**
     * Sets the city.
     *
     * @param city the new city
     */
    public void setCity(String city) {
        this.city = city;
    }

    /**
     * Gets the country.
     *
     * @return the country
     */
    public String getCountry() {
        return country;
    }

    /**
     * Sets the country.
     *
     * @param country the new country
     */
    public void setCountry(String country) {
        this.country = country;
    }

    /**
     * Gets the state.
     *
     * @return the state
     */
    public String getState() {
        return state;
    }

    /**
     * Sets the state.
     *
     * @param state the new state
     */
    public void setState(String state) {
        this.state = state;
    }

    @Override
    public String toString() {
        return "Address [address1=" + address1 + ", address2=" + address2 + ", address3=" + address3 + ", zipCode=" + zipCode + ", city=" + city + ", country=" + country
                + ", state=" + state + "]";
    }

}
