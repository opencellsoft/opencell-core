package org.meveo.api.dto.billing;

import java.util.List;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlRootElement;

import org.meveo.api.dto.BaseEntityDto;

@XmlRootElement(name = "InvoiceValidationDto")
@XmlAccessorType(XmlAccessType.FIELD)
public class InvoiceValidationDto extends BaseEntityDto {
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 4463519940872196926L;
	
	private List<Long> invoices;
	
	private Boolean deleteCanceledInvoices;
	
	/**
	 * @return the invoices
	 */
	public List<Long> getInvoices() {
		return invoices;
	}
	/**
	 * @param invoices the invoices to set
	 */
	public void setInvoices(List<Long> invoices) {
		this.invoices = invoices;
	}
	/**
	 * @return the deleteCanceledInvoices
	 */
	public Boolean getDeleteCanceledInvoices() {
		return deleteCanceledInvoices;
	}
	/**
	 * @param deleteCanceledInvoices the deleteCanceledInvoices to set
	 */
	public void setDeleteCanceledInvoices(Boolean deleteCanceledInvoices) {
		this.deleteCanceledInvoices = deleteCanceledInvoices;
	}
	
	
}