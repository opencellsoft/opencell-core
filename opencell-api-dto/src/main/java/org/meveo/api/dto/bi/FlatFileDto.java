package org.meveo.api.dto.bi;

import java.io.Serializable;

import javax.validation.constraints.Size;

import org.meveo.api.dto.BusinessEntityDto;
import org.meveo.model.admin.FileFormat;
import org.meveo.model.bi.FileStatusEnum;

public class FlatFileDto extends BusinessEntityDto implements Serializable {

    private static final long serialVersionUID = 2755942776843805138L;

    /**
     * File original name.
     */
    @Size(max = 255)
    private String fileOriginalName;

    /**
     * File current name.
     */
    @Size(max = 255)
    private String fileCurrentName;

    /**
     * Current directory.
     */
    @Size(max = 255)
    private String currentDirectory;

    /**
     * File status
     */
    private FileStatusEnum status;

    /**
     * File Format
     */
    private FileFormat fileFormat;

    /**
     * Rejection reason
     */
    private String errorMessage;

    /**
     * Number of lines in success
     */
    protected Integer linesInSuccess;

    /**
     * Number of lines in warning
     */
    protected Integer linesInWarning;

    /**
     * Number of lines in error
     */
    protected Integer linesInError;

    /**
     * Number of processing attempts
     */
    protected Integer processingAttempts;

    /**
     * Flat file job name (e.g : CDR_job).
     */
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
     * Gets the currentDirectory
     *
     * @return the currentDirectory
     */
    public String getCurrentDirectory() {
        return currentDirectory;
    }

    /**
     * Sets the currentDirectory.
     *
     * @param currentDirectory the new currentDirectory
     */
    public void setCurrentDirectory(String currentDirectory) {
        this.currentDirectory = currentDirectory;
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