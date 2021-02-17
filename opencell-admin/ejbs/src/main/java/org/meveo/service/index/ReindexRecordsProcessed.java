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

package org.meveo.service.index;

import java.io.Serializable;

/**
 * Tracks reindexing records processed - total and successful
 * 
 * @author Andrius Karpavicius
 */
@Deprecated
public class ReindexRecordsProcessed implements Serializable {

    private static final long serialVersionUID = 5047323028071238082L;

    public ReindexRecordsProcessed() {
    }

    /**
     * Constructor
     * 
     * @param total Total records indexed
     * @param failed Number of records failed
     */
    public ReindexRecordsProcessed(int total, int failed) {
        this.total = total;
        this.successfull = total - failed;
    }

    /**
     * Total number of records
     */
    private int total;

    /**
     * Number of successful records
     */
    private int successfull;

    /**
     * @return Total number of records
     */
    public int getTotal() {
        return total;
    }

    /**
     * @param total Total number of records
     */
    public void setTotal(int total) {
        this.total = total;
    }

    /**
     * @return Number of successful records
     */
    public int getSuccessfull() {
        return successfull;
    }

    /**
     * @param successfull Number of successful records
     */
    public void setSuccessfull(int successfull) {
        this.successfull = successfull;
    }

    /**
     * Update statistics with additional information
     * 
     * @param addTotal Additional total number of records processed
     * @param addFailed Additional number of records failed
     */
    public void updateStatistics(int addTotal, int addFailed) {
        total = total + addTotal;
        successfull = successfull + addTotal - addFailed;
    }

    /**
     * Update statistics
     * 
     * @param recordInfo Reindexing records processed info - total and successful
     */
    public void updateStatistics(ReindexRecordsProcessed recordInfo) {
        total = total + recordInfo.getTotal();
        successfull = successfull + recordInfo.getSuccessfull();
    }

    @Override
    public String toString() {
        return successfull + "/" + total;
    }
}