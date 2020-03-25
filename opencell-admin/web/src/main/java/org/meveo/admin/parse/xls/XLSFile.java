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
package org.meveo.admin.parse.xls;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.ss.usermodel.WorkbookFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author Gediminas Ubartas
 * @since 2010.11.09
 */
public class XLSFile implements Serializable {

	private static final long serialVersionUID = 1L;

	private Logger log = LoggerFactory.getLogger(XLSFile.class);

	private File file;
	private List<String[]> contexts;

	/**
	 * @param file xls file
	 */
	public XLSFile(File file) {
		this.file = file;
		contexts = new ArrayList<String[]>();
	}

	/**
	 * @throws IOException input/output exception
	 */
	public void parse() throws IOException {
		try (Workbook w = WorkbookFactory.create(new FileInputStream(file))) {
			// Get the first sheet
			Sheet sheet = w.getSheetAt(0);
			// Loop over first 10 column and lines

			Iterator<Row> rowIterator = sheet.rowIterator();
			while (rowIterator.hasNext()) {
				Row row = rowIterator.next();
				Iterator<Cell> cellIterator = row.cellIterator();
				String[] strs = new String[row.getPhysicalNumberOfCells()];

				int cellCtr = 0;
				while (cellIterator.hasNext()) {
					Cell cell = cellIterator.next();
					strs[cellCtr++] = cell.getStringCellValue();
				}

				contexts.add(strs);
			}
		} catch (Exception e) {
			log.error("invalid file format ",e);
		}
	}

	public List<String[]> getContexts() {
		return contexts;
	}

}
