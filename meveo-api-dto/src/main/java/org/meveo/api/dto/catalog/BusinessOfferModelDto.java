package org.meveo.api.dto.catalog;

import javax.validation.constraints.NotNull;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

import org.meveo.api.dto.module.ModuleDto;
import org.meveo.model.catalog.BusinessOfferModel;

@XmlRootElement(name = "BusinessOfferModel")
@XmlAccessorType(XmlAccessType.FIELD)
public class BusinessOfferModelDto extends ModuleDto {

	private static final long serialVersionUID = -7023791262640948222L;

	@NotNull
	@XmlElement(required = true)
	private String offerTemplateCode;

	private String scriptCode;
	
	public BusinessOfferModelDto() {
		
	}

	public BusinessOfferModelDto(BusinessOfferModel e) {
		if (e.getOfferTemplate() != null) {
			offerTemplateCode = e.getOfferTemplate().getCode();
		}
		if (e.getScript() != null) {
			scriptCode = e.getScript().getCode();
		}
	}

	public BusinessOfferModelDto(ModuleDto dto) {
		super(dto);
	}

	public String getOfferTemplateCode() {
		return offerTemplateCode;
	}

	public void setOfferTemplateCode(String offerTemplateCode) {
		this.offerTemplateCode = offerTemplateCode;
	}

	@Override
	public String toString() {
		return "BusinessOfferModelDto [offerTemplateCode=" + offerTemplateCode + ", scriptCode=" + scriptCode + "]";
	}

	public String getScriptCode() {
		return scriptCode;
	}

	public void setScriptCode(String scriptCode) {
		this.scriptCode = scriptCode;
	}

}
