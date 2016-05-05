package org.meveo.api.dto.response.catalog;

import java.util.List;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlRootElement;

import org.meveo.api.dto.response.BaseResponse;
import org.meveo.api.dto.script.OfferModelScriptDto;

/**
 * @author Edward P. Legaspi
 **/
@XmlRootElement(name = "GetOfferModelScriptResponse")
@XmlAccessorType(XmlAccessType.FIELD)
public class GetOfferModelScriptResponseDto extends BaseResponse {

	private static final long serialVersionUID = 6927904280468869975L;

	private List<OfferModelScriptDto> offerModelScripts;

	public List<OfferModelScriptDto> getOfferModelScripts() {
		return offerModelScripts;
	}

	public void setOfferModelScripts(List<OfferModelScriptDto> offerModelScripts) {
		this.offerModelScripts = offerModelScripts;
	}

	@Override
	public String toString() {
		return "GetOfferModelScriptResponseDto [offerModelScripts=" + offerModelScripts + ", toString()=" + super.toString() + "]";
	}

}
