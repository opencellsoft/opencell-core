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

package org.meveo.api.dto.document.sign;

import java.util.List;

import org.meveo.api.dto.BaseEntityDto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

/** 
 * A Dto holding inputs required to create a document signature procedure on Yousign platform side
 * 
 * @author Said Ramli 
 */ 
@JsonIgnoreProperties (ignoreUnknown = true)
public class SignProcedureDto  extends BaseEntityDto { 
    
    /** The Constant serialVersionUID. */ 
    private static final long serialVersionUID = 1L; 
    
    /** The id. */
    private String id;
    
    /** The name. */
    private String name;
    
    /** The description. */
    private String description;
    
    /** The start. default value is true*/
    private boolean start = true;
    
    /** The members. */
    private List<SignMemberRequestDto> members;
    
    /** The config. */
    private SignProcedureConfigDto config;

    /**
     * @return the name
     */
    public String getName() {
        return name;
    }

    /**
     * @return the description
     */
    public String getDescription() {
        return description;
    }

    /**
     * @return the start
     */
    public boolean isStart() {
        return start;
    }

    /**
     * @param name the name to set
     */
    public void setName(String name) {
        this.name = name;
    }

    /**
     * @param description the description to set
     */
    public void setDescription(String description) {
        this.description = description;
    }

    /**
     * @param start the start to set
     */
    public void setStart(boolean start) {
        this.start = start;
    }

    /**
     * @return the members
     */
    public List<SignMemberRequestDto> getMembers() {
        return members;
    }

    /**
     * @param members the members to set
     */
    public void setMembers(List<SignMemberRequestDto> members) {
        this.members = members;
    }

    /**
     * @return the config
     */
    public SignProcedureConfigDto getConfig() {
        return config;
    }

    /**
     * @param config the config to set
     */
    public void setConfig(SignProcedureConfigDto config) {
        this.config = config;
    }

    /**
     * @return the id
     */
    public String getId() {
        return id;
    }

    /**
     * @param id the id to set
     */
    public void setId(String id) {
        this.id = id;
    }

    @Override
    public String toString() {
        return "SignProcedureDto [id=" + id + ", name=" + name + ", description=" + description + ", start=" + start + ", members=" + members + ", config=" + config + "]";
    }
}
