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

package org.meveo.api.dto.catalog;

import javax.xml.bind.annotation.XmlType;
import java.io.Serializable;

@XmlType(name = "baseServiceChargeTemplateDto", propOrder = {
        "code",
        "wallets",
        "counterTemplate"
})

/**
 * The Class BaseServiceChargeTemplateDto.
 *
 * @author Edward P. Legaspi
 * @author Abdellatif BARI
 * @lastModifiedVersion 5.3
 */
public abstract class BaseServiceChargeTemplateDto implements Serializable {

    /** The Constant serialVersionUID. */
    private static final long serialVersionUID = 3771281513359110575L;

    /** The code. */
    private String code;

    /** The wallets. */
    private WalletsDto wallets;
    
    /** The counter template. */
    private String counterTemplate;

    /**
     * Gets the code.
     *
     * @return the code
     */
    public String getCode() {
        return code;
    }

    /**
     * Sets the code.
     *
     * @param code the new code
     */
    public void setCode(String code) {
        this.code = code;
    }

    /**
     * Gets the wallets.
     *
     * @return the wallets
     */
    public WalletsDto getWallets() {
        if (wallets == null)
            wallets = new WalletsDto();
        return wallets;
    }

    /**
     * Sets the wallets.
     *
     * @param wallets the new wallets
     */
    public void setWallets(WalletsDto wallets) {
        this.wallets = wallets;
    }
    
    /**
     * Gets the counter template.
     *
     * @return the counter template
     */
    public String getCounterTemplate() {
        return counterTemplate;
    }

    /**
     * Sets the counter template.
     *
     * @param counterTemplate the new counter template
     */
    public void setCounterTemplate(String counterTemplate) {
        this.counterTemplate = counterTemplate;
    }

    
    @Override
    public String toString() {
        return "BaseServiceChargeTemplateDto [code=" + code + ", wallets=" + wallets + ", counterTemplate=" + counterTemplate + "]";
    }   
}