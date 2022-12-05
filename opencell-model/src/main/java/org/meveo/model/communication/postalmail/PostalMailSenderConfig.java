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
package org.meveo.model.communication.postalmail;

import org.hibernate.type.NumericBooleanConverter;
import org.meveo.model.communication.MessageSenderConfig;
import org.meveo.model.shared.Address;

import jakarta.persistence.Column;
import jakarta.persistence.Convert;
import jakarta.persistence.DiscriminatorValue;
import jakarta.persistence.Embedded;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.validation.constraints.Size;

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

    @Convert(converter = NumericBooleanConverter.class)
    @Column(name = "printrectoverso")
    private boolean printRectoVerso;

    @Convert(converter = NumericBooleanConverter.class)
    @Column(name = "usecolor")
    private boolean useColor;

    @Convert(converter = NumericBooleanConverter.class)
    @Column(name = "addaddressfrontpage")
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
