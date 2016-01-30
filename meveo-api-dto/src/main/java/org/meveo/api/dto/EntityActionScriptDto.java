package org.meveo.api.dto;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

import org.meveo.model.scripts.EntityActionScript;

/**
 * @author Edward P. Legaspi
 **/
@XmlRootElement(name = "EntityActionScript")
@XmlAccessorType(XmlAccessType.FIELD)
public class EntityActionScriptDto extends CustomScriptDto {

    private static final long serialVersionUID = -2916923287316823939L;

    @XmlAttribute(required = false)
    protected String appliesTo;

    @XmlElement(required = false)
    private String applicableOnEl;

    @XmlElement(required = false)
    private String label;

    public EntityActionScriptDto() {
        super();
    }

    public EntityActionScriptDto(EntityActionScript e) {
        super(e.getLocalCodeForRead(), e.getDescription(), e.getSourceTypeEnum(), e.getScript());

        this.appliesTo = e.getAppliesTo();
        this.applicableOnEl = e.getApplicableOnEl();
        this.label = e.getLabel();
    }

    public String getFullCode() {
        return EntityActionScript.composeCode(getCode(), appliesTo);
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
    public String toString() {
        return String.format("EntityActionScriptDto [appliesTo=%s, code=%s, description=%s, type=%s, applicableOnEl=%s, label=%s]", appliesTo, getCode(), getDescription(),
            getType(), applicableOnEl, label, getCode(), getDescription(), getType());
    }
}