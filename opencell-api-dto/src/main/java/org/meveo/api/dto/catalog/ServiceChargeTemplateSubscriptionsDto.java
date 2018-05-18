package org.meveo.api.dto.catalog;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;

/**
 * The Class ServiceChargeTemplateSubscriptionsDto.
 *
 * @author Edward P. Legaspi
 */
@XmlAccessorType(XmlAccessType.FIELD)
public class ServiceChargeTemplateSubscriptionsDto implements Serializable {

    /** The Constant serialVersionUID. */
    private static final long serialVersionUID = -8759911503295449204L;

    /** The service charge template subscription. */
    private List<ServiceChargeTemplateSubscriptionDto> serviceChargeTemplateSubscription;

    /**
     * Gets the service charge template subscription.
     *
     * @return the service charge template subscription
     */
    public List<ServiceChargeTemplateSubscriptionDto> getServiceChargeTemplateSubscription() {
        if (serviceChargeTemplateSubscription == null)
            serviceChargeTemplateSubscription = new ArrayList<ServiceChargeTemplateSubscriptionDto>();
        return serviceChargeTemplateSubscription;
    }

    /**
     * Sets the service charge template subscription.
     *
     * @param serviceChargeTemplateSubscription the new service charge template subscription
     */
    public void setServiceChargeTemplateSubscription(List<ServiceChargeTemplateSubscriptionDto> serviceChargeTemplateSubscription) {
        this.serviceChargeTemplateSubscription = serviceChargeTemplateSubscription;
    }

    @Override
    public String toString() {
        return "ServiceChargeTemplateSubscriptionsDto [serviceChargeTemplateSubscription=" + serviceChargeTemplateSubscription + "]";
    }

}