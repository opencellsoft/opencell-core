package org.meveo.api.dto.module;

import java.io.Serializable;
import java.util.List;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlElementWrapper;
import javax.xml.bind.annotation.XmlRootElement;

import org.meveo.api.dto.BaseDto;
import org.meveo.model.admin.ModuleLicenseEnum;

/**
 * 
 * @author Tyshan Shi(tyshan@manaty.net)
 *
 */
@XmlRootElement(name = "Module")
@XmlAccessorType(XmlAccessType.FIELD)
public class ModuleDto extends BaseDto{

	private static final long serialVersionUID = 1L;
	
	@XmlAttribute(required=true)
	private String code;
	@XmlAttribute(required=true)
	private ModuleLicenseEnum license;
	@XmlAttribute(required=true)
	private String description;
	private Boolean disabled;
	@XmlElementWrapper(name="moduleItems")
	@XmlElement(name="ModuleItem")
	private List<ModuleItemDto> moduleItems;
	public ModuleDto(){}
	public ModuleDto(String code,String description,ModuleLicenseEnum license,Boolean disabled){
		this.code=code;
		this.description=description;
		this.license=license;
		this.disabled=disabled;
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
	
	public Boolean getDisabled() {
		return disabled;
	}
	public void setDisabled(Boolean disabled) {
		this.disabled = disabled;
	}
	public List<ModuleItemDto> getModuleItems() {
		return moduleItems;
	}
	public void setModuleItems(List<ModuleItemDto> moduleItems) {
		this.moduleItems = moduleItems;
	}
	
	public ModuleLicenseEnum getLicense() {
		return license;
	}
	public void setLicense(ModuleLicenseEnum license) {
		this.license = license;
	}
	@Override
	public String toString() {
		return "ModuleDto [code=" + code + ", description=" + description + ", diabled=" + disabled + ", license="+license+", moduleItems="
				+ moduleItems + "]";
	}
	
}
