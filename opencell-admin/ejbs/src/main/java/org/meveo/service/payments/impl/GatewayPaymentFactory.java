package org.meveo.service.payments.impl;

import java.io.Serializable;

import javax.ejb.Stateless;
import javax.inject.Inject;

import org.meveo.admin.exception.ElementNotFoundException;
import org.meveo.admin.exception.InvalidScriptException;
import org.meveo.model.crm.EntityReferenceWrapper;
import org.meveo.model.crm.Provider;
import org.meveo.service.crm.impl.CustomFieldInstanceService;
import org.meveo.service.script.ScriptInstanceService;
import org.meveo.service.script.payment.PaymentScriptInterface;
import org.meveo.util.ApplicationProvider;

@Stateless
public class GatewayPaymentFactory implements Serializable {

    private static final long serialVersionUID = -8729566002684225810L;

    @Inject
    private ScriptInstanceService scriptInstanceService;

    @Inject
    private CustomFieldInstanceService customFieldInstanceService;

    @Inject
    @ApplicationProvider
    protected Provider appProvider;

    /**
     * 
     * @param gatewayPaymentName
     * @return
     * @throws InvalidScriptException
     * @throws Exception
     */
    public GatewayPaymentInterface getInstance(GatewayPaymentNamesEnum gatewayPaymentName) throws Exception, InvalidScriptException {
        GatewayPaymentInterface gatewayPaymentInterface = null;
        if (GatewayPaymentNamesEnum.INGENICO_GC.name().equals(gatewayPaymentName.name())) {
            gatewayPaymentInterface = new IngenicoGatewayPayment();
        }
        if (GatewayPaymentNamesEnum.CUSTOM_API.name().equals(gatewayPaymentName.name())) {
            EntityReferenceWrapper entityReferenceWrapper = (EntityReferenceWrapper) customFieldInstanceService.getCFValue(appProvider, "CF_PRV_GW_PAY_SCRIPT");
            if (entityReferenceWrapper != null) {
                gatewayPaymentInterface = new CustomApiGatewayPayment((PaymentScriptInterface) scriptInstanceService.getScriptInstance(entityReferenceWrapper.getCode()));
            }
        }
        if (gatewayPaymentInterface == null) {
            throw new Exception("Payment gateway with code=" + gatewayPaymentName.name() + " not found" );
        }
        return gatewayPaymentInterface;

    }

}
