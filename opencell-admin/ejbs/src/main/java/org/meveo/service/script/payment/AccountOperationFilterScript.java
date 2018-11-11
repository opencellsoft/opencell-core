package org.meveo.service.script.payment;

import java.util.List;
import java.util.Map;

import org.meveo.admin.exception.BusinessException;
import org.meveo.commons.utils.EjbUtils;
import org.meveo.model.payments.AccountOperation;
import org.meveo.service.script.ScriptInterface;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * An abstract class extending ScriptInterface, to be used by a custom script, basically to filter AOs to pay
 * @author Said Ramli
 */
public abstract class AccountOperationFilterScript implements ScriptInterface {
    
    protected Logger log = LoggerFactory.getLogger(this.getClass());
    
    public static final String LIST_AO_TO_PAY = "LIST_AO_TO_PAY";

    @Override
    public void init(Map<String, Object> methodContext) throws BusinessException {
    }

    @Override
    public void execute(Map<String, Object> methodContext) throws BusinessException {
    }

    @Override
    public void finalize(Map<String, Object> methodContext) throws BusinessException {
    }

    public abstract List<AccountOperation> filterAoToPay(Map<String, Object> methodContext);

    public void checkPaymentRetry(Map<String, Object> methodContext) {
        // To be overridden by the custom script implementation ... 
    }
    
    protected Object getServiceInterface(String serviceInterfaceName) {
        return EjbUtils.getServiceInterface(serviceInterfaceName);
    }
    
}
