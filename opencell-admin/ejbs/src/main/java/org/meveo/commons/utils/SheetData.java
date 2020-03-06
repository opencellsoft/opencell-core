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
package org.meveo.commons.utils;

import java.util.Map;

/**
 * @author Hicham EL YOUSSOUFI
 * @lastModifiedVersion 5.1.1
 **/
public class SheetData {
    /**
     * index of the sheet.
     */
    private int index;
    /**
     * if we have a datatable, rowFrom = row number of the first line.
     */
    private int rowFrom = -1;
    /**
     * number of datatable rows.
     */
    private int numberOfRows;
    /**
     * refresh graphs.
     */
    private boolean forceFormulaRecalculation;
    /**
     * map to store data.
     */
    private Map<String, Object[][]> datas;

    /**
     * gets current index.
     * 
     * @return current index
     */
    public int getIndex() {
        return index;
    }

    /**
     * sets current index.
     * 
     * @param pIndex index
     */
    public void setIndex(int pIndex) {
        this.index = pIndex;
    }

    /**
     * get Row From.
     * 
     * @return current rowFrom
     */
    public int getRowFrom() {
        return rowFrom;
    }

    /**
     * set Row From.
     * 
     * @param pRowFrom row From
     */
    public void setRowFrom(int pRowFrom) {
        this.rowFrom = pRowFrom;
    }

    /**
     * Gets Number Of Rows.
     * 
     * @return number Of Rows
     */
    public int getNumberOfRows() {
        return numberOfRows;
    }

    /**
     * Sets Number Of Rows.
     * 
     * @param pNumberOfRows number Of Rows
     */
    public void setNumberOfRows(int pNumberOfRows) {
        this.numberOfRows = pNumberOfRows;
    }

    /**
     * Checks if we have to Force Formula Recalculation.
     * 
     * @return true or false
     */
    public boolean isForceFormulaRecalculation() {
        return forceFormulaRecalculation;
    }

    /**
     * Sets if we have to Force Formula Recalculation.
     * 
     * @param pForceFormulaRecalculation force Formula Recalculation
     */
    public void setForceFormulaRecalculation(boolean pForceFormulaRecalculation) {
        this.forceFormulaRecalculation = pForceFormulaRecalculation;
    }

    /**
     * Gets Matrix Datas.
     * 
     * @return Data Matrix
     */
    public Map<String, Object[][]> getDatas() {
        return datas;
    }

    /**
     * Sets Matrix Datas.
     * 
     * @param pDatas Data Matrix.
     */
    public void setDatas(Map<String, Object[][]> pDatas) {
        this.datas = pDatas;
    }

}
