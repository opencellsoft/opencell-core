package org.meveo.service.script.payment;

import java.util.List;
import java.util.Map;

import org.meveo.admin.exception.BusinessException;
import org.meveo.admin.sepa.DDRejectFileInfos;
import org.meveo.model.payments.AccountOperation;
import org.meveo.service.script.ScriptInterface;

/**
 * The Interface DDRequestBuilderScriptInterface.
 *
 * @author anasseh
 * @author Said Ramli
 * @lastModifiedVersion 5.3
 */
public interface DDRequestBuilderScriptInterface extends ScriptInterface {
   
    /**
     * Generate the sdd or sct request file.
     *
     * @param methodContext the method context
     * @throws BusinessException the business exception
     */
    public void generateDDRequestLotFile(Map<String, Object> methodContext) throws BusinessException ;

  
    /**
     * Build the sdd or sct request file name.
     *
     * @param methodContext the method context
     * @return the DD file name
     * @throws BusinessException the business exception
     */
    public String getDDFileName(Map<String, Object> methodContext) throws BusinessException;
    
    
    /**
     * Process the sdd reject file.
     * 
     * @param methodContext the context.
     * @return The ddRejectFileInfos
     * @throws BusinessException the BusinessException.
     */
    public DDRejectFileInfos processSDDRejectedFile(Map<String, Object> methodContext) throws BusinessException;
    
    /**
     * Process the sct reject file.
     * 
     * @param methodContext the context.
     * @return The ddRejectFileInfos
     * @throws BusinessException the BusinessException.
     */
    public DDRejectFileInfos processSCTRejectedFile(Map<String, Object> methodContext) throws BusinessException;

    
   /**
    * Find list of Account operations to pay or refund according the current ddrequestLotOp .
    * 
    * @param methodContext
    * @return the list of account operation
    * @throws BusinessException
    */
    public List<AccountOperation> findListAoToPay(Map<String, Object> methodContext) throws BusinessException;
    
}