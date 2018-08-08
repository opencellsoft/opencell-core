package org.meveo.api.rest.payment.impl;

import org.meveo.api.dto.ActionStatus;
import org.meveo.api.dto.ActionStatusEnum;
import org.meveo.api.logging.WsRestApiInterceptor;
import org.meveo.api.payment.IBanApi;
import org.meveo.api.rest.impl.BaseRs;
import org.meveo.api.rest.payment.IBanRs;

import javax.enterprise.context.RequestScoped;
import javax.inject.Inject;
import javax.interceptor.Interceptors;

/**
 * @author Mounir Bahije
 * @lastModifiedVersion 5.2
 *
 */
@RequestScoped
@Interceptors({ WsRestApiInterceptor.class })
public class IBanRsImpl extends BaseRs implements IBanRs {

    @Inject
    private IBanApi ibanApi;

    @Override
    public ActionStatus validate(String iban) {

        ActionStatus result = new ActionStatus();

        try {

            ibanApi.validate(iban);

        } catch (Exception e) {
            result.setStatus(ActionStatusEnum.FAIL);
            e.printStackTrace();
        }

        return result;
    }

}
