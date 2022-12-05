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

package org.meveo.api.ws;

import jakarta.jws.WebMethod;
import jakarta.jws.WebParam;
import jakarta.jws.WebService;

import org.meveo.api.dto.ActionStatus;
import org.meveo.api.dto.ScriptInstanceDto;
import org.meveo.api.dto.response.GetScriptInstanceResponseDto;
import org.meveo.api.dto.response.ScriptInstanceReponseDto;

@WebService
@Deprecated
public interface ScriptInstanceWs extends IBaseWs {

    @WebMethod
    ScriptInstanceReponseDto create(@WebParam(name = "createScriptInstanceRequest") ScriptInstanceDto scriptInstanceDto);

    @WebMethod
    ScriptInstanceReponseDto update(@WebParam(name = "updateScriptInstanceRequest") ScriptInstanceDto scriptInstanceDto);

    @WebMethod
    ActionStatus remove(@WebParam(name = "removeScriptInstanceRequest") String scriptInstanceCode);

    @WebMethod
    GetScriptInstanceResponseDto find(@WebParam(name = "findScriptInstanceRequest") String scriptInstanceCode);

    @WebMethod
    ScriptInstanceReponseDto createOrUpdate(@WebParam(name = "createOrUpdateScriptInstanceRequest") ScriptInstanceDto scriptInstanceDto);

    /**
     * Enable a Script by its code
     * 
     * @param code Script code
     * @return Request processing status
     */
    @WebMethod
    ActionStatus enable(@WebParam(name = "code") String code);

    /**
     * Disable a Script by its code
     * 
     * @param code Script code
     * @return Request processing status
     */
    @WebMethod
    ActionStatus disable(@WebParam(name = "code") String code);

    /**
     * Execute script by its code
     * 
     * @param code Script code
     * @return Request processing status
     */
    @WebMethod
    ActionStatus execute(@WebParam(name = "code") String code);
}