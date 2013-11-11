package org.meveo.api.dto;

import java.util.List;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlRootElement;

/**
 * @author Edward P. Legaspi
 * @since Oct 11, 2013
 **/
@XmlRootElement(name = "offer")
@XmlAccessorType(XmlAccessType.FIELD)
public class OfferDto extends BaseDto {

	private static final long serialVersionUID = -137632696663739285L;
	private String offerId;
	private List<DescriptionDto> descriptions;
	private List<String> services;

	public String getOfferId() {
		return offerId;
	}

	public void setOfferId(String offerId) {
		this.offerId = offerId;
	}

	public List<DescriptionDto> getDescriptions() {
		return descriptions;
	}

	public void setDescriptions(List<DescriptionDto> descriptions) {
		this.descriptions = descriptions;
	}

	public List<String> getServices() {
		return services;
	}

	public void setServices(List<String> services) {
		this.services = services;
	}

}
