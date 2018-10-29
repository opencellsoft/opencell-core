package org.meveo.api.dto.billing;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlRootElement;

import org.meveo.api.dto.BusinessEntityDto;

@XmlRootElement()
@XmlAccessorType(XmlAccessType.FIELD)
public class SubscriptionForCustomerRequestDto extends BusinessEntityDto {
    
    private static final long serialVersionUID = -6021918810749866648L;
    
    /** The subscription code. */
    private String subscriptionCode;
    
    /** The subscription client id : corresponding to Customer code */
    private String subscriptionClientId;
    
    /**
     * @return the subscriptionCode
     */
    public String getSubscriptionCode() {
        return subscriptionCode;
    }

    public void setSubscriptionCode(String subscriptionCode) {
        this.subscriptionCode = subscriptionCode;
    }

    public String getSubscriptionClientId() {
        return subscriptionClientId;
    }

    public void setSubscriptionClientId(String subscriptionClientId) {
        this.subscriptionClientId = subscriptionClientId;
    }

}
