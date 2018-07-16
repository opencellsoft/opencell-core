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
     * @return
     */
    public int getIndex() {
        return index;
    }

    /**
     * @param index
     */
    public void setIndex(int index) {
        this.index = index;
    }

    /**
     * @return
     */
    public int getRowFrom() {
        return rowFrom;
    }

    /**
     * @param rowFrom
     */
    public void setRowFrom(int rowFrom) {
        this.rowFrom = rowFrom;
    }

    /**
     * @return
     */
    public int getNumberOfRows() {
        return numberOfRows;
    }

    /**
     * @param numberOfRows
     */
    public void setNumberOfRows(int numberOfRows) {
        this.numberOfRows = numberOfRows;
    }

    /**
     * @return
     */
    public boolean isForceFormulaRecalculation() {
        return forceFormulaRecalculation;
    }

    /**
     * @param forceFormulaRecalculation
     */
    public void setForceFormulaRecalculation(boolean forceFormulaRecalculation) {
        this.forceFormulaRecalculation = forceFormulaRecalculation;
    }

    /**
     * @return
     */
    public Map<String, Object[][]> getDatas() {
        return datas;
    }

    /**
     * @param datas
     */
    public void setDatas(Map<String, Object[][]> datas) {
        this.datas = datas;
    }

}
