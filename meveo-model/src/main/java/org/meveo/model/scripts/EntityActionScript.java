package org.meveo.model.scripts;

import javax.persistence.Column;
import javax.persistence.DiscriminatorValue;
import javax.persistence.Entity;
import javax.persistence.Transient;
import javax.validation.constraints.Size;

import org.meveo.model.ExportIdentifier;

@Entity
@ExportIdentifier({ "code", "appliesTo", "provider" })
@DiscriminatorValue("EntityAction")
public class EntityActionScript extends CustomScript {

    private static final long serialVersionUID = -1640429569087958881L;

    @Transient
    private String localCode;

    @Column(name = "APPLIES_TO", nullable = false, length = 100)
    private String appliesTo;

    @Column(name = "APPLICABLE_ON_EL", length = 150)
    @Size(max = 150)
    private String applicableOnEl;

    @Column(name = "label", length = 50)
    @Size(max = 50)
    private String label;

//    @Override
//    public void setCode(String code) {
//        super.setCode(code);
//        getLocalCodeForRead();
//    }

    public String getLocalCode() {
        return localCode;
    }

    public String getLocalCodeForRead() {
        // Parse code, which consists of <localCode>_<appliesTo> to determine localCode value
        if (localCode == null && code != null) {
            localCode = code.split("_")[0];
        }
        return localCode;
    }

    public void setLocalCode(String localCode) {
        this.localCode = localCode;
    }

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

    @Override
    public boolean equals(Object obj) {

        if (this == obj) {
            return true;
        }

        if (obj == null) {
            return false;
        } else if (!(obj instanceof EntityActionScript)) { // Fails with proxed objects: getClass() != obj.getClass()){
            return false;
        }

        EntityActionScript other = (EntityActionScript) obj;

        if (getId() != null && other.getId() != null && getId() == other.getId()) {
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