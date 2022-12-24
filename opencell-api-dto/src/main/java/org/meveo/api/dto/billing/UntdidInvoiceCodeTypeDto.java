package org.meveo.api.dto.billing;

import javax.persistence.Column;
import javax.validation.constraints.Size;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlRootElement;

import org.meveo.api.dto.BaseEntityDto;
import org.meveo.model.billing.InvoiceType;
import org.meveo.model.billing.UntdidInvoiceCodeType;

@XmlRootElement()
@XmlAccessorType(XmlAccessType.FIELD)
public class UntdidInvoiceCodeTypeDto extends BaseEntityDto {
	
	private String interpretation16931;
	
	private String name;
	
	

	public UntdidInvoiceCodeTypeDto() {
		super();
	}

	public UntdidInvoiceCodeTypeDto(String interpretation16931, String name) {
		super();
		this.interpretation16931 = interpretation16931;
		this.name = name;
	}
	
	public UntdidInvoiceCodeTypeDto(UntdidInvoiceCodeType invoiceCodeType) {
		if(invoiceCodeType != null) {
			this.interpretation16931 = invoiceCodeType.getInterpretation16931();
			this.name= invoiceCodeType.getName();
		}
	}

	public String getInterpretation16931() {
		return interpretation16931;
	}

	public void setInterpretation16931(String interpretation16931) {
		this.interpretation16931 = interpretation16931;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}
	
	

}
