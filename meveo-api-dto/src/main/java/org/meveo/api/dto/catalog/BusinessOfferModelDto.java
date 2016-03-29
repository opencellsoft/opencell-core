package org.meveo.api.dto.catalog;

import java.util.List;

import javax.validation.constraints.NotNull;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlElementWrapper;
import javax.xml.bind.annotation.XmlRootElement;

import org.meveo.api.dto.module.ModuleDto;

@XmlRootElement(name = "BusinessOfferModel")
@XmlAccessorType(XmlAccessType.FIELD)
public class BusinessOfferModelDto extends ModuleDto {

	private static final long serialVersionUID = -7023791262640948222L;

	@NotNull
	@XmlElement(required = true)
	private String offerTemplateCode;

	private String scriptCode;

	@XmlElementWrapper(name = "bsmCodes")
	@XmlElement(name = "bsmCode")
	private List<String> bsmCodes;

	public String getOfferTemplateCode() {
		return offerTemplateCode;
	}

	public void setOfferTemplateCode(String offerTemplateCode) {
		this.offerTemplateCode = offerTemplateCode;
	}

	@Override
	public String toString() {
		return "BusinessOfferModelDto [offerTemplateCode=" + offerTemplateCode + ", scriptCode=" + scriptCode + ", bsmCodes=" + bsmCodes + ", toString()=" + super.toString() + "]";
	}

	public String getScriptCode() {
		return scriptCode;
	}

	public void setScriptCode(String scriptCode) {
		this.scriptCode = scriptCode;
	}

	public List<String> getBsmCodes() {
		return bsmCodes;
	}

	public void setBsmCodes(List<String> bsmCodes) {
		this.bsmCodes = bsmCodes;
	}

}
