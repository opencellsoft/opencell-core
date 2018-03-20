package org.meveo.api.dto.billing;

import javax.validation.constraints.NotNull;

import org.meveo.api.dto.BusinessEntityDto;
import org.meveo.model.billing.ChartOfAccountTypeEnum;
import org.meveo.model.billing.ChartOfAccountViewTypeEnum;

/**
 * @author Edward P. Legaspi
 * @version %I%, %G%
 * @since 5.0
 * @lastModifiedVersion 5.0
 **/
public class AccountingCodeDto extends BusinessEntityDto {

    private static final long serialVersionUID = 8093532321068428023L;

    private String parentAccountingCode;

    @NotNull
    private ChartOfAccountTypeEnum chartOfAccountTypeEnum;

    @NotNull
    private ChartOfAccountViewTypeEnum chartOfAccountViewTypeEnum;

    @NotNull
    private boolean disabled = false;

    private String reportingAccount;
    private String notes;
    private boolean migrated = false;

    public String getParentAccountingCode() {
        return parentAccountingCode;
    }

    public void setParentAccountingCode(String parentAccountingCode) {
        this.parentAccountingCode = parentAccountingCode;
    }

    public ChartOfAccountTypeEnum getChartOfAccountTypeEnum() {
        return chartOfAccountTypeEnum;
    }

    public void setChartOfAccountTypeEnum(ChartOfAccountTypeEnum chartOfAccountTypeEnum) {
        this.chartOfAccountTypeEnum = chartOfAccountTypeEnum;
    }

    public String getReportingAccount() {
        return reportingAccount;
    }

    public void setReportingAccount(String reportingAccount) {
        this.reportingAccount = reportingAccount;
    }

    public ChartOfAccountViewTypeEnum getChartOfAccountViewTypeEnum() {
        return chartOfAccountViewTypeEnum;
    }

    public void setChartOfAccountViewTypeEnum(ChartOfAccountViewTypeEnum chartOfAccountViewTypeEnum) {
        this.chartOfAccountViewTypeEnum = chartOfAccountViewTypeEnum;
    }

    public String getNotes() {
        return notes;
    }

    public void setNotes(String notes) {
        this.notes = notes;
    }

    public boolean isDisabled() {
        return disabled;
    }

    public void setDisabled(boolean disabled) {
        this.disabled = disabled;
    }

    public boolean isMigrated() {
        return migrated;
    }

    public void setMigrated(boolean migrated) {
        this.migrated = migrated;
    }

}
