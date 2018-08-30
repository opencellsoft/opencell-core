package org.meveo.service.script.payment;

import java.util.Map;

import org.meveo.admin.exception.BusinessException;
import org.meveo.service.script.Script;


/**
 * The Class DDRequestBuilderScript.
 * 
 * @author anasseh
 * @lastModifiedVersion 5.2
 */
public class DDRequestBuilderScript extends Script implements DDRequestBuilderScriptInterface {

    /** The Constant DD_REQUEST_LOT. */
    public static final String DD_REQUEST_LOT = "DD_REQUEST_LOT";
    
    /** The Constant PROVIDER. */
    public static final String PROVIDER = "PROVIDER";
    
    /** The Constant FILE_NAME. */
    public static final String FILE_NAME = "FILE_NAME";

     @Override
    public void generateDDRequestLotFile(Map<String, Object> methodContext) throws BusinessException {        
    }
   
    @Override
    public void getDDFileName(Map<String, Object> methodContext) throws BusinessException {       
    }
    
    
}