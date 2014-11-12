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
package org.meveo.admin.util;

import org.meveo.commons.utils.ParamBean;


public class ArConfig {

	private static final String BAYAD_PROPERTIES_FILENAME = "meveo-admin.properties";


	//private static final String INVOICES_OCC_CODE = "bayad.invoices.occCode";
	private static final String DDREQUEST_OCC_CODE = "bayad.ddrequest.occCode";
	//private static final String DDREQUEST_HEADER_DDMODE = "bayad.ddrequest.header.DDmode";
	private static final String DDREQUEST_HEADER_REFRENCE = "bayad.ddrequest.header.reference";

	private static final String DDREQUEST_DATE_VALUE_AFTER = "bayad.ddrequest.dateValueAfterNbDays";

	private static final String DDREQUEST_OUTPUT_DIR = "bayad.ddrequest.outputDir";
	private static final String DDREQUEST_FILE_NAME_EXTENSION = "bayad.ddrequest.fileName.extension";
	private static final String DDREQUEST_FILE_NAME_PREFIX = "bayad.ddrequest.fileName.prefix";
	//private static final String DDREQUEST_ADD_LAST_EMPTY_LINE = "bayad.ddrequest.addLastEmptyLine";
	//private static final String DDREQUEST_IS_TRUNCATE_STRING = "bayad.ddrequest.isTruncateString";
	
	//private static final String DUNNING_LOT_FILENAME = "bayad.dunning.lotFileName";
	private static final String DUNNING_BALANCE_FLAG = "bayad.dunning.blanceFlag";
	//private static final String DUNNING_OCC_CODE = "bayad.dunning.occCode";



	public static String getDDRequestHeaderReference() {
		return ParamBean.getInstance(BAYAD_PROPERTIES_FILENAME).getProperty(DDREQUEST_HEADER_REFRENCE,"DD");
	}

	public static String getDDRequestOutputDirectory() {
		//FIXME:set correct default value
		return ParamBean.getInstance(BAYAD_PROPERTIES_FILENAME).getProperty(DDREQUEST_OUTPUT_DIR,"/tmp");
	}

	public static String getDDRequestFileNamePrefix() {
		//FIXME:set correct default value
		return ParamBean.getInstance(BAYAD_PROPERTIES_FILENAME).getProperty(DDREQUEST_FILE_NAME_PREFIX,"");
	}

	public static String getDDRequestFileNameExtension() {
		//FIXME:set correct default value
		return ParamBean.getInstance(BAYAD_PROPERTIES_FILENAME).getProperty(DDREQUEST_FILE_NAME_EXTENSION,".txt");
	}

	public static String getDirectDebitOccCode() {
		//FIXME:set correct default value
		return ParamBean.getInstance(BAYAD_PROPERTIES_FILENAME).getProperty(DDREQUEST_OCC_CODE,"");
	}
	public static int getDateValueAfter() {
		//FIXME:set correct default value
		return Integer.parseInt(ParamBean.getInstance(BAYAD_PROPERTIES_FILENAME).getProperty(DDREQUEST_DATE_VALUE_AFTER,"0"));
	}
	
	public static int getDunningBlanceFlag() {
		return Integer.parseInt(ParamBean.getInstance(BAYAD_PROPERTIES_FILENAME).getProperty(DUNNING_BALANCE_FLAG, "1"));
	}

	
}
