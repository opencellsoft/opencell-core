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

package org.meveo.service.script.finance;

import org.meveo.service.script.Script;

/**
 * Parent class for ReportExtract java script. This class contains the available variables for the script.
 *
 * @author Edward P. Legaspi
 * @since 5.0
 * @lastModifiedVersion 5.1
 **/
public class ReportExtractScript extends Script {

    public static final String START_DATE = "START_DATE";
    public static final String END_DATE = "END_DATE";
    public static final String DIR = "DIR";
    public static final String FILENAME = "FILENAME";
    public static final String REPORTS_DIR = "reports";
    public static final String ERROR_MESSAGE = "ERROR_MESSAGE";
    public static final String LINE_COUNT = "LINE_COUNT";

}
