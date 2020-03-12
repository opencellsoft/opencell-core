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

package org.meveo.api.dto.response.utilities;

import java.util.Collection;
import java.util.Iterator;
import java.util.Map;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlRootElement;

import org.meveo.api.MeveoApiErrorCodeEnum;
import org.meveo.api.dto.ActionStatusEnum;
import org.meveo.api.dto.response.BaseResponse;

/**
 * The Class ImportExportResponseDto.
 *
 * @author Andrius Karpavicius
 */
@XmlRootElement(name = "ImportExportResponse")
@XmlAccessorType(XmlAccessType.FIELD)
public class ImportExportResponseDto extends BaseResponse {

    /** The Constant serialVersionUID. */
    private static final long serialVersionUID = 1L;

    /** The execution id. */
    private String executionId;

    /** Summary of entities imported/exported per entity class. */
    private Map<String, Integer> summary = null;

    /** Stores a list of field names that were not imported because of differences between original and current model - fields do not exist in current model. */
    private Map<String, FieldsNotImportedStringCollectionDto> fieldsNotImported = null;

    /** Occurred exception. */
    private String exceptionMessage;

    /** Occurred error message key. */
    private String errorMessageKey;

    /**
     * Instantiates a new import export response dto.
     */
    public ImportExportResponseDto() {
        super();
    }

    /**
     * Instantiates a new import export response dto.
     *
     * @param status the status
     * @param errorCode the error code
     * @param message the message
     */
    public ImportExportResponseDto(ActionStatusEnum status, MeveoApiErrorCodeEnum errorCode, String message) {
        super(status, errorCode, message);
    }

    /**
     * Instantiates a new import export response dto.
     *
     * @param executionId the execution id
     */
    public ImportExportResponseDto(String executionId) {
        super();
        this.executionId = executionId;
    }

    /**
     * Gets the execution id.
     *
     * @return the execution id
     */
    public String getExecutionId() {
        return executionId;
    }

    /**
     * Sets the execution id.
     *
     * @param executionId the new execution id
     */
    public void setExecutionId(String executionId) {
        this.executionId = executionId;
    }

    /**
     * Gets the summary.
     *
     * @return the summary
     */
    public Map<String, Integer> getSummary() {
        return summary;
    }

    /**
     * Sets the summary.
     *
     * @param summary the summary
     */
    public void setSummary(Map<String, Integer> summary) {
        this.summary = summary;
    }

    /**
     * Gets the exception message.
     *
     * @return the exception message
     */
    public String getExceptionMessage() {
        return exceptionMessage;
    }

    /**
     * Sets the exception message.
     *
     * @param exceptionMessage the new exception message
     */
    public void setExceptionMessage(String exceptionMessage) {
        this.exceptionMessage = exceptionMessage;
    }

    /**
     * Checks if is done.
     *
     * @return true, if is done
     */
    public boolean isDone() {
        return exceptionMessage != null || summary != null;
    }


    /**
     * Determine if request has failed.
     * 
     * @return true/false
     */
    public boolean isFailed() {
        return exceptionMessage != null || errorMessageKey != null || getActionStatus().getStatus() == ActionStatusEnum.FAIL;
    }

    /**
     * Get a failure message as a message file key.
     * 
     * @return error message key
     */
    public String getFailureMessageKey() {
        return errorMessageKey;
    }

    /**
     * Set a failure message as a message file key.
     * 
     * @param messageKey key of message.
     */
    public void setFailureMessageKey(String messageKey) {
        errorMessageKey = messageKey;
    }

    /**
     * Get a failure message as a complete message.
     * 
     * @return failure message.
     */
    public String getFailureMessage() {
        if (errorMessageKey != null) {
            return null;
        }
        if (exceptionMessage != null) {
            return exceptionMessage;
        } else {
            return getActionStatus().getMessage();
        }
    }

    /**
     * Gets the fields not imported.
     *
     * @return the fields not imported
     */
    public Map<String, FieldsNotImportedStringCollectionDto> getFieldsNotImported() {
        return fieldsNotImported;
    }

    /**
     * Sets the fields not imported.
     *
     * @param fieldsNotImported the fields not imported
     */
    public void setFieldsNotImported(Map<String, FieldsNotImportedStringCollectionDto> fieldsNotImported) {
        this.fieldsNotImported = fieldsNotImported;
    }
    
    /**
     * To string.
     *
     * @param collection collection
     * @param maxLen max length
     * @return displayed string.
     */
    private String toString(Collection<?> collection, int maxLen) {
        StringBuilder builder = new StringBuilder();
        builder.append("[");
        int i = 0;
        for (Iterator<?> iterator = collection.iterator(); iterator.hasNext() && i < maxLen; i++) {
            if (i > 0) {
                builder.append(", ");
            }
            builder.append(iterator.next());
        }
        builder.append("]");
        return builder.toString();
    }
    
    @Override
    public String toString() {
        final int maxLen = 10;
        return String.format("ImportExportResponseDto [executionId=%s, summary=%s, fieldsNotImported=%s, exception=%s]", executionId,
            summary != null ? toString(summary.entrySet(), maxLen) : null, fieldsNotImported != null ? toString(fieldsNotImported.entrySet(), maxLen) : null, exceptionMessage);
    }
}