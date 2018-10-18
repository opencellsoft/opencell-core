package org.meveo.service.script.payment;

import java.util.List;
import java.util.Map;

import org.meveo.admin.exception.BusinessException;
import org.meveo.admin.sepa.DDRejectFileInfos;
import org.meveo.model.payments.AccountOperation;
import org.meveo.service.script.Script;


/**
 * The Class DDRequestBuilderScript.
 * 
 * @author anasseh
 * @author Said Ramli
 * @lastModifiedVersion 5.2
 */
public abstract class  DDRequestBuilderScript extends Script implements DDRequestBuilderScriptInterface {

    /** The Constant DD_REQUEST_LOT. */
    public static final String DD_REQUEST_LOT = "DD_REQUEST_LOT";
    
    public static final String DD_REQUEST_LIST_AO = "DD_REQUEST_LIST_AO";
    
    /** The Constant PROVIDER. */
    public static final String PROVIDER = "PROVIDER";

    public static final String DD_REJECT_PREFIX = "DD_REJECT_PREFIX";

    public static final String DD_REJECT_EXTENSION = "DD_REJECT_EXTENSION";

    public static final String DD_REJECT_FILE = "DD_REJECT_FILE";

    public static final String DD_REJECT_FILE_INFOS = "DD_REJECT_FILE_INFOS";

     @Override
    public void generateDDRequestLotFile(Map<String, Object> methodContext) throws BusinessException {        
    }
   
    @Override
    public String getDDFileName(Map<String, Object> methodContext) throws BusinessException { 
        return null;
    }
    
    @Override
    public void generateSCTRequestLotFile(Map<String, Object> methodContext) throws BusinessException {        
    }
   
    @Override
    public String getSCTFileName(Map<String, Object> methodContext) throws BusinessException { 
        return null;
    }

    @Override
    public String getDDRejectFilePrefix(Map<String, Object> methodContext) throws BusinessException {
        return null;
    }


    @Override
    public String getDDRejectFileExtension(Map<String, Object> methodContext) throws BusinessException {
        return null;
    }

    @Override
    public DDRejectFileInfos processDDRejectedFile(Map<String, Object> methodContext) throws BusinessException {
        return null;
    }
    
    @Override
    public List<AccountOperation> findListAoToPay(Map<String, Object> methodContext) throws BusinessException {
        return (List<AccountOperation>) methodContext.get(DD_REQUEST_LIST_AO);
    }
}