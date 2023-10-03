package org.meveo.service.billing.impl;

import org.apache.commons.collections4.CollectionUtils;
import org.meveo.admin.exception.BusinessException;
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
		pdpStatusEntity = invoice.getPdpStatus() != null ? invoice.getPdpStatus() : pdpStatusEntity;
		pdpStatusEntity.getPdpStatusHistories().add(createHistory(pdpStatusEntity.getOrigin(), pdpStatusEntity.getStatus()));
		if(invoice.getPdpStatus() != null) {
			pdpStatusEntity.setId(invoice.getPdpStatus().getId());
			super.update(pdpStatusEntity);
		}else{
			super.create(pdpStatusEntity);
		}
		return pdpStatusEntity;
	}
}
