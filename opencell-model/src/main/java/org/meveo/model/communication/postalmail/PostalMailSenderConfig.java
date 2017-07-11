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
package org.meveo.model.communication.postalmail;

import javax.persistence.Column;
import javax.persistence.DiscriminatorValue;
import javax.persistence.Embedded;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.validation.constraints.Size;

import org.hibernate.annotations.Type;
import org.meveo.model.communication.MessageSenderConfig;
import org.meveo.model.shared.Address;

@Entity
@DiscriminatorValue("POSTAL_MAIL")
public class PostalMailSenderConfig extends MessageSenderConfig {

	private static final long serialVersionUID = 1L;

	@Embedded
	private Address undeliveredReturnAddress;

	@Enumerated(EnumType.STRING)
	private EnvelopeFormatEnum envelopFormat;

	@Enumerated(EnumType.STRING)
	private EnvelopeWindowType windowType;

	@Type(type="numeric_boolean")
	@Column()
	private boolean printRectoVerso;

	@Type(type="numeric_boolean")
	@Column()
	private boolean useColor;

	@Type(type="numeric_boolean")
	@Column()
	private boolean addAddressFrontPage;

	@Column(name = "stamptype", length = 255)
    @Size(max = 255)
	private String STAMPtype;

	public Address getUndeliveredReturnAddress() {
		return undeliveredReturnAddress;
	}

	public void setUndeliveredReturnAddress(Address undeliveredReturnAddress) {
		this.undeliveredReturnAddress = undeliveredReturnAddress;
	}

	public EnvelopeFormatEnum getEnvelopFormat() {
		return envelopFormat;
	}

	public void setEnvelopFormat(EnvelopeFormatEnum envelopFormat) {
		this.envelopFormat = envelopFormat;
	}

	public EnvelopeWindowType getWindowType() {
		return windowType;
	}

	public void setWindowType(EnvelopeWindowType windowType) {
		this.windowType = windowType;
	}

	public boolean isPrintRectoVerso() {
		return printRectoVerso;
	}

	public void setPrintRectoVerso(boolean printRectoVerso) {
		this.printRectoVerso = printRectoVerso;
	}

	public boolean isUseColor() {
		return useColor;
	}

	public void setUseColor(boolean useColor) {
		this.useColor = useColor;
	}

	public boolean isAddAddressFrontPage() {
		return addAddressFrontPage;
	}

	public void setAddAddressFrontPage(boolean addAddressFrontPage) {
		this.addAddressFrontPage = addAddressFrontPage;
	}

	public String getSTAMPtype() {
		return STAMPtype;
	}

	public void setSTAMPtype(String sTAMPtype) {
		STAMPtype = sTAMPtype;
	}

}
