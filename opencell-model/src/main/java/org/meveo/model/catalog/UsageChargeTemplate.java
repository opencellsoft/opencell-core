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
package org.meveo.model.catalog;

import java.math.BigDecimal;
import java.math.RoundingMode;

import javax.persistence.Column;
import javax.persistence.DiscriminatorValue;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.PostLoad;
import javax.persistence.PostPersist;
import javax.persistence.PostUpdate;
import javax.persistence.QueryHint;
import javax.persistence.Transient;
import javax.validation.constraints.Size;

import org.hibernate.annotations.Type;
import org.meveo.model.cpq.Attribute;

/**
 * Usage charge template
 * 
 * @author Andrius Karpavicius
 * @author Edward P. Legaspi
 * @lastModifiedVersion 7.0
 */
@Entity
@DiscriminatorValue("U")
@NamedQueries({
        @NamedQuery(name = "UsageChargeTemplate.getWithTemplateEDR", query = "SELECT u FROM UsageChargeTemplate u join u.edrTemplates t WHERE :edrTemplate=t"
                + " and u.disabled=false"),

        @NamedQuery(name = "usageChargeTemplate.getNbrUsagesChrgNotAssociated", query = "select count(*) from UsageChargeTemplate u where (u.id not in (select distinct serv.chargeTemplate.id from ServiceChargeTemplateUsage serv) "
                + " OR u.code not in (select distinct p.eventCode from  PricePlanMatrix p where p.eventCode is not null)) ", hints = {
                        @QueryHint(name = "org.hibernate.cacheable", value = "TRUE") }),

        @NamedQuery(name = "usageChargeTemplate.getUsagesChrgNotAssociated", query = "from UsageChargeTemplate u where (u.id not in (select distinct serv.chargeTemplate.id from ServiceChargeTemplateUsage serv) "
                + " OR u.code not in (select distinct p.eventCode from  PricePlanMatrix p where p.eventCode is not null)) ") })
public class UsageChargeTemplate extends ChargeTemplate {

    private static final long serialVersionUID = 1L;

    /**
     * EDR parameter to match
     */
    @Column(name = "filter_param_1", length = 255)
    @Size(max = 255)
    private String filterParam1;

    /**
     * EDR parameter to match
     */
    @Column(name = "filter_param_2", length = 255)
    @Size(max = 255)
    private String filterParam2;

    /**
     * EDR parameter to match
     */
    @Column(name = "filter_param_3", length = 255)
    @Size(max = 255)
    private String filterParam3;

    /**
     * EDR parameter to match
     */
    @Column(name = "filter_param_4", length = 255)
    @Size(max = 255)
    private String filterParam4;

    /**
     * The lower number, the higher the priority is
     */
    @Column(name = "priority", columnDefinition = "int default 1")
    private int priority = 1;

    /**
     * Used to track if "Priority" field value has changed. Value is populated on postLoad, postPersist and postUpdate JPA events
     */
    @Transient
    private int previousPriority = 1;

    /**
     * If true and (charge has no counter associated) then the next matching charge with the full quantity of the EDR.
     */
    @Type(type = "numeric_boolean")
    @Column(name = "trigger_next_charge")
    private boolean triggerNextCharge = false;

    /**
     * Overrides the triggerNextCharge switch.
     */
    @Column(name = "trigger_next_charge_el", length = 2000)
    @Size(max = 2000)
    private String triggerNextChargeEL;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "attribute_id")
    private Attribute usageQuantityAttribute;

    public String getFilterParam1() {
        return filterParam1;
    }

    public void setFilterParam1(String filterParam1) {
        this.filterParam1 = filterParam1;
    }

    public String getFilterParam2() {
        return filterParam2;
    }

    public void setFilterParam2(String filterParam2) {
        this.filterParam2 = filterParam2;
    }

    public String getFilterParam3() {
        return filterParam3;
    }

    public void setFilterParam3(String filterParam3) {
        this.filterParam3 = filterParam3;
    }

    public String getFilterParam4() {
        return filterParam4;
    }

    public void setFilterParam4(String filterParam4) {
        this.filterParam4 = filterParam4;
    }

    /**
     * @return Charge priority. The lower number, the higher the priority is.
     */
    public int getPriority() {
        return priority;
    }

    /**
     * @param priority Charge priority. The lower number, the higher the priority is.
     */
    public void setPriority(int priority) {
        this.priority = priority;
    }

    @Override
    public ChargeMainTypeEnum getChargeMainType() {
        return ChargeMainTypeEnum.USAGE;
    }

    public BigDecimal getInEDRUnit(BigDecimal chargeUnitValue) {
        return chargeUnitValue.divide(getUnitMultiplicator(), getRoundingEdrNbDecimal(), RoundingMode.HALF_UP);
    }

    @PostLoad
    @PostPersist
    @PostUpdate
    private void trackPreviousValues() {
        previousPriority = priority;
        previousCode = code;
    }

    /**
     * Check if current and previous "Priority" field values match. Note: previous value is set to current value at postLoad, postPersist, postUpdate JPA events
     * 
     * @return True if current and previous "Priority" field values DO NOT match
     */
    public boolean isPriorityChanged() {
        return priority != previousPriority;
    }

    public boolean getTriggerNextCharge() {
        return triggerNextCharge;
    }

    public void setTriggerNextCharge(boolean triggerNextCharge) {
        this.triggerNextCharge = triggerNextCharge;
    }

    public String getTriggerNextChargeEL() {
        return triggerNextChargeEL;
    }

    public void setTriggerNextChargeEL(String triggerNextChargeEL) {
        this.triggerNextChargeEL = triggerNextChargeEL;
    }

	/**
	 * @return the usageQuantityAttribute
	 */
	public Attribute getUsageQuantityAttribute() {
		return usageQuantityAttribute;
	}

	/**
	 * @param usageQuantityAttribute the usageQuantityAttribute to set
	 */
	public void setUsageQuantityAttribute(Attribute usageQuantityAttribute) {
		this.usageQuantityAttribute = usageQuantityAttribute;
	}
}