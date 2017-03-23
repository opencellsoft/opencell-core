package org.meveo.model.mediation;

public enum ActionEnum {
	UPLOAD("Upload"),DOWNLOAD("Download"),RENAME("Rename"),DELETE("Delete");
	private String label;
	private ActionEnum(String label){
		this.label=label;
	}
	public String getLabel(){
		return this.label;
	}
}
