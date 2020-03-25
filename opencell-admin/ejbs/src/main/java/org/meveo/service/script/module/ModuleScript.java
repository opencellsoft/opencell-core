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

package org.meveo.service.script.module;

import java.util.Map;

import org.meveo.admin.exception.BusinessException;
import org.meveo.service.script.Script;

/**
 * @author Andrius Karpavicius
 **/
public class ModuleScript extends Script implements ModuleScriptInterface {

    private static final long serialVersionUID = -445948060788051352L;

    @Override
    public void preInstallModule(Map<String, Object> methodContext) throws BusinessException {
    }

    @Override
    public void postInstallModule(java.util.Map<String, Object> methodContext) throws BusinessException {
    }

    @Override
    public void preUninstallModule(Map<String, Object> methodContext) throws BusinessException {
    }

    @Override
    public void postUninstallModule(Map<String, Object> methodContext) throws BusinessException {
    }

    @Override
    public void preEnableModule(java.util.Map<String, Object> methodContext) throws BusinessException {
    }

    @Override
    public void postEnableModule(java.util.Map<String, Object> methodContext) throws BusinessException {
    }

    @Override
    public void preDisableModule(java.util.Map<String, Object> methodContext) throws BusinessException {
    }

    @Override
    public void postDisableModule(java.util.Map<String, Object> methodContext) throws BusinessException {
    }
}