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

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.io.FileUtils;
import org.apache.poi.openxml4j.exceptions.InvalidFormatException;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.ConditionalFormatting;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.ss.usermodel.WorkbookFactory;
import org.apache.poi.ss.util.CellRangeAddress;

/**
 * @author Hicham EL YOUSSOUFI
 * @lastModifiedVersion 5.1.1
 **/
public final class ExcelExporter {

	/**
	 * Private Default Construct.
	 */
	private ExcelExporter() {
		super();
	}

	/**
	 * This function create and fill an Excel file (output) using the sheetData list
	 * and the excel template.
	 * 
	 * @param sheetDataList List of structured Data
	 * @param input         xls template
	 * @param output        xls output (final result report)
	 */
	public static void exportToExcel(List<SheetData> sheetDataList, File input, File output) {

		FileInputStream inputStream;
		Workbook workbook;

		try {
			inputStream = new FileInputStream(input);
			workbook = WorkbookFactory.create(inputStream);

		} catch (InvalidFormatException | IOException e) {
			throw new RuntimeException("Unable to create file", e);
		}

		if (sheetDataList != null && !sheetDataList.isEmpty()) {

			for (SheetData sheetData : sheetDataList) {

				Sheet sheet = workbook.getSheetAt(sheetData.getIndex());
				sheet.setForceFormulaRecalculation(sheetData.isForceFormulaRecalculation());

				Map<String, ConditionalFormatting> cfMap = getConditionalFormattingMap(sheet);

				// Dynamic data table feature
				// ====================================================================

				int sheetDataRowFrom = sheetData.getRowFrom();
				int sheetDataRowsNumber = sheetData.getNumberOfRows();

				if (sheetDataRowFrom > 0) {

					if (sheetDataRowsNumber == 1) {

						removeRow(sheet, sheetDataRowFrom);

					} else if (sheetDataRowsNumber == 0) {

						removeRow(sheet, sheetDataRowFrom);
						removeRow(sheet, sheetDataRowFrom - 1);

					} else {

						int oddRowsNumber = 0;
						if (sheetDataRowsNumber % 2 != 0) {
							oddRowsNumber = 1;
						}

						int halfSize = (sheetDataRowsNumber / 2) + oddRowsNumber - 1;

						for (int i = 0; i < halfSize; i++) {

							CopyRow.copyRow(sheet, (sheetDataRowFrom - 1), ((sheetDataRowFrom + 1) + 2 * i), cfMap);

							if (sheetDataRowsNumber % 2 == 0 || i < halfSize - 1) {
								CopyRow.copyRow(sheet, sheetDataRowFrom, ((sheetDataRowFrom + 2) + 2 * i), cfMap);
							}
						}
					}
				}
				// =========================================================

				for (String position : sheetData.getDatas().keySet()) {

					CellRangeAddress address = CellRangeAddress.valueOf(position);

					int rowStart = address.getFirstRow();
					int columnStart = address.getFirstColumn();

					if (sheetDataRowFrom > 0 && rowStart > sheetDataRowFrom) {
						rowStart = rowStart - 2 + sheetDataRowsNumber;
					}

					Object[][] datas = sheetData.getDatas().get(position);

					int rowIndex = rowStart;

					for (Object[] row : datas) {

						int columnIndex = columnStart;

						for (Object field : row) {

							Row currentRow = sheet.getRow(rowIndex);

							if (currentRow == null) {
								currentRow = sheet.createRow(rowIndex);
							}

							Cell cell = currentRow.getCell(columnIndex);

							if (cell == null) {
								cell = currentRow.createCell(columnIndex);
							}

							if (field instanceof Date) {
								cell.setCellValue((Date) field);
							} else if (field instanceof Boolean) {
								cell.setCellValue((Boolean) field);
							} else if (field instanceof String) {
								cell.setCellValue((String) field);
							} else if (field instanceof Double) {
								cell.setCellValue((Double) field);
							} else if (field instanceof Integer) {
								cell.setCellValue((Integer) field);
							}

							columnIndex++;
						}
						rowIndex++;
					}
				}
			}
			// Save to file
			try {
				inputStream.close();

				FileOutputStream outputStream = new FileOutputStream(output);
				workbook.write(outputStream);
				workbook.close();
				outputStream.close();
			} catch (Exception e) {
				throw new RuntimeException("Unable to export to Excel", e);
			}
		}
	}

	/**
	 * @param sheetDataList
	 * @param template
	 * @param output
	 */
	public static void exportToExcel(List<SheetData> sheetDataList, String template, File output) {

		File input;
		try {
			input = getFile(template);
		} catch (IOException e) {
			throw new RuntimeException("Unable to open template file : ", e);
		}
		exportToExcel(sheetDataList, input, output);
	}

	/**
	 * @param value
	 * @return
	 */
	public static Object[][] singleValueToMatrix(Object value) {
		return new Object[][] { new Object[] { value } };
	}

	/**
	 * @param values
	 * @return
	 */
	public static Object[][] arrayToColumn(Object[] values) {

		Object[][] datas = new Object[values.length][1];

		int i = 0;
		for (Object value : values) {
			datas[i++] = new Object[] { value };
		}
		return datas;
	}

	/**
	 * @param values
	 * @return
	 */
	public static Object[][] arrayToRow(Object[] values) {
		return new Object[][] { values };
	}

	/**
	 * @param sheet
	 * @param rowIndex
	 */
	private static void removeRow(Sheet sheet, int rowIndex) {
		int lastRowNum = sheet.getLastRowNum();
		if (rowIndex >= 0 && rowIndex < lastRowNum) {
			sheet.shiftRows(rowIndex + 1, lastRowNum, -1);
		}
		if (rowIndex == lastRowNum) {
			Row removingRow = sheet.getRow(rowIndex);
			if (removingRow != null) {
				sheet.removeRow(removingRow);
			}
		}
	}

	/**
	 * @param resource
	 * @return
	 * @throws IOException
	 */
	private static File getFile(String resource) throws IOException {
		ClassLoader cl = ExcelExporter.class.getClassLoader();
		InputStream cpResource = cl.getResourceAsStream(resource);
		File tmpFile = File.createTempFile("file", "temp");
		FileUtils.copyInputStreamToFile(cpResource, tmpFile);
		tmpFile.deleteOnExit();
		return tmpFile;
	}

	/**
	 * @param sheet
	 * @return
	 */
	private static Map<String, ConditionalFormatting> getConditionalFormattingMap(Sheet sheet) {

		int numConditionalFormattings = sheet.getSheetConditionalFormatting().getNumConditionalFormattings();

		Map<String, ConditionalFormatting> cfMap = new HashMap<>();

		for (int i = 0; i < numConditionalFormattings; i++) {

			ConditionalFormatting cf = sheet.getSheetConditionalFormatting().getConditionalFormattingAt(i);
			for (CellRangeAddress cellRangeAddress : cf.getFormattingRanges()) {
				cfMap.put(cellRangeAddress.formatAsString(), cf);
			}
		}
		return cfMap;
	}
}
