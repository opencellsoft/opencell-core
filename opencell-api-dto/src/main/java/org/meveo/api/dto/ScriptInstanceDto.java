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

package org.meveo.api.dto;

import static org.meveo.api.dto.LanguageDescriptionDto.convertMultiLanguageFromMapOfValues;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlRootElement;

import org.meveo.api.dto.script.CustomScriptDto;
import org.meveo.model.scripts.ScriptInstance;

import io.swagger.v3.oas.annotations.media.Schema;

/**
 * The Class ScriptInstanceDto.
 *
 * @author Edward P. Legaspi
 */
@XmlRootElement(name = "ScriptInstance")
@XmlAccessorType(XmlAccessType.FIELD)
public class ScriptInstanceDto extends CustomScriptDto {

    /** The Constant serialVersionUID. */
    private static final long serialVersionUID = 4555037251902559699L;

    /** The execution roles. */
    private List<String> executionRoles = new ArrayList<String>();

    /** The sourcing roles. */
    private List<String> sourcingRoles = new ArrayList<String>();
    
    private String scriptInstanceCategoryCode;

    @Schema(description = "list of the language description")
    private List<LanguageDescriptionDto> languageDescriptions;
    
    @Schema(description = "list of the script parameters")
    private List<ScriptParameterDto> scriptParameters;

    /**
     * Instantiates a new script instance dto.
     */
    public ScriptInstanceDto() {
        super();
    }

    /**
     * Convert script instance entity to DTO
     *
     * @param scriptInstance Entity to convert
     */
    public ScriptInstanceDto(ScriptInstance scriptInstance) {
        super(scriptInstance);

        if (scriptInstance.getExecutionRoles() != null) {
            executionRoles = new ArrayList<String>();
            executionRoles.addAll(scriptInstance.getExecutionRoles());
        }

        if (scriptInstance.getSourcingRoles() != null) {
            sourcingRoles = new ArrayList<String>();
            sourcingRoles.addAll(scriptInstance.getSourcingRoles());
        }
        
        if(scriptInstance.getScriptInstanceCategory() != null) {
        	scriptInstanceCategoryCode = scriptInstance.getScriptInstanceCategory().getCode();
        }
        
        if(scriptInstance.getScriptParameters() != null) {
        	scriptParameters = new ArrayList<>();
        	scriptParameters.addAll(scriptInstance.getScriptParameters().stream().map(ScriptParameterDto::new).collect(Collectors.toList()));
        }
        
        languageDescriptions = convertMultiLanguageFromMapOfValues(scriptInstance.getDescriptionI18n());
    }

    @Override
    public String toString() {
        return "ScriptInstanceDto [code=" + getCode() + ", description=" + getDescription() + ", type=" + getType() + ", script=" + getScript() + ", executionRoles="
                + executionRoles + ", sourcingRoles=" + sourcingRoles + "]";
    }

    /**
     * Gets the execution roles.
     *
     * @return the executionRoles
     */
    public List<String> getExecutionRoles() {
        return executionRoles;
    }

    /**
     * Sets the execution roles.
     *
     * @param executionRoles the executionRoles to set
     */
    public void setExecutionRoles(List<String> executionRoles) {
        this.executionRoles = executionRoles;
    }

    /**
     * Gets the sourcing roles.
     *
     * @return the sourcingRoles
     */
    public List<String> getSourcingRoles() {
        return sourcingRoles;
    }

    /**
     * Sets the sourcing roles.
     *
     * @param sourcingRoles the sourcingRoles to set
     */
    public void setSourcingRoles(List<String> sourcingRoles) {
        this.sourcingRoles = sourcingRoles;
    }

    /*
     * (non-Javadoc)
     * 
     * @see java.lang.Object#equals(java.lang.Object)
     */
    @Override
    public boolean equals(Object obj) {

        if (this == obj) {
            return true;
        }
        
        if (obj == null || !(obj instanceof ScriptInstanceDto)) { // Fails with proxed objects: getClass() != obj.getClass()){
            return false;
        }

        ScriptInstanceDto other = (ScriptInstanceDto) obj;

        if (getCode() == null) {
            if (other.getCode() != null) {
                return false;
            }
        } else if (!getCode().equals(other.getCode())) {
            return false;
        }
        return true;
    }

	public String getScriptInstanceCategoryCode() {
		return scriptInstanceCategoryCode;
	}

	public void setScriptInstanceCategoryCode(String scriptInstanceCategoryCode) {
		this.scriptInstanceCategoryCode = scriptInstanceCategoryCode;
	}
    
    public List<LanguageDescriptionDto> getLanguageDescriptions() {
        return languageDescriptions;
    }

    public void setLanguageDescriptions(List<LanguageDescriptionDto> languageDescriptions) {
        this.languageDescriptions = languageDescriptions;
    }

	public List<ScriptParameterDto> getScriptParameters() {
		return scriptParameters;
	}

	public void setScriptParameters(List<ScriptParameterDto> scriptParameters) {
		this.scriptParameters = scriptParameters;
	}
    
}