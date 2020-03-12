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

package org.meveo.service.script.product;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.ejb.Stateless;
import javax.inject.Inject;

import org.meveo.admin.exception.BusinessException;
import org.meveo.api.dto.CustomFieldDto;
import org.meveo.model.catalog.ProductTemplate;
import org.meveo.service.script.Script;
import org.meveo.service.script.ScriptInstanceService;
import org.meveo.service.script.service.ServiceScript;

/**
 * @author Edward P. Legaspi
 **/
@Stateless
public class ProductModelScriptService implements Serializable {

    private static final long serialVersionUID = -2580475102375024245L;

    @Inject
    private ScriptInstanceService scriptInstanceService;

    public ProductScriptInterface beforeCreate(List<CustomFieldDto> customFields, String scriptCode) throws BusinessException {
        ProductScriptInterface scriptInterface = (ProductScriptInterface) scriptInstanceService.getScriptInstance(scriptCode);
        Map<String, Object> scriptContext = new HashMap<>();
        if (customFields != null) {
            scriptContext.put(ServiceScript.CONTEXT_PARAMETERS, customFields);
        } else {
            scriptContext.put(ServiceScript.CONTEXT_PARAMETERS, new ArrayList<CustomFieldDto>());
        }
        scriptInterface.beforeCreate(scriptContext);
        return scriptInterface;
    }

    public void afterCreate(ProductTemplate entity, List<CustomFieldDto> customFields, ProductScriptInterface scriptInterface) throws BusinessException {
        Map<String, Object> scriptContext = new HashMap<>();
        scriptContext.put(Script.CONTEXT_ENTITY, entity);
        if (customFields != null) {
            scriptContext.put(ServiceScript.CONTEXT_PARAMETERS, customFields);
        } else {
            scriptContext.put(ServiceScript.CONTEXT_PARAMETERS, new ArrayList<CustomFieldDto>());
        }
        scriptInterface.afterCreate(scriptContext);
    }
}