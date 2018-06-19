package org.meveo.model.billing;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.validation.constraints.NotNull;

import org.hibernate.annotations.GenericGenerator;
import org.hibernate.annotations.Parameter;
import org.hibernate.annotations.Type;
import org.meveo.model.CustomFieldEntity;
import org.meveo.model.EnableBusinessEntity;
import org.meveo.model.ExportIdentifier;

/**
 * Use to store Chart of accounts. Previously accounting_code fields.
 * 
 * @author Edward P. Legaspi
 * @version %I%, %G%
 * @since 5.0
 * @lastModifiedVersion 5.0
 **/
@CustomFieldEntity(cftCodePrefix = "ACCT_CODE")
@ExportIdentifier({ "code" })
@Entity
@Table(name = "billing_accounting_code")
@GenericGenerator(name = "ID_GENERATOR", strategy = "org.hibernate.id.enhanced.SequenceStyleGenerator", parameters = {
        @Parameter(name = "sequence_name", value = "billing_accounting_code_seq") })
public class AccountingCode extends EnableBusinessEntity {

    private static final long serialVersionUID = -8962374797036999750L;

    @ManyToOne
    @JoinColumn(name = "parent_accounting_code_id")
    private AccountingCode parentAccountingCode;

    @NotNull
    @Enumerated(EnumType.STRING)
    @Column(name = "chart_of_account_type", length = 25)
    private ChartOfAccountTypeEnum chartOfAccountTypeEnum;

    @Column(name = "reporting_account", length = 50)
    private String reportingAccount;

    @NotNull
    @Enumerated(EnumType.STRING)
    @Column(name = "chart_of_account_view_type", length = 25)
    private ChartOfAccountViewTypeEnum chartOfAccountViewTypeEnum;

    @Column(name = "notes", length = 2000)
    private String notes;

    @Type(type = "numeric_boolean")
    @Column(name = "migrated", nullable = false)
    private boolean migrated = false;

    public AccountingCode getParentAccountingCode() {
        return parentAccountingCode;
    }

    public void setParentAccountingCode(AccountingCode parentAccountingCode) {
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
