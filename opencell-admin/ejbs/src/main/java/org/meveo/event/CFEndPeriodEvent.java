package org.meveo.event;

import java.io.Serializable;

import org.meveo.model.crm.CustomFieldInstance;

/**
 * @author Edward P. Legaspi
 **/
public class CFEndPeriodEvent implements Serializable {

    private static final long serialVersionUID = -1937181899381134353L;

    private CustomFieldInstance customFieldInstance;

    public CustomFieldInstance getCustomFieldInstace() {
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