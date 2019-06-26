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
    public static String CONTEXT_WO = "CONTEXT_WO";

    @Override
    public EDR updateEdr(Map<String, Object> methodContext) {
        return (EDR) methodContext.get(CONTEXT_ENTITY);
    }

}
