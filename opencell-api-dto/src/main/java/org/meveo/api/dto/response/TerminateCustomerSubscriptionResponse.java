package org.meveo.api.dto.response;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlRootElement;

/**
 * The Class TerminateCustomerSubscriptionResponse.
 *
 * @author Edward P. Legaspi
 * @since Nov 13, 2013
 */
@XmlRootElement(name = "TerminateCustomerSubscriptionResponse")
@XmlAccessorType(XmlAccessType.FIELD)
public class TerminateCustomerSubscriptionResponse extends BaseResponse {

    /** The Constant serialVersionUID. */
    private static final long serialVersionUID = 2890315995921193030L;

    /** The request id. */
    private String requestId;
    
    /** The accepted. */
    private Boolean accepted;
    
    /** The subscription id. */
    private String subscriptionId;
    
    /** The status. */
    private String status;

    /**
     * Gets the request id.
     *
     * @return the request id
     */
    public String getRequestId() {
        return requestId;
    }

    /**
     * Sets the request id.
     *
     * @param requestId the new request id
     */
    public void setRequestId(String requestId) {
        this.requestId = requestId;
    }

    /**
     * Gets the accepted.
     *
     * @return the accepted
     */
    public Boolean getAccepted() {
        return accepted;
    }

    /**
     * Sets the accepted.
     *
     * @param accepted the new accepted
     */
    public void setAccepted(Boolean accepted) {
        this.accepted = accepted;
    }

    /**
     * Gets the subscription id.
     *
     * @return the subscription id
     */
    public String getSubscriptionId() {
        return subscriptionId;
    }

    /**
     * Sets the subscription id.
     *
     * @param subscriptionId the new subscription id
     */
    public void setSubscriptionId(String subscriptionId) {
        this.subscriptionId = subscriptionId;
    }

    /**
     * Gets the status.
     *
     * @return the status
     */
    public String getStatus() {
        return status;
    }

    /**
     * Sets the status.
     *
     * @param status the new status
     */
    public void setStatus(String status) {
        this.status = status;
    }

    @Override
    public String toString() {
        return "TerminateCustomerSubscriptionResponse [requestId=" + requestId + ", accepted=" + accepted + ", subscriptionId=" + subscriptionId + ", status=" + status
                + ", toString()=" + super.toString() + "]";
    }
}