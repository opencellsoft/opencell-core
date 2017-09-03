package org.meveo.service.script.product;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.ejb.Singleton;
import javax.ejb.Startup;
import javax.inject.Inject;

import org.meveo.admin.exception.BusinessException;
import org.meveo.admin.exception.ElementNotFoundException;
import org.meveo.admin.exception.InvalidScriptException;
import org.meveo.api.dto.CustomFieldDto;
import org.meveo.model.catalog.ProductTemplate;
import org.meveo.service.script.Script;
import org.meveo.service.script.ScriptInstanceService;
import org.meveo.service.script.service.ServiceScript;

/**
 * @author Edward P. Legaspi
 **/
@Singleton
@Startup
public class ProductModelScriptService implements Serializable {

    private static final long serialVersionUID = -2580475102375024245L;

    @Inject
    private ScriptInstanceService scriptInstanceService;

    public void beforeCreateServiceFromBSM(List<CustomFieldDto> customFields, String scriptCode) throws ElementNotFoundException, InvalidScriptException, BusinessException {
        ProductScriptInterface scriptInterface = (ProductScriptInterface) scriptInstanceService.getScriptInstance(scriptCode);
        Map<String, Object> scriptContext = new HashMap<>();
        if (customFields != null) {
            scriptContext.put(ServiceScript.CONTEXT_PARAMETERS, customFields);
        } else {
            scriptContext.put(ServiceScript.CONTEXT_PARAMETERS, new ArrayList<CustomFieldDto>());
        }
        scriptInterface.beforeCreateProductFromPMS(scriptContext);
    }

    public void afterCreateServiceFromBSM(ProductTemplate entity, List<CustomFieldDto> customFields, String scriptCode)
            throws ElementNotFoundException, InvalidScriptException, BusinessException {
        ProductScriptInterface scriptInterface = (ProductScriptInterface) scriptInstanceService.getScriptInstance(scriptCode);
        Map<String, Object> scriptContext = new HashMap<>();
        scriptContext.put(Script.CONTEXT_ENTITY, entity);
        if (customFields != null) {
            scriptContext.put(ServiceScript.CONTEXT_PARAMETERS, customFields);
        } else {
            scriptContext.put(ServiceScript.CONTEXT_PARAMETERS, new ArrayList<CustomFieldDto>());
        }
        scriptInterface.afterCreateProductFromPMS(scriptContext);
    }
}