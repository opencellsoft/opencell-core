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
package org.meveo.admin.parse.csv;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.util.ArrayList;
import java.util.List;

import org.meveo.admin.exception.FileContentException;

public abstract class CSVFile<T extends CSVLineData> {

	private boolean parseHeader;
	private String header;
	private File file;
	private List<T> contexts;

	public CSVFile() {
	}

	public CSVFile(File file, boolean parseHeader) {
		this.file = file;
		this.parseHeader = parseHeader;
		contexts = new ArrayList<T>();
	}

	public void parse() throws FileContentException {
		try {
			FileInputStream fis = new FileInputStream(file);
			InputStreamReader read = new InputStreamReader(fis);
			BufferedReader reader = new BufferedReader(read);

			if (parseHeader) {
				// 1)----header--
				String header = reader.readLine();
				String[] headers = header.split(getSplit());
				if (!checkHeader(headers)) {
				    reader.close();
		            read.close();
		            fis.close();
					throw new FileContentException();
				}
				setHeader(header);
			}

			// 2)---context--
			for (String str = reader.readLine(); str != null; str = reader.readLine()) {
				String[] strs = str.split(getSplit());
				T tRow = getTFromRow(strs);
				if (tRow != null) {
					contexts.add(tRow);
					tRow.setCsvLine(str);
				}
			}

			reader.close();
			read.close();
			fis.close();

		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	abstract protected T getTFromRow(String[] strs);

	abstract protected String getRowFromT(T t);

	abstract protected String getSplit();

	abstract protected boolean checkHeader(String[] headers);

	abstract boolean validateRowData(String[] strs);

	public void addEnty(T t) {
		if (contexts == null)
			contexts = new ArrayList<T>();
		contexts.add(t);
	}

	public void createCsvFile() throws IOException {
		FileOutputStream fos = new FileOutputStream(file);
		OutputStreamWriter out = new OutputStreamWriter(fos, "GBK");
		BufferedWriter writer = new BufferedWriter(out);
		writer.write(getHeader());
		for (T t : contexts) {
			writer.newLine();
			writer.write(getRowFromT(t));
		}
		writer.flush();
		writer.close();
		out.close();
		fos.close();
	}


	public File getFile() {
		return file;
	}

	public void setFile(File file) {
		this.file = file;
	}

	public List<T> getContexts() {
		return contexts;
	}

	public void setContexts(List<T> contexts) {
		this.contexts = contexts;
	}

	public boolean isParseHeader() {
		return parseHeader;
	}

	public void setParseHeader(boolean parseHeader) {
		this.parseHeader = parseHeader;
	}

	public String getHeader() {
		return header;
	}

	public void setHeader(String header) {
		this.header = header;
	}

}
