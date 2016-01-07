package org.meveo.api.dto.response;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlRootElement;

import org.meveo.api.dto.ScriptInstanceDto;

/**
 * @author Edward P. Legaspi
 **/
@XmlRootElement(name = "GetScriptInstanceResponse")
@XmlAccessorType(XmlAccessType.FIELD)
public class GetScriptInstanceResponseDto extends BaseResponse {

	private static final long serialVersionUID = 962231443399621051L;

	private ScriptInstanceDto scriptInstance;

	public ScriptInstanceDto getScriptInstance() {
		return scriptInstance;
	}

	public void setScriptInstance(ScriptInstanceDto scriptInstance) {
		this.scriptInstance = scriptInstance;
	}

}
