package org.meveo.model.catalog;

import javax.persistence.Cacheable;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.persistence.UniqueConstraint;
import javax.validation.constraints.Size;

import org.hibernate.annotations.GenericGenerator;
import org.hibernate.annotations.Parameter;
import org.meveo.model.BusinessEntity;
import org.meveo.model.ExportIdentifier;
import org.meveo.model.ObservableEntity;
import org.meveo.model.communication.MeveoInstance;
import org.meveo.model.scripts.ScriptInstance;

/**
 * A rule for new EDR creation for a processed EDR
 * 
 * @author Andrius Karpavicius
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
    @Column(name = "subscription_el", columnDefinition = "TEXT")
    @Size(max = 2000)
    private String subscriptionEl;

    /**
     * Expression to determine subscription code - for Spark
     */
    @Column(name = "subscription_el_sp", columnDefinition = "TEXT")
    @Size(max = 2000)
    private String subscriptionElSpark;

    /**
     * Opencell instance of EDR should be send to another instance. If not empty, EDR will be send via API.
     */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "meveo_instance_id")
    private MeveoInstance meveoInstance;

    /**
     * Expression to determine if EDR rule applies
     */
    @Column(name = "condition_el", columnDefinition = "TEXT")
    @Size(max = 2000)
    private String conditionEl;

    /**
     * Expression to determine if EDR rule applies - for Spark
     */
    @Column(name = "condition_el_sp", columnDefinition = "TEXT")
    @Size(max = 2000)
    private String conditionElSpark;

    /**
     * Expression to determine quantity of new EDR
     */
    @Column(name = "quantity_el", columnDefinition = "TEXT")
    @Size(max = 2000)
    private String quantityEl;

    /**
     * Expression to determine quantity of new EDR - for Spark
     */
    @Column(name = "quantity_el_sp", columnDefinition = "TEXT")
    @Size(max = 2000)
    private String quantityElSpark;

    /**
     * Expression to determine parameter 1 of new EDR
     */
    @Column(name = "param_1_el", columnDefinition = "TEXT")
    @Size(max = 2000)
    private String param1El;

    /**
     * Expression to determine parameter 1 of new EDR - for Spark
     */
    @Column(name = "param_1_el_sp", columnDefinition = "TEXT")
    @Size(max = 2000)
    private String param1ElSpark;

    /**
     * Expression to determine parameter 2 of new EDR
     */
    @Column(name = "param_2_el", columnDefinition = "TEXT")
    @Size(max = 2000)
    private String param2El;

    /**
     * Expression to determine parameter 2 of new EDR - for Spark
     */
    @Column(name = "param_2_el_sp", columnDefinition = "TEXT")
    @Size(max = 2000)
    private String param2ElSpark;

    /**
     * Expression to determine parameter 3 of new EDR
     */
    @Column(name = "param_3_el", columnDefinition = "TEXT")
    @Size(max = 2000)
    private String param3El;

    /**
     * Expression to determine parameter 3 of new EDR - for Spark
     */
    @Column(name = "param_3_el_sp", columnDefinition = "TEXT")
    @Size(max = 2000)
    private String param3ElSpark;

    /**
     * Expression to determine parameter 4 of new EDR
     */
    @Column(name = "param_4_el", columnDefinition = "TEXT")
    @Size(max = 2000)
    private String param4El;

    /**
     * Expression to determine parameter 4 of new EDR - for Spark
     */
    @Column(name = "param_4_el_sp", columnDefinition = "TEXT")
    @Size(max = 2000)
    private String param4ElSpark;
    
    /**
     * Expression to compute the OpencellInstance code so the instance on which the EDR is triggered can be inferred from the Offer or whatever.
     */
    @Column(name = "opencell_instance_el", columnDefinition = "TEXT")
    @Size(max = 2000)
    private String opencellInstanceEL;
    
    /**
     * Script to run
     */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "script_instance_id")
    private ScriptInstance scriptInstance;

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
     * @return Expression to evaluate subscription code - for Spark
     */
    public String getSubscriptionElSpark() {
        return subscriptionElSpark;
    }

    /**
     * @param subscriptionElSpark Expression to evaluate subscription code - for Spark
     */
    public void setSubscriptionElSpark(String subscriptionElSpark) {
        this.subscriptionElSpark = subscriptionElSpark;
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
     * @return Expression to determine if EDR applies - for Spark
     */
    public String getConditionElSpark() {
        return conditionElSpark;
    }

    /**
     * @param conditionElSpark Expression to determine if EDR applies - for Spark
     */
    public void setConditionElSpark(String conditionElSpark) {
        this.conditionElSpark = conditionElSpark;
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
     * @return Expression to determine the quantity - for Spark
     */
    public String getQuantityElSpark() {
        return quantityElSpark;
    }

    /**
     * @param quantityElSpark Expression to determine the quantity - for Spark
     */
    public void setQuantityElSpark(String quantityElSpark) {
        this.quantityElSpark = quantityElSpark;
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
     * @return Expression to determine parameter 1 value - for Spark
     */
    public String getParam1ElSpark() {
        return param1ElSpark;
    }

    /**
     * @param param1ElSpark Expression to determine parameter 1 value - for Sparl
     */
    public void setParam1ElSpark(String param1ElSpark) {
        this.param1ElSpark = param1ElSpark;
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
     * @return Expression to determine parameter 2 value - for Spark
     */
    public String getParam2ElSpark() {
        return param2ElSpark;
    }

    /**
     * @param param2ElSpark Expression to determine parameter 2 value - for Sparl
     */
    public void setParam2ElSpark(String param2ElSpark) {
        this.param2ElSpark = param2ElSpark;
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
     * @return Expression to determine parameter 3 value - for Spark
     */
    public String getParam3ElSpark() {
        return param3ElSpark;
    }

    /**
     * @param param3ElSpark Expression to determine parameter 3 value - for Sparl
     */
    public void setParam3ElSpark(String param3ElSpark) {
        this.param3ElSpark = param3ElSpark;
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
     * @return Expression to determine parameter 4 value - for Spark
     */
    public String getParam4ElSpark() {
        return param4ElSpark;
    }

    /**
     * @param param4ElSpark Expression to determine parameter 4 value - for Sparl
     */
    public void setParam4ElSpark(String param4ElSpark) {
        this.param4ElSpark = param4ElSpark;
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
    public ScriptInstance getScriptInstance() {
        return scriptInstance;
    }

    /**
     * Set the script executed after TriggeredEdr construction.
     * @param scriptInstance {@link ScriptInstance}
     */
    public void setScriptInstance(ScriptInstance scriptInstance) {
        this.scriptInstance = scriptInstance;
    }
}