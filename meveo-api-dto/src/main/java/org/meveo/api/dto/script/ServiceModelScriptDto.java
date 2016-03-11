package org.meveo.api.dto.script;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlRootElement;

import org.meveo.model.scripts.ServiceModelScript;

/**
 * @author Edward P. Legaspi
 **/
@XmlRootElement(name = "ServiceModelScript")
@XmlAccessorType(XmlAccessType.FIELD)
public class ServiceModelScriptDto extends CustomScriptDto {

	private static final long serialVersionUID = -4658184651430456841L;

	public ServiceModelScriptDto() {

	}

	public ServiceModelScriptDto(ServiceModelScript e) {
		super(e.getCode(), e.getDescription(), e.getSourceTypeEnum(), e.getScript());
	}

	@Override
	public String toString() {
		return "ServiceModelScriptDto [toString()=" + super.toString() + "]";
	}

}
