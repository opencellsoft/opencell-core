package org.meveo.admin.action.audit;

import org.meveo.admin.action.BaseBean;
import org.meveo.model.AuditableField;
import org.meveo.service.audit.AuditableFieldService;
import org.meveo.service.base.local.IPersistenceService;

import javax.faces.view.ViewScoped;
import javax.inject.Inject;
import javax.inject.Named;

/**
 * @author Abdellatif BARI
 * @since 7.0
 */
@ViewScoped
@Named
public class AuditableFieldBean extends BaseBean<AuditableField> {

    private static final long serialVersionUID = 5116016605732446172L;

    @Inject
    private AuditableFieldService auditableFieldService;

    public AuditableFieldBean() {
        super(AuditableField.class);
    }

    @Override
    protected IPersistenceService<AuditableField> getPersistenceService() {
        return auditableFieldService;
    }

}
