package org.meveo.api.dto.response;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlRootElement;

/**
 * @author Edward P. Legaspi
 * @since Nov 13, 2013
 **/
@XmlRootElement(name = "TerminateCustomerSubscriptionResponse")
@XmlAccessorType(XmlAccessType.FIELD)
public class TerminateCustomerSubscriptionResponse extends BaseResponse {

	private static final long serialVersionUID = 2890315995921193030L;

	private String requestId;
	private Boolean accepted;
	private String subscriptionId;
	private String status;

	public String getRequestId() {
		return requestId;
	}

	public void setRequestId(String requestId) {
		this.requestId = requestId;
	}

	public Boolean getAccepted() {
		return accepted;
	}

	public void setAccepted(Boolean accepted) {
		this.accepted = accepted;
	}

	public String getSubscriptionId() {
		return subscriptionId;
	}

	public void setSubscriptionId(String subscriptionId) {
		this.subscriptionId = subscriptionId;
	}

	public String getStatus() {
		return status;
	}

	public void setStatus(String status) {
		this.status = status;
	}

	@Override
	public String toString() {
		return "TerminateCustomerSubscriptionResponse [requestId=" + requestId + ", accepted=" + accepted
				+ ", subscriptionId=" + subscriptionId + ", status=" + status + ", toString()=" + super.toString()
				+ "]";
	}

}
