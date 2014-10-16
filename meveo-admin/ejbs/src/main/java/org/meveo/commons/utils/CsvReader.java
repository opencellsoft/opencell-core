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
package org.meveo.commons.utils;
import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;


public class CsvReader {

	
	
	private String delimiter=null;
	private boolean useQuotes=false;	

	private FileInputStream fis = null;
	private InputStreamReader read = null;
	private BufferedReader reader = null;
	private String currentLine = null;
	private int numLine=0;

	public CsvReader(String absoluteFileName,String delimiter,boolean useQuotes) throws FileNotFoundException {
		this.fis = new FileInputStream(absoluteFileName);
		this.read = new InputStreamReader(fis);
		this.reader = new BufferedReader(read);
		this.delimiter=delimiter;
		this.useQuotes=useQuotes;
	}

	public boolean hasNext() throws IOException{
		numLine++;
		currentLine = reader.readLine();
		if(currentLine == null){
			return false;
		}
		if(useQuotes){
			currentLine=currentLine.substring(1, currentLine.length()-1);
		}
		return true;
	}

	public String getCurrentLine(){
		return currentLine;
	}
	public int getNumLine(){
		return numLine;
	}
	
	public String[] getFields(){
		return currentLine.split(delimiter);
	}
	
	public void close() throws IOException{
		if(reader != null){
			reader.close();
		}
		if(read != null){
			read.close();
		}		
		if(fis != null){
			fis.close();
		}		
	}
}
