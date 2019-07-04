package org.meveo.service.payments.impl;

import java.io.File;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.meveo.admin.exception.BusinessException;
import org.meveo.admin.sepa.DDRejectFileInfos;
import org.meveo.model.crm.Provider;
import org.meveo.model.payments.AccountOperation;
import org.meveo.model.payments.DDRequestLOT;
import org.meveo.model.payments.DDRequestLotOp;
import org.meveo.service.script.payment.DDRequestBuilderScript;
import org.meveo.service.script.payment.DDRequestBuilderScriptInterface;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * The Class CustomDDRequestBuilder.
 *
 * @author anasseh
 * @author Said Ramli
 * @lastModifiedVersion 5.3
 */

public class CustomDDRequestBuilder extends AbstractDDRequestBuilder {

    /** The log. */
    protected Logger log = LoggerFactory.getLogger(CustomDDRequestBuilder.class);

    /** The dd request builder script interface. */
    private DDRequestBuilderScriptInterface ddRequestBuilderScriptInterface;

    /**
     * Instantiates a new custom DD request builder.
     *
     * @param ddRequestBuilderScriptInterface the dd request builder script interface
     */
    public CustomDDRequestBuilder(DDRequestBuilderScriptInterface ddRequestBuilderScriptInterface) {
        this.ddRequestBuilderScriptInterface = ddRequestBuilderScriptInterface;
    }

    @Override
    public void generateDDRequestLotFile(DDRequestLOT ddRequestLot, Provider appProvider) throws BusinessException {
        Map<String, Object> scriptContext = new HashMap<String, Object>();
        scriptContext.put(DDRequestBuilderScript.DD_REQUEST_LOT, ddRequestLot);
        scriptContext.put(DDRequestBuilderScript.PROVIDER, appProvider);
        ddRequestBuilderScriptInterface.generateDDRequestLotFile(scriptContext);
    }

    @Override
    public String getDDFileName(DDRequestLOT ddRequestLot, Provider appProvider) throws BusinessException {
        Map<String, Object> scriptContext = new HashMap<String, Object>();
        scriptContext.put(DDRequestBuilderScript.DD_REQUEST_LOT, ddRequestLot);
        scriptContext.put(DDRequestBuilderScript.PROVIDER, appProvider);
        return ddRequestBuilderScriptInterface.getDDFileName(scriptContext);

    }

    @Override
    public List<AccountOperation> findListAoToPay(DDRequestLotOp ddrequestLotOp) throws BusinessException {
        Map<String, Object> scriptContext = new HashMap<String, Object>();
        scriptContext.put(DDRequestBuilderScript.DD_REQUEST_LIST_AO, super.findListAoToPay(ddrequestLotOp));
        return ddRequestBuilderScriptInterface.findListAoToPay(scriptContext);
    }

    @Override
    public DDRejectFileInfos processSCTRejectedFile(File file) throws BusinessException {
        Map<String, Object> scriptContext = new HashMap<String, Object>();
        scriptContext.put(DDRequestBuilderScript.DD_REJECT_FILE, file);
        return ddRequestBuilderScriptInterface.processSCTRejectedFile(scriptContext);
    }

    @Override
    public DDRejectFileInfos processSDDRejectedFile(File file) throws BusinessException {
        Map<String, Object> scriptContext = new HashMap<String, Object>();
        scriptContext.put(DDRequestBuilderScript.DD_REJECT_FILE, file);
        return ddRequestBuilderScriptInterface.processSDDRejectedFile(scriptContext);
    }

}