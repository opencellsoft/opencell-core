package org.meveo.model.dunning;

import org.hibernate.annotations.GenericGenerator;
import org.hibernate.annotations.Parameter;
import org.hibernate.annotations.Type;
import org.meveo.model.AuditableEntity;
import org.meveo.model.BusinessEntity;
import org.meveo.model.admin.Currency;
import org.meveo.model.payments.DunningAction;

import javax.persistence.CascadeType;
import javax.persistence.CollectionTable;
import javax.persistence.Column;
import javax.persistence.ElementCollection;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.FetchType;
import javax.persistence.JoinColumn;
import javax.persistence.JoinTable;
import javax.persistence.ManyToMany;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import javax.validation.constraints.NotNull;
import java.math.BigDecimal;
import java.util.List;

/**
 *The dunning level
 * @author khalid.horri
 *
 */
@Entity
@Table(name = "dunning_level")
@GenericGenerator(name = "ID_GENERATOR", strategy = "org.hibernate.id.enhanced.SequenceStyleGenerator", parameters = {
        @Parameter(name = "sequence_name", value = "dunning_level_seq")})
public class DunningLevel extends BusinessEntity {

    @Type(type = "numeric_boolean")
    @Column(name = "reminder")
    private boolean isReminder = Boolean.FALSE;

    @Type(type = "numeric_boolean")
    @Column(name = "active")
    private boolean isActive = Boolean.TRUE;

    @Column(name = "days_overdue")
    @NotNull
    private Integer daysOverdue;

    @Type(type = "numeric_boolean")
    @Column(name = "soft_decline")
    private boolean isSoftDecline = Boolean.FALSE;

    @Column(name = "min_balance", precision = NB_PRECISION, scale = NB_DECIMALS)
    private BigDecimal minBalance;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "min_balance_currency_id")
    private Currency minBalanceCurrency;

    @Enumerated(EnumType.STRING)
    @Column(name = "charge_type")
    private DunningLevelChargeTypeEnum chargeType;

    @Column(name = "charge_value", precision = NB_PRECISION, scale = NB_DECIMALS)
    private BigDecimal  chargeValue;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "charge_currency_id")
    private Currency chargeCurrency;

    @Type(type = "numeric_boolean")
    @Column(name = "end_dunning_level")
    private boolean isEndOfDunningLevel = Boolean.FALSE;

    @ManyToMany(fetch = FetchType.LAZY)
    @JoinTable(name = "dunning_level_dunning_action", joinColumns = @JoinColumn(name = "dunning_level_id", referencedColumnName = "id"), inverseJoinColumns = @JoinColumn(name = "dunning_action_id", referencedColumnName = "id"))
    private List<DunningAction> dunningActions;

    @OneToMany(mappedBy = "dunningLevel", fetch = FetchType.LAZY, cascade = CascadeType.ALL, orphanRemoval = true)
    private List<DunningPolicyLevel> relatedPolicies;

    public boolean isReminder() {
        return isReminder;
    }

    public void setReminder(boolean reminder) {
        isReminder = reminder;
    }

    public boolean isActive() {
        return isActive;
    }

    public void setActive(boolean active) {
        isActive = active;
    }

    public Integer getDaysOverdue() {
        return daysOverdue;
    }

    public void setDaysOverdue(Integer daysOverdue) {
        this.daysOverdue = daysOverdue;
    }

    public boolean isSoftDecline() {
        return isSoftDecline;
    }

    public void setSoftDecline(boolean softDecline) {
        isSoftDecline = softDecline;
    }

    public BigDecimal getMinBalance() {
        return minBalance;
    }

    public void setMinBalance(BigDecimal minBalance) {
        this.minBalance = minBalance;
    }

    public Currency getMinBalanceCurrency() {
        return minBalanceCurrency;
    }

    public void setMinBalanceCurrency(Currency minBalanceCurrency) {
        this.minBalanceCurrency = minBalanceCurrency;
    }

    public DunningLevelChargeTypeEnum getChargeType() {
        return chargeType;
    }

    public void setChargeType(DunningLevelChargeTypeEnum chargeType) {
        this.chargeType = chargeType;
    }

    public BigDecimal getChargeValue() {
        return chargeValue;
    }

    public void setChargeValue(BigDecimal chargeValue) {
        this.chargeValue = chargeValue;
    }

    public Currency getChargeCurrency() {
        return chargeCurrency;
    }

    public void setChargeCurrency(Currency chargeCurrency) {
        this.chargeCurrency = chargeCurrency;
    }

    public boolean isEndOfDunningLevel() {
        return isEndOfDunningLevel;
    }

    public void setEndOfDunningLevel(boolean endOfDunningLevel) {
        isEndOfDunningLevel = endOfDunningLevel;
    }

    public List<DunningAction> getDunningActions() {
        return dunningActions;
    }

    public void setDunningActions(List<DunningAction> dunningActions) {
        this.dunningActions = dunningActions;
    }

    public List<DunningPolicyLevel> getRelatedPolicies() {
        return relatedPolicies;
    }

    public void setRelatedPolicies(List<DunningPolicyLevel> relatedPolicies) {
        this.relatedPolicies = relatedPolicies;
    }
}
