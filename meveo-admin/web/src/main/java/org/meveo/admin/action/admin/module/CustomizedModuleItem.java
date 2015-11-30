package org.meveo.admin.action.admin.module;

import java.io.Serializable;

import org.meveo.model.admin.ModuleItemTypeEnum;

/**
 * @author Tyshan Shi(tyshan@manaty.net)
 *
**/
public class CustomizedModuleItem implements Serializable {
	private static final long serialVersionUID = 1L;

	private String code;
	private String description;
	private ModuleItemTypeEnum type;
	private String appliesTo;
	private boolean root=false;
	public CustomizedModuleItem(String code,boolean root){
		this.code=code;
		this.root=root;
	}
	public CustomizedModuleItem(String code,String description,ModuleItemTypeEnum type){
		this.code=code;
		this.type=type;
		this.description=description;
	}
	public CustomizedModuleItem(String code,String description,String appliesTo,ModuleItemTypeEnum type){
		this(code,description,type);
		this.appliesTo=appliesTo;
	}
	public String getCode() {
		return code;
	}
	public void setCode(String code) {
		this.code = code;
	}
	public ModuleItemTypeEnum getType() {
		return type;
	}
	public void setType(ModuleItemTypeEnum type) {
		this.type = type;
	}
	public String getAppliesTo() {
		return appliesTo;
	}
	public void setAppliesTo(String appliesTo) {
		this.appliesTo = appliesTo;
	}
	public String getDescription() {
		return description;
	}
	public void setDescription(String description) {
		this.description = description;
	}
	public boolean isRoot() {
		return root;
	}
	public void setRoot(boolean root) {
		this.root = root;
	}
	
}
