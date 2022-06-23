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

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang3.ArrayUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.ConditionalFormatting;
import org.apache.poi.ss.usermodel.ConditionalFormattingRule;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.util.CellRangeAddress;

/**
 * @author Hicham EL YOUSSOUFI
 * @lastModifiedVersion 5.1.1
 **/
public final class CopyRow {

    /**
     * Private Default Constructor.
     */
    private CopyRow() {
        super();
    }

    /**
     * Copy row from index from to index to.
     * 
     * @param worksheet Excel work sheet
     * @param from start index
     * @param to end index
     * @param cfMap Conditional Formatting Map
     */
    public static void copyRow(Sheet worksheet, int from, int to, Map<String, ConditionalFormatting> cfMap) {

        Row sourceRow = worksheet.getRow(from);
        Row newRow = worksheet.getRow(to);

        if (alreadyExists(newRow)) {
            worksheet.shiftRows(to, worksheet.getLastRowNum(), 1);
        } else {
            newRow = worksheet.createRow(to);
        }

        for (int i = 0; i < sourceRow.getLastCellNum(); i++) {

            Cell oldCell = sourceRow.getCell(i);
            Cell newCell = newRow.createCell(i);

            if (oldCell != null) {

                copyCellStyle(oldCell, newCell);
                copyCellComment(oldCell, newCell);
                copyCellHyperlink(oldCell, newCell);
                copyCellDataTypeAndValue(oldCell, newCell);

                ConditionalFormatting cf = cfMap.get(oldCell.getAddress().formatAsString());

                if (cf != null) {

                    CellRangeAddress newCellRangeAddress = CellRangeAddress.valueOf(newCell.getAddress().formatAsString());
                    CellRangeAddress[] newCellRangeAddressArray = ArrayUtils.add(cf.getFormattingRanges(), newCellRangeAddress);

                    List<ConditionalFormattingRule> rules = new ArrayList<>();
                    for (int j = 0; j < cf.getNumberOfRules(); j++) {
                        rules.add(cf.getRule(j));
                    }
                    worksheet.getSheetConditionalFormatting().addConditionalFormatting(newCellRangeAddressArray, rules.toArray(new ConditionalFormattingRule[rules.size()]));
                }
            }
        }

        copyAnyMergedRegions(worksheet, sourceRow, newRow);
        newRow.setHeight(sourceRow.getHeight());
    }

    /**
     * Copy cell style.
     * 
     * @param oldCell old Excel cell
     * @param newCell target Excel cell
     */
    private static void copyCellStyle(Cell oldCell, Cell newCell) {
        newCell.setCellStyle(oldCell.getCellStyle());
    }

    /**
     * Copy cell Comment.
     * 
     * @param oldCell old Excel cell
     * @param newCell target Excel cell
     */
    private static void copyCellComment(Cell oldCell, Cell newCell) {
        if (newCell.getCellComment() != null) {
            newCell.setCellComment(oldCell.getCellComment());
        }
    }

    /**
     * copy Cell Hyper link.
     * 
     * @param oldCell old Excel cell
     * @param newCell target Excel cell
     */
    private static void copyCellHyperlink(Cell oldCell, Cell newCell) {
        if (oldCell.getHyperlink() != null) {
            newCell.setHyperlink(oldCell.getHyperlink());
        }
    }

    /**
     * copy Cell Data Type And Value.
     * 
     * @param oldCell old Excel cell
     * @param newCell target Excel cell
     */
    private static void copyCellDataTypeAndValue(Cell oldCell, Cell newCell) {
        setCellDataType(oldCell, newCell);
        setCellDataValue(oldCell, newCell);
    }

    /**
     * set Cell Data Type.
     * 
     * @param oldCell old Excel cell
     * @param newCell target Excel cell
     */
    @SuppressWarnings("deprecation")
    private static void setCellDataType(Cell oldCell, Cell newCell) {
        newCell.setCellType(oldCell.getCellType());
    }

    /**
     * set Cell Data Value.
     * 
     * @param oldCell old Excel cell
     * @param newCell target Excel cell
     */
    private static void setCellDataValue(Cell oldCell, Cell newCell) {
        switch (oldCell.getCellType()) {
        case STRING:
            newCell.setCellValue(oldCell.getStringCellValue());
            break;
        case BOOLEAN:
            newCell.setCellValue(oldCell.getBooleanCellValue());
            break;
        case ERROR:
            newCell.setCellErrorValue(oldCell.getErrorCellValue());
            break;
        case FORMULA:
            newCell.setCellFormula(oldCell.getCellFormula());
            break;
        case NUMERIC:
            newCell.setCellValue(oldCell.getNumericCellValue());
            break;
        default:
            oldCell.setCellValue(StringUtils.EMPTY);
            break;
        }
    }

    /**
     * Checks if a row exists.
     * 
     * @param newRow Excel Row
     * @return true or false
     */
    private static boolean alreadyExists(Row newRow) {
        return newRow != null;
    }

    /**
     * copy Any Merged Regions.
     * 
     * @param worksheet Excel work sheet
     * @param sourceRow source Excel Row
     * @param newRow target Excel Row
     */
    private static void copyAnyMergedRegions(Sheet worksheet, Row sourceRow, Row newRow) {
        for (int i = 0; i < worksheet.getNumMergedRegions(); i++) {
            copyMergeRegion(worksheet, sourceRow, newRow, worksheet.getMergedRegion(i));
        }
    }

    /**
     * copy and Merge Region.
     * 
     * @param worksheet Excel work sheet
     * @param sourceRow source Excel Row
     * @param newRow target Excel Row
     * @param mergedRegion mergedRegion
     */
    private static void copyMergeRegion(Sheet worksheet, Row sourceRow, Row newRow, CellRangeAddress mergedRegion) {
        CellRangeAddress range = mergedRegion;
        if (range.getFirstRow() == sourceRow.getRowNum()) {
            int lastRow = newRow.getRowNum() + (range.getLastRow() - range.getFirstRow());
            CellRangeAddress newCellRangeAddress = new CellRangeAddress(newRow.getRowNum(), lastRow, range.getFirstColumn(), range.getLastColumn());
            worksheet.addMergedRegion(newCellRangeAddress);
        }
    }
}
