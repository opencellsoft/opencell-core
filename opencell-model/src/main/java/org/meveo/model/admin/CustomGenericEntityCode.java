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

package org.meveo.model.admin;

import static javax.persistence.FetchType.LAZY;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

import org.hibernate.annotations.GenericGenerator;
import org.hibernate.annotations.Parameter;
import org.meveo.model.AuditableEntity;
import org.meveo.model.ExportIdentifier;
import org.meveo.model.sequence.Sequence;

/**
 * Custom generic entity code.
 *
 * @author Abdellatif BARI.
 * @since 7.0
 */
@Entity
@Cacheable
@ExportIdentifier("entityClass")
@Table(name = "adm_custom_generic_entity_code")
@GenericGenerator(name = "ID_GENERATOR", strategy = "org.hibernate.id.enhanced.SequenceStyleGenerator", parameters = {
        @Parameter(name = "sequence_name", value = "adm_custom_generic_entity_code_seq"), })

public class CustomGenericEntityCode extends AuditableEntity {

    private static final long serialVersionUID = 84222776645282176L;

    public CustomGenericEntityCode() {
    }

    @Column(name = "entity_class")
    @Size(max = 255)
    @NotNull
    private String entityClass;

    @Column(name = "code_el", nullable = false)
    @Size(max = 255, min = 1)
    private String codeEL;

    @Column(name = "format_el", length = 2000)
    @Size(max = 2000)
    private String formatEL;

    @ManyToOne(fetch = LAZY)
    @JoinColumn(name = "sequence_id")
    private Sequence sequence;

    /**
     * @return the entityClass
     */
    public String getEntityClass() {
        return entityClass;
    }

    /**
     * @param entityClass the entityClass to set
     */
    public void setEntityClass(String entityClass) {
        this.entityClass = entityClass;
    }

    /**
     * @return the codeEL
     */
    public String getCodeEL() {
        return codeEL;
    }

    /**
     * @param codeEL the codeEL to set
     */
    public void setCodeEL(String codeEL) {
        this.codeEL = codeEL;
    }

    /* (non-Javadoc)
     * @see java.lang.Object#hashCode()
     */
    @Override
    public int hashCode() {
        final int prime = 31;
        int result = super.hashCode();
        result = prime * result + ((entityClass == null) ? 0 : entityClass.hashCode());
        return result;
    }

    /* (non-Javadoc)
     * @see java.lang.Object#equals(java.lang.Object)
     */
    @Override
    public boolean equals(Object obj) {
        if (this == obj)
            return true;
        if (!super.equals(obj))
            return false;
        if (getClass() != obj.getClass())
            return false;
        CustomGenericEntityCode other = (CustomGenericEntityCode) obj;
        if (entityClass == null) {
            if (other.entityClass != null)
                return false;
        } else if (!entityClass.equals(other.entityClass))
            return false;
        return true;
    }

    /* (non-Javadoc)
     * @see java.lang.Object#toString()
     */
    @Override
    public String toString() {
        return "CustomGenericEntityCode [entityClass=" + entityClass + ", codeEL=" + codeEL + ", sequenceSize=" + sequence.getSequenceSize() + ", sequenceCurrentValue=" + sequence.getCurrentNumber() + "]";
    }

    public String getFormatEL() {
        return formatEL;
    }

    public void setFormatEL(String formatEL) {
        this.formatEL = formatEL;
    }

    public Sequence getSequence() {
        return sequence;
    }

    public void setSequence(Sequence sequence) {
        this.sequence = sequence;
    }
}