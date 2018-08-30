package org.meveo.service.script.payment;

import java.util.Map;

import org.meveo.admin.exception.BusinessException;
import org.meveo.service.script.ScriptInterface;

/**
 * The Interface DDRequestBuilderScriptInterface.
 *
 * @author anasseh
 * @lastModifiedVersion 5.2
 */
public interface DDRequestBuilderScriptInterface extends ScriptInterface {
   
    /**
     * Generate the dd request file.
     *
     * @param methodContext the method context
     * @throws BusinessException the business exception
     */
    void generateDDRequestLotFile(Map<String, Object> methodContext) throws BusinessException ;

  
    /**
     * Build DD request file name.
     *
     * @param methodContext the method context
     * @throws BusinessException the business exception
     */
    void getDDFileName(Map<String, Object> methodContext) throws BusinessException;

}