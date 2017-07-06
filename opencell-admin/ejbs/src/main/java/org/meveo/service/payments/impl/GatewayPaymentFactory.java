package org.meveo.service.payments.impl;

import java.io.Serializable;

import javax.ejb.Stateless;
import javax.inject.Inject;

import org.meveo.admin.exception.ElementNotFoundException;
import org.meveo.admin.exception.InvalidScriptException;
import org.meveo.model.crm.EntityReferenceWrapper;
import org.meveo.service.crm.impl.CustomFieldInstanceService;
import org.meveo.service.crm.impl.ProviderService;
import org.meveo.service.script.ScriptInstanceService;
import org.meveo.service.script.payment.PaymentScriptInterface;

@Stateless
public class GatewayPaymentFactory implements Serializable{
	
	private static final long serialVersionUID = -8729566002684225810L;
	
	@Inject
    private ScriptInstanceService scriptInstanceService;
	
	@Inject
    private CustomFieldInstanceService customFieldInstanceService;
	
	@Inject
    private ProviderService providerService;
	
	/**
     * 
     * @param gatewayPaymentName
     * @return
	 * @throws InvalidScriptException 
	 * @throws ElementNotFoundException 
     */
	public GatewayPaymentInterface getInstance(GatewayPaymentNamesEnum gatewayPaymentName) throws ElementNotFoundException, InvalidScriptException{
		GatewayPaymentInterface gatewayPaymentInterface = null;
		if(GatewayPaymentNamesEnum.INGENICO_GC.name().equals(gatewayPaymentName.name())){
			gatewayPaymentInterface = new IngenicoGatewayPayment();
		}
		if(GatewayPaymentNamesEnum.CUSTOM_API.name().equals(gatewayPaymentName.name())){
			EntityReferenceWrapper entityReferenceWrapper = (EntityReferenceWrapper) customFieldInstanceService.getCFValue(providerService.getProvider(), "");	
			if(entityReferenceWrapper != null){
				gatewayPaymentInterface =  new CustomApiGatewayPayment((PaymentScriptInterface) scriptInstanceService.getScriptInstance(entityReferenceWrapper.getCode()));		
			}
		}
		if(gatewayPaymentInterface == null){
			throw new ElementNotFoundException(gatewayPaymentName.name(), "Payment gateway");
		}
		return gatewayPaymentInterface;
		
	}

}
