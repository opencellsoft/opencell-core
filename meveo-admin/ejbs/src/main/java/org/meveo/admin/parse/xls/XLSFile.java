/*
* (C) Copyright 2009-2014 Manaty SARL (http://manaty.net/) and contributors.
*
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Affero General Public License as
 * published by the Free Software Foundation, either version 3 of the
 * License, or (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Affero General Public License for more details.
 *
 * You should have received a copy of the GNU Affero General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
*/
package org.meveo.admin.parse.xls;

import java.io.File;
import java.io.IOException;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import jxl.Cell;
import jxl.Sheet;
import jxl.Workbook;
import jxl.read.biff.BiffException;

/**
 * @author Gediminas Ubartas
 * @created 2010.11.09
 */
public class XLSFile implements Serializable {

    private static final long serialVersionUID = 1L;

    private File file;

    private List<String[]> contexts;

    public XLSFile(File file) {
        this.file = file;
        contexts = new ArrayList<String[]>();
    }

    public void parse() throws IOException {
        Workbook w;
        try {
            w = Workbook.getWorkbook(file);
            // Get the first sheet
            Sheet sheet = w.getSheet(0);
            // Loop over first 10 column and lines

            for (int j = 0; j < sheet.getRows(); j++) {
                String[] strs = new String[sheet.getColumns()];
                for (int i = 0; i < sheet.getColumns(); i++) {
                    Cell cell = sheet.getCell(i, j);
                    strs[i] = cell.getContents();
                }
                contexts.add(strs);
            }
        } catch (BiffException e) {
            e.printStackTrace();
        }
    }

    public List<String[]> getContexts() {
        return contexts;
    }

}
