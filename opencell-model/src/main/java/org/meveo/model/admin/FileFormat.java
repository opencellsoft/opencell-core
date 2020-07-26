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
package org.meveo.model.admin;

import org.hibernate.annotations.GenericGenerator;
import org.hibernate.annotations.Parameter;
import org.hibernate.annotations.Type;
import org.meveo.model.BusinessEntity;
import org.meveo.model.ExportIdentifier;

import javax.persistence.Cacheable;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.JoinColumn;
import javax.persistence.JoinTable;
import javax.persistence.ManyToMany;
import javax.persistence.Table;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

/**
 * File format entity
 *
 * @author Abdellatif BARI
 * @since 7.3.0
 */
@Entity
@Cacheable
@ExportIdentifier("code")
@Table(name = "adm_file_format")
@GenericGenerator(name = "ID_GENERATOR", strategy = "org.hibernate.id.enhanced.SequenceStyleGenerator", parameters = {@Parameter(name = "sequence_name", value = "adm_file_format_seq"),})
public class FileFormat extends BusinessEntity {

    private static final long serialVersionUID = 1932955932186440723L;

    /**
     * File name pattern with which we control the file name.
     */
    @Column(name = "file_name_pattern", length = 255)
    @Size(max = 255)
    private String fileNamePattern;

    /**
     * Indicates if file name uniqueness is required.
     */
    @Type(type = "numeric_boolean")
    @Column(name = "file_name_uniqueness")
    private boolean fileNameUniqueness;

    /**
     * File type.
     */
    @ManyToMany(fetch = FetchType.LAZY)
    @JoinTable(name = "adm_file_format_file_type", joinColumns = @JoinColumn(name = "file_format_id", referencedColumnName = "id"), inverseJoinColumns = @JoinColumn(name = "file_type_id", referencedColumnName = "id"))
    private List<FileType> fileTypes = new ArrayList<>();

    /**
     * Configuration template.
     */
    @Column(name = "configuration_template", columnDefinition = "TEXT", nullable = false)
    private String configurationTemplate;

    /**
     * File name.
     */
    @Column(name = "record_name", length = 255)
    @Size(max = 255)
    private String recordName;

    /**
     * Input directory.
     */
    @Column(name = "input_directory", length = 255, nullable = false)
    @Size(max = 255)
    @NotNull
    private String inputDirectory;

    /**
     * Output directory.
     */
    @Column(name = "output_directory", length = 255)
    @Size(max = 255)
    private String outputDirectory;

    /**
     * Reject directory.
     */
    @Column(name = "reject_directory", length = 255)
    @Size(max = 255)
    private String rejectDirectory;

    /**
     * Archive directory.
     */
    @Column(name = "archive_directory", length = 255)
    @Size(max = 255)
    private String archiveDirectory;

    /**
     * Job name (e.g : CDR_job) to process file contents.
     */
    @Column(name = "job_code", length = 255)
    @Size(max = 255)
    private String jobCode;

    /**
     * Gets the code
     *
     * @return the code
     */
    public String getCode() {
        return code;
    }

    /**
     * Sets the code.
     *
     * @param code the new code
     */
    public void setCode(String code) {
        this.code = code;
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
    public boolean isFileNameUniqueness() {
        return fileNameUniqueness;
    }

    /**
     * Sets the fileNameUniqueness.
     *
     * @param fileNameUniqueness the fileNameUniqueness
     */
    public void setFileNameUniqueness(boolean fileNameUniqueness) {
        this.fileNameUniqueness = fileNameUniqueness;
    }

    /**
     * Gets the fileTypes
     *
     * @return the fileTypes
     */
    public List<FileType> getFileTypes() {
        return fileTypes;
    }

    /**
     * Sets the fileTypes.
     *
     * @param fileTypes the new fileTypes
     */
    public void setFileTypes(List<FileType> fileTypes) {
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

    @Override
    public boolean equals(Object o) {
        if (this == o)
            return true;
        if (o == null || getClass() != o.getClass())
            return false;
        FileFormat that = (FileFormat) o;
        return Objects.equals(code, that.code);
    }

    @Override
    public int hashCode() {
        return Objects.hash(super.hashCode(), code);
    }
}
