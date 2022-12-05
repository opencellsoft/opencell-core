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

import org.apache.poi.util.IOUtils;
import org.meveo.admin.storage.StorageFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import jakarta.servlet.http.HttpServletResponse;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.Writer;


/**
 * The Class CsvBuilder.
 *
 * @author anasseh
 */
public class CsvBuilder {

	/** The log. */
	private Logger log = LoggerFactory.getLogger(CsvBuilder.class);

	/** The Constant BREAK_LINE_DOS. */
	public final static String BREAK_LINE_DOS = "\r\n";
	
	/** The Constant BREAK_LINE_UNIX. */
	public final static String BREAK_LINE_UNIX = "\n";
	
	/** The delimiter. */
	private String DELIMITER = ";";
	
	/** The use quotes. */
	private boolean useQuotes = true;
	
	/** The break line char. */
	private String breakLineChar = BREAK_LINE_DOS;

	/** The sb. */
	private StringBuffer sb = new StringBuffer();
	
	/** The first element. */
	private boolean firstElement = true;

	/**
	 * Instantiates a new csv builder.
	 */
	public CsvBuilder() {
	}

	/**
	 * Instantiates a new csv builder.
	 *
	 * @param sep the sep
	 * @param useQuotes the use quotes
	 */
	public CsvBuilder(String sep, boolean useQuotes) {
		this.DELIMITER = sep;
		this.useQuotes = useQuotes;
	}

	/**
	 * Instantiates a new csv builder.
	 *
	 * @param sep the sep
	 * @param useQuotes the use quotes
	 * @param breakLineChar the break line char
	 */
	public CsvBuilder(String sep, boolean useQuotes, String breakLineChar) {
		this.DELIMITER = sep;
		this.useQuotes = useQuotes;
		this.breakLineChar = breakLineChar;
	}

	/**
	 * Append values.
	 *
	 * @param values the values
	 * @return the csv builder
	 */
	public CsvBuilder appendValues(String[] values) {
		for (String value : values)
			appendValue(value);
		return this;
	}

	/**
	 * Append value.
	 *
	 * @param value the value
	 * @return the csv builder
	 */
	public CsvBuilder appendValue(String value) {
		if (!firstElement)
			sb.append(DELIMITER);
		else
			firstElement = false;

		if (value != null) {
			if (useQuotes) {
				sb.append("\"" + value + "\"");
			} else {
				sb.append(value);
			}
		}
		return this;
	}

	/**
	 * Start new line.
	 *
	 * @return the csv builder
	 */
	public CsvBuilder startNewLine() {
		sb.append(breakLineChar);
		firstElement = true;
		return this;
	}


	public String toString() {
		return sb.toString();
	}

	/**
	 * To file.
	 *
	 * @param absolutFfilename the absolut ffilename
	 */
	public void toFile(String absolutFfilename) {
		Writer fw = null;
		try {
			File tmp = new File(absolutFfilename);
			File createDir = tmp.getParentFile();

			createDir.mkdirs();
			fw = StorageFactory.getWriter(absolutFfilename);
			fw.write(sb.toString());
			fw.close();
		} catch (Exception e) {
			log.error("error on toFile", e);
		} finally {
			if (fw != null) {
				try {
					fw.close();
				} catch (IOException e) {
					log.error("exception on toFile", e);
					;
				}
			}
		}
	}

	/**
	 * Write file.
	 *
	 * @param content  content to be written to the file
	 * @param filename name of file to write the content
	 * @throws IOException input/output exception.
	 */
	public void writeFile(byte[] content, String filename) throws IOException {
		File file = new File(filename);
		if (!file.exists()) {
			file.createNewFile();
		}
		try (FileOutputStream fop = new FileOutputStream(file, true)) {
			fop.write(content);
			fop.flush();
		} catch (IOException ex) {
			throw ex;
		}

	}

	/**
	 * Download.
	 *
	 * @param inputStream the input stream
	 * @param fileName the file name
	 */
	public void download(InputStream inputStream, String fileName) {
		log.info("start to download...");
		if (inputStream != null) {
			try {

				jakarta.faces.context.FacesContext context = jakarta.faces.context.FacesContext.getCurrentInstance();
				HttpServletResponse res = (HttpServletResponse) context.getExternalContext().getResponse();
				res.setContentType("application/force-download");
				res.addHeader("Content-disposition", "attachment;filename=\"" + fileName + "\"");

				OutputStream out = res.getOutputStream();

				IOUtils.copy(inputStream, out);
				out.flush();
				out.close();
				context.responseComplete();
				log.info("download over!");
			} catch (Exception e) {
				log.error("Error:" + e.getMessage() + ", when dowload file: " + fileName);
			}
			log.info("downloaded successfully!");
		}

	}

	/**
	 * Checks if is empty.
	 *
	 * @return true, if is empty
	 */
	public boolean isEmpty() {
		return sb.length() == 0;
	}

}
