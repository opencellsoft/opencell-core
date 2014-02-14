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
package org.meveo.admin.utils;

import java.util.HashMap;
import java.util.Map;

import org.meveo.commons.utils.ParamBean;


public class ArConfig {

	private static final String BAYAD_PROPERTIES_FILENAME = "meveo-admin.properties";


	private static final String INVOICES_OCC_CODE = "bayad.invoices.occCode";
	private static final String DDREQUEST_OCC_CODE = "bayad.ddrequest.occCode";
	private static final String DDREQUEST_HEADER_DDMODE = "bayad.ddrequest.header.DDmode";
	private static final String DDREQUEST_HEADER_REFRENCE = "bayad.ddrequest.header.reference";

	private static final String DDREQUEST_DATE_VALUE_AFTER = "bayad.ddrequest.dateValueAfterNbDays";

	private static final String DDREQUEST_OUTPUT_DIR = "bayad.ddrequest.outputDir";
	private static final String DDREQUEST_FILE_NAME_EXTENSION = "bayad.ddrequest.fileName.extension";
	private static final String DDREQUEST_FILE_NAME_PREFIX = "bayad.ddrequest.fileName.prefix";
	private static final String DDREQUEST_ADD_LAST_EMPTY_LINE = "bayad.ddrequest.addLastEmptyLine";
	private static final String DDREQUEST_IS_TRUNCATE_STRING = "bayad.ddrequest.isTruncateString";
	

	private static final String DDREQUEST_SCANNING_INTERVAL = "bayad.ddrequest.scanningInterval";
	private static final String INVOICES_SCANNING_INTERVAL = "bayad.invoices.scanningInterval";
	private static final String DUNNING_SCANNING_INTERVAL = "bayad.dunning.scanningInterval";
	private static final String WORKING_THREADS = "bayad.workingThreadCount";

	private static final String DUNNING_LOT_FILENAME = "bayad.dunning.lotFileName";
	private static final String DUNNING_LOT_FILENAME_EXT = "bayad.dunning.lotFileName.extention";
	private static final String DUNNING_BALANCE_FLAG = "bayad.dunning.blanceFlag";
	private static final String DUNNING_OCC_CODE = "bayad.dunning.occCode";

	public static long getInvoicesScanningInterval() {
		String value = ParamBean.getInstance(BAYAD_PROPERTIES_FILENAME).getProperty(INVOICES_SCANNING_INTERVAL);
		return Long.parseLong(value);
	}

	public static long getDunningScanningInterval() {
		String value = ParamBean.getInstance(BAYAD_PROPERTIES_FILENAME).getProperty(DUNNING_SCANNING_INTERVAL);
		return Long.parseLong(value);
	}

	/**
	 * Get maximum number of working threads.
	 */
	public static int getThreadCount() {
		String value = ParamBean.getInstance(BAYAD_PROPERTIES_FILENAME).getProperty(WORKING_THREADS);
		return Integer.parseInt(value);
	}


	public static String getInvoiceOccCode() {
		return ParamBean.getInstance(BAYAD_PROPERTIES_FILENAME).getProperty(INVOICES_OCC_CODE);
	}

	public static String getDDRequestHeaderDDMode() {
		return ParamBean.getInstance(BAYAD_PROPERTIES_FILENAME).getProperty(DDREQUEST_HEADER_DDMODE);
	}

	public static String getDDRequestHeaderReference() {
		return ParamBean.getInstance(BAYAD_PROPERTIES_FILENAME).getProperty(DDREQUEST_HEADER_REFRENCE,"DD");
	}

	

	
	public static String getDunningLotFileName() {
		return ParamBean.getInstance(BAYAD_PROPERTIES_FILENAME).getProperty(DUNNING_LOT_FILENAME);
	}



	
	public static String getDDRequestOutputDirectory() {
		return ParamBean.getInstance(BAYAD_PROPERTIES_FILENAME).getProperty(DDREQUEST_OUTPUT_DIR);
	}

	public static String getDDRequestFileNamePrefix() {
		return ParamBean.getInstance(BAYAD_PROPERTIES_FILENAME).getProperty(DDREQUEST_FILE_NAME_PREFIX);
	}

	public static String getDDRequestFileNameExtension() {
		return ParamBean.getInstance(BAYAD_PROPERTIES_FILENAME).getProperty(DDREQUEST_FILE_NAME_EXTENSION);
	}

	public static String getDirectDebitOccCode() {
		return ParamBean.getInstance(BAYAD_PROPERTIES_FILENAME).getProperty(DDREQUEST_OCC_CODE);
	}

	public static int getDateValueAfter() {
		return Integer.parseInt(ParamBean.getInstance(BAYAD_PROPERTIES_FILENAME).getProperty(DDREQUEST_DATE_VALUE_AFTER));
	}

	

	public static long getDDRequestScanningInterval() {
		String value = ParamBean.getInstance(BAYAD_PROPERTIES_FILENAME).getProperty(DDREQUEST_SCANNING_INTERVAL);
		return Long.parseLong(value);
	}

	
	public static String getDDRequestAddLastEmptyLine() {
		return ParamBean.getInstance(BAYAD_PROPERTIES_FILENAME).getProperty(DDREQUEST_ADD_LAST_EMPTY_LINE);
	}

	public static int getDunningBlanceFlag() {
		return Integer.parseInt(ParamBean.getInstance(BAYAD_PROPERTIES_FILENAME).getProperty(DUNNING_BALANCE_FLAG, "1"));
	}

	public static String getDunningOccCode() {
		return ParamBean.getInstance(BAYAD_PROPERTIES_FILENAME).getProperty(DUNNING_OCC_CODE);
	}

	public static String getDDRequestIsTruncateString() {
		return ParamBean.getInstance(BAYAD_PROPERTIES_FILENAME).getProperty(DDREQUEST_IS_TRUNCATE_STRING);
	}

	
}
