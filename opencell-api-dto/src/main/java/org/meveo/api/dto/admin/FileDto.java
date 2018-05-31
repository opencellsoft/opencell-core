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