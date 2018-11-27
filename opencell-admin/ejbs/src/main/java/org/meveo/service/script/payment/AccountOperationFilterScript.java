package org.meveo.service.script.payment;

import java.util.List;
import java.util.Map;

import org.meveo.commons.utils.EjbUtils;
import org.meveo.model.payments.AccountOperation;
import org.meveo.service.script.Script;

/**
 * An abstract class extending ScriptInterface, to be used by a custom script, basically to filter AOs to pay
 * 
 * @author Said Ramli
 */
public abstract class AccountOperationFilterScript extends Script {

    public static final String LIST_AO_TO_PAY = "LIST_AO_TO_PAY";

    public abstract List<AccountOperation> filterAoToPay(Map<String, Object> methodContext);

    public void checkPaymentRetry(Map<String, Object> methodContext) {
        // To be overridden by the custom script implementation ...
    }

    protected Object getServiceInterface(String serviceInterfaceName) {
        return EjbUtils.getServiceInterface(serviceInterfaceName);
    }

}
