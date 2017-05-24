package org.meveo.admin.audit.log;

import javax.faces.view.ViewScoped;
import javax.inject.Inject;
import javax.inject.Named;

import org.meveo.admin.action.BaseBean;
import org.meveo.model.audit.logging.AuditLog;
import org.meveo.service.audit.logging.AuditLogService;
import org.meveo.service.base.local.IPersistenceService;

/**
 * @author Edward P. Legaspi
 **/
@ViewScoped
@Named
public class AuditLogBean extends BaseBean<AuditLog> {

	private static final long serialVersionUID = -1770786826750578145L;

	@Inject
	private AuditLogService auditLogService;

	public AuditLogBean() {
		super(AuditLog.class);
	}

	@Override
	protected IPersistenceService<AuditLog> getPersistenceService() {
		return auditLogService;
	}

}
