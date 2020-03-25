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
package org.meveo.model.scripts;

import java.io.Serializable;

public class ScriptInstanceError implements Serializable {

    private static final long serialVersionUID = -5517252645289726288L;

    private String message;

    private long lineNumber;

    private long columnNumber;

    private String sourceFile;

    /**
     * @return the message
     */
    public String getMessage() {
        return message;
    }

    /**
     * @param message the message to set
     */
    public void setMessage(String message) {
        this.message = message;
    }

    /**
     * @return the lineNumber
     */
    public long getLineNumber() {
        return lineNumber;
    }

    /**
     * @param lineNumber the lineNumber to set
     */
    public void setLineNumber(long lineNumber) {
        this.lineNumber = lineNumber;
    }

    /**
     * @return the columnNUmber
     */
    public long getColumnNumber() {
        return columnNumber;
    }

    /**
     * @param columnNUmber the columnNUmber to set
     */
    public void setColumnNumber(long columnNUmber) {
        this.columnNumber = columnNUmber;
    }

    /**
     * @return the sourceFile
     */
    public String getSourceFile() {
        return sourceFile;
    }

    /**
     * @param sourceFile the sourceFile to set
     */
    public void setSourceFile(String sourceFile) {
        this.sourceFile = sourceFile;
    }

    @Override
    public String toString() {
        return String.format("%s location %s:%s: %s", sourceFile, lineNumber, columnNumber, message);
    }
}