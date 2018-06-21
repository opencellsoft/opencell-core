package org.meveo.api.admin;

import org.meveo.admin.exception.BusinessException;
import org.meveo.api.BaseApi;
import org.meveo.api.exception.MeveoApiException;
import org.meveo.audit.logging.configuration.AuditConfiguration;
import org.meveo.audit.logging.core.AuditContext;

import javax.ejb.Stateless;


/**
 * @author Mounir Bahije
 */
@Stateless
public class AuditApi extends BaseApi {


    public void auditActive(Boolean active)  throws MeveoApiException, BusinessException {

        try {
            final AuditConfiguration auditConfiguration = AuditContext.getInstance().getAuditConfiguration();
            auditConfiguration.setEnabled(active);
        } catch (Exception e) {
            log.error("Failed to set Active boolean", e);
            throw e;
        }

    }



}
