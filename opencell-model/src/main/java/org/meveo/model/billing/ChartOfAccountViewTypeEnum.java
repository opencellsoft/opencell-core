package org.meveo.model.billing;

/**
 * @author Edward P. Legaspi
 * @version %I%, %G%
 * @since 5.0
 * @lastModifiedVersion 5.0
 **/
public enum ChartOfAccountViewTypeEnum {
    VIEW, REGULAR;
    
    public String getLabel() {
        return "ChartOfAccountViewTypeEnum." + name();
    }
}
