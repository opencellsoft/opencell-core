package org.meveo.api.dto.response.catalog;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlRootElement;

import org.meveo.api.dto.catalog.OfferTemplateDto;
import org.meveo.api.dto.response.BaseResponse;

/**
 * @author Edward P. Legaspi
 **/
@XmlRootElement(name = "GetOfferTemplateResponse")
@XmlAccessorType(XmlAccessType.FIELD)
public class GetOfferTemplateResponse extends BaseResponse {

	private static final long serialVersionUID = -8776189890084137788L;

	public OfferTemplateDto offerTemplate;

	public OfferTemplateDto getOfferTemplate() {
		return offerTemplate;
	}

	public void setOfferTemplate(OfferTemplateDto offerTemplate) {
		this.offerTemplate = offerTemplate;
	}

	@Override
	public String toString() {
		return "GetOfferTemplateResponse [offerTemplate=" + offerTemplate + ", toString()=" + super.toString() + "]";
	}

}
