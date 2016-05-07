package org.meveo.api.dto.response.script;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlRootElement;

import org.meveo.api.dto.response.BaseResponse;
import org.meveo.api.dto.script.RevenueRecognitionScriptDto;


@XmlRootElement(name = "RevenueRecognitionScriptResponse")
@XmlAccessorType(XmlAccessType.FIELD)
public class RevenueRecognitionScriptResponseDto extends BaseResponse {

	private static final long serialVersionUID = 3320673620683295748L;

	private RevenueRecognitionScriptDto RevenueRecognitionScript;

	public RevenueRecognitionScriptDto getRevenueRecognitionScript() {
		return RevenueRecognitionScript;
	}

	public void setRevenueRecognitionScript(RevenueRecognitionScriptDto RevenueRecognitionScript) {
		this.RevenueRecognitionScript = RevenueRecognitionScript;
	}

	@Override
	public String toString() {
		return "RevenueRecognitionScriptResponseDto [RevenueRecognitionScript=" + RevenueRecognitionScript + ", toString()=" + super.toString()
				+ "]";
	}

}
