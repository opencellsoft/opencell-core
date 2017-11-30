package org.meveo.service.audit.logging;

import javax.ejb.Stateless;

import org.meveo.model.audit.logging.AuditLog;
import org.meveo.service.base.PersistenceService;

/**
 * @author Edward P. Legaspi
 **/
@Stateless
public class AuditLogService extends PersistenceService<AuditLog> {

    /**
     * purge audit logs.
     */
    public void purge() {
        String hqlQuery = String.format("delete from AuditLog");
        getEntityManager().createQuery(hqlQuery).executeUpdate();
    }

}
