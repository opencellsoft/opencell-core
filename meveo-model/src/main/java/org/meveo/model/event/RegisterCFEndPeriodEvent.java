package org.meveo.model.event;

import java.io.Serializable;

import org.meveo.model.crm.CustomFieldPeriod;

/**
 * @author Edward P. Legaspi
 **/
public class RegisterCFEndPeriodEvent implements Serializable {

	private static final long serialVersionUID = 7005176879373066662L;
	
	private CustomFieldPeriod customFieldPeriod;

	public CustomFieldPeriod getCustomFieldPeriod() {
		return customFieldPeriod;
	}

	public void setCustomFieldPeriod(CustomFieldPeriod customFieldPeriod) {
		this.customFieldPeriod = customFieldPeriod;
	}

}
