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