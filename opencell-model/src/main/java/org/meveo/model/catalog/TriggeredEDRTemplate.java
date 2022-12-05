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

import java.sql.Types;

import org.hibernate.annotations.GenericGenerator;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.annotations.Parameter;
import org.meveo.model.BusinessEntity;
import org.meveo.model.ExportIdentifier;
import org.meveo.model.ObservableEntity;
import org.meveo.model.communication.MeveoInstance;
import org.meveo.model.scripts.ScriptInstance;

import jakarta.persistence.Cacheable;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;
import jakarta.validation.constraints.Size;

/**
 * A rule for new EDR creation for a processed EDR
 * 
 * @author Andrius Karpavicius
 * @author Edward P. Legaspi
 * @lastModifiedVersion 7.0
 */
@Entity
@ObservableEntity
@Cacheable
@ExportIdentifier({ "code" })
@Table(name = "cat_triggered_edr", uniqueConstraints = @UniqueConstraint(columnNames = { "code" }))
@GenericGenerator(name = "ID_GENERATOR", strategy = "org.hibernate.id.enhanced.SequenceStyleGenerator", parameters = {
        @Parameter(name = "sequence_name", value = "cat_triggered_edr_seq"), })
public class TriggeredEDRTemplate extends BusinessEntity {

    private static final long serialVersionUID = 7130351886235128064L;

    /**
     * Expression to determine subscription code
     */
    @JdbcTypeCode(Types.LONGVARCHAR)
    @Column(name = "subscription_el")
    @Size(max = 2000)
    private String subscriptionEl;

    /**
     * Opencell instance of EDR should be send to another instance. If not empty, EDR will be send via API.
     */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "meveo_instance_id")
    private MeveoInstance meveoInstance;

    /**
     * Expression to determine if EDR rule applies
     */
    @Column(name = "condition_el")
    @Size(max = 2000)
    private String conditionEl;

    /**
     * Expression to determine quantity of new EDR
     */
    @JdbcTypeCode(Types.LONGVARCHAR)
    @Column(name = "quantity_el")
    @Size(max = 2000)
    private String quantityEl;

    /**
     * Expression to determine parameter 1 of new EDR
     */
    @JdbcTypeCode(Types.LONGVARCHAR)
    @Column(name = "param_1_el")
    @Size(max = 2000)
    private String param1El;

    /**
     * Expression to determine parameter 2 of new EDR
     */
    @JdbcTypeCode(Types.LONGVARCHAR)
    @Column(name = "param_2_el")
    @Size(max = 2000)
    private String param2El;

    /**
     * Expression to determine parameter 3 of new EDR
     */
    @JdbcTypeCode(Types.LONGVARCHAR)
    @Column(name = "param_3_el")
    @Size(max = 2000)
    private String param3El;

    /**
     * Expression to determine parameter 4 of new EDR
     */
    @JdbcTypeCode(Types.LONGVARCHAR)
    @Column(name = "param_4_el")
    @Size(max = 2000)
    private String param4El;
    
    /**
     * Expression to compute the OpencellInstance code so the instance on which the EDR is triggered can be inferred from the Offer or whatever.
     * It overrides the value on meveoInstance.
     */
    @JdbcTypeCode(Types.LONGVARCHAR)
    @Column(name = "opencell_instance_el")
    @Size(max = 2000)
    private String opencellInstanceEL;
    
    /**
     * Script to run
     */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "triggered_edr_script_instance_id")
    private ScriptInstance triggeredEdrScript;

    /**
     * @return Expression to evaluate subscription code
     */
    public String getSubscriptionEl() {
        return subscriptionEl;
    }

    /**
     * @param subscriptionEl Expression to evaluate subscription code
     */
    public void setSubscriptionEl(String subscriptionEl) {
        this.subscriptionEl = subscriptionEl;
    }

    /**
     * @return Meveo instance to register a new EDR on. If not empty, EDR will be send via API
     */
    public MeveoInstance getMeveoInstance() {
        return meveoInstance;
    }

    /**
     * @param meveoInstance Meveo instance to register a new EDR on. If not empty, EDR will be send via API
     */
    public void setMeveoInstance(MeveoInstance meveoInstance) {
        this.meveoInstance = meveoInstance;
    }

    /**
     * @return Expression to determine if EDR applies
     */
    public String getConditionEl() {
        return conditionEl;
    }

    /**
     * @param conditionEl Expression to determine if EDR applies
     */
    public void setConditionEl(String conditionEl) {
        this.conditionEl = conditionEl;
    }

    /**
     * @return Expression to determine the quantity
     */
    public String getQuantityEl() {
        return quantityEl;
    }

    /**
     * @param quantityEl Expression to determine the quantity
     */
    public void setQuantityEl(String quantityEl) {
        this.quantityEl = quantityEl;
    }

    /**
     * @return Expression to determine parameter 1 value
     */
    public String getParam1El() {
        return param1El;
    }

    /**
     * @param param1El Expression to determine parameter 1 value
     */
    public void setParam1El(String param1El) {
        this.param1El = param1El;
    }

    /**
     * @return Expression to determine parameter 2 value
     */
    public String getParam2El() {
        return param2El;
    }

    /**
     * @param param2El Expression to determine parameter 2 value
     */
    public void setParam2El(String param2El) {
        this.param2El = param2El;
    }

    /**
     * @return Expression to determine parameter 3 value
     */
    public String getParam3El() {
        return param3El;
    }

    /**
     * @param param3El Expression to determine parameter 3 value
     */
    public void setParam3El(String param3El) {
        this.param3El = param3El;
    }

    /**
     * @return Expression to determine parameter 4 value
     */
    public String getParam4El() {
        return param4El;
    }

    /**
     * @param param4El Expression to determine parameter 4 value
     */
    public void setParam4El(String param4El) {
        this.param4El = param4El;
    }

    /**
     * Get an EL expression.
     * @return Expression to evaluate the Opencell instance.
     */
    public String getOpencellInstanceEL() {
        return opencellInstanceEL;
    }

    /**
     * Set the EL expression
     * @param opencellInstanceEL Expression to evaluate the Opencell instance.
     */
    public void setOpencellInstanceEL(String opencellInstanceEL) {
        this.opencellInstanceEL = opencellInstanceEL;
    }

    /**
     * Get the script executed after TriggeredEdr construction.
     * @return {@link ScriptInstance}
     */
    public ScriptInstance getTriggeredEdrScript() {
        return triggeredEdrScript;
    }

    /**
     * Set the script executed after TriggeredEdr construction.
     * @param triggeredEdrScript {@link ScriptInstance}
     */
    public void setTriggeredEdrScript(ScriptInstance triggeredEdrScript) {
        this.triggeredEdrScript = triggeredEdrScript;
    }
}