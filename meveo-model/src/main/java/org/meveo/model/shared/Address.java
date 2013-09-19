/*
 * (C) Copyright 2009-2013 Manaty SARL (http://manaty.net/) and contributors.
 *
 * Licensed under the GNU Public Licence, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.gnu.org/licenses/gpl-2.0.txt
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.meveo.model.shared;

import java.io.Serializable;

import javax.persistence.Column;
import javax.persistence.Embeddable;
import javax.validation.constraints.Size;

@Embeddable
public class Address implements Serializable, Cloneable {
	
	private static final long serialVersionUID = 1L;

	@Column(name = "ADDRESS_1", length = 50)
	@Size(max = 80)
	protected String address1;

	@Column(name = "ADDRESS_2", length = 50)
	@Size(max = 80)
	protected String address2;

	@Column(name = "ADDRESS_3", length = 50)
	@Size(max = 80)
	protected String address3;

	@Column(name = "ADDRESS_ZIPCODE", length = 10)
	@Size(max = 10)
	protected String zipCode;

	@Column(name = "ADDRESS_CITY", length = 50)
	@Size(max = 50)
	protected String city;

	@Column(name = "ADDRESS_COUNTRY", length = 50)
	@Size(max = 50)
	protected String country;

	@Column(name = "ADDRESS_STATE", length = 50)
	@Size(max = 50)
	protected String state;

	public Address() {
	}

	public Address(Address address) {
		this(address.address1, address.address2, address.address3, address.zipCode, address.city,
				address.country, address.state);
	}

	public Address(String address1, String address2, String address3, String zipCode, String city,
			String country, String state) {
		super();
		this.address1 = address1;
		this.address2 = address2;
		this.address3 = address3;
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

	public String getCountry() {
		return country;
	}

	public void setCountry(String country) {
		this.country = country;
	}

	public String getCountryBundle() {
		if (country == null)
			return null;
		return "countries." + country.toLowerCase();
	}

	@Override
	public String toString() {
		return (address1 == null ? "" : address1) + "|" + (address2 == null ? "" : address2) + "|"
				+ (address3 == null ? "" : address3) + "|" + (zipCode == null ? "" : zipCode) + "|"
				+ (city == null ? "" : city) + "|" + (state == null ? "" : state) + "|"
				+ (country == null ? "" : country);
	}

	@Override
	public Object clone() throws CloneNotSupportedException {
		Address o = (Address) super.clone();
		return o;
	}
}
