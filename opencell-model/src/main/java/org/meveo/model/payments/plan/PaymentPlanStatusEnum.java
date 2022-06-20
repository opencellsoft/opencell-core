package org.meveo.model.payments.plan;

public enum PaymentPlanStatusEnum {
    DRAFT, // when created
    ACTIVE, // when user activate it from GUI
    COMPLETED // when all installement AOs are matched
}
