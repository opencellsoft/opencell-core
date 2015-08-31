package org.meveo.model.catalog;

/**
 * @author Edward P. Legaspi
 **/
public enum CounterTemplateLevel {
	UA, BA;

	public String getLabel() {
		return "enum.counterTemplateLevel." + name();
	}

}
