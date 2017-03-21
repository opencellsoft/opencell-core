package org.meveo.api.dto.billing;

import java.util.Date;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

import org.meveo.api.dto.BaseDto;

/**
 * @author Edward P. Legaspi
 **/
@XmlRootElement(name = "OperationSubscriptionRequest")
@XmlAccessorType(XmlAccessType.FIELD)
public class OperationSubscriptionRequestDto extends BaseDto {

	private static final long serialVersionUID = -4477259461644796968L;

	@XmlElement(required = true)
	private String subscriptionCode;	
	
	private Date actionDate;

	public String getSubscriptionCode() {
		return subscriptionCode;
	}

	public void setSubscriptionCode(String subscriptionCode) {
		this.subscriptionCode = subscriptionCode;
	}

	public Date getActionDate() {
		return actionDate;
	}

	public void setActionDate(Date suspensionDate) {
		this.actionDate = suspensionDate;
	}

	@Override
	public String toString() {
		return "OperationSubscriptionRequestDto  [subscriptionCode=" + subscriptionCode + ", actionDate=" + actionDate + "]";
	}
	

}
