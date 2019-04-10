/**
 * 
 */
package org.meveo.api.dto.payment;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlRootElement;

import org.meveo.api.dto.response.BaseResponse;


/**
 * The Class PaymentScheduleInstanceResponseDto.
 *
 * @author anasseh
 */

@XmlRootElement(name = "PaymentScheduleInstanceResponseDto")
@XmlAccessorType(XmlAccessType.FIELD)
public class PaymentScheduleInstanceResponseDto extends BaseResponse {
    
    /** The Constant serialVersionUID. */
    private static final long serialVersionUID = 1L;
    
    /** The payment schedule instance dto. */
    private PaymentScheduleInstanceDto paymentScheduleInstanceDto;
    
    
    
    /**
     * Instantiates a new payment schedule instance response dto.
     */
    public PaymentScheduleInstanceResponseDto(){
        
    }



	/**
	 * Gets the payment schedule instance dto.
	 *
	 * @return the payment schedule instance dto
	 */
	public PaymentScheduleInstanceDto getPaymentScheduleInstanceDto() {
		return paymentScheduleInstanceDto;
	}



	/**
	 * Sets the payment schedule instance dto.
	 *
	 * @param paymentScheduleInstanceDto the new payment schedule instance dto
	 */
	public void setPaymentScheduleInstanceDto(PaymentScheduleInstanceDto paymentScheduleInstanceDto) {
		this.paymentScheduleInstanceDto = paymentScheduleInstanceDto;
	}

   
}
