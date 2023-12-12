package org.meveo.service.billing.impl;

import org.apache.commons.collections4.CollectionUtils;
import org.meveo.admin.exception.BusinessException;
import org.meveo.api.exception.EntityDoesNotExistsException;
import org.meveo.commons.utils.StringUtils;
import org.meveo.model.billing.Invoice;
import org.meveo.model.billing.PDPStatusEntity;
import org.meveo.model.billing.PDPStatusEnum;
import org.meveo.model.billing.PDPStatusHistory;
import org.meveo.service.base.PersistenceService;

import javax.ejb.Stateless;
import javax.inject.Inject;
import java.util.Date;
import java.util.List;

@Stateless
public class EinvoiceService extends PersistenceService<PDPStatusEntity> {
	
	@Inject
	private InvoiceService invoiceService;
	@Inject
	private PdpStatusHistoryService pdpStatusHistoryService;
	
	public void assagnPdpStatus(PDPStatusEntity pdpStatusEntity){
		if(StringUtils.isBlank(pdpStatusEntity.getInvoiceNumber())) {
			throw new BusinessException("the invoice number is missing");
		}
		List<Invoice> invoices = invoiceService.findByInvoicesNumber(pdpStatusEntity.getInvoiceNumber());
		
		
		
		if(CollectionUtils.isNotEmpty(invoices)) {
			Invoice invoice = invoices.get(0);
			
			PDPStatusEntity entity = setPdpStatus(pdpStatusEntity, invoice);
			invoice.setPdpStatus(entity);
			invoiceService.update(invoice);
		}else{
			throw new EntityDoesNotExistsException(Invoice.class, pdpStatusEntity.getInvoiceNumber());
		}
	}
	
	private PDPStatusHistory createHistory(String origin, PDPStatusEnum statusEnum) {
		PDPStatusHistory entity = new PDPStatusHistory();
		entity.setOrigin(origin);
		entity.setPdpStatus(statusEnum);
		entity.setEventDate(new Date());
		pdpStatusHistoryService.create(entity);
		return entity;
	}
	
	private PDPStatusEntity setPdpStatus(PDPStatusEntity pdpStatusEntity, Invoice invoice){
		pdpStatusEntity.getPdpStatusHistories().add(createHistory(pdpStatusEntity.getOrigin(), pdpStatusEntity.getStatus()));
		if(invoice.getPdpStatus() != null) {
			PDPStatusEntity currentPdpStatus = invoice.getPdpStatus();
			currentPdpStatus.setTransmittedFormatEnum(pdpStatusEntity.getTransmittedFormatEnum());
			currentPdpStatus.setStatus(pdpStatusEntity.getStatus());
			currentPdpStatus.setOrigin(pdpStatusEntity.getOrigin());
			currentPdpStatus.setReturnCode(pdpStatusEntity.getReturnCode());
			currentPdpStatus.setInvoiceNumber(pdpStatusEntity.getInvoiceNumber());
			currentPdpStatus.setInvoiceIdentifier(pdpStatusEntity.getInvoiceIdentifier());
			currentPdpStatus.setLabel(pdpStatusEntity.getLabel());
			currentPdpStatus.setDepositDate(pdpStatusEntity.getDepositDate());
			currentPdpStatus.getPdpStatusHistories().addAll(pdpStatusEntity.getPdpStatusHistories());
			super.update(pdpStatusEntity);
			return currentPdpStatus;
		}else{
			super.create(pdpStatusEntity);
		}
		return pdpStatusEntity;
	}
}
