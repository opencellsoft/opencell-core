package org.meveo.api.dto.billing;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlRootElement;

import org.meveo.api.dto.BaseEntityDto;
import org.meveo.model.billing.UntdidVatPaymentOption;

@XmlRootElement()
@XmlAccessorType(XmlAccessType.FIELD)
public class UntdidVatPaymentOptionDto extends BaseEntityDto {
	
	private String code2005;
	
	private String value2005;
	
	private String code2475;
	
	private String value2475;
	
	

	public UntdidVatPaymentOptionDto() {
		super();
	}

	public UntdidVatPaymentOptionDto(String code2005, String value2005, String code2475, String value2475) {
		super();
		this.code2005 = code2005;
		this.value2005 = value2005;
		this.code2475 = code2475;
		this.value2475 = value2475;
	}
	
	public UntdidVatPaymentOptionDto(UntdidVatPaymentOption untdidVatPaymentOption) {
		if(untdidVatPaymentOption != null) {
			this.code2005 = untdidVatPaymentOption.getCode2005();
			this.value2005 = untdidVatPaymentOption.getValue2005();
			this.code2475 = untdidVatPaymentOption.getCode2475();
			this.value2475 = untdidVatPaymentOption.getValue2475();
		}
	}


	public String getCode2005() {
		return code2005;
	}

	public void setCode2005(String code2005) {
		this.code2005 = code2005;
	}

	public String getValue2005() {
		return value2005;
	}

	public void setValue2005(String value2005) {
		this.value2005 = value2005;
	}

	public String getCode2475() {
		return code2475;
	}

	public void setCode2475(String code2475) {
		this.code2475 = code2475;
	}

	public String getValue2475() {
		return value2475;
	}

	public void setValue2475(String value2475) {
		this.value2475 = value2475;
	}	
}
