/*
 * (C) Copyright 2015-2016 Opencell SAS (http://opencellsoft.com/) and contributors.
 * (C) Copyright 2009-2014 Manaty SARL (http://manaty.net/) and contributors.
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Affero General Public License as
 * published by the Free Software Foundation, either version 3 of the
 * License, or (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  
 * This program is not suitable for any direct or indirect application in MILITARY industry
 * See the GNU Affero General Public License for more details.
 *
 * You should have received a copy of the GNU Affero General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package org.meveo.model.shared;

import java.io.Serializable;

import javax.persistence.Column;
import javax.persistence.Embeddable;
import javax.validation.constraints.Size;

@Embeddable
public class ContactInformation implements Serializable, Cloneable {

	private static final long serialVersionUID = 1L;

	@Column(name = "email", length = 100)
	// @Pattern(regexp = ".+@.+\\..{2,4}")
	@Size(max = 100)
	protected String email;

	@Column(name = "phone", length = 15)
	@Size(max = 15)
	protected String phone;

	@Column(name = "mobile", length = 15)
	@Size(max = 15)
	protected String mobile;

	@Column(name = "fax", length = 15)
	@Size(max = 15)
	protected String fax;

	public ContactInformation() {
	}

	public ContactInformation(ContactInformation contactInformation) {
		this(contactInformation.email, contactInformation.phone, contactInformation.mobile, contactInformation.fax);
	}

	public ContactInformation(String email, String phone, String mobile, String fax) {
		super();
		this.email = email;
		this.phone = phone;
		this.mobile = mobile;
		this.fax = fax;
	}

	public String getEmail() {
		return email;
	}

	public void setEmail(String email) {
		this.email = email;
	}

	public String getPhone() {
		return phone;
	}

	public void setPhone(String phone) {
		this.phone = phone;
	}

	public String getMobile() {
		return mobile;
	}

	public void setMobile(String mobile) {
		this.mobile = mobile;
	}

	public String getFax() {
		return fax;
	}

	public void setFax(String fax) {
		this.fax = fax;
	}

	@Override
	public Object clone() throws CloneNotSupportedException {
		return super.clone();
	}

}
