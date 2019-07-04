package org.meveo.model;

import org.meveo.model.billing.DiscountPlanInstance;

import java.util.List;

public interface IDiscountable {
    List<DiscountPlanInstance> getAllDiscountPlanInstances();
    void addDiscountPlanInstances(DiscountPlanInstance discountPlanInstance);
}
