package org.meveo.api.dto.payment;

import java.util.List;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlRootElement;

import org.meveo.api.dto.BaseDto;

@XmlRootElement(name = "dunningInclusionExclusion")
@XmlAccessorType(XmlAccessType.FIELD)
public class DunningInclusionExclusionDto extends BaseDto {

	private static final long serialVersionUID = 4329241417200680028L;

	private List<String> invoiceReferences;
	private Boolean exclude;
	
	public List<String> getInvoiceReferences() {
		return invoiceReferences;
	}
	public void setInvoiceReferences(List<String> invoiceReferences) {
		this.invoiceReferences = invoiceReferences;
	}
	public Boolean getExclude() {
		return exclude;
	}
	public void setExclude(Boolean exclude) {
		this.exclude = exclude;
	}
	
	

}
