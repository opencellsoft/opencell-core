package org.meveo.model.billing;

/**
 * @author Edward P. Legaspi
 * @created 19 Feb 2018
 **/
public enum ChartOfAccountTypeEnum {
    ASSETS, LIABILITIES, EQUITY, REVENUE, EXPENSE;
    
    public String getLabel() {
        return "ChartOfAccountTypeEnum." + name();
    }
}
