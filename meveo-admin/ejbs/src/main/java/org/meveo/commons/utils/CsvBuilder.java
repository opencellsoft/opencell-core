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
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;


public class CsvBuilder {

    /** Creates a new instance of CsvBuilder */
     private final static String BREAK_LINE="\r\n";
     private String DELIMITER=";";
     private boolean useQuotes=true;
     

    private StringBuffer sb = new StringBuffer();
    private boolean firstElement = true;

    public CsvBuilder() {
    }

    public CsvBuilder(String sep,boolean useQuotes) {
        DELIMITER = sep;
        this.useQuotes = useQuotes;
    }

    public CsvBuilder appendValues(String[] values) {
        for(String value : values)
            appendValue(value);
        return this;
    }

    public CsvBuilder appendValue(String value) {
        if(!firstElement)
            sb.append(DELIMITER);
        else
            firstElement=false;

        if(value!=null){
        	if(useQuotes){
        		sb.append("\""+value+"\"");
        	} else {
        		sb.append(value);
        	}
        }            
        return this;
    }

    public CsvBuilder startNewLine() {
        sb.append(BREAK_LINE);
        firstElement=true;
        return this;
    }

    public String toString() {
        return sb.toString();
    }

    public void toFile(String absolutFfilename ){
    	FileWriter fw = null;
    	try {
	    	File tmp =new File(absolutFfilename);
	    	File createDir = tmp.getParentFile();
	    	
	    	createDir.mkdirs();	
			fw = new FileWriter(absolutFfilename, false);
	    	fw.write(sb.toString());
	    	fw.close();
		} catch (Exception e) {
			e.printStackTrace();
		}finally{
			if(fw != null){
				try {
					fw.close();
				} catch (IOException e) {					
					e.printStackTrace();
				}
			}			
		}
    }

   
    public boolean isEmpty(){
    	return sb.length() == 0;	
    }
}
