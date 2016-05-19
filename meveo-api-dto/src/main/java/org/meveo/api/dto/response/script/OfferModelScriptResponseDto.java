package org.meveo.api.dto.response.script;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlRootElement;

import org.meveo.api.dto.response.BaseResponse;
import org.meveo.api.dto.script.OfferModelScriptDto;

/**
 * @author Edward P. Legaspi
 **/
@XmlRootElement(name = "OfferModelScriptResponse")
@XmlAccessorType(XmlAccessType.FIELD)
public class OfferModelScriptResponseDto extends BaseResponse {

	private static final long serialVersionUID = 3320673620683295748L;

	private OfferModelScriptDto offerModelScript;

	public OfferModelScriptDto getOfferModelScript() {
		return offerModelScript;
	}

	public void setOfferModelScript(OfferModelScriptDto offerModelScript) {
		this.offerModelScript = offerModelScript;
	}

	@Override
	public String toString() {
		return "OfferModelScriptResponseDto [offerModelScript=" + offerModelScript + ", toString()=" + super.toString()
				+ "]";
	}

}
