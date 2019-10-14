package org.meveo.model.billing;

import java.math.BigDecimal;

import javax.persistence.DiscriminatorValue;
import javax.persistence.Entity;

import org.meveo.model.catalog.OneShotChargeTemplate;

@Entity
@DiscriminatorValue("T")
public class TerminationChargeInstance extends OneShotChargeInstance {

    private static final long serialVersionUID = 8396486496100011318L;

    /**
     * Constructor
     */
    public TerminationChargeInstance() {
        super();
    }

    /**
     * Constructor
     * 
     * @param amountWithoutTax Amount without tax
     * @param amountWithTax Amount with tax
     * @param chargeTemplate Charge template
     * @param serviceInstance Service instance to associate with
     * @param status Status
     */
    public TerminationChargeInstance(BigDecimal amountWithoutTax, BigDecimal amountWithTax, OneShotChargeTemplate chargeTemplate, ServiceInstance serviceInstance,
            InstanceStatusEnum status) {
        super(amountWithoutTax, amountWithTax, chargeTemplate, serviceInstance, status);
    }
}