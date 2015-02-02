package org.meveo.api.dto.billing;

import java.util.Date;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlRootElement;

import org.meveo.api.dto.BaseDto;

/**
 * @author Edward P. Legaspi
 **/
@XmlRootElement(name = "TerminateSubscription")
@XmlAccessorType(XmlAccessType.FIELD)
public class TerminateSubscriptionDto extends BaseDto {

	private static final long serialVersionUID = -4477259461644796968L;

	@XmlAttribute(required = true)
	private String subscriptionCode;

	@XmlAttribute(required = true)
	private String terminationReason;

	@XmlAttribute(required = true)
	private Date terminationDate;

	public String getSubscriptionCode() {
		return subscriptionCode;
	}

	public void setSubscriptionCode(String subscriptionCode) {
		this.subscriptionCode = subscriptionCode;
	}

	public String getTerminationReason() {
		return terminationReason;
	}

	public void setTerminationReason(String terminationReason) {
		this.terminationReason = terminationReason;
	}

	public Date getTerminationDate() {
		return terminationDate;
	}

	public void setTerminationDate(Date terminationDate) {
		this.terminationDate = terminationDate;
	}

	@Override
	public String toString() {
		return "TerminateSubscriptionDto [subscriptionCode=" + subscriptionCode + ", terminationReason="
				+ terminationReason + ", terminationDate=" + terminationDate + "]";
	}

}
