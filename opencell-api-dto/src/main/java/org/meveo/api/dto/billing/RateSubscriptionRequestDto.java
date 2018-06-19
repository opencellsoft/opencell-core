package org.meveo.api.dto.billing;

import java.util.Date;

import org.meveo.api.dto.BaseEntityDto;

/**
 * A Dto class holding the request criterion for the Rate Subscription.
 * @author Said Ramli
 * @lastModifiedVersion 5.1
 */
public class RateSubscriptionRequestDto extends BaseEntityDto {


    /** The Constant serialVersionUID. */
    private static final long serialVersionUID = 9111851021979642538L;
    
    /** The subscription code. */
    private String subscriptionCode;
    
    /** The rate until date. */
    private Date rateUntilDate;

    /**
     * @return the subscriptionCode
     */
    public String getSubscriptionCode() {
        return subscriptionCode;
    }

    /**
     * @param subscriptionCode the subscriptionCode to set
     */
    public void setSubscriptionCode(String subscriptionCode) {
        this.subscriptionCode = subscriptionCode;
    }

    /**
     * @return the rateUntilDate
     */
    public Date getRateUntilDate() {
        return rateUntilDate;
    }

    /**
     * @param rateUntilDate the rateUntilDate to set
     */
    public void setRateUntilDate(Date rateUntilDate) {
        this.rateUntilDate = rateUntilDate;
    }

}
