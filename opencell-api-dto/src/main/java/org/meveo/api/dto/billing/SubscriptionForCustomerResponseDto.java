package org.meveo.api.dto.billing;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlRootElement;

import org.meveo.api.dto.response.BaseResponse;

@XmlRootElement()
@XmlAccessorType(XmlAccessType.FIELD)
public class SubscriptionForCustomerResponseDto extends BaseResponse {

    private static final long serialVersionUID = 1L;

    private String subscriptionEndDate;

    /**
     * @return the subscriptionEndDate
     */
    public String getSubscriptionEndDate() {
        return subscriptionEndDate;
    }

    /**
     * @param subscriptionEndDate the subscriptionEndDate to set
     */
    public void setSubscriptionEndDate(String subscriptionEndDate) {
        this.subscriptionEndDate = subscriptionEndDate;
    }

}
