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

/**
 * 
 */
package org.meveo.admin.async;

/**
 * The Class FlatFileAsyncUnitResponse contains a single the asynchronous response.
 * 
 * @author anasseh
 * @lastModifiedVersion willBeSetLater
 */
public class FlatFileAsyncUnitResponse {
    
    /** The line record. */
    private String lineRecord;
    
    /** The reason. */
    private String reason;
    
    /** The line number. */
    private long lineNumber =0;
    
    /** The success. */
    private boolean success  = false;
    
    /**
     * Instantiates a new flat file async unit response.
     */
    public FlatFileAsyncUnitResponse() {
        
    }
    
    /**
     * Checks if is success.
     *
     * @return the success
     */
    public boolean isSuccess() {
        return success;
    }

    /**
     * Sets the success.
     *
     * @param success the success to set
     */
    public void setSuccess(boolean success) {
        this.success = success;
    }

    
    /**
     * Gets the line record.
     *
     * @return the lineRecord
     */
    public String getLineRecord() {
        return lineRecord;
    }

    /**
     * Sets the line record.
     *
     * @param lineRecord the lineRecord to set
     */
    public void setLineRecord(String lineRecord) {
        this.lineRecord = lineRecord;
    }

    /**
     * Gets the reason.
     *
     * @return the reason
     */
    public String getReason() {
        return reason;
    }
    
    /**
     * Sets the reason.
     *
     * @param reason the reason to set
     */
    public void setReason(String reason) {
        this.reason = reason;
    }

    /**
     * Gets the line number.
     *
     * @return the lineNumber
     */
    public long getLineNumber() {
        return lineNumber;
    }

    /**
     * Sets the line number.
     *
     * @param lineNumber the lineNumber to set
     */
    public void setLineNumber(long lineNumber) {
        this.lineNumber = lineNumber;
    }

    @Override
    public String toString() {
        return "FlatFileAsyncUnitResponse [lineRecord=" + lineRecord + ", reason=" + reason + ", lineNumber=" + lineNumber + ", success=" + success + "]";
    }
   
}
