package org.meveo.service.script.catalog;

import java.util.Map;

import org.meveo.model.rating.EDR;

/**
 * Interface use for creating a triggered edr script.
 * 
 * @author Edward P. Legaspi
 * @lastModifiedVersion 6.0
 */
public interface TriggeredEdrScriptInterface {

    /**
     * Updates the values of a given EDR.
     * 
     * @param methodContext values: EDR
     * @return the updated EDR
     */
    EDR updateEdr(Map<String, Object> methodContext);

}
