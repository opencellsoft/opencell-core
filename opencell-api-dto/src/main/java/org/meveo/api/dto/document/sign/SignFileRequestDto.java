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

import java.util.Arrays;
import java.util.List;

import org.meveo.api.dto.BaseEntityDto;

/**
 * DTO class for a document request informations.
 */
public class SignFileRequestDto  extends BaseEntityDto {
    
    /** The Constant serialVersionUID. */ 
    private static final long serialVersionUID = 1L; 
    
    /** The id. */
    private String id;

    /** The name. */
    private String name;
    
    /** The file path. */
    private String filePath;

    /** The File order */
    private int position;
    
    /** The internal position. */
    @Deprecated
    private String internalPosition;
    
    /** The external position. */
    @Deprecated
    private String externalPosition;
    
    /** The internal page. */
    @Deprecated
    private int internalPage;
    
    /** The external page. */
    private int externalPage;
    
    /** The content. */
    private byte[] content;
    
    /** 
     * The list external positions.
     * List of SignFileObjectRequestDto items , encapsulating positions and pages for each file on which the signature will be put
     *  */
    private List<SignFileObjectRequestDto> listExternalPositions;
    
    /**
     * Gets the name.
     *
     * @return the name
     */
    public String getName() {
        return name;
    }
    
    /**
     * Gets the file path.
     *
     * @return the filePath
     */
    public String getFilePath() {
        return filePath;
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
     * Sets the file path.
     *
     * @param filePath the filePath to set
     */
    public void setFilePath(String filePath) {
        this.filePath = filePath;
    }

    /**
     * Gets the content.
     *
     * @return the content
     */
    public byte[] getContent() {
        return content;
    }

    /**
     * Sets the content.
     *
     * @param content the content to set
     */
    public void setContent(byte[] content) {
        this.content = content;
    }

    /**
     * Gets the id.
     *
     * @return the id
     */
    public String getId() {
        return id;
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
     * @return the internalPosition
     */
    @Deprecated
    public String getInternalPosition() {
        return internalPosition;
    }

    /**
     * @return the externalPosition
     * Should be removed for the upcoming versions , and to be replaced by listExternalPositions
     */
    @Deprecated
    public String getExternalPosition() {
        return externalPosition;
    }

    /**
     * @return the internalPage
     */
    public int getInternalPage() {
        return internalPage;
    }

    /**
     * @return the externalPage
     */
    public int getExternalPage() {
        return externalPage;
    }

    /**
     * @param internalPosition the internalPosition to set
     */
    public void setInternalPosition(String internalPosition) {
        this.internalPosition = internalPosition;
    }

    /**
     * @param externalPosition the externalPosition to set
     */
    public void setExternalPosition(String externalPosition) {
        this.externalPosition = externalPosition;
    }

    /**
     * @param internalPage the internalPage to set
     */
    public void setInternalPage(int internalPage) {
        this.internalPage = internalPage;
    }

    /**
     * @param externalPage the externalPage to set
     */
    public void setExternalPage(int externalPage) {
        this.externalPage = externalPage;
    }

    /**
     * @return the listExternalPositions
     */
    public List<SignFileObjectRequestDto> getListExternalPositions() {
        return listExternalPositions;
    }

    /**
     * @param listExternalPositions the listExternalPositions to set
     */
    public void setListExternalPositions(List<SignFileObjectRequestDto> listExternalPositions) {
        this.listExternalPositions = listExternalPositions;
    }

    public int getPosition() {
        return position;
    }

    public void setPosition(int position) {
        this.position = position;
    }

    @Override
    public String toString() {
        return "SignFileRequestDto [id=" + id + ", name=" + name + ", filePath=" + filePath + ", position=" + position + ", internalPosition=" + internalPosition
                + ", externalPosition=" + externalPosition + ", internalPage=" + internalPage + ", externalPage=" + externalPage + ", content=" + Arrays.toString(content)
                + ", listExternalPositions=" + listExternalPositions + "]";
    }
}
