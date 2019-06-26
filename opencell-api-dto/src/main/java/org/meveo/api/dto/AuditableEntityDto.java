package org.meveo.api.dto;

import org.meveo.api.dto.audit.AuditableFieldDto;
import org.meveo.model.AuditableEntity;

import javax.xml.bind.Marshaller;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlElementWrapper;
import javax.xml.bind.annotation.XmlRootElement;
import java.util.List;

/**
 * Equivalent of AuditableEntity in DTO
 *
 * @author Edward P. Legaspi
 * @author Abdellatif BARI
 * @lastModifiedVersion 7.0
 */
@XmlRootElement(name = "AuditableEntity")
@XmlAccessorType(XmlAccessType.FIELD)
public abstract class AuditableEntityDto extends BaseEntityDto {

    /**
     * serial version uid.
     */
    private static final long serialVersionUID = 1040133977061424749L;

    private AuditableDto auditable;

    @XmlElementWrapper(name = "auditableFields")
    @XmlElement(name = "auditableField")
    private List<AuditableFieldDto> auditableFields;

    public AuditableEntityDto() {
        super();
    }

    public AuditableEntityDto(AuditableEntity e) {
        super();
        setAuditable(e);
    }

    // invoked by Marshaller before marshalling
    void beforeMarshal(Marshaller marshaller) {
        if(auditableFields != null && auditableFields.isEmpty()) {
            auditableFields = null;
        }
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

    /**
     * Gets the auditableFields
     *
     * @return the auditableFields
     */
    public List<AuditableFieldDto> getAuditableFields() {
        return auditableFields;
    }

    /**
     * Sets the auditableFields.
     *
     * @param auditableFields the new auditableFields
     */
    public void setAuditableFields(List<AuditableFieldDto> auditableFields) {
        this.auditableFields = auditableFields;
    }
}