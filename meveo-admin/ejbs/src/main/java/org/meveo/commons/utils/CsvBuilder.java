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
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import javax.servlet.http.HttpServletResponse;

import org.apache.poi.util.IOUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class CsvBuilder {

	private Logger log = LoggerFactory.getLogger(CsvBuilder.class);

	/** Creates a new instance of CsvBuilder */
	private final static String BREAK_LINE = "\r\n";
	private String DELIMITER = ";";
	private boolean useQuotes = true;

	private StringBuffer sb = new StringBuffer();
	private boolean firstElement = true;

	public CsvBuilder() {
	}

	public CsvBuilder(String sep, boolean useQuotes) {
		DELIMITER = sep;
		this.useQuotes = useQuotes;
	}

	public CsvBuilder appendValues(String[] values) {
		for (String value : values)
			appendValue(value);
		return this;
	}

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

	public CsvBuilder startNewLine() {
		sb.append(BREAK_LINE);
		firstElement = true;
		return this;
	}

	public String toString() {
		return sb.toString();
	}

	public void toFile(String absolutFfilename) {
		FileWriter fw = null;
		try {
			File tmp = new File(absolutFfilename);
			File createDir = tmp.getParentFile();

			createDir.mkdirs();
			fw = new FileWriter(absolutFfilename, false);
			fw.write(sb.toString());
			fw.close();
		} catch (Exception e) {
			log.error("error on toFile",e);
		} finally {
			if (fw != null) {
				try {
					fw.close();
				} catch (IOException e) {
					log.error("exception on toFile",e);;
				}
			}
		}
	}
	
	
	public void writeFile(byte[] content, String filename) throws IOException { 
		File file = new File(filename);
		if (!file.exists()) {
			file.createNewFile();
		}
		FileOutputStream fop = new FileOutputStream(file,true);
		fop.write(content);
		fop.flush();
		fop.close();
 
	}
	
	
	 public void download(InputStream inputStream, String fileName) {
			log.info("start to download...");
			if(inputStream!=null){
				try {
					
					javax.faces.context.FacesContext context = javax.faces.context.FacesContext
							.getCurrentInstance();
					HttpServletResponse res = (HttpServletResponse) context.getExternalContext()
							.getResponse();
					res.setContentType("application/force-download");
					res.addHeader("Content-disposition", "attachment;filename=\"" + fileName
							+ "\""); 
					
					OutputStream out = res.getOutputStream();

					IOUtils.copy(inputStream, out);
		            out.flush();
					out.close();
					context.responseComplete();
					log.info("download over!");
				} catch (Exception e) {
					log.error("Error:"+e.getMessage()+", when dowload file: "+fileName);
				}
				log.info("downloaded successfully!");
			}

		}
	

	public boolean isEmpty() {
		return sb.length() == 0;
	}
	
	
	
	
}
