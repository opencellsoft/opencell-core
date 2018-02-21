package org.meveo.model.billing;

/**
 * @author Edward P. Legaspi
 * @created 19 Feb 2018
 **/
public enum ChartOfAccountViewTypeEnum {
    VIEW, REGULAR;
    
    public String getLabel() {
        return "ChartOfAccountViewTypeEnum." + name();
    }
}
