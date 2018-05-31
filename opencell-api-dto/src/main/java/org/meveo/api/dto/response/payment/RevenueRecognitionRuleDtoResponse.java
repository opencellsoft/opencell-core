package org.meveo.api.dto.response.payment;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

import org.meveo.api.dto.finance.RevenueRecognitionRuleDto;
import org.meveo.api.dto.response.BaseResponse;

/**
 * The Class RevenueRecognitionRuleDtoResponse.
 * 
 * @author anasseh
 */
@XmlRootElement(name = "RevenueRecognitionRuleResponse")
@XmlAccessorType(XmlAccessType.FIELD)
public class RevenueRecognitionRuleDtoResponse extends BaseResponse {

    /** The Constant serialVersionUID. */
    private static final long serialVersionUID = -2160634983966387377L;

    /** The revenue recognition rule dto. */
    @XmlElement(name = "revenueRecognitionRule")
    private RevenueRecognitionRuleDto revenueRecognitionRuleDto;

    /**
     * Gets the revenue recognition rule dto.
     *
     * @return the revenue recognition rule dto
     */
    public RevenueRecognitionRuleDto getRevenueRecognitionRuleDto() {
        return revenueRecognitionRuleDto;
    }

    /**
     * Sets the revenue recognition rule dto.
     *
     * @param revenueRecognitionRuleDto the new revenue recognition rule dto
     */
    public void setRevenueRecognitionRuleDto(RevenueRecognitionRuleDto revenueRecognitionRuleDto) {
        this.revenueRecognitionRuleDto = revenueRecognitionRuleDto;
    }

    @Override
    public String toString() {
        return "RevenueRecognitionRuleDtoResponse [revenueRecognitionRule=" + revenueRecognitionRuleDto + "]";
    }
}