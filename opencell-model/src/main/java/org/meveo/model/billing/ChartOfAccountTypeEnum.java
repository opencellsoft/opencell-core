package org.meveo.model.billing;

/**
 * Type of Chart of accounts.
 * @author Edward P. Legaspi
 * @created 19 Feb 2018
 * @lastModifiedVersion 5.0
 **/
public enum ChartOfAccountTypeEnum {
    ASSETS, LIABILITIES, EQUITY, REVENUE, EXPENSE;
    
    public String getLabel() {
        return "ChartOfAccountTypeEnum." + name();
    }
}
