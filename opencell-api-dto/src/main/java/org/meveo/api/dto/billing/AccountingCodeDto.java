package org.meveo.api.dto.billing;

import javax.validation.constraints.NotNull;

import org.meveo.api.dto.BusinessEntityDto;
import org.meveo.model.billing.ChartOfAccountTypeEnum;
import org.meveo.model.billing.ChartOfAccountViewTypeEnum;

/**
 * The Class AccountingCodeDto.
 *
 * @author Edward P. Legaspi
 * @version %I%, %G%
 * @since 5.0
 * @lastModifiedVersion 5.0
 */
public class AccountingCodeDto extends BusinessEntityDto {

    /** The Constant serialVersionUID. */
    private static final long serialVersionUID = 8093532321068428023L;

    /** The parent accounting code. */
    private String parentAccountingCode;

    /** The chart of account type enum. */
    @NotNull
    private ChartOfAccountTypeEnum chartOfAccountTypeEnum;

    /** The chart of account view type enum. */
    @NotNull
    private ChartOfAccountViewTypeEnum chartOfAccountViewTypeEnum;

    /** The disabled. */
    @NotNull
    private boolean disabled = false;

    /** The reporting account. */
    private String reportingAccount;

    /** The notes. */
    private String notes;

    /** The migrated. */
    private boolean migrated = false;

    /**
     * Gets the parent accounting code.
     *
     * @return the parent accounting code
     */
    public String getParentAccountingCode() {
        return parentAccountingCode;
    }

    /**
     * Sets the parent accounting code.
     *
     * @param parentAccountingCode the new parent accounting code
     */
    public void setParentAccountingCode(String parentAccountingCode) {
        this.parentAccountingCode = parentAccountingCode;
    }

    /**
     * Gets the chart of account type enum.
     *
     * @return the chart of account type enum
     */
    public ChartOfAccountTypeEnum getChartOfAccountTypeEnum() {
        return chartOfAccountTypeEnum;
    }

    /**
     * Sets the chart of account type enum.
     *
     * @param chartOfAccountTypeEnum the new chart of account type enum
     */
    public void setChartOfAccountTypeEnum(ChartOfAccountTypeEnum chartOfAccountTypeEnum) {
        this.chartOfAccountTypeEnum = chartOfAccountTypeEnum;
    }

    /**
     * Gets the reporting account.
     *
     * @return the reporting account
     */
    public String getReportingAccount() {
        return reportingAccount;
    }

    /**
     * Sets the reporting account.
     *
     * @param reportingAccount the new reporting account
     */
    public void setReportingAccount(String reportingAccount) {
        this.reportingAccount = reportingAccount;
    }

    /**
     * Gets the chart of account view type enum.
     *
     * @return the chart of account view type enum
     */
    public ChartOfAccountViewTypeEnum getChartOfAccountViewTypeEnum() {
        return chartOfAccountViewTypeEnum;
    }

    /**
     * Sets the chart of account view type enum.
     *
     * @param chartOfAccountViewTypeEnum the new chart of account view type enum
     */
    public void setChartOfAccountViewTypeEnum(ChartOfAccountViewTypeEnum chartOfAccountViewTypeEnum) {
        this.chartOfAccountViewTypeEnum = chartOfAccountViewTypeEnum;
    }

    /**
     * Gets the notes.
     *
     * @return the notes
     */
    public String getNotes() {
        return notes;
    }

    /**
     * Sets the notes.
     *
     * @param notes the new notes
     */
    public void setNotes(String notes) {
        this.notes = notes;
    }

    /**
     * Checks if is disabled.
     *
     * @return true, if is disabled
     */
    public boolean isDisabled() {
        return disabled;
    }

    /**
     * Sets the disabled.
     *
     * @param disabled the new disabled
     */
    public void setDisabled(boolean disabled) {
        this.disabled = disabled;
    }

    /**
     * Checks if is migrated.
     *
     * @return true, if is migrated
     */
    public boolean isMigrated() {
        return migrated;
    }

    /**
     * Sets the migrated.
     *
     * @param migrated the new migrated
     */
    public void setMigrated(boolean migrated) {
        this.migrated = migrated;
    }

}