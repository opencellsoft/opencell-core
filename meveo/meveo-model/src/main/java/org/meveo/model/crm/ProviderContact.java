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
package org.meveo.model.crm;

import javax.persistence.Column;
import javax.persistence.Embedded;
import javax.persistence.Entity;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;
import javax.validation.constraints.Pattern;
import javax.validation.constraints.Size;

import org.meveo.model.BusinessEntity;
import org.meveo.model.shared.Address;

@Entity
@Table(name = "CRM_PROVIDER_CONTACT")
@SequenceGenerator(name = "ID_GENERATOR", sequenceName = "CRM_PROVIDER_CONTACT_SEQ")
public class ProviderContact extends BusinessEntity {

	private static final long serialVersionUID = 1L;

	@Column(name = "FIRSTNAME", length = 50)
	@Size(max = 50)
	protected String firstName;

	@Column(name = "LASTNAME", length = 50)
	@Size(max = 50)
	protected String lastName;

	@Column(name = "EMAIL", length = 100)
	@Pattern(regexp = ".+@.+\\..{2,4}")
	@Size(max = 100)
	protected String email;

	@Column(name = "PHONE", length = 15)
	@Size(max = 15)
	protected String phone;

	@Column(name = "MOBILE", length = 15)
	@Size(max = 15)
	protected String mobile;

	@Column(name = "FAX", length = 15)
	@Size(max = 15)
	protected String fax;

	@Column(name = "GENERIC_MAIL", length = 100)
	@Pattern(regexp = ".+@.+\\..{2,4}")
	@Size(max = 100)
	protected String genericMail;

	@Embedded
	private Address address = new Address();

	public Address getAddress() {
		if (address != null) {
			return address;
		}
		return new Address();
	}

	public void setAddress(Address address) {
		this.address = address;
	}

	public String getFirstName() {
		return firstName;
	}

	public void setFirstName(String firstName) {
		this.firstName = firstName;
	}

	public String getLastName() {
		return lastName;
	}

	public void setLastName(String lastName) {
		this.lastName = lastName;
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

	public String getGenericMail() {
		return genericMail;
	}

	public void setGenericMail(String genericMail) {
		this.genericMail = genericMail;
	}

}