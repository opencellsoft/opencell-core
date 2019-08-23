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
package org.meveo.model.bi;

import org.hibernate.annotations.GenericGenerator;
import org.hibernate.annotations.Parameter;
import org.meveo.model.BusinessEntity;
import org.meveo.model.admin.FileFormat;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.FetchType;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.persistence.UniqueConstraint;
import javax.validation.constraints.Size;

/**
 * Flat file entity
 *
 * @author Abdellatif BARI
 * @since 7.3.0
 */
@Entity
@Table(name = "flat_file", uniqueConstraints = @UniqueConstraint(columnNames = { "code" }))
@GenericGenerator(name = "ID_GENERATOR", strategy = "org.hibernate.id.enhanced.SequenceStyleGenerator", parameters = {
        @Parameter(name = "sequence_name", value = "flat_file_seq"), })
public class FlatFile extends BusinessEntity {

    private static final long serialVersionUID = -4989724064567423956L;

    /**
     * File original name.
     */
    @Column(name = "file_original_name", length = 255)
    @Size(max = 255)
    private String fileOriginalName;


    /**
     * File current name.
     */
    @Column(name = "file_current_name", length = 255)
    @Size(max = 255)
    private String fileCurrentName;

    /**
     * File status
     */
    @Enumerated(EnumType.STRING)
    @Column(name = "status")
    private FileStatusEnum status = FileStatusEnum.WELL_FORMED;

    /**
     * File Format
     */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "file_format_id")
    private FileFormat fileFormat;

    /**
     * Rejection reason
     */
    @Column(name = "error_message", columnDefinition = "TEXT")
    private String errorMessage;

    /**
     * Number of lines in success
     */
    @Column(name = "lines_in_success")
    protected Integer linesInSuccess;

    /**
     * Number of lines in warning
     */
    @Column(name = "lines_in_warning")
    protected Integer linesInWarning;

    /**
     * Number of lines in error
     */
    @Column(name = "lines_in_error")
    protected Integer linesInError;

    /**
     * Number of processing attempts
     */
    @Column(name = "processing_attempts")
    protected Integer processingAttempts;

    /**
     * Flat file job name (e.g : CDR_job).
     */
    @Column(name = "flat_file_job_code", length = 255)
    @Size(max = 255)
    private String flatFileJobCode;

    /**
     * Gets the fileOriginalName
     *
     * @return the fileOriginalName
     */
    public String getFileOriginalName() {
        return fileOriginalName;
    }

    /**
     * Sets the fileOriginalName.
     *
     * @param fileOriginalName the new fileOriginalName
     */
    public void setFileOriginalName(String fileOriginalName) {
        this.fileOriginalName = fileOriginalName;
    }

    /**
     * Gets the fileCurrentName
     *
     * @return the fileCurrentName
     */
    public String getFileCurrentName() {
        return fileCurrentName;
    }

    /**
     * Sets the fileCurrentName.
     *
     * @param fileCurrentName the new fileCurrentName
     */
    public void setFileCurrentName(String fileCurrentName) {
        this.fileCurrentName = fileCurrentName;
    }

    /**
     * Gets the status
     *
     * @return the status
     */
    public FileStatusEnum getStatus() {
        return status;
    }

    /**
     * Sets the status.
     *
     * @param status the new status
     */
    public void setStatus(FileStatusEnum status) {
        this.status = status;
    }

    /**
     * Gets the fileFormat
     *
     * @return the fileFormat
     */
    public FileFormat getFileFormat() {
        return fileFormat;
    }

    /**
     * Sets the fileFormat.
     *
     * @param fileFormat the new fileFormat
     */
    public void setFileFormat(FileFormat fileFormat) {
        this.fileFormat = fileFormat;
    }

    /**
     * Gets the errorMessage
     *
     * @return the errorMessage
     */
    public String getErrorMessage() {
        return errorMessage;
    }

    /**
     * Sets the errorMessage.
     *
     * @param errorMessage the new errorMessage
     */
    public void setErrorMessage(String errorMessage) {
        this.errorMessage = errorMessage;
    }

    /**
     * Gets the linesInSuccess
     *
     * @return the linesInSuccess
     */
    public Integer getLinesInSuccess() {
        return linesInSuccess;
    }

    /**
     * Sets the linesInSuccess.
     *
     * @param linesInSuccess the new linesInSuccess
     */
    public void setLinesInSuccess(Integer linesInSuccess) {
        this.linesInSuccess = linesInSuccess;
    }

    /**
     * Gets the linesInWarning
     *
     * @return the linesInWarning
     */
    public Integer getLinesInWarning() {
        return linesInWarning;
    }

    /**
     * Sets the linesInWarning.
     *
     * @param linesInWarning the new linesInWarning
     */
    public void setLinesInWarning(Integer linesInWarning) {
        this.linesInWarning = linesInWarning;
    }

    /**
     * Gets the linesInError
     *
     * @return the linesInError
     */
    public Integer getLinesInError() {
        return linesInError;
    }

    /**
     * Sets the linesInError.
     *
     * @param linesInError the new linesInError
     */
    public void setLinesInError(Integer linesInError) {
        this.linesInError = linesInError;
    }

    /**
     * Gets the processingAttempts
     *
     * @return the processingAttempts
     */
    public Integer getProcessingAttempts() {
        return processingAttempts;
    }

    /**
     * Sets the processingAttempts.
     *
     * @param processingAttempts the new processingAttempts
     */
    public void setProcessingAttempts(Integer processingAttempts) {
        this.processingAttempts = processingAttempts;
    }

    /**
     * Gets the flatFileJobCode
     *
     * @return the flatFileJobCode
     */
    public String getFlatFileJobCode() {
        return flatFileJobCode;
    }

    /**
     * Sets the flatFileJobCode.
     *
     * @param flatFileJobCode the new flatFileJobCode
     */
    public void setFlatFileJobCode(String flatFileJobCode) {
        this.flatFileJobCode = flatFileJobCode;
    }
}
