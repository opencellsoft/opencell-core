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
		einvoice.setForceXmlGeneration(resource.getForceXmlGeneration() != null ? resource.getForceXmlGeneration() : false);
		einvoice.setForcePDFGeneration(resource.getForcePDFGeneration() != null ? resource.getForcePDFGeneration() : false);
		einvoice.setForceUBLGeneration(resource.getForceUBLGeneration() != null ? resource.getForceUBLGeneration() : false);
		einvoice.setInvoicingJob(resource.getInvoicingJob());
		einvoice.setXmlGenerationJob(resource.getXmlGenerationJob());
		einvoice.setPdfGenerationJob(resource.getPdfGenerationJob());
		einvoice.setUblGenerationJob(resource.getUblGenerationJob());
		return einvoice;
	}
}
