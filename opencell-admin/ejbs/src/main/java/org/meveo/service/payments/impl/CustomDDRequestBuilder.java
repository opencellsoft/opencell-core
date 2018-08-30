package org.meveo.service.payments.impl;

import java.util.HashMap;
import java.util.Map;

import org.meveo.admin.exception.BusinessException;
import org.meveo.model.crm.Provider;
import org.meveo.model.payments.DDRequestLOT;

import org.meveo.service.script.payment.DDRequestBuilderScript;
import org.meveo.service.script.payment.DDRequestBuilderScriptInterface;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * 
 *  @author anasseh
 *  @lastModifiedVersion 5.2
 */

public class CustomDDRequestBuilder implements DDRequestBuilderInterface {

    protected Logger log = LoggerFactory.getLogger(CustomDDRequestBuilder.class);
    private DDRequestBuilderScriptInterface ddRequestBuilderScriptInterface;

    public CustomDDRequestBuilder(DDRequestBuilderScriptInterface ddRequestBuilderScriptInterface) {
        this.ddRequestBuilderScriptInterface = ddRequestBuilderScriptInterface;
    }

  
    @Override
    public void generateDDRequestLotFile(DDRequestLOT ddRequestLot,Provider appProvider) throws BusinessException {
        Map<String, Object> scriptContext = new HashMap<String, Object>();
        scriptContext.put(DDRequestBuilderScript.DD_REQUEST_LOT, ddRequestLot);    
        scriptContext.put(DDRequestBuilderScript.PROVIDER, appProvider);    
        ddRequestBuilderScriptInterface.generateDDRequestLotFile(scriptContext);        
    }

  
    @Override
    public String getDDFileName(DDRequestLOT ddRequestLot,Provider appProvider) throws BusinessException {
        Map<String, Object> scriptContext = new HashMap<String, Object>();
        scriptContext.put(DDRequestBuilderScript.DD_REQUEST_LOT, ddRequestLot);  
        scriptContext.put(DDRequestBuilderScript.PROVIDER, appProvider); 
        ddRequestBuilderScriptInterface.getDDFileName(scriptContext);
        return (String) scriptContext.get(DDRequestBuilderScript.FILE_NAME);
    }

}