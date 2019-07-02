/*
 * (C) Copyright 2015-2016 Opencell SAS (http://opencellsoft.com/) and contributors.
 * (C) Copyright 2009-2014 Manaty SARL (http://manaty.net/) and contributors.
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Affero General Public License as
 * published by the Free Software Foundation, either version 3 of the
 * License, or (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.
 * This program is not suitable for any direct or indirect application in MILITARY industry
 * See the GNU Affero General Public License for more details.
 *
 * You should have received a copy of the GNU Affero General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package org.meveo.model.admin;

import org.hibernate.annotations.GenericGenerator;
import org.hibernate.annotations.Parameter;
import org.meveo.model.AuditableEntity;
import org.meveo.model.ExportIdentifier;

import javax.persistence.Cacheable;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
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
@GenericGenerator(name = "ID_GENERATOR", strategy = "org.hibernate.id.enhanced.SequenceStyleGenerator", parameters = {
        @Parameter(name = "sequence_name", value = "adm_file_format_seq"), })
public class FileFormat extends AuditableEntity {

    private static final long serialVersionUID = 1932955932186440723L;

    /**
     * File code e.g. CDR for CDR File.
     */
    @Column(name = "code", length = 10, nullable = false, unique = true)
    @Size(max = 10, min = 1)
    @NotNull
    private String code;

    /**
     * File name pattern with which we control the file name.
     */
    @Column(name = "file_name_pattern", length = 255)
    @Size(max = 255)
    private String fileNamePattern;

    /**
     * File name.
     */
    @Column(name = "file_type", length = 255)
    @Size(max = 255)
    private String fileType;

    /**
     * Configuration template.
     */
    @Column(name = "configuration_template", length = 4000)
    @Size(max = 4000)
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
     * Gets the fileType
     *
     * @return the fileType
     */
    public String getFileType() {
        return fileType;
    }

    /**
     * Sets the fileType.
     *
     * @param fileType the new fileType
     */
    public void setFileType(String fileType) {
        this.fileType = fileType;
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
