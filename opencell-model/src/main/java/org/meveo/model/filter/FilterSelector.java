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

package org.meveo.model.filter;

import java.util.ArrayList;
import java.util.List;

import org.hibernate.annotations.GenericGenerator;
import org.hibernate.annotations.Parameter;
import org.meveo.model.BaseEntity;
import org.meveo.validation.constraint.ClassName;

import jakarta.persistence.CollectionTable;
import jakarta.persistence.Column;
import jakarta.persistence.ElementCollection;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.Table;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

/**
 * @author Edward P. Legaspi
 **/
@Entity
@Table(name = "meveo_filter_selector")
@GenericGenerator(name = "ID_GENERATOR", strategy = "org.hibernate.id.enhanced.SequenceStyleGenerator", parameters = {
        @Parameter(name = "sequence_name", value = "meveo_filter_selector_seq"), })
public class FilterSelector extends BaseEntity {

    private static final long serialVersionUID = -7068163052219180546L;

    @ClassName
    @Size(max = 100)
    @NotNull
    @Column(name = "target_entity", length = 100, nullable = false)
    private String targetEntity;

    @Column(name = "alias", length = 50)
    @Size(max = 50)
    private String alias;

    /**
     * List of field names to display or export.
     */
    @ElementCollection(fetch = FetchType.LAZY)
    @CollectionTable(name = "meveo_filter_selector_display_fields", joinColumns = @JoinColumn(name = "filter_selector_id"))
    @Column(name = "display_field")
    private List<String> displayFields = new ArrayList<String>();

    @ElementCollection(fetch = FetchType.LAZY)
    @CollectionTable(name = "meveo_filter_selector_export_fields", joinColumns = @JoinColumn(name = "filter_selector_id"))
    @Column(name = "export_field")
    private List<String> exportFields = new ArrayList<String>();

    /**
     * List of fields to ignore if foreign key not found.
     */
    @ElementCollection(fetch = FetchType.LAZY)
    @CollectionTable(name = "meveo_filter_selector_ignore_fields", joinColumns = @JoinColumn(name = "filter_selector_id"))
    @Column(name = "ignored_field")
    private List<String> ignoreIfNotFoundForeignKeys = new ArrayList<String>();

    public String getTargetEntity() {
        return targetEntity;
    }

    public void setTargetEntity(String targetEntity) {
        this.targetEntity = targetEntity;
    }

    public String getAlias() {
        return alias;
    }

    public void setAlias(String alias) {
        this.alias = alias;
    }

    @Override
    public boolean equals(Object obj) {

        if (this == obj) {
            return true;
        } else if (obj == null) {
            return false;
        } else if (!(obj instanceof FilterSelector)) {
            return false;
        }

        FilterSelector other = (FilterSelector) obj;
        return (other.getId() != null) && other.getId().equals(this.getId());
    }

    public List<String> getDisplayFields() {
        return displayFields;
    }

    public void setDisplayFields(List<String> displayFields) {
        this.displayFields = displayFields;
    }

    public List<String> getExportFields() {
        return exportFields;
    }

    public void setExportFields(List<String> exportFields) {
        this.exportFields = exportFields;
    }

    public List<String> getIgnoreIfNotFoundForeignKeys() {
        return ignoreIfNotFoundForeignKeys;
    }

    public void setIgnoreIfNotFoundForeignKeys(List<String> ignoreIfNotFoundForeignKeys) {
        this.ignoreIfNotFoundForeignKeys = ignoreIfNotFoundForeignKeys;
    }

}
