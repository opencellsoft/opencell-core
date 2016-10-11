package org.meveo.api.dto;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

import org.meveo.model.crm.custom.EntityCustomAction;

/**
 * @author Edward P. Legaspi
 **/
@XmlRootElement(name = "EntityCustomAction")
@XmlAccessorType(XmlAccessType.FIELD)
public class EntityCustomActionDto extends BaseDto {

    private static final long serialVersionUID = -2916923287316823939L;

    @XmlAttribute(required = true)
    private String code;

    @XmlAttribute()
    private String description;

    @XmlAttribute(required = false)
    protected String appliesTo;

    @XmlElement(required = false)
    private String applicableOnEl;

    @XmlElement(required = false)
    private String label;

    private ScriptInstanceDto script;

    public EntityCustomActionDto() {
        super();
    }

    public EntityCustomActionDto(EntityCustomAction e) {
        this.code = e.getCode();
        this.description = e.getDescription();

        this.appliesTo = e.getAppliesTo();
        this.applicableOnEl = e.getApplicableOnEl();
        this.label = e.getLabel();

        this.setScript(new ScriptInstanceDto(e.getScript()));
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
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

    public ScriptInstanceDto getScript() {
        return script;
    }

    public void setScript(ScriptInstanceDto script) {
        this.script = script;
    }

    @Override
    public String toString() {
        return String.format("EntityCustomActionDto [code=%s, description=%s, appliesTo=%s, applicableOnEl=%s, label=%s, script=%s]", code, description, appliesTo, applicableOnEl,
            label, script);
    }
}