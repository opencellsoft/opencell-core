package org.meveo.api.dto.script;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;

import org.apache.commons.lang3.StringUtils;
import org.meveo.api.dto.EnableBusinessDto;
import org.meveo.model.scripts.ScriptInstance;
import org.meveo.model.scripts.ScriptSourceTypeEnum;

/**
 * @author Andrius Karpavicius
 **/
@XmlAccessorType(XmlAccessType.FIELD)
public abstract class CustomScriptDto extends EnableBusinessDto {

    private static final long serialVersionUID = -977313726064562882L;

    @XmlElement
    private ScriptSourceTypeEnum type;

    @XmlElement(required = true)
    private String script;

    public CustomScriptDto() {

    }

    public CustomScriptDto(ScriptInstance scriptInstance) {
        super(scriptInstance);
        this.type = scriptInstance.getSourceTypeEnum();
        this.script = scriptInstance.getScript();
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