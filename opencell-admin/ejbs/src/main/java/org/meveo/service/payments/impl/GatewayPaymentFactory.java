package org.meveo.service.payments.impl;

public class GatewayPaymentFactory {
	
	/**
     * 
     * @param gatewayPaymentName
     * @return
     */
	public static GatewayPaymentInterface getInstance(GatewayPaymentNamesEnum gatewayPaymentName){
		if(GatewayPaymentNamesEnum.INGENICO.name().equals(gatewayPaymentName.name())){
			return new IngenicoGatewayPayment();
		}
		return null;
		
	}

}
