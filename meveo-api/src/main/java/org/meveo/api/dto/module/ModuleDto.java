package org.meveo.api.dto.module;

import java.io.Serializable;
import java.util.List;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

/**
 * 
 * @author Tyshan Shi(tyshan@manaty.net)
 *
 */
@XmlRootElement(name = "Module")
@XmlAccessorType(XmlAccessType.FIELD)
public class ModuleDto implements Serializable{

	private static final long serialVersionUID = 6955822636724508496L;
	
	@XmlElement(required=true)
	private String code;
	@XmlElement(required=true)
	private String description;
	private Boolean diabled;
	private List<ModuleItemDto> moduleItems;
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
	
	public Boolean getDiabled() {
		return diabled;
	}
	public void setDiabled(Boolean diabled) {
		this.diabled = diabled;
	}
	public List<ModuleItemDto> getModuleItems() {
		return moduleItems;
	}
	public void setModuleItems(List<ModuleItemDto> moduleItems) {
		this.moduleItems = moduleItems;
	}
	@Override
	public String toString() {
		return "ModuleDto [code=" + code + ", description=" + description + ", diabled=" + diabled + ", moduleItems="
				+ moduleItems + "]";
	}
	
}
