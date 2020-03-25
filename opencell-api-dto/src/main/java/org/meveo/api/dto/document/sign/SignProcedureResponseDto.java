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

import org.meveo.api.dto.response.BaseResponse;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

/** 
 * A DTO Class holding the response of Creating a document signature procedure.
 * 
 * @author Said Ramli
 */ 
@JsonIgnoreProperties (ignoreUnknown = true)
public class SignProcedureResponseDto extends BaseResponse { 
    
    /** The Constant serialVersionUID. */ 
    private static final long serialVersionUID = 1L; 
    
    /** The id. */
    private String id;
    
    /** The name. */
    private String name;
    
    /** The description. */
    private String description;
    
    /** The status. */
    private String status;
    
    /** The members. */
    private List<SignMemberDto> members;
    
    /** The files. */
    private List<SignFileResponseDto> files;
    
    /**
     * Gets the id.
     *
     * @return the id
     */
    public String getId() {
        return id;
    }
    
    /**
     * Gets the name.
     *
     * @return the name
     */
    public String getName() {
        return name;
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
     * Gets the status.
     *
     * @return the status
     */
    public String getStatus() {
        return status;
    }
    
    /**
     * Sets the id.
     *
     * @param id the id to set
     */
    public void setId(String id) {
        this.id = id;
    }
    
    /**
     * Sets the name.
     *
     * @param name the name to set
     */
    public void setName(String name) {
        this.name = name;
    }
    
    /**
     * Sets the description.
     *
     * @param description the description to set
     */
    public void setDescription(String description) {
        this.description = description;
    }
    
    /**
     * Sets the status.
     *
     * @param status the status to set
     */
    public void setStatus(String status) {
        this.status = status;
    }

    /**
     * @return the members
     */
    public List<SignMemberDto> getMembers() {
        return members;
    }

    /**
     * @param members the members to set
     */
    public void setMembers(List<SignMemberDto> members) {
        this.members = members;
    }

    /**
     * @return the files
     */
    public List<SignFileResponseDto> getFiles() {
        return files;
    }

    /**
     * @param files the files to set
     */
    public void setFiles(List<SignFileResponseDto> files) {
        this.files = files;
    }

}
