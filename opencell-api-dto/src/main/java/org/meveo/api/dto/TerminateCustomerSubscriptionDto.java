package org.meveo.api.dto;

import java.util.Date;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlRootElement;

/**
 * The Class TerminateCustomerSubscriptionDto.
 *
 * @author Edward P. Legaspi
 */
@XmlRootElement(name = "TerminateSubscription")
@XmlAccessorType(XmlAccessType.FIELD)
public class TerminateCustomerSubscriptionDto extends BaseDto {

    /** The Constant serialVersionUID. */
    private static final long serialVersionUID = -6700315102709912658L;

    /** The user id. */
    private String userId; // unused
    
    /** The organization id. */
    private String organizationId;
    
    /** The subscription id. */
    private String subscriptionId;
    
    /** The termination date. */
    private Date terminationDate;

    /**
     * Gets the user id.
     *
     * @return the user id
     */
    public String getUserId() {
        return userId;
    }

    /**
     * Sets the user id.
     *
     * @param userId the new user id
     */
    public void setUserId(String userId) {
        this.userId = userId;
    }

    /**
     * Gets the organization id.
     *
     * @return the organization id
     */
    public String getOrganizationId() {
        return organizationId;
    }

    /**
     * Sets the organization id.
     *
     * @param organizationId the new organization id
     */
    public void setOrganizationId(String organizationId) {
        this.organizationId = organizationId;
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
     * Gets the termination date.
     *
     * @return the termination date
     */
    public Date getTerminationDate() {
        return terminationDate;
    }

    /**
     * Sets the termination date.
     *
     * @param terminationDate the new termination date
     */
    public void setTerminationDate(Date terminationDate) {
        this.terminationDate = terminationDate;
    }
}