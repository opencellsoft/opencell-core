package org.meveo.model.dunning;

import java.math.BigDecimal;
import java.util.List;

import org.hibernate.annotations.GenericGenerator;
import org.hibernate.annotations.Parameter;
import org.hibernate.type.NumericBooleanConverter;
import org.meveo.model.BusinessEntity;
import org.meveo.model.admin.Currency;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Convert;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.JoinTable;
import jakarta.persistence.ManyToMany;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import jakarta.validation.constraints.NotNull;

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

    private static final long serialVersionUID = 8092970257735394941L;

    /**
     * A reminder level, is a level with a send notification action, its purpose is to remind the customer that his payment is due in a couple of days.
     * It is the first level of a policy, and a policy can only have one reminder level.
     * This level is previous to a collection plan, it doesn’t trigger a collection plan.
     */
    @Convert(converter = NumericBooleanConverter.class)
    @Column(name = "reminder")
    private Boolean isReminder = Boolean.FALSE;

    /**
     * A level can be activated or deactivate at any time, it means it is triggered or not within a policy
     */
    @Convert(converter = NumericBooleanConverter.class)
    @Column(name = "active")
    private Boolean isActive = Boolean.TRUE;

    /**
     * It represents the difference between Today and invoice due date when the invoice is not paid
     */
    @Column(name = "days_overdue")
    @NotNull
    private Integer daysOverdue;

    /**
     * If set to TRUE, the level is only triggered when the reason for invoice failure is a soft decline of an automatic payment.
     */
    @Convert(converter = NumericBooleanConverter.class)
    @Column(name = "soft_decline")
    private Boolean isSoftDecline = Boolean.FALSE;

    /**
     * It is a threshold for triggering a dunning level within a policy
     */
    @Column(name = "min_balance", precision = NB_PRECISION, scale = NB_DECIMALS)
    private BigDecimal minBalance;

    /**
     * The currency of the min balance of the dunning level
     */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "min_balance_currency_id")
    private Currency minBalanceCurrency;

    /**
     * Charge is specific to each dunning level, it can be either a percentage of the balance or a flat amount.
     */
    @Enumerated(EnumType.STRING)
    @Column(name = "charge_type")
    private DunningLevelChargeTypeEnum chargeType;

    /**
     * Value of the charge to be applied at the level.
     *
     *  ​if DunningLevelChargeType = Percentage, value < 100
     */
    @Column(name = "charge_value", precision = NB_PRECISION, scale = NB_DECIMALS)
    private BigDecimal chargeValue;

    /**
     * The currency of dunning charge
     */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "charge_currency_id")
    private Currency chargeCurrency;

    /**
     * The end of dunning level refers to the last level of a dunning policy
     */
    @Convert(converter = NumericBooleanConverter.class)
    @Column(name = "end_dunning_level")
    private Boolean isEndOfDunningLevel = Boolean.FALSE;

    @ManyToMany(fetch = FetchType.LAZY)
    @JoinTable(name = "dunning_level_dunning_action", joinColumns = @JoinColumn(name = "dunning_level_id", referencedColumnName = "id"), inverseJoinColumns = @JoinColumn(name = "dunning_action_id", referencedColumnName = "id"))
    private List<DunningAction> dunningActions;

    @OneToMany(mappedBy = "dunningLevel", fetch = FetchType.LAZY, cascade = CascadeType.ALL, orphanRemoval = true)
    private List<DunningPolicyLevel> relatedPolicies;

    public Boolean isReminder() {
        return isReminder;
    }

    public void setReminder(Boolean reminder) {
        isReminder = reminder;
    }

    public Boolean isActive() {
        return isActive;
    }

    public void setActive(Boolean active) {
        isActive = active;
    }

    public Integer getDaysOverdue() {
        return daysOverdue;
    }

    public void setDaysOverdue(Integer daysOverdue) {
        this.daysOverdue = daysOverdue;
    }

    public Boolean isSoftDecline() {
        return isSoftDecline;
    }

    public void setSoftDecline(Boolean softDecline) {
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

    public Boolean isEndOfDunningLevel() {
        return isEndOfDunningLevel;
    }

    public void setEndOfDunningLevel(Boolean endOfDunningLevel) {
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
