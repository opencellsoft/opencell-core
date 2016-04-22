package org.meveo.service.script.offer;

import java.util.Map;

import org.meveo.admin.exception.BusinessException;
import org.meveo.model.admin.User;
import org.meveo.service.script.Script;

/**
 * @author Edward P. Legaspi
 **/
public class OfferScript extends Script implements OfferScriptInterface {

    public static String CONTEXT_ACTIVATION_DATE = "CONTEXT_ACTIVATION_DATE";
    public static String CONTEXT_SUSPENSION_DATE = "CONTEXT_SUSPENSION_DATE";
    public static String CONTEXT_TERMINATION_DATE = "CONTEXT_TERMINATION_DATE";
    public static String CONTEXT_TERMINATION_REASON = "CONTEXT_TERMINATION_REASON";

    @Override
    public void createOfferTemplate(Map<String, Object> methodContext, User currentUser) throws BusinessException {

    }

    @Override
    public void updateOfferTemplate(Map<String, Object> methodContext, User currentUser) throws BusinessException {

    }

    @Override
    public void subscribe(Map<String, Object> methodContext, User currentUser) throws BusinessException {

    }

    @Override
    public void suspendSubscription(Map<String, Object> methodContext, User currentUser) throws BusinessException {

    }

    @Override
    public void reactivateSubscription(Map<String, Object> methodContext, User currentUser) throws BusinessException {

    }

    @Override
    public void terminateSubscription(Map<String, Object> methodContext, User currentUser) throws BusinessException {

    }
}