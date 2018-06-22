package org.meveo.api.admin;

import org.meveo.admin.exception.BusinessException;
import org.meveo.api.BaseApi;
import org.meveo.api.exception.MeveoApiException;
import org.meveo.audit.logging.configuration.AuditConfiguration;
import org.meveo.audit.logging.core.AuditContext;

import javax.ejb.Stateless;

/**
 * @author Mounir Bahije
 * @lastModifiedVersion 5.1
 *
 */
@Stateless
public class AuditApi extends BaseApi {

    /**
     * Update audit logging
     * 
     * @param active
     *
     */
    public void auditActive(Boolean active) {

        final AuditConfiguration auditConfiguration = AuditContext.getInstance().getAuditConfiguration();
        auditConfiguration.setEnabled(active);

    }

}
