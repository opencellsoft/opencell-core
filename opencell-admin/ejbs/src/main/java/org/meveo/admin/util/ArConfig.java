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
package org.meveo.admin.util;

import org.meveo.commons.utils.ParamBean;

public class ArConfig {

    private static final String BAYAD_PROPERTIES_FILENAME = "opencell-admin.properties";

    // private static final String DDREQUEST_HEADER_DDMODE = "bayad.ddrequest.header.DDmode";
    private static final String DDREQUEST_HEADER_REFRENCE = "bayad.ddrequest.header.reference";

    private static final String DDREQUEST_DATE_VALUE_AFTER = "bayad.ddrequest.dateValueAfterNbBusinessDays";

    private static final String DDREQUEST_OUTPUT_DIR = "bayad.ddrequest.outputDir";
    private static final String DDREQUEST_FILE_NAME_EXTENSION = "bayad.ddrequest.fileName.extension";
    private static final String DDREQUEST_FILE_NAME_PREFIX = "bayad.ddrequest.fileName.prefix";
    // private static final String DDREQUEST_ADD_LAST_EMPTY_LINE = "bayad.ddrequest.addLastEmptyLine";
    // private static final String DDREQUEST_IS_TRUNCATE_STRING = "bayad.ddrequest.isTruncateString";

    // private static final String DUNNING_LOT_FILENAME = "bayad.dunning.lotFileName";
    private static final String DUNNING_BALANCE_FLAG = "bayad.dunning.blanceFlag";
    // private static final String DUNNING_OCC_CODE = "bayad.dunning.occCode";

    public static final String SCTREQUEST_FILE_NAME_PREFIX = "bayad.sctrequest.fileName.prefix";
    private static final String SCTREQUEST_OUTPUT_DIR = "bayad.sctrequest.outputDir";
    private static final String SCTREQUEST_FILE_NAME_EXTENSION = "bayad.sctrequest.fileName.extension";
    private static final String SCTREQUEST_HEADER_REFRENCE = "bayad.sctrequest.header.reference";

    public static String getDDRequestHeaderReference() {
        return ParamBean.getInstance(BAYAD_PROPERTIES_FILENAME).getProperty(DDREQUEST_HEADER_REFRENCE, "DD");
    }

    public static String getDDRequestOutputDirectory() {
        return ParamBean.getInstance(BAYAD_PROPERTIES_FILENAME).getProperty(DDREQUEST_OUTPUT_DIR, "tmp");
    }

    public static String getDDRequestFileNamePrefix() {
        return ParamBean.getInstance(BAYAD_PROPERTIES_FILENAME).getProperty(DDREQUEST_FILE_NAME_PREFIX, "SDD_");
    }

    public static String getDDRequestFileNameExtension() {
        return ParamBean.getInstance(BAYAD_PROPERTIES_FILENAME).getProperty(DDREQUEST_FILE_NAME_EXTENSION, ".xml");
    }

    public static int getDateValueAfter() {
        return ParamBean.getInstance(BAYAD_PROPERTIES_FILENAME).getPropertyAsInteger(DDREQUEST_DATE_VALUE_AFTER, 3);
    }

    public static int getDunningBlanceFlag() {
        return Integer.parseInt(ParamBean.getInstance(BAYAD_PROPERTIES_FILENAME).getProperty(DUNNING_BALANCE_FLAG, "1"));
    }

    public static String getSCTRequestFileNameExtension() {
        return ParamBean.getInstance(BAYAD_PROPERTIES_FILENAME).getProperty(SCTREQUEST_FILE_NAME_EXTENSION, ".xml");
    }

    public static String getSCTRequestFileNamePrefix() {
        return ParamBean.getInstance(BAYAD_PROPERTIES_FILENAME).getProperty(SCTREQUEST_FILE_NAME_PREFIX, "SCT_");
    }

    public static String getSCTRequestOutputDir() {
        return ParamBean.getInstance(BAYAD_PROPERTIES_FILENAME).getProperty(SCTREQUEST_OUTPUT_DIR, "tmp");
    }

    public static String getSCTRequestHeaderRefrence() {
        return ParamBean.getInstance(BAYAD_PROPERTIES_FILENAME).getProperty(SCTREQUEST_HEADER_REFRENCE, "OPENCELL-SCT-");
    }
    
    

}
