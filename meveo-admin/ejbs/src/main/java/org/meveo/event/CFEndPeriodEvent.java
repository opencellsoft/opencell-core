package org.meveo.event;

import java.io.Serializable;

import org.meveo.model.crm.CustomFieldPeriod;

/**
 * @author Edward P. Legaspi
 **/
public class CFEndPeriodEvent implements Serializable {

	private static final long serialVersionUID = -1937181899381134353L;
	
	private CustomFieldPeriod customFieldPeriod;

	public CustomFieldPeriod getCustomFieldPeriod() {
		return customFieldPeriod;
	}

	public void setCustomFieldPeriod(CustomFieldPeriod customFieldPeriod) {
		this.customFieldPeriod = customFieldPeriod;
	}

	@Override
	public String toString() {
		return "CFEndPeriodEvent [customFieldPeriod=" + customFieldPeriod + "]";
	}

}
