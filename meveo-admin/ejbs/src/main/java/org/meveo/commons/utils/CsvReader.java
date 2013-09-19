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
