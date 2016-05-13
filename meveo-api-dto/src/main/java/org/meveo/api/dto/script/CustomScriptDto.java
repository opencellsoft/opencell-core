package org.meveo.api.dto.script;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;

import org.apache.commons.lang3.StringUtils;
import org.meveo.api.dto.BaseDto;
import org.meveo.model.scripts.ScriptSourceTypeEnum;

/**
 * @author Andrius Karpavicius
 **/
@XmlAccessorType(XmlAccessType.FIELD)
public abstract class CustomScriptDto extends BaseDto {

    private static final long serialVersionUID = -977313726064562882L;

    @XmlAttribute(required = true)
    private String code;

    @XmlAttribute()
    private String description;

    @XmlElement
    private ScriptSourceTypeEnum type;

    @XmlElement(required = true)
    private String script;

    public CustomScriptDto() {

    }

    public CustomScriptDto(String code, String description, ScriptSourceTypeEnum type, String script) {
        this.code = code;
        this.description = description;
        this.type = type;
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

    public ScriptSourceTypeEnum getType() {
        return type;
    }

    public void setType(ScriptSourceTypeEnum type) {
        this.type = type;
    }

    public String getScript() {
        return script;
    }

    public void setScript(String script) {
        this.script = script;
    }

    public boolean isCodeOnly() {
        return StringUtils.isBlank(script);
    }

    @Override
    public String toString() {
        return "CustomScriptDto [code=" + code + ", description=" + description + ", type=" + type + ", script=" + script + "]";
    }
}