package org.meveo.api.dto.module;

import java.io.Serializable;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlTransient;

import org.meveo.model.admin.ModuleItemTypeEnum;

/**
 * @author Tyshan Shi(tyshan@manaty.net)
 *
**/
@XmlRootElement
@XmlAccessorType(XmlAccessType.FIELD)
public class ModuleItemDto implements Serializable {
	private static final long serialVersionUID = -2237671803222827809L;
	@XmlElement(required=true)
	private ModuleItemTypeEnum itemType;
	private String appliesTo;
	@XmlElement(required=true)
	private String itemCode;
	@XmlTransient
	private String description;
	public ModuleItemDto(){}
	public ModuleItemDto(String itemCode,String appliesTo,ModuleItemTypeEnum itemType){
		this.itemCode=itemCode;
		this.appliesTo=appliesTo;
		this.itemType=itemType;
	}
	public ModuleItemTypeEnum getItemType() {
		return itemType;
	}
	public void setItemType(ModuleItemTypeEnum itemType) {
		this.itemType = itemType;
	}
	public String getAppliesTo() {
		return appliesTo;
	}
	public void setAppliesTo(String appliesTo) {
		this.appliesTo = appliesTo;
	}
	public String getItemCode() {
		return itemCode;
	}
	public void setItemCode(String itemCode) {
		this.itemCode = itemCode;
	}
	public String getDescription() {
		return description;
	}
	public void setDescription(String description) {
		this.description = description;
	}
	
}
