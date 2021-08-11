package org.meveo.model.accounting;

import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;

import org.hibernate.annotations.GenericGenerator;
import org.hibernate.annotations.Parameter;
import org.meveo.model.BaseEntity;

@Entity
@Table(name = "sub_accounting_period")
@GenericGenerator(name = "ID_GENERATOR", strategy = "org.hibernate.id.enhanced.SequenceStyleGenerator", parameters = {
        @Parameter(name = "sequence_name", value = "sub_accounting_period_seq"), })
public class SubAccountingPeriod extends BaseEntity {

    /**
     * 
     */
    private static final long serialVersionUID = 4444444770355105322L;

    @Column(name = "number")
    private Integer number;

    @Temporal(TemporalType.TIMESTAMP)
    @Column(name = "start_date")
    private Date startDate;

    @Temporal(TemporalType.TIMESTAMP)
    @Column(name = "end_date")
    private Date endDate;

    @Column(name = "regular_users_sub_period_status")
    private String regularUsersSubPeriodStatus;

    @Temporal(TemporalType.TIMESTAMP)
    @Column(name = "regular_users_closed_date")
    private Date regularUsersClosedDate;

    @Temporal(TemporalType.TIMESTAMP)
    @Column(name = "effective_closed_date")
    private Date effectiveClosedDate;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "accounting_period_id")
    private AccountingPeriod accountingPeriod;

    public SubAccountingPeriod() {
        super();
    }

    public Integer getNumber() {
        return number;
    }

    public void setNumber(Integer number) {
        this.number = number;
    }

    public Date getStartDate() {
        return startDate;
    }

    public void setStartDate(Date startDate) {
        this.startDate = startDate;
    }

    public Date getEndDate() {
        return endDate;
    }

    public void setEndDate(Date endDate) {
        this.endDate = endDate;
    }

    public String getRegularUsersSubPeriodStatus() {
        return regularUsersSubPeriodStatus;
    }

    public void setRegularUsersSubPeriodStatus(String regularUsersSubPeriodStatus) {
        this.regularUsersSubPeriodStatus = regularUsersSubPeriodStatus;
    }

    public Date getRegularUsersClosedDate() {
        return regularUsersClosedDate;
    }

    public void setRegularUsersClosedDate(Date regularUsersClosedDate) {
        this.regularUsersClosedDate = regularUsersClosedDate;
    }

    public Date getEffectiveClosedDate() {
        return effectiveClosedDate;
    }

    public void setEffectiveClosedDate(Date effectiveClosedDate) {
        this.effectiveClosedDate = effectiveClosedDate;
    }

    public AccountingPeriod getAccountingPeriod() {
        return accountingPeriod;
    }

    public void setAccountingPeriod(AccountingPeriod accountingPeriod) {
        this.accountingPeriod = accountingPeriod;
    }

}
