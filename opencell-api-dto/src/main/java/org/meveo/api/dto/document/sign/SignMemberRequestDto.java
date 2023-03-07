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

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

/**
 * A DTO class for a Request of a Signature procedure Member. 
 */
@JsonIgnoreProperties (ignoreUnknown = true)
public class SignMemberRequestDto extends SignMemberDto { 
    
    
    /** The Constant serialVersionUID. */
    private static final long serialVersionUID = 1L;
    
    public SignMemberRequestDto () {
    }
    
    public SignMemberRequestDto (String user) {
        super(user);
    }
    
    /** The file objects. */
    private List<SignFileObjectRequestDto> fileObjects;

    /**
     * @return the fileObjects
     */
    public List<SignFileObjectRequestDto> getFileObjects() {
        return fileObjects;
    }

    /**
     * @param fileObjects the fileObjects to set
     */
    public void setFileObjects(List<SignFileObjectRequestDto> fileObjects) {
        this.fileObjects = fileObjects;
    }

    @Override
    public String toString() {
        return "SignMemberRequestDto [fileObjects=" + fileObjects + ", firstname=" + getFirstname() + ", lastname=" + getLastname()
                + ", email=" + getEmail() + ", phone=" + getPhone() + ", user=" + getUser() + ", internal=" + getInternal() + ", id=" + getId() + "]";
    }
}
