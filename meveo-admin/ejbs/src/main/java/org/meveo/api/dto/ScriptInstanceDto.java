package org.meveo.api.dto;

import java.util.ArrayList;
import java.util.List;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

import org.meveo.model.scripts.ScriptInstance;
import org.meveo.model.security.Role;

/**
 * @author Edward P. Legaspi
 **/
@XmlRootElement(name = "ScriptInstance")
@XmlAccessorType(XmlAccessType.FIELD)
public class ScriptInstanceDto extends BaseDto{

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	@XmlAttribute(required = true)
	private String code;

	@XmlAttribute(required = false)
	private String description;

	@XmlElement
	private String type;

	@XmlElement(required = true)
	private String script;
	
	private List<RoleDto> executionRoles = new ArrayList<RoleDto>();
	private List<RoleDto> sourcingRoles = new ArrayList<RoleDto>();

	public ScriptInstanceDto() {
	}

	public ScriptInstanceDto(ScriptInstance e) {
		code = e.getCode();
		description = e.getDescription();
		script = e.getScript();
		if (e.getSourceTypeEnum() != null) {
			type = e.getSourceTypeEnum().name();
		}
		if(e.getExecutionRoles() != null){
			for(Role role : e.getExecutionRoles()){
				executionRoles.add(new RoleDto(role));
			}
		}
		if(e.getSourcingRoles() != null){
			for(Role role : e.getSourcingRoles()){
				sourcingRoles.add(new RoleDto(role));
			}
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
		return "ScriptInstanceDto [code=" + code + ", description=" + description + ", type=" + type + ", script=" + script + ", executionRoles=" + executionRoles + ", sourcingRoles=" + sourcingRoles + "]";
	}

	/**
	 * @return the executionRoles
	 */
	public List<RoleDto> getExecutionRoles() {
		return executionRoles;
	}

	/**
	 * @param executionRoles the executionRoles to set
	 */
	public void setExecutionRoles(List<RoleDto> executionRoles) {
		this.executionRoles = executionRoles;
	}

	/**
	 * @return the sourcingRoles
	 */
	public List<RoleDto> getSourcingRoles() {
		return sourcingRoles;
	}

	/**
	 * @param sourcingRoles the sourcingRoles to set
	 */
	public void setSourcingRoles(List<RoleDto> sourcingRoles) {
		this.sourcingRoles = sourcingRoles;
	}
	
	

}
