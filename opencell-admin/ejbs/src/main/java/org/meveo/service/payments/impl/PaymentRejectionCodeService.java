package org.meveo.service.payments.impl;

import static java.lang.String.format;
import static java.util.Optional.empty;
import static java.util.Optional.of;

import org.meveo.admin.exception.BusinessException;
import org.meveo.model.payments.PaymentGateway;
import org.meveo.model.payments.PaymentRejectionCode;
import org.meveo.service.base.BusinessService;

import javax.ejb.Stateless;
import javax.persistence.NoResultException;
import javax.persistence.Query;
import java.util.Optional;

@Stateless
public class PaymentRejectionCodeService extends BusinessService<PaymentRejectionCode>  {

    /**
     * Create payment rejection code
     *
     * @param rejectionCode payment rejection code
     */
    @Override
    public void create(PaymentRejectionCode rejectionCode) {
        if(findByCodeAndPaymentGateway(rejectionCode.getCode(),
                rejectionCode.getPaymentGateway().getId()).isPresent()) {
            throw new BusinessException(format("Rejection code with code %s already exists in gateway %s",
                    rejectionCode.getCode(), rejectionCode.getPaymentGateway().getCode()));
        }
        super.create(rejectionCode);

    }

    /**
     * Find a payment rejection code using code and payment gateway id
     *
     * @param code payment rejection code
     * @param paymentGatewayId payment gateway id
     * @return Optional of PaymentRejectionCode
     */
    public Optional<PaymentRejectionCode> findByCodeAndPaymentGateway(String code, Long paymentGatewayId) {
        try {
            return of((PaymentRejectionCode) getEntityManager()
                    .createNamedQuery("PaymentRejectionCode.findByCodeAndPaymentGateway")
                    .setParameter("code", code)
                    .setParameter("paymentGatewayId", paymentGatewayId)
                    .getSingleResult());
        } catch (NoResultException exception) {
            return empty();
        }
    }

    /**
     * Update payment rejection code
     *
     * @param rejectionCode payment rejection code
     * @return RejectionCode updated entity
     */
    @Override
    public PaymentRejectionCode update(PaymentRejectionCode rejectionCode) {
        if(findByCodeAndPaymentGateway(rejectionCode.getCode(),
                rejectionCode.getPaymentGateway().getId()).isPresent()) {
            throw new BusinessException(format("Rejection code with code %s already exists in gateway %s",
                    rejectionCode.getCode(), rejectionCode.getPaymentGateway().getCode()));
        }
        return super.update(rejectionCode);
    }

    /**
     * Clear rejectionsCodes by gateway
     *
     * @param paymentGateway payment gateway
     */
    public int  clearAll(PaymentGateway paymentGateway) {
        String namedQuery = paymentGateway != null
                ? "PaymentRejectionCode.clearAllByPaymentGateway" : "PaymentRejectionCode.clearAll";
        Query clearQuery = getEntityManager().createNamedQuery(namedQuery);
        if(paymentGateway != null) {
            clearQuery.setParameter("paymentGatewayId", paymentGateway.getId());
        }
        try {
            return clearQuery.executeUpdate();
        } catch (Exception exception) {
            throw new BusinessException("Error occurred during rejection codes clearing " + exception.getMessage());
        }
    }
}
