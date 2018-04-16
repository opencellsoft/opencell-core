package org.meveo.api.dto;

import java.util.Date;
import java.util.List;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlRootElement;

/**
 * The Class SubscriptionWithCreditLimitDto.
 *
 * @author Edward P. Legaspi
 * @since Nov 13, 2013
 */
@XmlRootElement(name = "SubscriptionWithCreditLimit")
@XmlAccessorType(XmlAccessType.FIELD)
public class SubscriptionWithCreditLimitDto extends BaseDto {

    /** The Constant serialVersionUID. */
    private static final long serialVersionUID = -6700315102709912658L;

    /** The user id. */
    private String userId; // unused
    
    /** The organization id. */
    private String organizationId;
    
    /** The offer id. */
    private String offerId;
    
    /** The services to add. */
    private List<ServiceToAddDto> servicesToAdd;
    
    /** The credit limits. */
    private List<CreditLimitDto> creditLimits;
    
    /** The subscription date. */
    private Date subscriptionDate;

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
     * Gets the offer id.
     *
     * @return the offer id
     */
    public String getOfferId() {
        return offerId;
    }

    /**
     * Sets the offer id.
     *
     * @param offerId the new offer id
     */
    public void setOfferId(String offerId) {
        this.offerId = offerId;
    }

    /**
     * Gets the services to add.
     *
     * @return the services to add
     */
    public List<ServiceToAddDto> getServicesToAdd() {
        return servicesToAdd;
    }

    /**
     * Sets the services to add.
     *
     * @param servicesToAdd the new services to add
     */
    public void setServicesToAdd(List<ServiceToAddDto> servicesToAdd) {
        this.servicesToAdd = servicesToAdd;
    }

    /**
     * Gets the credit limits.
     *
     * @return the credit limits
     */
    public List<CreditLimitDto> getCreditLimits() {
        return creditLimits;
    }

    /**
     * Sets the credit limits.
     *
     * @param creditLimits the new credit limits
     */
    public void setCreditLimits(List<CreditLimitDto> creditLimits) {
        this.creditLimits = creditLimits;
    }

    /**
     * Gets the subscription date.
     *
     * @return the subscription date
     */
    public Date getSubscriptionDate() {
        return subscriptionDate;
    }

    /**
     * Sets the subscription date.
     *
     * @param subscriptionDate the new subscription date
     */
    public void setSubscriptionDate(Date subscriptionDate) {
        this.subscriptionDate = subscriptionDate;
    }
}