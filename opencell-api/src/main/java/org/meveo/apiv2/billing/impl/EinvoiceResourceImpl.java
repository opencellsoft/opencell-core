package org.meveo.apiv2.billing.impl;

import org.apache.commons.lang3.StringUtils;
import org.meveo.api.exception.EntityDoesNotExistsException;
import org.meveo.api.logging.WsRestApiInterceptor;
import org.meveo.apiv2.billing.EinvoiceSetting;
import org.meveo.apiv2.billing.resource.EinvoiceResource;
import org.meveo.model.billing.ElectronicInvoiceSetting;
import org.meveo.model.jobs.JobInstance;
import org.meveo.service.billing.impl.EinvoiceSettingService;
import org.meveo.service.job.JobInstanceService;

import javax.inject.Inject;
import javax.interceptor.Interceptors;
import javax.ws.rs.core.Response;
@Interceptors({ WsRestApiInterceptor.class })
public class EinvoiceResourceImpl implements EinvoiceResource {
	
	public static final EinvoiceMapper EINVOICE_MAPPER = new EinvoiceMapper();
	@Inject
	private EinvoiceSettingService einvoiceSettingService;
	@Inject
	private JobInstanceService jobInstanceService;
	
	private enum jobInstanceEInvoiceEnum {
		INVOICING,
		XML,
		PDF,
		UBL
	}
	@Override
	public Response updateEinvoiceSettings(EinvoiceSetting einvoiceSettingInput) {
		ElectronicInvoiceSetting einvoiceSetting = einvoiceSettingService.findEinvoiceSetting();
		if(einvoiceSetting == null) {
			einvoiceSettingService.create(EINVOICE_MAPPER.toEntity(einvoiceSettingInput));
			return Response.ok().build();
		}
		
		if(einvoiceSettingInput.getForceXmlGeneration() != null)
			einvoiceSetting.setForceXmlGeneration(einvoiceSettingInput.getForceXmlGeneration());
		if(einvoiceSettingInput.getForcePDFGeneration() != null)
			einvoiceSetting.setForcePDFGeneration(einvoiceSettingInput.getForcePDFGeneration());
		if(einvoiceSettingInput.getForceUBLGeneration() != null)
			einvoiceSetting.setForceUBLGeneration(einvoiceSettingInput.getForceUBLGeneration());
		
		checkAndAssignJobInstanceCodeExisting(einvoiceSettingInput.getInvoicingJob(), jobInstanceEInvoiceEnum.INVOICING, einvoiceSetting);
		checkAndAssignJobInstanceCodeExisting(einvoiceSettingInput.getXmlGenerationJob(), jobInstanceEInvoiceEnum.XML, einvoiceSetting);
		checkAndAssignJobInstanceCodeExisting(einvoiceSettingInput.getPdfGenerationJob(), jobInstanceEInvoiceEnum.PDF, einvoiceSetting);
		checkAndAssignJobInstanceCodeExisting(einvoiceSettingInput.getUblGenerationJob(), jobInstanceEInvoiceEnum.UBL, einvoiceSetting);
		
		einvoiceSettingService.update(einvoiceSetting);
		
		einvoiceSettingService.chainToNextJob(einvoiceSetting);
		
		return Response.noContent().build();
	}
	
	private void checkAndAssignJobInstanceCodeExisting(String jobInstanceCode, jobInstanceEInvoiceEnum jobInstanceEInvoiceEnum, ElectronicInvoiceSetting electronicInvoiceSetting) {
		if(StringUtils.isNotEmpty(jobInstanceCode)) {
			JobInstance jobInstance = jobInstanceService.findByCode(jobInstanceCode);
			if(jobInstance == null) {
				throw new EntityDoesNotExistsException(JobInstance.class, jobInstanceCode);
			}
			switch (jobInstanceEInvoiceEnum) {
				case INVOICING: electronicInvoiceSetting.setInvoicingJob(jobInstanceCode);break;
				case XML: electronicInvoiceSetting.setXmlGenerationJob(jobInstanceCode); break;
				case PDF: electronicInvoiceSetting.setPdfGenerationJob(jobInstanceCode); break;
				case UBL: electronicInvoiceSetting.setUblGenerationJob(jobInstanceCode); break;
			}
		}
	}
}
