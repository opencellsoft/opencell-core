package org.meveo.model.module;

/**
 * @author Tyshan Shi(tyshan@manaty.net)
 *
**/
public enum ModuleLicenseEnum {
	APACHE("license.apache"),
	BSD3_N("license.bsd3_n"),
	BSD3_R( "license.bsd3_r"),
	BSD2_S("license.bsd2_s"),
	FREE_BSD("license.free_bsd"),
	GPL("license.gpl"),
	AGPL("license.agpl"),
	LGPL("license.lgpl"),
	MIT("license.mit"),
	MOZ("license.moz"),
	CDDL("license.cddl"),
	EPL("license.epl"),
	OPEN("license.open"),
	COM("license.com");
	private String label;
	private ModuleLicenseEnum(String label){
		this.label=label;
	}
	public String getLabel() {
		return label;
	}
	public void setLabel(String label) {
		this.label = label;
	}
	public static ModuleLicenseEnum getValue(String label){
		if(label!=null){
			for(ModuleLicenseEnum license:values()){
				if(label.equals(license.getLabel())){
					return license;
				}
			}
		}
		return null;
	}

}
