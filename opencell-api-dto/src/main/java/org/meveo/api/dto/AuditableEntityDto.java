/*
 * (C) Copyright 2015-2020 Opencell SAS (https://opencellsoft.com/) and contributors.
 *
 * This program is free software: you can redistribute it and/or modify it under the terms of the GNU Affero General
 * Public License as published by the Free Software Foundation, either version 3 of the License, or (at your option)
 * any later version.
 *
 * THERE IS NO WARRANTY FOR THE PROGRAM, TO THE EXTENT PERMITTED BY APPLICABLE LAW. EXCEPT WHEN
 * OTHERWISE STATED IN WRITING THE COPYRIGHT HOLDERS AND/OR OTHER PARTIES PROVIDE THE PROGRAM "AS
 * IS" WITHOUT WARRANTY OF ANY KIND, EITHER EXPRESSED OR IMPLIED, INCLUDING, BUT NOT LIMITED TO, THE
 * IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE. THE ENTIRE RISK AS TO
 * THE QUALITY AND PERFORMANCE OF THE PROGRAM IS WITH YOU. SHOULD THE PROGRAM PROVE DEFECTIVE,
 * YOU ASSUME THE COST OF ALL NECESSARY SERVICING, REPAIR OR CORRECTION.
 *
 * For more information on the GNU Affero General Public License, please consult
 * <https://www.gnu.org/licenses/agpl-3.0.en.html>.
 */

package org.meveo.api.dto;

import com.fasterxml.jackson.annotation.JsonIgnore;
import io.swagger.v3.oas.annotations.Hidden;
import io.swagger.v3.oas.annotations.media.Schema;
import org.meveo.api.dto.audit.AuditableFieldDto;
import org.meveo.model.AuditableEntity;

import javax.xml.bind.Marshaller;
import javax.xml.bind.annotation.*;
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
public abstract @Hidden class AuditableEntityDto extends BaseEntityDto {

    /**
     * serial version uid.
     */
    private static final long serialVersionUID = 1040133977061424749L;

    @Schema(hidden = true)
    @JsonIgnore
    private AuditableDto auditable;

    @XmlElementWrapper(name = "auditableFields")
    @XmlElement(name = "auditableField")
    @Schema(hidden = true)
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
        if (auditableFields != null && auditableFields.isEmpty()) {
            auditableFields = null;
        }
    }

    @Schema(hidden = true)
    public void setAuditable(AuditableEntity e) {
        if (e != null && e.getAuditable() != null) {
            auditable = new AuditableDto(e.getAuditable());
        }
    }

    @Schema(hidden = true)
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