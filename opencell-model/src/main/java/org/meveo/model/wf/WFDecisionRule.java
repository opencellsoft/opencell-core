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

import java.util.HashSet;
import java.util.Set;

import org.hibernate.annotations.GenericGenerator;
import org.hibernate.annotations.Parameter;
import org.hibernate.type.NumericBooleanConverter;
import org.meveo.model.BaseEntity;
import org.meveo.model.ExportIdentifier;

import jakarta.persistence.Column;
import jakarta.persistence.Convert;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.ManyToMany;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

/**
 * Workflow decicion rule
 * 
 * @author Andrius Karpavicius
 */
@Entity
@ExportIdentifier({ "name", "value" })
@Table(name = "wf_decision_rule", uniqueConstraints = @UniqueConstraint(columnNames = { "name", "value" }))
@GenericGenerator(name = "ID_GENERATOR", strategy = "org.hibernate.id.enhanced.SequenceStyleGenerator", parameters = {
        @Parameter(name = "sequence_name", value = "wf_decision_rule_seq"), })
public class WFDecisionRule extends BaseEntity implements Comparable<WFDecisionRule> {

    private static final long serialVersionUID = 1L;

    /**
     * Name
     */
    @Column(name = "name")
    @Size(max = 255)
    @NotNull
    private String name;

    /**
     * Value
     */
    @Size(max = 255)
    @Column(name = "value")
    @NotNull
    private String value;

    /**
     * Decision rule type
     */
    @Column(name = "type")
    @Enumerated(EnumType.STRING)
    @NotNull
    private DecisionRuleTypeEnum type;

    /**
     * Expression to evaluate if decision rule applies
     */
    @Column(name = "condition_el", length = 2000)
    @Size(max = 2000)
    @NotNull
    private String conditionEl;

    /**
     * Is this a model
     */
    @Convert(converter = NumericBooleanConverter.class)
    @Column(name = "model")
    private boolean model = false;

    /**
     * Transitions that have this decision rule
     */
    @ManyToMany(mappedBy = "wfDecisionRules")
    private Set<WFTransition> wfTransitions = new HashSet<>();

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }

    public DecisionRuleTypeEnum getType() {
        return type;
    }

    public void setType(DecisionRuleTypeEnum type) {
        this.type = type;
    }

    /**
     * @return the conditionEl
     */
    public String getConditionEl() {
        return conditionEl;
    }

    /**
     * @param conditionEl the conditionEl to set
     */
    public void setConditionEl(String conditionEl) {
        this.conditionEl = conditionEl;
    }

    public boolean getModel() {
        return model;
    }

    public void setModel(boolean model) {
        this.model = model;
    }

    public Set<WFTransition> getWfTransitions() {
        return wfTransitions;
    }

    public void setWfTransitions(Set<WFTransition> wfTransitions) {
        this.wfTransitions = wfTransitions;
    }

    @Override
    public int hashCode() {
        return 961 + ("WFDecisionRule" + id).hashCode();
    }

    @Override
    public boolean equals(Object obj) {

        if (this == obj) {
            return true;
        } else if (obj == null) {
            return false;
        } else if (!(obj instanceof WFDecisionRule)) {
            return false;
        }

        WFDecisionRule other = (WFDecisionRule) obj;
        if (getId() == null) {
            if (other.getId() != null)
                return false;
        } else if (!getId().equals(other.getId()))
            return false;
        return true;
    }

    @Override
    public int compareTo(WFDecisionRule o) {
        Boolean model1 = this.getModel();
        Boolean model2 = o.getModel();
        int bComp = model2.compareTo(model1);

        if (bComp != 0) {
            return bComp;
        } else {
            String x1 = this.getValue();
            String x2 = o.getValue();
            return x1.compareTo(x2);
        }
    }
}
