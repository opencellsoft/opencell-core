package org.meveo.api.dto.script;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;

import org.apache.commons.lang3.StringUtils;
import org.meveo.api.dto.BaseDto;
import org.meveo.model.scripts.ScriptSourceTypeEnum;

/**
 * The Class CustomScriptDto.
 *
 * @author Andrius Karpavicius
 */
@XmlAccessorType(XmlAccessType.FIELD)
public abstract class CustomScriptDto extends BaseDto {

    /** The Constant serialVersionUID. */
    private static final long serialVersionUID = -977313726064562882L;

    /** The code. */
    @XmlAttribute(required = true)
    private String code;

    /** The description. */
    @XmlAttribute()
    private String description;

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
     * Instantiates a new custom script dto.
     *
     * @param code the code
     * @param description the description
     * @param type the type
     * @param script the script
     */
    public CustomScriptDto(String code, String description, ScriptSourceTypeEnum type, String script) {
        this.code = code;
        this.description = description;
        this.type = type;
        this.script = script;
    }

    /**
     * Gets the code.
     *
     * @return the code
     */
    public String getCode() {
        return code;
    }

    /**
     * Sets the code.
     *
     * @param code the new code
     */
    public void setCode(String code) {
        this.code = code;
    }

    /**
     * Gets the description.
     *
     * @return the description
     */
    public String getDescription() {
        return description;
    }

    /**
     * Sets the description.
     *
     * @param description the new description
     */
    public void setDescription(String description) {
        this.description = description;
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