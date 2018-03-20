package org.meveo.model.billing;

/**
 * Type of Chart of accounts.
 * 
 * @author Edward P. Legaspi
 * @version %I%, %G%
 * @since 5.0
 * @lastModifiedVersion 5.0
 **/
public enum ChartOfAccountTypeEnum {
    ASSETS, LIABILITIES, EQUITY, REVENUE, EXPENSE;
    
    public String getLabel() {
        return "ChartOfAccountTypeEnum." + name();
    }
}
