package org.meveo.model.billing;

import org.hibernate.annotations.GenericGenerator;
import org.hibernate.annotations.Parameter;
import org.hibernate.annotations.Type;
import org.meveo.model.AuditableEntity;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;

@Entity
@Table(name = "electronic_invoice_settings")
@GenericGenerator(name = "ID_GENERATOR", strategy = "org.hibernate.id.enhanced.SequenceStyleGenerator", parameters = {
		@Parameter(name = "sequence_name", value = "electronic_invoice_settings_seq"), })
public class ElectronicInvoiceSetting  extends AuditableEntity {
	
	@Type(type = "numeric_boolean")
	@Column(name = "force_xml_generation")
	private boolean forceXmlGeneration = false;
	
	@Type(type = "numeric_boolean")
	@Column(name = "force_pdf_generation")
	private boolean forcePDFGeneration = false;
	
	@Type(type = "numeric_boolean")
	@Column(name = "force_ubl_generation")
	private boolean forceUBLGeneration = false;
	
	@Column(name = "invoicing_job")
	private String invoicingJob;
	
	@Column(name = "pdf_generation_job")
	private String pdfGenerationJob;
	
	@Column(name = "ubl_generation_job")
	private String ublGenerationJob;
	
	@Column(name = "xml_generation_job")
	private String xmlGenerationJob;
	
	public ElectronicInvoiceSetting(){}
	
	public boolean isForceXmlGeneration() {
		return forceXmlGeneration;
	}
	
	public void setForceXmlGeneration(boolean forceXmlGeneration) {
		this.forceXmlGeneration = forceXmlGeneration;
	}
	
	public boolean isForcePDFGeneration() {
		return forcePDFGeneration;
	}
	
	public void setForcePDFGeneration(boolean forcePDFGeneration) {
		this.forcePDFGeneration = forcePDFGeneration;
	}
	
	public boolean isForceUBLGeneration() {
		return forceUBLGeneration;
	}
	
	public void setForceUBLGeneration(boolean forceUBLGeneration) {
		this.forceUBLGeneration = forceUBLGeneration;
	}
	
	public String getInvoicingJob() {
		return invoicingJob;
	}
	
	public void setInvoicingJob(String invoicingJob) {
		this.invoicingJob = invoicingJob;
	}
	
	public String getPdfGenerationJob() {
		return pdfGenerationJob;
	}
	
	public void setPdfGenerationJob(String pdfGenerationJob) {
		this.pdfGenerationJob = pdfGenerationJob;
	}
	
	public String getUblGenerationJob() {
		return ublGenerationJob;
	}
	
	public void setUblGenerationJob(String ublGenerationJob) {
		this.ublGenerationJob = ublGenerationJob;
	}
	
	public String getXmlGenerationJob() {
		return xmlGenerationJob;
	}
	
	public void setXmlGenerationJob(String xmlGenerationJob) {
		this.xmlGenerationJob = xmlGenerationJob;
	}
}
