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

package org.meveo.commons.parsers;

public class RecordContext {
    int lineNumber = -1;
    String lineContent = null;
    Object record = null;

    /**
     * When line is rejected, exception of reject
     */
    private Exception rejectReason = null;

    public RecordContext() {

    }

    /**
     * @return the lineNumber
     */
    public int getLineNumber() {
        return lineNumber;
    }

    /**
     * @param lineNumber the lineNumber to set
     */
    public void setLineNumber(int lineNumber) {
        this.lineNumber = lineNumber;
    }

    /**
     * @return the lineContent
     */
    public String getLineContent() {
        return lineContent;
    }

    /**
     * @param lineContent the lineContent to set
     */
    public void setLineContent(String lineContent) {
        this.lineContent = lineContent;
    }

    /**
     * @return the record
     */
    public Object getRecord() {
        return record;
    }

    /**
     * @param record the record to set
     */
    public void setRecord(Object record) {
        this.record = record;
    }

    /**
     * @return When line is rejected, exception of reject
     */
    public Exception getRejectReason() {
        return rejectReason;
    }

    /**
     * @param rejectReason When line is rejected, exception of reject
     */
    public void setRejectReason(Exception rejectReason) {
        this.rejectReason = rejectReason;
    }
}