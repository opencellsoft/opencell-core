package org.meveo.api.admin;

import javax.ejb.Stateless;

import org.meveo.api.BaseApi;
import org.meveo.audit.logging.configuration.AuditConfiguration;
import org.meveo.audit.logging.core.AuditContext;

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
     * @param active is the audit active
     *
     */
    public void auditActive(Boolean active) {

        final AuditConfiguration auditConfiguration = AuditContext.getInstance().getAuditConfiguration();
        auditConfiguration.setEnabled(active);

    }

}
