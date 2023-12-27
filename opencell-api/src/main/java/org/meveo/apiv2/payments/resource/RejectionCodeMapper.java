package org.meveo.apiv2.payments.resource;

import org.meveo.apiv2.generic.ResourceMapper;
import org.meveo.apiv2.models.ImmutableResource;
import org.meveo.apiv2.payments.ImmutableRejectionCode;
import org.meveo.apiv2.payments.ImmutableRejectionCode.Builder;
import org.meveo.apiv2.payments.RejectionCode;
import org.meveo.model.payments.PaymentGateway;
import org.meveo.model.payments.PaymentRejectionCode;

public class RejectionCodeMapper extends ResourceMapper<RejectionCode, PaymentRejectionCode> {

    @Override
    public RejectionCode toResource(PaymentRejectionCode entity) {
        Builder rejectionCodeBuilder = ImmutableRejectionCode.builder()
                .id(entity.getId())
                .code(entity.getCode())
                .description(entity.getDescription())
                .descriptionI18n(entity.getDescriptionI18n());
        if (entity.getPaymentGateway() != null) {
            ImmutableResource paymentGateway = ImmutableResource.builder()
                    .id(entity.getPaymentGateway().getId())
                    .code(entity.getPaymentGateway().getCode()).build();
            rejectionCodeBuilder.paymentGateway(paymentGateway);
        }
        return rejectionCodeBuilder.build();
    }

    @Override
    public PaymentRejectionCode toEntity(RejectionCode resource) {
        PaymentRejectionCode paymentRejectionCode = new PaymentRejectionCode();
        paymentRejectionCode.setId(resource.getId());
        paymentRejectionCode.setCode(resource.getCode());
        paymentRejectionCode.setDescription(resource.getDescription());
        paymentRejectionCode.setDescriptionI18n(resource.getDescriptionI18n());
        return paymentRejectionCode;
    }

    public PaymentRejectionCode toEntity(RejectionCode resource, PaymentGateway paymentGateway) {
        PaymentRejectionCode paymentRejectionCode =toEntity(resource);
        if (paymentGateway != null) {
            paymentRejectionCode.setPaymentGateway(paymentGateway);
        }
        return paymentRejectionCode;
    }
}
