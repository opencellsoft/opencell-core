package org.meveo.api.rest.admin.impl;

import org.meveo.api.admin.AuditApi;
import org.meveo.api.dto.ActionStatus;
import org.meveo.api.logging.WsRestApiInterceptor;
import org.meveo.api.rest.admin.AuditRs;
import org.meveo.api.rest.impl.BaseRs;

import javax.enterprise.context.RequestScoped;
import javax.inject.Inject;
import javax.interceptor.Interceptors;

/**
 * @author Mounir Bahije
 * @lastModifiedVersion 5.1
 *
 */
@RequestScoped
@Interceptors({ WsRestApiInterceptor.class })
public class AuditRsImpl extends BaseRs implements AuditRs {

    @Inject
    private AuditApi auditApi;

    @Override
    public ActionStatus enableORdisableAudit(String enableORdisable) {

        ActionStatus result = new ActionStatus();

        try {

            switch (enableORdisable) {
            case "enable":
                auditApi.auditActive(true);
                break;
            case "disable":
                auditApi.auditActive(false);
                break;
            default:
                break;
            }

        } catch (Exception e) {
            e.printStackTrace();
        }

        return result;
    }

}
