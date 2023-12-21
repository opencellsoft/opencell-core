package org.meveo.api.dto.catalog;

import java.util.Map;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;

import io.swagger.v3.oas.annotations.media.Schema;


@XmlRootElement(name = "EnableOfferTemplateDto")
@XmlType(name = "EnableOfferTemplateDto")
@XmlAccessorType(XmlAccessType.FIELD)
public class EnableOfferTemplateDto {

	@XmlElement
	@Schema(description = "Provided filters to filter Offer Template")
    private Map<String, Object> filters;

	public Map<String, Object> getFilters() {
		return filters;
	}

	public void setFilters(Map<String, Object> filters) {
		this.filters = filters;
	}
    
}
