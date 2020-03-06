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

package org.meveo.api.dto.billing;

import org.meveo.api.dto.BaseEntityDto;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlRootElement;

/**
 * The Class ChargeCDRDto.
 *
 * @author HORRI Khalid
 * @lastModifiedVersion 7.3
 */
@XmlRootElement(name = "ChargeCDRDto")
@XmlAccessorType(XmlAccessType.FIELD)
public class ChargeCDRDto extends BaseEntityDto {

    /**
     * The CDR string
     */
    private String cdr;

    /**
     * The remote IP
     */
    private String ip;
    /**
     * Rating must happen in a transaction no change performed during rating is persisted if isVirtual=true.
     */
    private boolean virtual;

    /**
     * rate all TriggeredEDR created by the rating of the charge.
     */
    private boolean rateTriggeredEdr;

    /**
     * If true, the API will return the list of all wallet operations produced during, even if the are virtual.
     */
    private boolean returnWalletOperations;

    /**
     * The max deep used in triggered EDR.
     */
    private Integer maxDepth;

    public ChargeCDRDto() {
        virtual = false;
        rateTriggeredEdr = false;
        maxDepth = 1;
        returnWalletOperations = false;
    }

    public ChargeCDRDto(String cdr, String ip, boolean virtual, boolean rateTriggeredEdr, boolean returnWalletOperations, Integer maxDepth) {
        this.cdr = cdr;
        this.ip = ip;
        this.virtual = virtual;
        this.rateTriggeredEdr = rateTriggeredEdr;
        this.returnWalletOperations = returnWalletOperations;
        this.maxDepth = maxDepth == null ? 1 : maxDepth;
    }

    public String getCdr() {
        return cdr;
    }

    public void setCdr(String cdr) {
        this.cdr = cdr;
    }

    public String getIp() {
        return ip;
    }

    public void setIp(String ip) {
        this.ip = ip;
    }

    /**
     * check if is virtual.
     *
     * @return true if is virtual.
     */
    public boolean isVirtual() {
        return virtual;
    }

    /**
     * Sets virtual
     *
     * @param virtual
     */
    public void setVirtual(boolean virtual) {
        this.virtual = virtual;
    }

    /**
     * check if is a rateTriggeredEdr.
     *
     * @return true if rateTriggeredEdr, false else.
     */
    public boolean isRateTriggeredEdr() {
        return rateTriggeredEdr;
    }

    /**
     * Sets rateTriggeredEdr
     *
     * @param rateTriggeredEdr
     */
    public void setRateTriggeredEdr(boolean rateTriggeredEdr) {
        this.rateTriggeredEdr = rateTriggeredEdr;
    }

    /**
     * check if return WalletOperations is enabled
     *
     * @return true if return WalletOperations is enabled, false else.
     */
    public boolean isReturnWalletOperations() {
        return returnWalletOperations;
    }

    /**
     * Sets returnWalletOperations
     *
     * @param returnWalletOperations
     */
    public void setReturnWalletOperations(boolean returnWalletOperations) {
        this.returnWalletOperations = returnWalletOperations;
    }

    /**
     * Gets the maxDepth
     *
     * @return the maxDepth
     */
    public Integer getMaxDepth() {
        if (maxDepth == null) {
            maxDepth = 1;
        }
        return maxDepth;
    }

    /**
     * Sets the maxDepth
     *
     * @param maxDepth the maxDepth
     */
    public void setMaxDepth(Integer maxDepth) {
        this.maxDepth = maxDepth;
    }
}