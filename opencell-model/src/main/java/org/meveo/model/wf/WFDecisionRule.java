/*
 * (C) Copyright 2015-2016 Opencell SAS (http://opencellsoft.com/) and contributors.
 * (C) Copyright 2009-2014 Manaty SARL (http://manaty.net/) and contributors.
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Affero General Public License as
 * published by the Free Software Foundation, either version 3 of the
 * License, or (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  
 * This program is not suitable for any direct or indirect application in MILITARY industry
 * See the GNU Affero General Public License for more details.
 *
 * You should have received a copy of the GNU Affero General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package org.meveo.model.wf;

import java.util.HashSet;
import java.util.Set;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.ManyToMany;
import javax.persistence.Table;
import javax.persistence.UniqueConstraint;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

import org.hibernate.annotations.GenericGenerator;
import org.hibernate.annotations.Parameter;
import org.hibernate.annotations.Type;
import org.meveo.model.EnableEntity;
import org.meveo.model.ExportIdentifier;

@Entity
@ExportIdentifier({ "name", "value" })
@Table(name = "wf_decision_rule", uniqueConstraints = @UniqueConstraint(columnNames = { "name", "value" }))
@GenericGenerator(name = "ID_GENERATOR", strategy = "org.hibernate.id.enhanced.SequenceStyleGenerator", parameters = {
        @Parameter(name = "sequence_name", value = "wf_decision_rule_seq"), })
public class WFDecisionRule extends EnableEntity implements Comparable<WFDecisionRule> {

    private static final long serialVersionUID = 1L;

    @Column(name = "name")
    @Size(max = 255)
    @NotNull
    private String name;

    @Size(max = 255)
    @Column(name = "value")
    @NotNull
    private String value;

    @Column(name = "type")
    @Enumerated(EnumType.STRING)
    @NotNull
    private DecisionRuleTypeEnum type;

    @Column(name = "condition_el", length = 2000)
    @Size(max = 2000)
    @NotNull
    private String conditionEl;

    @Type(type = "numeric_boolean")
    @Column(name = "model")
    private boolean model = false;

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
