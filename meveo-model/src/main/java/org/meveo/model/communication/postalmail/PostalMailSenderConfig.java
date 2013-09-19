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
package org.meveo.model.communication.postalmail;

import javax.persistence.DiscriminatorValue;
import javax.persistence.Embedded;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;

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

	private boolean printRectoVerso;

	private boolean useColor;

	private boolean addAddressFrontPage;

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
