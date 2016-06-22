package org.meveo.api.dto.payment;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;

import org.meveo.api.dto.BaseDto;
import org.meveo.model.payments.DunningPlan;
import org.meveo.model.payments.DunningPlanStatusEnum;
import org.meveo.model.payments.PaymentMethodEnum;

/**
 * 
 * @author Tyshan　Shi(tyshan@manaty.net)
 * @date Jun 3, 2016 5:52:06 AM
 *
 */
@XmlType(name = "DunningPlan")
@XmlRootElement(name = "DunningPlan")
@XmlAccessorType(XmlAccessType.FIELD)
public class DunningPlanDto extends BaseDto {

	/**
	 * 
	 */
	private static final long serialVersionUID = 8309866046667741458L;
	@XmlElement(required=true)
	private String code;
	private String description;
	private PaymentMethodEnum paymentMethod;
	private DunningPlanStatusEnum status;
	public DunningPlanDto(){
		
	}
	public DunningPlanDto(DunningPlan dunningPlan) {
		this.code=dunningPlan.getCode();
		this.description=dunningPlan.getDescription();
		this.paymentMethod=dunningPlan.getPaymentMethod();
		this.status=dunningPlan.getStatus();
	}
	public String getCode() {
		return code;
	}
	public void setCode(String code) {
		this.code = code;
	}
	public String getDescription() {
		return description;
	}
	public void setDescription(String description) {
		this.description = description;
	}
	public PaymentMethodEnum getPaymentMethod() {
		return paymentMethod;
	}
	public void setPaymentMethod(PaymentMethodEnum paymentMethod) {
		this.paymentMethod = paymentMethod;
	}
	public DunningPlanStatusEnum getStatus() {
		return status;
	}
	public void setStatus(DunningPlanStatusEnum status) {
		this.status = status;
	}
	

}

