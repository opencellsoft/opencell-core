package org.meveo.service.payments.impl;

import java.io.File;

import org.meveo.admin.exception.BusinessException;
import org.meveo.admin.sepa.DDRejectFileInfos;
import org.meveo.model.crm.Provider;
import org.meveo.model.payments.DDRequestLOT;

/**
 * @author anasseh
 * @lastModifiedVersion 5.2
 *
 */
public interface DDRequestBuilderInterface {

       
   /**
    * Generate the ddRequest file for a given DDRequestLot.
    * 
    * @param ddRequestLot The DDRequestLot to process.
    * @throws BusinessException the BusinessException.
    */
    public void generateDDRequestLotFile(DDRequestLOT ddRequestLot,Provider appProvider) throws BusinessException;
    
    /**
     * Build the file name for a given DDRequestLot.
     * 
     * @param ddRequestLot The DDRequestLot to process.
     * @return The payment file name.
     * @throws BusinessException the BusinessException.
     */
    public String getDDFileName(DDRequestLOT ddRequestLot,Provider appProvider) throws BusinessException;
    
    /**
     * Return the prefix for the DD reject file.
     * 
     * @return The prefix.
     * @throws BusinessException the BusinessException.
     */
    public String getDDRejectFilePrefix() throws BusinessException;
    
    /**
     * Return the extension for the DD reject file.
     * 
     * @return The extension.
     * @throws BusinessException the BusinessException.
     */
    public String getDDRejectFileExtension() throws BusinessException;
    
    /**
     * Process the dd reject file.
     * 
     * @param file The dd reject file to process.
     * @throws BusinessException the BusinessException.
     */
    public DDRejectFileInfos processDDRejectedFile(File file) throws BusinessException;

    /**
     * Generate the SCT Request file for a given DDRequestLot.
     * 
     * @param ddRequestLot The DDRequestLot to process.
     * @param appProvider The provider
     * @throws BusinessException the BusinessException.
     */
    void generateSCTRequestLotFile(DDRequestLOT ddRequestLot, Provider appProvider) throws BusinessException;

    /**
     * Build SCT request file name.
     *
     * @param ddRequestLot The DDRequestLot to process.
     * @param appProvider The provider
     * @return the SCT file name
     * @throws BusinessException the business exception
     */
    String getSCTFileName(DDRequestLOT ddRequestLot, Provider appProvider) throws BusinessException;

   
}