package org.meveo.api.dto;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

import org.meveo.model.jobs.ScriptInstance;

/**
 * @author Edward P. Legaspi
 **/
@XmlRootElement(name = "ScriptInstance")
@XmlAccessorType(XmlAccessType.FIELD)
public class ScriptInstanceDto {

	@XmlAttribute(required = true)
	private String code;

	@XmlAttribute(required = false)
	private String description;

	@XmlElement
	private String type;

	@XmlElement
	private String script;

	public ScriptInstanceDto() {
	}

	public ScriptInstanceDto(ScriptInstance e) {
		code = e.getCode();
		description = e.getDescription();
		script = e.getScript();
		if (e.getScriptTypeEnum() != null) {
			type = e.getScriptTypeEnum().name();
		}
	}

	public String getCode() {
		return code;
	}

	public void setCode(String code) {
		this.code = code;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}

	public String getScript() {
		return script;
	}

	public void setScript(String script) {
		this.script = script;
	}

	@Override
	public String toString() {
		return "ScriptInstanceDto [code=" + code + ", description=" + description + ", type=" + type + ", script="
				+ script + "]";
	}

}
