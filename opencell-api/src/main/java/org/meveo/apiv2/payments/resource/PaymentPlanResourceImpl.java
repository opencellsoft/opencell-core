package org.meveo.apiv2.payments.resource;

import org.meveo.apiv2.payments.PaymentPlanDto;
import org.meveo.apiv2.payments.service.PaymentPlanApi;

import javax.inject.Inject;

public class PaymentPlanResourceImpl implements PaymentPlanResource {

    @Inject
    private PaymentPlanApi paymentPlanApi;

    @Override
    public Long create(PaymentPlanDto paymentPlanDto) {
        return paymentPlanApi.create(paymentPlanDto);
    }
}
