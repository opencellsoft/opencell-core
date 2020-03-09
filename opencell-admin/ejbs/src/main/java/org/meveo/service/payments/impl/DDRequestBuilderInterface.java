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

package org.meveo.service.payments.impl;

import java.io.File;
import java.util.List;

import org.meveo.admin.exception.BusinessException;
import org.meveo.admin.sepa.DDRejectFileInfos;
import org.meveo.model.crm.Provider;
import org.meveo.model.payments.AccountOperation;
import org.meveo.model.payments.DDRequestLOT;
import org.meveo.model.payments.DDRequestLotOp;

/**
 * @author anasseh
 * @author Said Ramli
 * @lastModifiedVersion 5.3
 *
 */
public interface DDRequestBuilderInterface {
    
    /**
     * Find list of Account operations to pay or refund according the current ddrequestLotOp .
     *
     * @param ddrequestLotOp the ddrequest lot op
     * @return the list of account operation
     * @throws BusinessException the business exception
     */
    public List<AccountOperation> findListAoToPay(DDRequestLotOp ddrequestLotOp) throws BusinessException;
    
         
   /**
    * Generate the  sdd or sct request file for a given DDRequestLot.
    * 
    * @param ddRequestLot The DDRequestLot to process.
    * @throws BusinessException the BusinessException.
    */
    public void generateDDRequestLotFile(DDRequestLOT ddRequestLot,Provider appProvider) throws BusinessException;
       
    
    /**
     * Build the the  sdd or sct file name for a given DDRequestLot.
     * 
     * @param ddRequestLot The DDRequestLot to process.
     * @return The payment file name.
     * @throws BusinessException the BusinessException.
     */
    public String getDDFileName(DDRequestLOT ddRequestLot,Provider appProvider) throws BusinessException;
      
    
    /**
     * Process the sdd reject file.
     * 
     * @param file The sdd reject file to process.
     * @throws BusinessException the BusinessException.
     */
    public DDRejectFileInfos processSDDRejectedFile(File file) throws BusinessException;
    
    /**
     * Process the sct reject file.
     * 
     * @param file The sct reject file to process.
     * @throws BusinessException the BusinessException.
     */
    public DDRejectFileInfos processSCTRejectedFile(File file) throws BusinessException;
          
}