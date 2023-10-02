package org.meveo.service.billing.impl;

import org.apache.commons.collections4.CollectionUtils;
import org.meveo.admin.exception.BusinessException;
import org.meveo.commons.utils.StringUtils;
import org.meveo.model.billing.Invoice;
import org.meveo.model.billing.PDPStatusEntity;
import org.meveo.model.billing.PDPStatusEnum;
import org.meveo.model.billing.PDPStatusHistory;
import org.meveo.model.billing.TransmittedFormatEnum;
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
	private PdpStatusHistoryService PdpStatusHistoryService;
	
	public void assagnPdpStatus(TransmittedFormatEnum transmittedFormat, String origin, int returnCode, String label, String invoiceIdentifier, String invoiceNumber, PDPStatusEnum status, Date dateDeposit){
		if(StringUtils.isBlank(invoiceNumber)) {
			throw new BusinessException("the invoice number is missing");
		}
		List<Invoice> invoices = invoiceService.findByInvoicesNumber(invoiceNumber);
		
		
		if(CollectionUtils.isNotEmpty(invoices)) {
			Invoice invoice = invoices.get(0);
			PDPStatusEntity entity = setPdpStatus(transmittedFormat, origin, returnCode, label, invoiceIdentifier, invoiceNumber, status, dateDeposit, invoice);
			invoice.setPdpStatus(entity);
			invoiceService.update(invoice);
		}
	}
	
	private PDPStatusHistory createHistory(String origin, PDPStatusEnum statusEnum) {
		PDPStatusHistory entity = new PDPStatusHistory();
		entity.setOrigin(origin);
		entity.setPdpStatus(statusEnum);
		entity.setEventDate(new Date());
		PdpStatusHistoryService.create(entity);
		return entity;
	}
	
	private PDPStatusEntity setPdpStatus(TransmittedFormatEnum transmittedFormat, String origin, int returnCode, String label, String invoiceIdentifier, String invoiceNumber, PDPStatusEnum status, Date dateDeposit, Invoice invoice){
		PDPStatusEntity pdpStatusEntity = invoice.getPdpStatus() != null ? invoice.getPdpStatus() : new PDPStatusEntity();
		pdpStatusEntity.setTransmittedFormatEnum(transmittedFormat);
		pdpStatusEntity.setStatus(status);
		pdpStatusEntity.setOrigin(origin);
		pdpStatusEntity.setReturnCode(returnCode);
		pdpStatusEntity.setInvoiceNumber(invoiceNumber);
		pdpStatusEntity.setInvoiceIdentifier(invoiceIdentifier);
		pdpStatusEntity.setLabel(label);
		pdpStatusEntity.setDepositDate(dateDeposit);
		pdpStatusEntity.setInvoice(invoice);
		pdpStatusEntity.getPdpStatusHistories().add(createHistory(origin, status));
		if(invoice.getPdpStatus() == null)
			super.create(pdpStatusEntity);
		else
			super.update(pdpStatusEntity);
		
		return pdpStatusEntity;
	}
}
