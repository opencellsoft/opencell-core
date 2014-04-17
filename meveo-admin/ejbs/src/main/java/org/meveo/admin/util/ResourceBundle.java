package org.meveo.admin.util;

import java.io.Serializable;

public class ResourceBundle implements Serializable {

	private static final long serialVersionUID = -5269169718061449505L;
	
	private transient java.util.ResourceBundle  proxiedBundle;
	
	public ResourceBundle(java.util.ResourceBundle proxiedBundle){
		this.proxiedBundle = proxiedBundle;	
	}
	
	public String getString(String key){
		String result=key;
		if(proxiedBundle.containsKey(key)){
			result=proxiedBundle.getString(key);
		}
		return result;
	}
}
