package org.meveo.api.billing;

import org.meveo.api.BaseApi;
import org.meveo.api.dto.billing.PdpStatusDto;
import org.meveo.model.billing.PDPStatusEntity;
import org.meveo.service.billing.impl.EinvoiceService;

import javax.ejb.Stateless;
import javax.inject.Inject;

@Stateless
public class EinvoiceApi extends BaseApi {

	@Inject
	private EinvoiceService einvoiceService;
	
	
	public void assignPdpStatus(PdpStatusDto pdpStatusDto) {
		
		PDPStatusEntity pdpStatusEntity = new PDPStatusEntity();
		
		pdpStatusEntity.setTransmittedFormatEnum(pdpStatusDto.getTransmittedFormat());
		pdpStatusEntity.setStatus(pdpStatusDto.getStatus());
		pdpStatusEntity.setOrigin(pdpStatusDto.getOrigin());
		pdpStatusEntity.setReturnCode(pdpStatusDto.getReturnCode());
		pdpStatusEntity.setInvoiceNumber(pdpStatusDto.getInvoiceNumber());
		pdpStatusEntity.setInvoiceIdentifier(pdpStatusDto.getInvoiceIdentifier());
		pdpStatusEntity.setLabel(pdpStatusDto.getLabel());
		pdpStatusEntity.setDepositDate(pdpStatusDto.getDepositDate());
		einvoiceService.assagnPdpStatus(pdpStatusEntity);
	}
	
	
}
