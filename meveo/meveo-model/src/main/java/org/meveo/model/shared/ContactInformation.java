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
import javax.validation.constraints.Pattern;
import javax.validation.constraints.Size;

/**
 * @author Ignas Lelys
 * @created Oct 31, 2010
 * 
 */
@Embeddable
public class ContactInformation implements Serializable, Cloneable {

	private static final long serialVersionUID = 1L;

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

	public ContactInformation() {
	}

	public ContactInformation(ContactInformation contactInformation) {
		this(contactInformation.email, contactInformation.phone,
				contactInformation.mobile, contactInformation.fax);
	}

	public ContactInformation(String email, String phone, String mobile,
			String fax) {
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
