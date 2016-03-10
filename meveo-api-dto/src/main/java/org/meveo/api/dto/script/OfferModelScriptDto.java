package org.meveo.api.dto.script;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlRootElement;

import org.meveo.model.scripts.OfferModelScript;

/**
 * @author Edward P. Legaspi
 **/
@XmlRootElement(name = "OfferModelScript")
@XmlAccessorType(XmlAccessType.FIELD)
public class OfferModelScriptDto extends CustomScriptDto {

	private static final long serialVersionUID = -4658184651430456841L;

	public OfferModelScriptDto() {

	}

	public OfferModelScriptDto(OfferModelScript e) {
		super(e.getCode(), e.getDescription(), e.getSourceTypeEnum(), e.getScript());
	}

	@Override
	public String toString() {
		return "OfferModelScriptDto [toString()=" + super.toString() + "]";
	}

}
