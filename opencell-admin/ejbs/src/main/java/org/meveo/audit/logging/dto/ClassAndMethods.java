package org.meveo.audit.logging.dto;

import java.util.ArrayList;
import java.util.List;

/**
 * @author Edward P. Legaspi
 **/
public class ClassAndMethods {

	private String className;
	private List<String> methods;

	public String getClassName() {
		return className;
	}

	public void setClassName(String className) {
		this.className = className;
	}

	public List<String> getMethods() {
		if (methods == null) {
			methods = new ArrayList<>();
		}
		return methods;
	}

	public void setMethods(List<String> methods) {
		this.methods = methods;
	}
}
