package org.meveo.api.dto.billing;

import javax.validation.constraints.NotNull;

import org.meveo.api.dto.EnableBusinessDto;
import org.meveo.model.billing.AccountingCode;
import org.meveo.model.billing.ChartOfAccountTypeEnum;
import org.meveo.model.billing.ChartOfAccountViewTypeEnum;

/**
 * @author Edward P. Legaspi
 * @since 5.0
 * @lastModifiedVersion 5.0
 **/
public class AccountingCodeDto extends EnableBusinessDto {

    private static final long serialVersionUID = 8093532321068428023L;

    private String parentAccountingCode;

    @NotNull
    private ChartOfAccountTypeEnum chartOfAccountTypeEnum;

    @NotNull
    private ChartOfAccountViewTypeEnum chartOfAccountViewTypeEnum;

    private String reportingAccount;
    private String notes;
    private boolean migrated = false;

    public AccountingCodeDto() {
    }

    public AccountingCodeDto(AccountingCode accountingCode) {
        super(accountingCode);

        setChartOfAccountTypeEnum(accountingCode.getChartOfAccountTypeEnum());
        setChartOfAccountViewTypeEnum(accountingCode.getChartOfAccountViewTypeEnum());
        setNotes(accountingCode.getNotes());
        setReportingAccount(accountingCode.getReportingAccount());
        setMigrated(accountingCode.isMigrated());
    }

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

    public boolean isMigrated() {
        return migrated;
    }

    public void setMigrated(boolean migrated) {
        this.migrated = migrated;
    }
}