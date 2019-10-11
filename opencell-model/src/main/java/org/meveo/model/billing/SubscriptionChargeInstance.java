package org.meveo.model.billing;

import java.math.BigDecimal;

import javax.persistence.DiscriminatorValue;
import javax.persistence.Entity;

import org.meveo.model.catalog.OneShotChargeTemplate;

@Entity
@DiscriminatorValue("S")
public class SubscriptionChargeInstance extends OneShotChargeInstance {

    private static final long serialVersionUID = 9173788298851287042L;

    /**
     * Constructor
     */
    public SubscriptionChargeInstance() {
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
    public SubscriptionChargeInstance(BigDecimal amountWithoutTax, BigDecimal amountWithTax, OneShotChargeTemplate chargeTemplate, ServiceInstance serviceInstance,
            InstanceStatusEnum status) {
        super(amountWithoutTax, amountWithTax, chargeTemplate, serviceInstance, status);
    }
}