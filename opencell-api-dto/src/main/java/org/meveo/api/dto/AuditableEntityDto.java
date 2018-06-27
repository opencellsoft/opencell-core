package org.meveo.api.dto;

import org.meveo.model.AuditableEntity;

/**
 * Equivalent of AuditableEntity in DTO
 * 
 * @author Edward P. Legaspi
 */
public abstract class AuditableEntityDto extends BaseEntityDto {

    /**
     * serial version uid.
     */
    private static final long serialVersionUID = 1040133977061424749L;

    private AuditableDto auditable;

    public AuditableEntityDto() {
        super();
    }

    public AuditableEntityDto(AuditableEntity e) {
        super();
        setAuditable(e);
    }

    public void setAuditable(AuditableEntity e) {
        if (e != null && e.getAuditable() != null) {
            auditable = new AuditableDto(e.getAuditable());
        }
    }

    public AuditableDto getAuditableNullSafe() {
        if (auditable == null) {
            auditable = new AuditableDto();
        }

        return auditable;
    }

    public AuditableDto getAuditable() {
        return auditable;
    }

    public void setAuditable(AuditableDto auditable) {
        this.auditable = auditable;
    }

}
