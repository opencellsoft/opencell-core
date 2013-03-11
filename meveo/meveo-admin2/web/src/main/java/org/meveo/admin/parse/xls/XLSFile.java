/*
* (C) Copyright 2009-2013 Manaty SARL (http://manaty.net/) and contributors.
*
* Licensed under the GNU Public Licence, Version 2.0 (the "License");
* you may not use this file except in compliance with the License.
* You may obtain a copy of the License at
*
*     http://www.gnu.org/licenses/gpl-2.0.txt
*
* Unless required by applicable law or agreed to in writing, software
* distributed under the License is distributed on an "AS IS" BASIS,
* WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
* See the License for the specific language governing permissions and
* limitations under the License.
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
