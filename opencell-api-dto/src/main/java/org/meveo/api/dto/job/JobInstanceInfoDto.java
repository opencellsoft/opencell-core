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

package org.meveo.api.dto.job;

import java.io.Serializable;
import java.util.Date;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

import org.meveo.api.dto.CustomFieldsDto;

/**
 * The Class JobInstanceInfoDto.
 *
 * @author Edward P. Legaspi
 * @author Said Ramli
 * @lastModifiedVersion 5.1
 */
@XmlRootElement(name = "JobInstanceInfo")
@XmlAccessorType(XmlAccessType.FIELD)
public class JobInstanceInfoDto implements Serializable {

    /** The Constant serialVersionUID. */
    private static final long serialVersionUID = -7091372162470026030L;

    /** The timer name. */
    @Deprecated
    @XmlElement(required = false)
    private String timerName;

    /** The code. */
    @XmlAttribute(required = true)
    private String code;

    private boolean start = true;

    /** The last transaction date. */
    private Date lastTransactionDate;

    /** The invoice date. */
    private Date invoiceDate;

    /** The billing cycle. */
    private String billingCycle;

    /** Ignore a check if job is currently running and launch it anyway. */
    private boolean forceExecution;
    
    /** The run on nodes. */
    private String runOnNodes;
    
    /** The parametres. */
    private String parameters;
    
    /** The custom fields. */
    private CustomFieldsDto customFields;
    
    /**
     * @return the parameters
     */
    public String getParameters() {
        return parameters;
    }

    /**
     * @param parameters the parameters to set
     */
    public void setParameters(String parameters) {
        this.parameters = parameters;
    }


    /**
     * Gets the last transaction date.
     *
     * @return the last transaction date
     */
    public Date getLastTransactionDate() {
        return lastTransactionDate;
    }

    /**
     * Sets the last transaction date.
     *
     * @param lastTransactionDate the new last transaction date
     */
    public void setLastTransactionDate(Date lastTransactionDate) {
        this.lastTransactionDate = lastTransactionDate;
    }

    /**
     * Gets the invoice date.
     *
     * @return the invoice date
     */
    public Date getInvoiceDate() {
        return invoiceDate;
    }

    /**
     * Sets the invoice date.
     *
     * @param invoiceDate the new invoice date
     */
    public void setInvoiceDate(Date invoiceDate) {
        this.invoiceDate = invoiceDate;
    }

    /**
     * Gets the billing cycle.
     *
     * @return the billing cycle
     */
    public String getBillingCycle() {
        return billingCycle;
    }

    /**
     * Sets the billing cycle.
     *
     * @param billingCycle the new billing cycle
     */
    public void setBillingCycle(String billingCycle) {
        this.billingCycle = billingCycle;
    }

    /**
     * Gets the timer name.
     *
     * @return the timer name
     */
    public String getTimerName() {
        return timerName;
    }

    /**
     * Sets the timer name.
     *
     * @param timerName the new timer name
     */
    public void setTimerName(String timerName) {
        this.timerName = timerName;
    }

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
     * Checks if is force execution.
     *
     * @return true, if is force execution
     */
    public boolean isForceExecution() {
        return forceExecution;
    }

    /**
     * Sets the force execution.
     *
     * @param forceExecution the new force execution
     */
    public void setForceExecution(boolean forceExecution) {
        this.forceExecution = forceExecution;
    }
    
    /**
     * @return the runOnNodes
     */
    public String getRunOnNodes() {
        return runOnNodes;
    }

    /**
     * @param runOnNodes the runOnNodes to set
     */
    public void setRunOnNodes(String runOnNodes) {
        this.runOnNodes = runOnNodes;
    }

    /**
     * @return the customFields
     */
    public CustomFieldsDto getCustomFields() {
        return customFields;
    }

    /**
     * @param customFields the customFields to set
     */
    public void setCustomFields(CustomFieldsDto customFields) {
        this.customFields = customFields;
    }

    public boolean isStart() {
        return start;
    }

    public void setStart(boolean start) {
        this.start = start;
    }

    @Override
    public String toString() {
        return "JobInstanceInfoDto [timerName=" + timerName + ", code=" + code + ", lastTransactionDate=" + lastTransactionDate + ", invoiceDate=" + invoiceDate + ", billingCycle="
                + billingCycle + "]";
    }
}