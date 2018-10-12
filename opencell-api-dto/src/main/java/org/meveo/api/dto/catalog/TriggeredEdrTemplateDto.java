package org.meveo.api.dto.catalog;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

import org.meveo.api.dto.BusinessEntityDto;
import org.meveo.model.catalog.TriggeredEDRTemplate;

/**
 * The Class TriggeredEdrTemplateDto.
 *
 * @author Edward P. Legaspi
 */
@XmlRootElement(name = "TriggeredEdrTemplate")
@XmlAccessorType(XmlAccessType.FIELD)
public class TriggeredEdrTemplateDto extends BusinessEntityDto {

    /** The Constant serialVersionUID. */
    private static final long serialVersionUID = 5790679004639676207L;

    /** The subscription el. */
    private String subscriptionEl;

    /** The subscription el. */
    private String subscriptionElSpark;

    /** The meveo instance code. */
    private String meveoInstanceCode;

    /** The condition el. */
    private String conditionEl;

    /** The condition el. */
    private String conditionElSpark;

    /** The quantity el. */
    @XmlElement(required = true)
    private String quantityEl;

    /** The quantity el. */
    @XmlElement()
    private String quantityElSpark;

    /** The param 1 el. */
    private String param1El;

    /** The param 1 el. */
    private String param1ElSpark;

    /** The param 2 el. */
    private String param2El;

    /** The param 2 el. */
    private String param2ElSpark;

    /** The param 3 el. */
    private String param3El;

    /** The param 3 el. */
    private String param3ElSpark;

    /** The param 4 el. */
    private String param4El;

    /** The param 4 el. */
    private String param4ElSpark;

    /**
     * Instantiates a new triggered edr template dto.
     */
    public TriggeredEdrTemplateDto() {

    }

    /**
     * Instantiates a new triggered edr template dto.
     *
     * @param triggeredEDRTemplate the TriggeredEDRTemplate entity
     */
    public TriggeredEdrTemplateDto(TriggeredEDRTemplate triggeredEDRTemplate) {
        super(triggeredEDRTemplate);

        subscriptionEl = triggeredEDRTemplate.getSubscriptionEl();
        meveoInstanceCode = triggeredEDRTemplate.getMeveoInstance() == null ? null : triggeredEDRTemplate.getMeveoInstance().getCode();
        conditionEl = triggeredEDRTemplate.getConditionEl();
        conditionElSpark = triggeredEDRTemplate.getConditionElSpark();
        quantityEl = triggeredEDRTemplate.getQuantityEl();
        quantityElSpark = triggeredEDRTemplate.getQuantityElSpark();
        param1El = triggeredEDRTemplate.getParam1El();
        param1ElSpark = triggeredEDRTemplate.getParam1ElSpark();
        param2El = triggeredEDRTemplate.getParam2El();
        param2ElSpark = triggeredEDRTemplate.getParam2ElSpark();
        param3El = triggeredEDRTemplate.getParam3El();
        param3ElSpark = triggeredEDRTemplate.getParam3ElSpark();
        param4El = triggeredEDRTemplate.getParam4El();
        param4ElSpark = triggeredEDRTemplate.getParam4ElSpark();
    }

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
     * @return Meveo instance code to register a new EDR on. If not empty, EDR will be send via API
     */
    public String getMeveoInstanceCode() {
        return meveoInstanceCode;
    }

    /**
     * @param meveoInstanceCode Meveo instance to register a new EDR on. If not empty, EDR will be send via API
     */
    public void setMeveoInstanceCode(String meveoInstanceCode) {
        this.meveoInstanceCode = meveoInstanceCode;
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

    @Override
    public String toString() {
        return "TriggeredEdrTemplateDto [subscriptionEl=" + subscriptionEl + ", subscriptionElSpark=" + subscriptionElSpark + ", meveoInstanceCode=" + meveoInstanceCode
                + ", conditionEl=" + conditionEl + ", conditionElSpark=" + conditionElSpark + ", quantityEl=" + quantityEl + ", quantityElSpark=" + quantityElSpark + ", param1El="
                + param1El + ", param1ElSpark=" + param1ElSpark + ", param2El=" + param2El + ", param2ElSpark=" + param2ElSpark + ", param3El=" + param3El + ", param3ElSpark="
                + param3ElSpark + ", param4El=" + param4El + ", param4ElSpark=" + param4ElSpark + ", id=" + id + ", code=" + code + ", description=" + description
                + ", updatedCode=" + updatedCode + "]";
    }
}