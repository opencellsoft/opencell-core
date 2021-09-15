package org.meveo.model.accounting;

import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.FetchType;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
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
@NamedQueries({
	@NamedQuery(name = "SubAccountingPeriod.findByNumber", query = "SELECT SAP FROM SubAccountingPeriod SAP where SAP.number=:number") })
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

    @Enumerated(value = EnumType.STRING)
    @Column(name = "regular_users_sub_period_status")
    private SubAccountingPeriodStatusEnum regularUsersSubPeriodStatus;
    

    @Enumerated(value = EnumType.STRING)
    @Column(name = "all_users_sub_period_status")
    private SubAccountingPeriodStatusEnum allUsersSubPeriodStatus;
    

    @Temporal(TemporalType.TIMESTAMP)
    @Column(name = "regular_users_closed_date")
    private Date regularUsersClosedDate;

    @Temporal(TemporalType.TIMESTAMP)
    @Column(name = "effective_closed_date")
    private Date effectiveClosedDate;

    @ManyToOne
    @JoinColumn(name = "accounting_period_id")
    private AccountingPeriod accountingPeriod;
    
    @Column(name = "regularUsers_reopening_reason")
    private String regularUsersReopeningReason;
    
    @Column(name = "allUsers_reopening_reason")
    private String allUsersReopeningReason;

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

    public SubAccountingPeriodStatusEnum getRegularUsersSubPeriodStatus() {
        return regularUsersSubPeriodStatus;
    }

    public void setRegularUsersSubPeriodStatus(SubAccountingPeriodStatusEnum regularUsersSubPeriodStatus) {
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

	public SubAccountingPeriodStatusEnum getAllUsersSubPeriodStatus() {
		return allUsersSubPeriodStatus;
	}

	public void setAllUsersSubPeriodStatus(SubAccountingPeriodStatusEnum allUsersSubPeriodStatus) {
		this.allUsersSubPeriodStatus = allUsersSubPeriodStatus;
	}

	public String getRegularUsersReopeningReason() {
		return regularUsersReopeningReason;
	}

	public void setRegularUsersReopeningReason(String regularUsersReopeningReason) {
		this.regularUsersReopeningReason = regularUsersReopeningReason;
	}

	public String getAllUsersReopeningReason() {
		return allUsersReopeningReason;
	}

	public void setAllUsersReopeningReason(String allUsersReopeningReason) {
		this.allUsersReopeningReason = allUsersReopeningReason;
	}
	
}
