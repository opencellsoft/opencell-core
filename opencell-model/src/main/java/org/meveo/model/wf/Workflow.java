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
package org.meveo.model.wf;

import java.util.ArrayList;
import java.util.List;

import org.hibernate.annotations.GenericGenerator;
import org.hibernate.annotations.Parameter;
import org.hibernate.type.NumericBooleanConverter;
import org.meveo.model.EnableBusinessEntity;
import org.meveo.model.ExportIdentifier;
import org.meveo.model.ModuleItem;

import jakarta.persistence.Cacheable;
import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Convert;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.OneToMany;
import jakarta.persistence.OrderBy;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

/**
 * Workflow for entity data processing
 */
@Entity
@ModuleItem
@Cacheable
@ExportIdentifier({ "code" })
@Table(name = "wf_workflow", uniqueConstraints = @UniqueConstraint(columnNames = { "code" }))
@GenericGenerator(name = "ID_GENERATOR", strategy = "org.hibernate.id.enhanced.SequenceStyleGenerator", parameters = {
        @Parameter(name = "sequence_name", value = "wf_workflow_seq"), })
public class Workflow extends EnableBusinessEntity {

    private static final long serialVersionUID = 1L;

    /**
     * Workflow type
     */
    @Column(name = "wf_type", length = 255)
    @NotNull
    @Size(max = 255)
    String wfType;

    /**
     * A list of transitions making up worklfow
     */
    @OneToMany(mappedBy = "workflow", fetch = FetchType.LAZY, cascade = { CascadeType.REMOVE })
    @OrderBy("priority ASC")
    private List<WFTransition> transitions = new ArrayList<WFTransition>();

    /**
     * Should worklfow history be tracked
     */
    @Convert(converter = NumericBooleanConverter.class)
    @Column(name = "enable_history")
    private boolean enableHistory;

    /**
     * @return the wfType
     */
    public String getWfType() {
        return wfType;
    }

    /**
     * @param wfType the wfType to set
     */
    public void setWfType(String wfType) {
        this.wfType = wfType;
    }

    /**
     * @return the transitions
     */
    public List<WFTransition> getTransitions() {
        return transitions;
    }

    /**
     * @param transitions the transitions to set
     */
    public void setTransitions(List<WFTransition> transitions) {
        this.transitions = transitions;
    }

    /**
     * @return the enbaleHistory
     */
    public boolean isEnableHistory() {
        return enableHistory;
    }

    /**
     * @param enbaleHistory the enbaleHistory to set
     */
    public void setEnableHistory(boolean enbaleHistory) {
        this.enableHistory = enbaleHistory;
    }

    @Override
    public String toString() {
        return "Workflow [code=" + code + ", description=" + description + "]";
    }

}
