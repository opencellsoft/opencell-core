package org.meveo.util;

import java.math.BigDecimal;
import java.util.Map;

import org.meveo.admin.exception.BusinessException;
import org.meveo.model.billing.WalletOperation;
import org.meveo.service.script.Script;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class PPMScript extends org.meveo.service.script.Script {

    /**
	 * 
	 */
	private static final long serialVersionUID = -3883274241247376940L;
	private static final Logger log = LoggerFactory.getLogger(PPMScript.class);

    public void execute(Map<String, Object> context) throws BusinessException {
        log.info("EXECUTE context {}", context);
        WalletOperation wo = (WalletOperation) context.get(Script.CONTEXT_ENTITY);
        String param1 = (String) context.get("param1");
		BigDecimal amount = BigDecimal.ZERO; 
		//Business logic to calculate the amount
        context.put(Script.RESULT_VALUE,amount);
         
    }
}

