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
package org.meveo.admin.parse.xls;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.apache.poi.openxml4j.exceptions.InvalidFormatException;
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

	public XLSFile(File file) {
		this.file = file;
		contexts = new ArrayList<String[]>();
	}

	public void parse() throws IOException {
		Workbook w;
		try {
			w = WorkbookFactory.create(new FileInputStream(file));
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
		} catch (InvalidFormatException e) {
			log.error("invalid file format ",e);
		}
	}

	public List<String[]> getContexts() {
		return contexts;
	}

}
