package org.meveo.apiv2.billing.impl;

import org.meveo.apiv2.billing.EinvoiceSetting;
import org.meveo.apiv2.billing.ImmutableEinvoiceSetting;
import org.meveo.apiv2.generic.ResourceMapper;
import org.meveo.model.billing.ElectronicInvoiceSetting;

public class EinvoiceMapper extends ResourceMapper<EinvoiceSetting, ElectronicInvoiceSetting> {
	
	
	@Override
	protected EinvoiceSetting toResource(ElectronicInvoiceSetting entity) {
		return ImmutableEinvoiceSetting.builder()
				.id(entity.getId())
				.forceXmlGeneration(entity.isForceXmlGeneration())
				.forcePDFGeneration(entity.isForcePDFGeneration())
				.forceUBLGeneration(entity.isForceUBLGeneration())
				.invoicingJob(entity.getInvoicingJob())
				.xmlGenerationJob(entity.getXmlGenerationJob())
				.pdfGenerationJob(entity.getPdfGenerationJob())
				.ublGenerationJob(entity.getUblGenerationJob())
				.build();
	}
	
	@Override
	protected ElectronicInvoiceSetting toEntity(EinvoiceSetting resource) {
		ElectronicInvoiceSetting einvoice = new ElectronicInvoiceSetting();
		einvoice.setForceXmlGeneration(resource.getForceXmlGeneration() || Boolean.FALSE);
		einvoice.setForcePDFGeneration(resource.getForcePDFGeneration() || Boolean.FALSE);
		einvoice.setForceUBLGeneration(resource.getForceUBLGeneration() || Boolean.FALSE);
		einvoice.setInvoicingJob(resource.getInvoicingJob());
		einvoice.setXmlGenerationJob(resource.getXmlGenerationJob());
		einvoice.setPdfGenerationJob(resource.getPdfGenerationJob());
		einvoice.setUblGenerationJob(resource.getUblGenerationJob());
		return einvoice;
	}
}
