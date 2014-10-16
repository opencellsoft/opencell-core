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

import java.io.File;
import java.io.FilenameFilter;

/**
 * @author anasseh
 *
 */
public class ImportFileFiltre implements  FilenameFilter{
	   private String  prefix = null;
	    private String  ext = null;
	   
	    
		public ImportFileFiltre(String prefix,String ext){
			this.prefix = prefix;
			this.ext    = ext;
			if(StringUtils.isBlank(prefix)){
				this.prefix = "*";
			}
			if(StringUtils.isBlank(ext)){
				this.ext = "*";
			}
		}
		public boolean accept(File dir,String name) {
		   if(name == null) return false;
		if( ("*".equals(ext)    ||  name.toUpperCase().endsWith("."+ext.toUpperCase())) && 
			("*".equals(prefix) ||	name.startsWith(prefix))
			){
			return true;
		}
		else {
			return false;
		}
	}
		
	}