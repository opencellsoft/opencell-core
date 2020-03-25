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

/** 
 * A Dto holding inputs to create a document signature procedure from OC side.
 * 
 * @author Said Ramli 
 */ 
public class CreateProcedureRequestDto  extends BaseEntityDto {

    /** The Constant serialVersionUID. */ 
    private static final long serialVersionUID = 1L; 
    
    /** The absolute paths. */
    private boolean absolutePaths;
    
    /** if true an internal member will be added to the procedure with internal member. */
    private boolean withInternalMember;
    
    /** List of files to be signed. it will be used to created the procedure */
    private List<SignFileRequestDto> filesToSign;
    
    /** The Signature procedure. */
    private SignProcedureDto procedure;
    
    /**
     * Gets the procedure.
     *
     * @return the procedure
     */
    public SignProcedureDto getProcedure() {
        return procedure;
    }

    /**
     * Sets the procedure.
     *
     * @param procedure the procedure to set
     */
    public void setProcedure(SignProcedureDto procedure) {
        this.procedure = procedure;
    }

    /**
     * Gets the files to sign.
     *
     * @return the filesToSign
     */
    public List<SignFileRequestDto> getFilesToSign() {
        return filesToSign;
    }

    /**
     * Sets the files to sign.
     *
     * @param filesToSign the filesToSign to set
     */
    public void setFilesToSign(List<SignFileRequestDto> filesToSign) {
        this.filesToSign = filesToSign;
    }

    /**
     * Checks if is with internal member.
     *
     * @return the withInternalMember
     */
    public boolean isWithInternalMember() {
        return withInternalMember;
    }

    /**
     * Sets the with internal member.
     *
     * @param withInternalMember the withInternalMember to set
     */
    public void setWithInternalMember(boolean withInternalMember) {
        this.withInternalMember = withInternalMember;
    }

    /**
     * @return the absolutePaths
     */
    public boolean isAbsolutePaths() {
        return absolutePaths;
    }

    /**
     * @param absolutePaths the absolutePaths to set
     */
    public void setAbsolutePaths(boolean absolutePaths) {
        this.absolutePaths = absolutePaths;
    }
}
