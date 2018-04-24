package org.meveo.api.dto.response.payment;

import java.util.List;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlElementWrapper;
import javax.xml.bind.annotation.XmlRootElement;

import org.meveo.api.dto.finance.RevenueRecognitionRuleDto;
import org.meveo.api.dto.response.BaseResponse;

/**
 * The Class RevenueRecognitionRuleDtosResponse.
 * 
 * @author anasseh
 */
@XmlRootElement(name = "AccountOperationsResponse")
@XmlAccessorType(XmlAccessType.FIELD)
public class RevenueRecognitionRuleDtosResponse extends BaseResponse {

    /** The Constant serialVersionUID. */
    private static final long serialVersionUID = 4142972198689221570L;

    /** The revenue recognition rules. */
    @XmlElementWrapper(name = "revenueRecognitionRules")
    @XmlElement(name = "revenueRecognitionRule")
    private List<RevenueRecognitionRuleDto> revenueRecognitionRules;

    /**
     * Gets the revenue recognition rules.
     *
     * @return the revenue recognition rules
     */
    public List<RevenueRecognitionRuleDto> getRevenueRecognitionRules() {
        return revenueRecognitionRules;
    }

    /**
     * Sets the revenue recognition rules.
     *
     * @param revenueRecognitionRules the new revenue recognition rules
     */
    public void setRevenueRecognitionRules(List<RevenueRecognitionRuleDto> revenueRecognitionRules) {
        this.revenueRecognitionRules = revenueRecognitionRules;
    }

    @Override
    public String toString() {
        return "RevenueRecognitionRuleDtosResponse [revenueRecognitionRules=" + revenueRecognitionRules + "]";
    }
}