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

import org.meveo.api.dto.BusinessEntityDto;
import org.meveo.api.dto.CustomFieldsDto;
import org.meveo.model.admin.FileFormat;
import org.meveo.model.admin.FileType;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlRootElement;
import java.util.ArrayList;
import java.util.List;

/**
 * The Class FileFormatDto.
 *
 * @author Abdellatif BARI
 * @since 8.0.0
 */
@XmlRootElement(name = "FileFormat")
@XmlAccessorType(XmlAccessType.FIELD)
public class FileFormatDto extends BusinessEntityDto {

    /**
     * The Constant serialVersionUID.
     */
    private static final long serialVersionUID = -2539099102487957375L;

    /**
     * The file name pattern.
     */
    private String fileNamePattern;

    /**
     * Indicates if file name uniqueness is required.
     */
    private Boolean fileNameUniqueness;

    /**
     * The file type codes.
     */
    private List<String> fileTypes = new ArrayList<>();

    /**
     * The configuration template.
     */
    private String configurationTemplate;

    /**
     * The record name.
     */
    private String recordName;

    /**
     * The input directory.
     */
    private String inputDirectory;

    /**
     * The output directory.
     */
    private String outputDirectory;

    /**
     * The reject directory.
     */
    private String rejectDirectory;

    /**
     * The archive directory.
     */
    private String archiveDirectory;

    /**
     * Job name (e.g : CDR_job) to process file contents
     */
    private String jobCode;

    /**
     * Constructor
     */
    public FileFormatDto() {
        super();
    }

    /**
     * Constructor
     *
     * @param fileFormat           File format entity to convert to DTO
     * @param customFieldInstances Custom field values. Not applicable here.
     */
    public FileFormatDto(FileFormat fileFormat, CustomFieldsDto customFieldInstances) {
        super(fileFormat);
        this.archiveDirectory = fileFormat.getArchiveDirectory();
        this.configurationTemplate = fileFormat.getConfigurationTemplate();
        this.fileNamePattern = fileFormat.getFileNamePattern();
        this.fileNameUniqueness = fileFormat.isFileNameUniqueness();
        this.inputDirectory = fileFormat.getInputDirectory();
        this.jobCode = fileFormat.getJobCode();
        this.outputDirectory = fileFormat.getOutputDirectory();
        this.recordName = fileFormat.getRecordName();
        this.rejectDirectory = fileFormat.getRejectDirectory();

        if (fileFormat.getFileTypes() != null && !fileFormat.getFileTypes().isEmpty()) {
            this.fileTypes = new ArrayList<>();
            for (FileType fileType : fileFormat.getFileTypes()) {
                this.fileTypes.add(fileType.getCode());
            }
        }
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
     * Gets the fileNamePattern
     *
     * @return the fileNamePattern
     */
    public String getFileNamePattern() {
        return fileNamePattern;
    }

    /**
     * Sets the fileNamePattern.
     *
     * @param fileNamePattern the new fileNamePattern
     */
    public void setFileNamePattern(String fileNamePattern) {
        this.fileNamePattern = fileNamePattern;
    }

    /**
     * Gets the fileNameUniqueness
     *
     * @return the fileNameUniqueness
     */
    public Boolean isFileNameUniqueness() {
        return fileNameUniqueness;
    }

    /**
     * Sets the fileNameUniqueness.
     *
     * @param fileNameUniqueness the fileNameUniqueness
     */
    public void setFileNameUniqueness(Boolean fileNameUniqueness) {
        this.fileNameUniqueness = fileNameUniqueness;
    }

    /**
     * Gets the fileTypes
     *
     * @return the fileTypes
     */
    public List<String> getFileTypes() {
        return fileTypes;
    }

    /**
     * Sets the fileTypes.
     *
     * @param fileTypes the new fileTypes
     */
    public void setFileTypes(List<String> fileTypes) {
        this.fileTypes = fileTypes;
    }

    /**
     * Gets the configurationTemplate
     *
     * @return the configurationTemplate
     */
    public String getConfigurationTemplate() {
        return configurationTemplate;
    }

    /**
     * Sets the configurationTemplate.
     *
     * @param configurationTemplate the new configurationTemplate
     */
    public void setConfigurationTemplate(String configurationTemplate) {
        this.configurationTemplate = configurationTemplate;
    }

    /**
     * Gets the recordName
     *
     * @return the recordName
     */
    public String getRecordName() {
        return recordName;
    }

    /**
     * Sets the recordName.
     *
     * @param recordName the new recordName
     */
    public void setRecordName(String recordName) {
        this.recordName = recordName;
    }

    /**
     * Gets the inputDirectory
     *
     * @return the inputDirectory
     */
    public String getInputDirectory() {
        return inputDirectory;
    }

    /**
     * Sets the inputDirectory.
     *
     * @param inputDirectory the new inputDirectory
     */
    public void setInputDirectory(String inputDirectory) {
        this.inputDirectory = inputDirectory;
    }

    /**
     * Gets the outputDirectory
     *
     * @return the outputDirectory
     */
    public String getOutputDirectory() {
        return outputDirectory;
    }

    /**
     * Sets the outputDirectory.
     *
     * @param outputDirectory the new outputDirectory
     */
    public void setOutputDirectory(String outputDirectory) {
        this.outputDirectory = outputDirectory;
    }

    /**
     * Gets the rejectDirectory
     *
     * @return the rejectDirectory
     */
    public String getRejectDirectory() {
        return rejectDirectory;
    }

    /**
     * Sets the rejectDirectory.
     *
     * @param rejectDirectory the new rejectDirectory
     */
    public void setRejectDirectory(String rejectDirectory) {
        this.rejectDirectory = rejectDirectory;
    }

    /**
     * Gets the archiveDirectory
     *
     * @return the archiveDirectory
     */
    public String getArchiveDirectory() {
        return archiveDirectory;
    }

    /**
     * Sets the archiveDirectory.
     *
     * @param archiveDirectory the new archiveDirectory
     */
    public void setArchiveDirectory(String archiveDirectory) {
        this.archiveDirectory = archiveDirectory;
    }

    /**
     * @return Job name (e.g : CDR_job) to process file contents
     */
    public String getJobCode() {
        return jobCode;
    }

    /**
     * @param jobCode Job name (e.g : CDR_job) to process file contents
     */
    public void setJobCode(String jobCode) {
        this.jobCode = jobCode;
    }
}