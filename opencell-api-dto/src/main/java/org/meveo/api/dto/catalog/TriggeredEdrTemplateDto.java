package org.meveo.api.dto.catalog;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

import org.meveo.api.dto.BusinessDto;
import org.meveo.model.catalog.TriggeredEDRTemplate;

/**
 * The Class TriggeredEdrTemplateDto.
 *
 * @author Edward P. Legaspi
 */
@XmlRootElement(name = "TriggeredEdrTemplate")
@XmlAccessorType(XmlAccessType.FIELD)
public class TriggeredEdrTemplateDto extends BusinessDto {

    /** The Constant serialVersionUID. */
    private static final long serialVersionUID = 5790679004639676207L;

    /** The subscription el. */
    private String subscriptionEl;

    /** The meveo instance code. */
    private String meveoInstanceCode;

    /** The condition el. */
    private String conditionEl;

    /** The quantity el. */
    @XmlElement(required = true)
    private String quantityEl;

    /** The param 1 el. */
    private String param1El;

    /** The param 2 el. */
    private String param2El;

    /** The param 3 el. */
    private String param3El;

    /** The param 4 el. */
    private String param4El;

    /**
     * Instantiates a new triggered edr template dto.
     */
    public TriggeredEdrTemplateDto() {

    }

    /**
     * Instantiates a new triggered edr template dto.
     *
     * @param e the e
     */
    public TriggeredEdrTemplateDto(TriggeredEDRTemplate e) {
        super(e);

        subscriptionEl = e.getSubscriptionEl();
        meveoInstanceCode = e.getMeveoInstance() == null ? null : e.getMeveoInstance().getCode();
        conditionEl = e.getConditionEl();
        quantityEl = e.getQuantityEl();
        param1El = e.getParam1El();
        param2El = e.getParam2El();
        param3El = e.getParam3El();
        param4El = e.getParam4El();
    }

    /**
     * Gets the subscription el.
     *
     * @return the subscription el
     */
    public String getSubscriptionEl() {
        return subscriptionEl;
    }

    /**
     * Sets the subscription el.
     *
     * @param subscriptionEl the new subscription el
     */
    public void setSubscriptionEl(String subscriptionEl) {
        this.subscriptionEl = subscriptionEl;
    }

    /**
     * Gets the meveo instance code.
     *
     * @return the meveo instance code
     */
    public String getMeveoInstanceCode() {
        return meveoInstanceCode;
    }

    /**
     * Sets the meveo instance code.
     *
     * @param meveoInstanceCode the new meveo instance code
     */
    public void setMeveoInstanceCode(String meveoInstanceCode) {
        this.meveoInstanceCode = meveoInstanceCode;
    }

    /**
     * Gets the condition el.
     *
     * @return the condition el
     */
    public String getConditionEl() {
        return conditionEl;
    }

    /**
     * Sets the condition el.
     *
     * @param conditionEl the new condition el
     */
    public void setConditionEl(String conditionEl) {
        this.conditionEl = conditionEl;
    }

    /**
     * Gets the quantity el.
     *
     * @return the quantity el
     */
    public String getQuantityEl() {
        return quantityEl;
    }

    /**
     * Sets the quantity el.
     *
     * @param quantityEl the new quantity el
     */
    public void setQuantityEl(String quantityEl) {
        this.quantityEl = quantityEl;
    }

    /**
     * Gets the param 1 el.
     *
     * @return the param 1 el
     */
    public String getParam1El() {
        return param1El;
    }

    /**
     * Sets the param 1 el.
     *
     * @param param1El the new param 1 el
     */
    public void setParam1El(String param1El) {
        this.param1El = param1El;
    }

    /**
     * Gets the param 2 el.
     *
     * @return the param 2 el
     */
    public String getParam2El() {
        return param2El;
    }

    /**
     * Sets the param 2 el.
     *
     * @param param2El the new param 2 el
     */
    public void setParam2El(String param2El) {
        this.param2El = param2El;
    }

    /**
     * Gets the param 3 el.
     *
     * @return the param 3 el
     */
    public String getParam3El() {
        return param3El;
    }

    /**
     * Sets the param 3 el.
     *
     * @param param3El the new param 3 el
     */
    public void setParam3El(String param3El) {
        this.param3El = param3El;
    }

    /**
     * Gets the param 4 el.
     *
     * @return the param 4 el
     */
    public String getParam4El() {
        return param4El;
    }

    /**
     * Sets the param 4 el.
     *
     * @param param4El the new param 4 el
     */
    public void setParam4El(String param4El) {
        this.param4El = param4El;
    }

    /*
     * (non-Javadoc)
     * 
     * @see java.lang.Object#toString()
     */
    @Override
    public String toString() {
        return "TriggeredEdrTemplateDto [code=" + getCode() + ", description=" + getDescription() + ", subscriptionEl=" + subscriptionEl + ", conditionEl=" + conditionEl
                + ", quantityEl=" + quantityEl + ", param1El=" + param1El + ", param2El=" + param2El + ", param3El=" + param3El + ", param4El=" + param4El + "]";
    }

}
