package org.meveo.model.accounting;

import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;

import org.hibernate.annotations.GenericGenerator;
import org.hibernate.annotations.Parameter;
import org.hibernate.annotations.Type;
import org.meveo.model.AuditableEntity;

@Entity
@Table(name = "accounting_period")
@GenericGenerator(name = "ID_GENERATOR", strategy = "org.hibernate.id.enhanced.SequenceStyleGenerator", parameters = {
        @Parameter(name = "sequence_name", value = "accounting_period_seq") })
@NamedQueries({ @NamedQuery(name = "AccountingPeriod.findByFiscalYear", query = "SELECT AP FROM AccountingPeriod AP where AP.accountingPeriodYear=:fiscalYear"),
        @NamedQuery(name = "AccountingPeriod.findLastAP", query = "SELECT AP FROM AccountingPeriod AP where AP.endDate = (select max(endDate) from AccountingPeriod)"),
        @NamedQuery(name = "AccountingPeriod.findOpenAP", query = "SELECT AP FROM AccountingPeriod AP where AP.accountingPeriodStatus = 'OPEN' order by AP.startDate asc"),
        @NamedQuery(name = "AccountingPeriod.findOpenAPByDate", query = "SELECT AP FROM AccountingPeriod AP where AP.accountingPeriodStatus = 'OPEN' and (:date between AP.startDate and AP.endDate)")})
public class AccountingPeriod extends AuditableEntity {

    private static final long serialVersionUID = -5424629380105543225L;

    @Column(name = "accounting_period_year", unique = true, nullable = false)
    private String accountingPeriodYear;

    @Temporal(TemporalType.TIMESTAMP)
    @Column(name = "start_date", unique = true, nullable = false)
    private Date startDate ;

    @Temporal(TemporalType.TIMESTAMP)
    @Column(name = "end_date", unique = true, nullable = false)
    private Date endDate;

    @Type(type = "numeric_boolean")
    @Column(name = "use_sub_accounting_cycles")
    private boolean useSubAccountingCycles;

    @Enumerated(value = EnumType.STRING)
    @Column(name = "status")
    private AccountingPeriodStatusEnum accountingPeriodStatus;

    @Enumerated(value = EnumType.STRING)
    @Column(name = "sub_accounting_period_type")
    private SubAccountingPeriodTypeEnum subAccountingPeriodType;

    @Column(name = "sub_accounting_period_progress")
    private String subAccountingPeriodProgress;

    @Column(name = "ongoing_sub_accounting_periods")
    private String ongoingSubAccountingPeriods;

    @Enumerated(value = EnumType.STRING)
    @Column(name = "accounting_operation_action")
    private AccountingOperationAction accountingOperationAction;

    @Enumerated(value = EnumType.STRING)
    @Column(name = "regular_user_lock_option")
    private RegularUserLockOption regularUserLockOption;

    @Column(name = "custom_lock_number_days")
    private Integer customLockNumberDays;

    @Enumerated(value = EnumType.STRING)
    @Column(name = "custom_lock_option")
    private CustomLockOption customLockOption;

    @Enumerated(value = EnumType.STRING)
    @Column(name = "force_option")
    private AccountingPeriodForceEnum forceOption;

    @Column(name = "force_custom_day")
    private Integer forceCustomDay;

    public AccountingPeriod() {
        super();
    }

    public AccountingPeriodStatusEnum getAccountingPeriodStatus() {
        return accountingPeriodStatus;
    }

    public void setAccountingPeriodStatus(AccountingPeriodStatusEnum accountingPeriodStatus) {
        this.accountingPeriodStatus = accountingPeriodStatus;
    }

    public SubAccountingPeriodTypeEnum getSubAccountingPeriodType() {
        return subAccountingPeriodType;
    }

    public void setSubAccountingPeriodType(SubAccountingPeriodTypeEnum subAccountingPeriodType) {
        this.subAccountingPeriodType = subAccountingPeriodType;
    }

    public String getSubAccountingPeriodProgress() {
        return subAccountingPeriodProgress;
    }

    public void setSubAccountingPeriodProgress(String subAccountingPeriodProgress) {
        this.subAccountingPeriodProgress = subAccountingPeriodProgress;
    }

    public String getOngoingSubAccountingPeriods() {
        return ongoingSubAccountingPeriods;
    }

    public void setOngoingSubAccountingPeriods(String ongoingSubAccountingPeriods) {
        this.ongoingSubAccountingPeriods = ongoingSubAccountingPeriods;
    }

    public AccountingOperationAction getAccountingOperationAction() {
        return accountingOperationAction;
    }

    public void setAccountingOperationAction(AccountingOperationAction accountingOperationAction) {
        this.accountingOperationAction = accountingOperationAction;
    }

    public RegularUserLockOption getRegularUserLockOption() {
        return regularUserLockOption;
    }

    public void setRegularUserLockOption(RegularUserLockOption regularUserLockOption) {
        this.regularUserLockOption = regularUserLockOption;
    }

    public Integer getCustomLockNumberDays() {
        return customLockNumberDays;
    }

    public void setCustomLockNumberDays(Integer customLockNumberDays) {
        this.customLockNumberDays = customLockNumberDays;
    }

    public CustomLockOption getCustomLockOption() {
        return customLockOption;
    }

    public void setCustomLockOption(CustomLockOption customLockOption) {
        this.customLockOption = customLockOption;
    }

    /**
     * @return the accountingPeriodYear
     */
    public String getAccountingPeriodYear() {
        return accountingPeriodYear;
    }

    /**
     * @param accountingPeriodYear the accountingPeriodYear to set
     */
    public void setAccountingPeriodYear(String accountingPeriodYear) {
        this.accountingPeriodYear = accountingPeriodYear;
    }

    /**
     * @return the startDate
     */
    public Date getStartDate() {
        return startDate;
    }

    /**
     * @param startDate the startDate to set
     */
    public void setStartDate(Date startDate) {
        this.startDate = startDate;
    }

    /**
     * @return the endDate
     */
    public Date getEndDate() {
        return endDate;
    }

    /**
     * @param endDate the endDate to set
     */
    public void setEndDate(Date endDate) {
        this.endDate = endDate;
    }

    /**
     * @return the useSubAccountingCycles
     */
    public boolean isUseSubAccountingCycles() {
        return useSubAccountingCycles;
    }

    /**
     * @param useSubAccountingCycles the useSubAccountingCycles to set
     */
    public void setUseSubAccountingCycles(boolean useSubAccountingCycles) {
        this.useSubAccountingCycles = useSubAccountingCycles;
    }

    /**
     * @return the forceOption
     */
    public AccountingPeriodForceEnum getForceOption() {
        return forceOption;
    }

    /**
     * @param forceOption the forceOption to set
     */
    public void setForceOption(AccountingPeriodForceEnum forceOption) {
        this.forceOption = forceOption;
    }

    /**
     * @return the forceCustomDay
     */
    public Integer getForceCustomDay() {
        return forceCustomDay;
    }

    /**
     * @param forceCustomDay the forceCustomDay to set
     */
    public void setForceCustomDay(Integer forceCustomDay) {
        this.forceCustomDay = forceCustomDay;
    }

    /**
     * Check if the current AP is open
     * 
     * @return
     */
    public boolean isOpen() {
        return this.accountingPeriodStatus == AccountingPeriodStatusEnum.OPEN;
    }

    /**
     * Check if the current AP is closed
     * 
     * @return
     */
    public boolean isClosed() {
        return this.accountingPeriodStatus == AccountingPeriodStatusEnum.CLOSED;
    }
}
