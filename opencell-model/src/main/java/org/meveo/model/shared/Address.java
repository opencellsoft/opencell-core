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
package org.meveo.model.shared;

import java.io.Serializable;

import javax.persistence.Column;
import javax.persistence.Embeddable;
import javax.persistence.FetchType;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.validation.constraints.Size;

import org.meveo.model.billing.Country;

/**
 * Address information
 * 
 * @author anasseh
 * @author Edward P. Legaspi
 * @lastModifiedVersion 5.2
 */
@Embeddable
public class Address implements Serializable, Cloneable {

    private static final long serialVersionUID = 1L;

    /**
     * Address line 1
     */
    @Column(name = "address_1", length = 255)
    @Size(max = 255)
    protected String address1;

    /**
     * Address line 2
     */
    @Column(name = "address_2", length = 255)
    @Size(max = 255)
    protected String address2;

    /**
     * Address line 3
     */
    @Column(name = "address_3", length = 255)
    @Size(max = 255)
    protected String address3;

    /**
     * Address line 2
     */
    @Column(name = "address_4", length = 255)
    @Size(max = 255)
    protected String address4;

    /**
     * Address line 3
     */
    @Column(name = "address_5", length = 255)
    @Size(max = 255)
    protected String address5;
    /**
     * Postal code
     */
    @Column(name = "address_zipcode", length = 10)
    @Size(max = 10)
    protected String zipCode;

    /**
     * City
     */
    @Column(name = "address_city", length = 50)
    @Size(max = 50)
    protected String city;

    /**
     * Country
     */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "address_country_id")
    protected Country country;

    /**
     * State
     */
    @Column(name = "address_state", length = 50)
    @Size(max = 50)
    protected String state;

    public Address() {
    }

    public Address(Address address) {
        this(address.address1, address.address2, address.address3,address.address4, address.address5, address.zipCode, address.city, address.country, address.state);
    }

    public Address(String address1, String address2, String address3, String zipCode, String city, Country country, String state) {
        super();
        this.address1 = address1;
        this.address2 = address2;
        this.address3 = address3;
        this.zipCode = zipCode;
        this.city = city;
        this.country = country;
        this.state = state;
    }

    public Address(String address1, String address2, String address3,String address4, String address5, String zipCode, String city, Country country, String state) {
        super();
        this.address1 = address1;
        this.address2 = address2;
        this.address3 = address3;
        this.address4 = address4;
        this.address5 = address5;
        this.zipCode = zipCode;
        this.city = city;
        this.country = country;
        this.state = state;
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

    public String getZipCode() {
        return zipCode;
    }

    public void setZipCode(String zipCode) {
        this.zipCode = zipCode;
    }

    public String getCity() {
        return city;
    }

    public void setCity(String city) {
        this.city = city;
    }

    public String getState() {
        return state;
    }

    public void setState(String state) {
        this.state = state;
    }

    public Country getCountry() {
        return country;
    }

    public void setCountry(Country country) {
        this.country = country;
    }

    public String getCountryBundle() {
        if (country == null)
            return null;
        return "countries." + country.getDescription();
    }

    @Override
    public String toString() {
        return (address1 == null ? "" : address1) + "|" + (address2 == null ? "" : address2) + "|" + (address3 == null ? "" : address3) + "|" + (zipCode == null ? "" : zipCode)
                + "|" + (city == null ? "" : city) + "|" + (state == null ? "" : state) + "|" + (country == null ? "" : country);
    }

    @Override
    public Object clone() throws CloneNotSupportedException {
        Address o = (Address) super.clone();
        return o;
    }

    public void anonymize(String code) {
        setAddress1(code);
        setAddress2(code);
        setAddress3(code);
        setCity(code);
        setState(code);
        setZipCode("xxx");
        setCountry(null);
    }

	/**
	 * @return the address4
	 */
	public String getAddress4() {
		return address4;
	}

	/**
	 * @param address4 the address4 to set
	 */
	public void setAddress4(String address4) {
		this.address4 = address4;
	}

	/**
	 * @return the address5
	 */
	public String getAddress5() {
		return address5;
	}

	/**
	 * @param address5 the address5 to set
	 */
	public void setAddress5(String address5) {
		this.address5 = address5;
	}
}
