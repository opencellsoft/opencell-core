package org.meveo.service.script.catalog;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;

import javax.ejb.Stateless;
import javax.inject.Inject;

import org.meveo.admin.exception.BusinessException;
import org.meveo.model.rating.EDR;
import org.meveo.service.script.ScriptInstanceService;

/**
 * Takes care of {@link TriggeredEdrScript} related script method invocation.
 * 
 * @author Edward P. Legaspi
 * @lastModifiedVersion 6.0
 */
@Stateless
public class TriggeredEdrScriptService implements Serializable {

    private static final long serialVersionUID = 1L;

    @Inject
    private ScriptInstanceService scriptInstanceService;

    public EDR updateEdr(String scriptCode, EDR edr) throws BusinessException {
        TriggeredEdrScriptInterface scriptInterface = (TriggeredEdrScriptInterface) scriptInstanceService.getScriptInstance(scriptCode);

        Map<String, Object> scriptContext = new HashMap<>();
        scriptContext.put(TriggeredEdrScript.CONTEXT_ENTITY, edr);

        return scriptInterface.updateEdr(scriptContext);
    }

}
