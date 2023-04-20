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

package org.meveo.api.dto.admin;

import java.io.File;
import java.util.Date;

/**
 * The Class FileDto.
 * 
 * @author anasseh
 */
public class FileDto {

    /** The name. */
    private String name;
    
    /** The is directory. */
    private boolean isDirectory;
    
    /** The last modified. */
    private Date lastModified;

    /**
     * Instantiates a new file dto.
     */
    public FileDto() {

    }

    /**
     * Instantiates a new file dto.
     *
     * @param file the file
     */
    public FileDto(File file) {
        name = file.getName();
        isDirectory = file.isDirectory();
        lastModified = new Date(file.lastModified());
    }

    /**
     * Instantiates a new file dto.
     *
     * @param fileName the fileName
     */
    public FileDto(String fileName, Date lastModified) {
        if (fileName.endsWith("/")) {
            isDirectory = true;
            fileName = fileName.substring(0, fileName.length() - 1);
        }
        name = fileName;
        this.lastModified = lastModified;
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
     * Sets the name.
     *
     * @param name the new name
     */
    public void setName(String name) {
        this.name = name;
    }

    /**
     * Checks if is directory.
     *
     * @return true, if is directory
     */
    public boolean isDirectory() {
        return isDirectory;
    }

    /**
     * Sets the directory.
     *
     * @param isDirectory the new directory
     */
    public void setDirectory(boolean isDirectory) {
        this.isDirectory = isDirectory;
    }

    /**
     * Gets the last modified.
     *
     * @return the last modified
     */
    public Date getLastModified() {
        return lastModified;
    }

    /**
     * Sets the last modified.
     *
     * @param lastModified the new last modified
     */
    public void setLastModified(Date lastModified) {
        this.lastModified = lastModified;
    }

}