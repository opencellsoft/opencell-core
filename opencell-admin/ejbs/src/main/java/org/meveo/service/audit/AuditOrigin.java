package org.meveo.service.audit;

import org.meveo.model.audit.ChangeOriginEnum;

import javax.enterprise.context.RequestScoped;

/**
 * keep the audit origin
 *
 * @author Abdellatif BARI
 * @since 7.0
 */
@RequestScoped
public class AuditOrigin {

    /**
     * Source of change
     */
    private ChangeOriginEnum auditOrigin;

    /**
     * Source name of change
     */
    private String auditOriginName;


    /**
     * Get source of change value
     *
     * @return Source of change
     */
    public ChangeOriginEnum getAuditOrigin() {
        return auditOrigin;
    }

    /**
     * Set source of change value
     *
     * @param auditOrigin Source of change
     */
    public void setAuditOrigin(ChangeOriginEnum auditOrigin) {
        this.auditOrigin = auditOrigin;
    }

    /**
     * Get source name of change value
     *
     * @return Source name of change
     */
    public String getAuditOriginName() {
        return auditOriginName;
    }

    /**
     * Set source name of change value
     *
     * @param auditOriginName Source name of change
     */
    public void setAuditOriginName(String auditOriginName) {
        this.auditOriginName = auditOriginName;
    }
}
