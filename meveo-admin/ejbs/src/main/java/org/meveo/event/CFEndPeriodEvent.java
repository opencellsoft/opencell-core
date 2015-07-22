package org.meveo.event;

import org.meveo.model.crm.CustomFieldInstance;

/**
 * @author Edward P. Legaspi
 **/
public class CFEndPeriodEvent {

	private CustomFieldInstance customFieldInstance;

	public CustomFieldInstance getCustomFieldInstance() {
		return customFieldInstance;
	}

	public void setCustomFieldInstance(CustomFieldInstance customFieldInstance) {
		this.customFieldInstance = customFieldInstance;
	}

	@Override
	public String toString() {
		return "CFEndPeriodEvent [customFieldInstance=" + customFieldInstance + "]";
	}

}
