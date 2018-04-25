package org.meveo.api.dto;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlRootElement;


/**
 * The Class BillingCyclesDto.
 *
 * @author Edward P. Legaspi
 */
@XmlRootElement(name = "BillingCycles")
@XmlAccessorType(XmlAccessType.FIELD)
public class BillingCyclesDto implements Serializable {

    /** The Constant serialVersionUID. */
    private static final long serialVersionUID = -9161253039720538973L;

    /** The billing cycle. */
    private List<BillingCycleDto> billingCycle;

    /**
     * Gets the billing cycle.
     *
     * @return the billing cycle
     */
    public List<BillingCycleDto> getBillingCycle() {
        if (billingCycle == null)
            billingCycle = new ArrayList<BillingCycleDto>();
        return billingCycle;
    }

    /**
     * Sets the billing cycle.
     *
     * @param billingCycle the new billing cycle
     */
    public void setBillingCycle(List<BillingCycleDto> billingCycle) {
        this.billingCycle = billingCycle;
    }

    @Override
    public String toString() {
        return "BillingCyclesDto [billingCycle=" + billingCycle + "]";
    }

}
