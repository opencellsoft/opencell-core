package org.meveo.api.dto.response;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlRootElement;

import org.meveo.api.dto.BillingCycleDto;

/**
 * The Class GetBillingCycleResponse.
 *
 * @author Edward P. Legaspi
 */
@XmlRootElement(name = "GetBillingCycleResponse")
@XmlAccessorType(XmlAccessType.FIELD)
public class GetBillingCycleResponse extends BaseResponse {

    /** The Constant serialVersionUID. */
    private static final long serialVersionUID = -1125948385137327401L;

    /** The billing cycle. */
    private BillingCycleDto billingCycle;

    /**
     * Gets the billing cycle.
     *
     * @return the billing cycle
     */
    public BillingCycleDto getBillingCycle() {
        return billingCycle;
    }

    /**
     * Sets the billing cycle.
     *
     * @param billingCycle the new billing cycle
     */
    public void setBillingCycle(BillingCycleDto billingCycle) {
        this.billingCycle = billingCycle;
    }

    @Override
    public String toString() {
        return "GetBillingCycleResponse [billingCycle=" + billingCycle + ", toString()=" + super.toString() + "]";
    }

}
