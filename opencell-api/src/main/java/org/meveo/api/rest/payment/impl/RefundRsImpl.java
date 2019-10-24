package org.meveo.api.rest.payment.impl;

import javax.enterprise.context.RequestScoped;
import javax.inject.Inject;
import javax.interceptor.Interceptors;

import org.meveo.api.dto.ActionStatusEnum;
import org.meveo.api.dto.RefundActionStatus;
import org.meveo.api.dto.payment.RefundDto;
import org.meveo.api.logging.WsRestApiInterceptor;
import org.meveo.api.payment.RefundApi;
import org.meveo.api.rest.impl.BaseRs;
import org.meveo.api.rest.payment.RefundRs;

/**
 * The implementation for RefundRs.
 * 
 * @author abdelmounaim akadid
 */
@RequestScoped
@Interceptors({ WsRestApiInterceptor.class })
public class RefundRsImpl extends BaseRs implements RefundRs {

    @Inject
    private RefundApi refundApi;

    /**
     * @return payment action status which contains payment id.
     * @see org.meveo.api.rest.payment.PaymentRs#create(org.meveo.api.dto.payment.PaymentDto)
     */
    @Override
    public RefundActionStatus createRefund(RefundDto postData) {
        RefundActionStatus result = new RefundActionStatus(ActionStatusEnum.SUCCESS, "");

        try {
            Long id = refundApi.createRefund(postData);
            result.setRefundId(id);
        } catch (Exception e) {
            processException(e, result);
        }

        return result;
    }

}