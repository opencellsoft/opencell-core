package org.meveo.api.dto.catalog;

import java.util.List;
import java.util.Map;

import javax.validation.constraints.NotNull;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

import org.meveo.api.dto.BaseDto;
import org.meveo.api.dto.CustomFieldsDto;


@XmlRootElement(name = "BomOffer")
@XmlAccessorType(XmlAccessType.FIELD)
public class BomOfferDto extends BaseDto {

	private static final long serialVersionUID = 4557706201829891403L;

	private String bomVersion;

	@NotNull
	@XmlAttribute(required = true)
	private String bomCode;

	@NotNull
	@XmlElement(required = true)
	private String offerCode;

	private CustomFieldsDto offerCustomFields;

	private String serviceCodePrefix;

	private List<String> servicesToActivate;

	private Map<String, CustomFieldsDto> serviceCFVs;

	public String getBomVersion() {
		return bomVersion;
	}

	public void setBomVersion(String bomVersion) {
		this.bomVersion = bomVersion;
	}

	public String getBomCode() {
		return bomCode;
	}

	public void setBomCode(String bomCode) {
		this.bomCode = bomCode;
	}

	public String getOfferCode() {
		return offerCode;
	}

	public void setOfferCode(String offerCode) {
		this.offerCode = offerCode;
	}

	public CustomFieldsDto getOfferCustomFields() {
		return offerCustomFields;
	}

	public void setOfferCustomFields(CustomFieldsDto offerCustomFields) {
		this.offerCustomFields = offerCustomFields;
	}

	public String getServiceCodePrefix() {
		return serviceCodePrefix;
	}

	public void setServiceCodePrefix(String serviceCodePrefix) {
		this.serviceCodePrefix = serviceCodePrefix;
	}

	public List<String> getServicesToActivate() {
		return servicesToActivate;
	}

	public void setServicesToActivate(List<String> servicesToActivate) {
		this.servicesToActivate = servicesToActivate;
	}

	public Map<String, CustomFieldsDto> getServiceCFVs() {
		return serviceCFVs;
	}

	public void setServiceCFVs(Map<String, CustomFieldsDto> serviceCFVs) {
		this.serviceCFVs = serviceCFVs;
	}

	@Override
	public String toString() {
		return "BomOfferDto [bomVersion=" + bomVersion + ", bomCode=" + bomCode + ", offerCode=" + offerCode
				+ ", offerCustomFields=" + offerCustomFields + ", serviceCodePrefix=" + serviceCodePrefix
				+ ", servicesToActivate=" + servicesToActivate + ", serviceCFVs=" + serviceCFVs + "]";
	}
}
