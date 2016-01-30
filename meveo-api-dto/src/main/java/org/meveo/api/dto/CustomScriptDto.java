package org.meveo.api.dto;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;

import org.meveo.model.scripts.ScriptSourceTypeEnum;

/**
 * @author Andrius Karpavicius
 **/
@XmlAccessorType(XmlAccessType.FIELD)
public abstract class CustomScriptDto extends BaseDto {

    private static final long serialVersionUID = -977313726064562882L;

    @XmlAttribute(required = true)
    private String code;

    @XmlAttribute(required = false)
    private String description;

    @XmlElement
    private String type;

    @XmlElement(required = true)
    private String script;

    public CustomScriptDto() {

    }

    public CustomScriptDto(String code, String description, ScriptSourceTypeEnum type, String script) {
        this.code = code;
        this.description = description;
        if (type != null) {
            this.type = type.name();
        }
        this.script = script;
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

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getScript() {
        return script;
    }

    public void setScript(String script) {
        this.script = script;
    }
}