package org.meveo.service.payments.impl;

import org.meveo.admin.exception.BusinessException;
import org.meveo.model.crm.Provider;
import org.meveo.model.payments.DDRequestLOT;

/**
 * @author anasseh
 * @lastModifiedVersion 5.2
 *
 */
public interface DDRequestBuilderInterface {

       
   /**
    * Generate the ddRequest file for a given DDRequestLot and paymentGateway.
    * 
    * @param ddRequestLot The DDRequestLot to process.
    * @throws BusinessException
    */
    public void generateDDRequestLotFile(DDRequestLOT ddRequestLot,Provider appProvider) throws BusinessException;
    
    /**
     * Build the file name for a given DDRequestLot and paymentGateway.
     * 
     * @param ddRequestLot The DDRequestLot to process.
     * @return The payment file name
     * @throws BusinessException the BusinessException
     */
    public String getDDFileName(DDRequestLOT ddRequestLot,Provider appProvider) throws BusinessException;

   
}