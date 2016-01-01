package org.meveo.api.dto.module;

import java.io.Serializable;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlTransient;

import org.meveo.model.admin.ModuleItemTypeEnum;

/**
 * @author Tyshan Shi(tyshan@manaty.net)
 *
**/
//@XmlRootElement(name="ModuleItem")
//@XmlAccessorType(XmlAccessType.FIELD)
//public class ModuleItemDto implements Serializable {
//	private static final long serialVersionUID = -2237671803222827809L;
//	@XmlAttribute(required=true)
//	private ModuleItemTypeEnum itemType;
//	private String appliesTo;
//	@XmlAttribute(required=true)
//	private String itemCode;
//	@XmlTransient
//	private String description;
//	public ModuleItemDto(){}
//	public ModuleItemDto(String itemCode,String appliesTo,ModuleItemTypeEnum itemType){
//		this.itemCode=itemCode;
//		this.appliesTo=appliesTo;
//		this.itemType=itemType;
//	}
//	public ModuleItemTypeEnum getItemType() {
//		return itemType;
//	}
//	public void setItemType(ModuleItemTypeEnum itemType) {
//		this.itemType = itemType;
//	}
//	public String getAppliesTo() {
//		return appliesTo;
//	}
//	public void setAppliesTo(String appliesTo) {
//		this.appliesTo = appliesTo;
//	}
//	public String getItemCode() {
//		return itemCode;
//	}
//	public void setItemCode(String itemCode) {
//		this.itemCode = itemCode;
//	}
//	public String getDescription() {
//		return description;
//	}
//	public void setDescription(String description) {
//		this.description = description;
//	}
//	@Override
//	public String toString() {
//		return "ModuleItemDto [itemType=" + itemType + ", appliesTo=" + appliesTo + ", itemCode=" + itemCode + "]";
//	}
//}
