package org.meveo.api.dto.script;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;

import org.apache.commons.lang3.StringUtils;
import org.meveo.api.dto.EnableBusinessDto;
import org.meveo.model.scripts.ScriptInstance;
import org.meveo.model.scripts.ScriptSourceTypeEnum;

/**
 * The Class CustomScriptDto.
 *
 * @author Andrius Karpavicius
 */
@XmlAccessorType(XmlAccessType.FIELD)
public abstract class CustomScriptDto extends EnableBusinessDto {

    /** The Constant serialVersionUID. */
    private static final long serialVersionUID = -977313726064562882L;

    /** The type. */
    @XmlElement
    private ScriptSourceTypeEnum type;

    /** The script. */
    @XmlElement(required = true)
    private String script;

    /**
     * Instantiates a new custom script dto.
     */
    public CustomScriptDto() {

    }

    /**
     * Convert script instance entity to DTO
     * 
     * @param scriptInstance Entity to convert
     */
    public CustomScriptDto(ScriptInstance scriptInstance) {
        super(scriptInstance);
        this.type = scriptInstance.getSourceTypeEnum();
        this.script = scriptInstance.getScript();
    }

    /**
     * Gets the type.
     *
     * @return the type
     */
    public ScriptSourceTypeEnum getType() {
        return type;
    }

    /**
     * Sets the type.
     *
     * @param type the new type
     */
    public void setType(ScriptSourceTypeEnum type) {
        this.type = type;
    }

    /**
     * Gets the script.
     *
     * @return the script
     */
    public String getScript() {
        return script;
    }

    /**
     * Sets the script.
     *
     * @param script the new script
     */
    public void setScript(String script) {
        this.script = script;
    }

    /**
     * Checks if is code only.
     *
     * @return true, if is code only
     */
    public boolean isCodeOnly() {
        return StringUtils.isBlank(script);
    }

    @Override
    public String toString() {
        return "CustomScriptDto [code=" + code + ", description=" + description + ", type=" + type + ", script=" + script + "]";
    }
}