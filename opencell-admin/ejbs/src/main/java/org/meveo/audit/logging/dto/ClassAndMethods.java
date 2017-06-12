package org.meveo.audit.logging.dto;

import java.util.ArrayList;
import java.util.List;

/**
 * @author Edward P. Legaspi
 **/
public class ClassAndMethods {

	private String className;
	private List<String> methods;

	public void addMethod(String method) {
		getMethods().add(method);
	}

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

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((className == null) ? 0 : className.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		ClassAndMethods other = (ClassAndMethods) obj;
		if (className == null) {
			if (other.className != null)
				return false;
		} else if (!className.equals(other.className))
			return false;
		return true;
	}
}
