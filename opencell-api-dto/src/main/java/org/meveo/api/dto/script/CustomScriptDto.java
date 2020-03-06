/*
 * (C) Copyright 2015-2020 Opencell SAS (https://opencellsoft.com/) and contributors.
 *
 * This program is free software: you can redistribute it and/or modify it under the terms of the GNU Affero General
 * Public License as published by the Free Software Foundation, either version 3 of the License, or (at your option)
 * any later version.
 *
 * THERE IS NO WARRANTY FOR THE PROGRAM, TO THE EXTENT PERMITTED BY APPLICABLE LAW. EXCEPT WHEN
 * OTHERWISE STATED IN WRITING THE COPYRIGHT HOLDERS AND/OR OTHER PARTIES PROVIDE THE PROGRAM "AS
 * IS" WITHOUT WARRANTY OF ANY KIND, EITHER EXPRESSED OR IMPLIED, INCLUDING, BUT NOT LIMITED TO, THE
 * IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE. THE ENTIRE RISK AS TO
 * THE QUALITY AND PERFORMANCE OF THE PROGRAM IS WITH YOU. SHOULD THE PROGRAM PROVE DEFECTIVE,
 * YOU ASSUME THE COST OF ALL NECESSARY SERVICING, REPAIR OR CORRECTION.
 *
 * For more information on the GNU Affero General Public License, please consult
 * <https://www.gnu.org/licenses/agpl-3.0.en.html>.
 */

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

    /**
     * Shall same script instance be utilized in repeated calls
     */
    @XmlElement
    private Boolean reuse;

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
        this.reuse = scriptInstance.getReuse();
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
     * @return Shall same script instance be utilized in repeated calls
     */
    public Boolean getReuse() {
        return reuse;
    }

    /**
     * @param reuse Shall same script instance be utilized in repeated calls
     */
    public void setReuse(Boolean reuse) {
        this.reuse = reuse;
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