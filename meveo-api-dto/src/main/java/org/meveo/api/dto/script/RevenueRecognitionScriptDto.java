package org.meveo.api.dto.script;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlRootElement;

import org.meveo.model.scripts.RevenueRecognitionScriptEntity;


@XmlRootElement(name = "RevenueRecognitionScript")
@XmlAccessorType(XmlAccessType.FIELD)
public class RevenueRecognitionScriptDto extends CustomScriptDto {

	private static final long serialVersionUID = -4658184651430456841L;

	public RevenueRecognitionScriptDto() {

	}

	public RevenueRecognitionScriptDto(RevenueRecognitionScriptEntity e) {
		super(e.getCode(), e.getDescription(), e.getSourceTypeEnum(), e.getScript());
	}

	@Override
	public String toString() {
		return "RevenueRecognitionScriptDto [toString()=" + super.toString() + "]";
	}

}
