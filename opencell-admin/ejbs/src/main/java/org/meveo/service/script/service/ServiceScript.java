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

package org.meveo.service.script.service;

import java.util.Map;

import org.meveo.admin.exception.BusinessException;
import org.meveo.service.script.module.ModuleScript;

/**
 * @author Edward P. Legaspi
 **/
public class ServiceScript extends ModuleScript implements ServiceScriptInterface {

    private static final long serialVersionUID = 8671655503725431499L;

    public static final String CONTEXT_ACTIVATION_DATE = "CONTEXT_ACTIVATION_DATE";
    public static final String CONTEXT_SUSPENSION_DATE = "CONTEXT_SUSPENSION_DATE";
    public static final String CONTEXT_TERMINATION_DATE = "CONTEXT_TERMINATION_DATE";
    public static final String CONTEXT_TERMINATION_REASON = "CONTEXT_TERMINATION_REASON";
    public static final String CONTEXT_PARAMETERS = "CONTEXT_PARAMETERS";

    @Override
    public void instantiateServiceInstance(Map<String, Object> methodContext) throws BusinessException {

    }

    @Override
    public void activateServiceInstance(Map<String, Object> methodContext) throws BusinessException {

    }

    @Override
    public void suspendServiceInstance(Map<String, Object> methodContext) throws BusinessException {

    }

    @Override
    public void reactivateServiceInstance(Map<String, Object> methodContext) throws BusinessException {

    }

    @Override
    public void terminateServiceInstance(Map<String, Object> methodContext) throws BusinessException {

    }

    @Override
    public void beforeCreateServiceFromBSM(Map<String, Object> methodContext) throws BusinessException {

    }

    @Override
    public void afterCreateServiceFromBSM(Map<String, Object> methodContext) throws BusinessException {

    }
}