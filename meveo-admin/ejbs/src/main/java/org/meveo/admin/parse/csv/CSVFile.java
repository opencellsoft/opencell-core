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
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public abstract class CSVFile<T extends CSVLineData> {

	private boolean parseHeader;
	private String header;
	private File file;
	private List<T> contexts;

	private Logger log = LoggerFactory.getLogger(CSVFile.class);

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
			for (String str = reader.readLine(); str != null; str = reader
					.readLine()) {
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
			log.error("file not found exception",e);
		} catch (IOException e) {
			log.error("error parsing file ",e);
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
