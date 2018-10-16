/**
 * 
 */
package org.meveo.api.dto.payment;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlRootElement;

import org.meveo.api.dto.response.BaseResponse;

/**
 * The Class PaymentScheduleTemplateResponseDto.
 *
 * @author anasseh
 */

@XmlRootElement(name = "PaymentScheduleTemplateResponseDto")
@XmlAccessorType(XmlAccessType.FIELD)
public class PaymentScheduleTemplateResponseDto extends BaseResponse {
    
    /** The Constant serialVersionUID. */
    private static final long serialVersionUID = 1L;
    
    /** The payment schedule template dto. */
    private PaymentScheduleTemplateDto paymentScheduleTemplateDto;
    
    
    
    /**
     * Instantiates a new payment schedule template response dto.
     */
    public PaymentScheduleTemplateResponseDto(){
        
    }

    /**
     * Gets the payment schedule template dto.
     *
     * @return the paymentScheduleTemplateDto
     */
    public PaymentScheduleTemplateDto getPaymentScheduleTemplateDto() {
        return paymentScheduleTemplateDto;
    }

    /**
     * Sets the payment schedule template dto.
     *
     * @param paymentScheduleTemplateDto the paymentScheduleTemplateDto to set
     */
    public void setPaymentScheduleTemplateDto(PaymentScheduleTemplateDto paymentScheduleTemplateDto) {
        this.paymentScheduleTemplateDto = paymentScheduleTemplateDto;
    }
    
    

}
