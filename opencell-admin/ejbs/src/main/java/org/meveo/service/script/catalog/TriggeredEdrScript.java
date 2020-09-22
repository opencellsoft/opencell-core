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

package org.meveo.service.script.catalog;

import java.util.Map;

import org.meveo.model.rating.EDR;
import org.meveo.service.script.Script;

/**
 * This script is executed after {@link EDR} creation.
 * 
 * @author Edward P. Legaspi
 * @lastModifiedVersion 6.0
 */
public class TriggeredEdrScript extends Script implements TriggeredEdrScriptInterface {
    
    /**
     * Wallet Operation which trigger the creation of TriggeredEdr
     */
    public static final String CONTEXT_WO = "CONTEXT_WO";

    @Override
    public EDR updateEdr(Map<String, Object> methodContext) {
        return (EDR) methodContext.get(CONTEXT_ENTITY);
    }

}
