package org.meveo.model.crm.custom;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;
import javax.persistence.UniqueConstraint;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

import org.meveo.model.BusinessEntity;
import org.meveo.model.ExportIdentifier;
import org.meveo.model.ModuleItem;
import org.meveo.model.scripts.ScriptInstance;

@Entity
@ModuleItem
@ExportIdentifier({ "code", "appliesTo", "provider" })
@Table(name = "CRM_CUSTOM_ACTION", uniqueConstraints = @UniqueConstraint(columnNames = { "CODE", "APPLIES_TO", "PROVIDER_ID" }))
@SequenceGenerator(name = "ID_GENERATOR", sequenceName = "CRM_CUSTOM_ACTION_SEQ")
public class EntityCustomAction extends BusinessEntity {

    private static final long serialVersionUID = -1640429569087958881L;

    @Column(name = "APPLIES_TO", nullable = false, length = 100)
    @Size(max = 100)
    @NotNull
    private String appliesTo;

    @Column(name = "APPLICABLE_ON_EL", length = 2000)
    @Size(max = 2000)
    private String applicableOnEl;

    @Column(name = "label", length = 50)
    @Size(max = 50)
    private String label;

    @ManyToOne()
    @JoinColumn(name = "SCRIPT_INSTANCE_ID")
    private ScriptInstance script;

      public String getAppliesTo() {
        return appliesTo;
    }

    public void setAppliesTo(String appliesTo) {
        this.appliesTo = appliesTo;
    }

    public String getApplicableOnEl() {
        return applicableOnEl;
    }

    public void setApplicableOnEl(String applicableOnEl) {
        this.applicableOnEl = applicableOnEl;
    }

    public String getLabel() {
        return label;
    }

    public void setLabel(String label) {
        this.label = label;
    }

    public void setScript(ScriptInstance script) {
        this.script = script;
    }

    public ScriptInstance getScript() {
        return script;
    }

    @Override
    public boolean equals(Object obj) {

        if (this == obj) {
            return true;
        }

        if (obj == null) {
            return false;
        } else if (!(obj instanceof EntityCustomAction)) { // Fails with proxed objects: getClass() != obj.getClass()){
            return false;
        }

        EntityCustomAction other = (EntityCustomAction) obj;

        if (getId() != null && other.getId() != null && getId().equals(other.getId())) {
            // return true;
        }

        if (code == null && other.getCode() != null) {
            return false;
        } else if (!code.equals(other.getCode())) {
            return false;
        } else if (appliesTo == null && other.getAppliesTo() != null) {
            return false;
        } else if (!appliesTo.equals(other.getAppliesTo())) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return String.format("EntityActionScript [id=%s, appliesTo=%s, code=%s]", id, appliesTo, code);
    }
}