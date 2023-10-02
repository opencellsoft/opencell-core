package org.meveo.api.billing;

import org.meveo.api.BaseApi;
import org.meveo.api.dto.billing.PdpStatusDto;
import org.meveo.service.billing.impl.EinvoiceService;

import javax.ejb.Stateless;
import javax.inject.Inject;

@Stateless
public class EinvoiceApi extends BaseApi {

	@Inject
	private EinvoiceService einvoiceService;
	
	
	public void assignPdpStatus(PdpStatusDto pdpStatusDto) {
		einvoiceService.assagnPdpStatus(pdpStatusDto.getTransmittedFormat(), pdpStatusDto.getOrigin(), pdpStatusDto.getReturnCode(),
				pdpStatusDto.getLabel(), pdpStatusDto.getInvoiceIdentifier(), pdpStatusDto.getInvoiceNumber(), pdpStatusDto.getStatus(), pdpStatusDto.getDepositDate());
	}
	
	
}
