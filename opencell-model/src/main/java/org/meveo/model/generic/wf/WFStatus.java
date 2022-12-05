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
package org.meveo.model.generic.wf;

import java.util.UUID;

import org.hibernate.annotations.GenericGenerator;
import org.hibernate.annotations.Parameter;
import org.meveo.model.BusinessEntity;
import org.meveo.model.ExportIdentifier;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.NamedQueries;
import jakarta.persistence.NamedQuery;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

/**
 * Generic workflow status
 */
@Entity
@ExportIdentifier({ "uuid" })
@Table(name = "wf_status", uniqueConstraints = @UniqueConstraint(columnNames = { "uuid" }))
@GenericGenerator(name = "ID_GENERATOR", strategy = "org.hibernate.id.enhanced.SequenceStyleGenerator", parameters = {
        @Parameter(name = "sequence_name", value = "wf_status_seq") })
@NamedQueries({
    @NamedQuery(name = "WFStatus.findByCodeAndGWF", query = "From WFStatus where code=:code and genericWorkflow=:genericWorkflow"),
    @NamedQuery(name = "WFStatus.deleteByGenericWorkflow", query = "delete from WFStatus wfs where wfs.genericWorkflow.id=:genericWorkflowId")})
public class WFStatus extends BusinessEntity {

    private static final long serialVersionUID = 1L;

    /**
     * Unique identifier - UUID
     */
    @Column(name = "uuid", nullable = false, updatable = false, length = 60)
    @Size(max = 60)
    @NotNull
    private String uuid = UUID.randomUUID().toString();

    /**
     * Generic workflow
     */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "generic_wf_id")
    private GenericWorkflow genericWorkflow;

    public String getUuid() {
        return uuid;
    }

    public void setUuid(String uuid) {
        this.uuid = uuid;
    }

    public GenericWorkflow getGenericWorkflow() {
        return genericWorkflow;
    }

    public void setGenericWorkflow(GenericWorkflow genericWorkflow) {
        this.genericWorkflow = genericWorkflow;
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = super.hashCode();
        result = prime * result + ((genericWorkflow == null) ? 0 : genericWorkflow.hashCode());
        return result;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj)
            return true;
        if (!super.equals(obj))
            return false;
        if (getClass() != obj.getClass())
            return false;
        WFStatus other = (WFStatus) obj;
        if (genericWorkflow == null) {
            if (other.genericWorkflow != null)
                return false;
        } else if (!genericWorkflow.equals(other.genericWorkflow))
            return false;
        return true;
    }
}
