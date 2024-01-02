package org.meveo.model.dunning;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.FetchType;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.OneToOne;
import javax.persistence.Table;
import javax.persistence.UniqueConstraint;

import org.hibernate.annotations.GenericGenerator;
import org.hibernate.annotations.Parameter;
import org.hibernate.annotations.Type;
import org.meveo.model.BusinessEntity;
import org.meveo.model.article.AccountingArticle;
import org.meveo.model.payments.CustomerBalance;

/**
 * @author Mbarek-Ay
 * @version 11.0
 */
@Entity
@Table(name = "dunning_settings", uniqueConstraints = @UniqueConstraint(columnNames = { "code" }))
@GenericGenerator(name = "ID_GENERATOR", strategy = "org.hibernate.id.enhanced.SequenceStyleGenerator", parameters = {
        @Parameter(name = "sequence_name", value = "dunning_settings_seq") })
public class DunningSettings extends BusinessEntity {

    private static final long serialVersionUID = 1L;

    public DunningSettings() {
        super();
    }

    public DunningSettings(DunningModeEnum dunningMode, Integer maxDunningLevels, int maxDaysOutstanding, boolean allowInterestForDelay, BigDecimal interestForDelayRate,
            boolean allowDunningCharges, boolean applyDunningChargeFxExchangeRate, AccountingArticle accountingArticle) {
        super();
        this.dunningMode = dunningMode;
        this.maxDunningLevels = maxDunningLevels;
        this.maxDaysOutstanding = maxDaysOutstanding;
        this.allowInterestForDelay = allowInterestForDelay;
        this.interestForDelayRate = interestForDelayRate;
        this.allowDunningCharges = allowDunningCharges;
        this.applyDunningChargeFxExchangeRate = applyDunningChargeFxExchangeRate;
        this.accountingArticle = accountingArticle;
    }

    public DunningSettings(DunningSettings copy) {
        super();
        this.dunningMode = copy.dunningMode;
        this.maxDunningLevels = copy.maxDunningLevels;
        this.maxDaysOutstanding = copy.maxDaysOutstanding;
        this.allowInterestForDelay = copy.allowInterestForDelay;
        this.interestForDelayRate = copy.interestForDelayRate;
        this.allowDunningCharges = copy.allowDunningCharges;
        this.applyDunningChargeFxExchangeRate = copy.applyDunningChargeFxExchangeRate;
        this.accountingArticle = copy.accountingArticle;
        this.code = copy.code;
    }

    /**
     * dunning mode
     */
    @Column(name = "dunning_mode", nullable = false)
    @Enumerated(EnumType.STRING)
    private DunningModeEnum dunningMode = DunningModeEnum.INVOICE_LEVEL;

    /**
     * Maximum number of dunning levels
     */
    @Column(name = "max_dunning_levels")
    private Integer maxDunningLevels = 15;

    /**
     * Maximum days outstanding
     */
    @Column(name = "max_days_outstanding")
    private int maxDaysOutstanding;

    /**
     * Allow interest for delay
     */
    @Type(type = "numeric_boolean")
    @Column(name = "allow_interest_for_delay")
    private boolean allowInterestForDelay = true;

    /**
     * Interest for delay
     */
    @Column(name = "interest_for_delay_rate", precision = NB_PRECISION, scale = NB_DECIMALS)
    private BigDecimal interestForDelayRate;

    /**
     * Allow dunning charges
     */
    @Type(type = "numeric_boolean")
    @Column(name = "allow_dunning_charges")
    private boolean allowDunningCharges = true;

    /**
     * apply dunning charge fx exchange_rate
     */
    @Type(type = "numeric_boolean")
    @Column(name = "apply_dunning_charge_fx_exchange_rate")
    private boolean applyDunningChargeFxExchangeRate = true;

    /**
     * Article code for dunning penalties
     */

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "accounting_article_id", referencedColumnName = "id")
    private AccountingArticle accountingArticle;

    @OneToMany(mappedBy = "dunningSettings", fetch = FetchType.LAZY, cascade = CascadeType.ALL, orphanRemoval = true)
    private List<DunningAgent> dunningAgents = new ArrayList<>();


    @OneToMany(mappedBy = "dunningSettings", fetch = FetchType.LAZY, cascade = CascadeType.ALL, orphanRemoval = true)
    private List<DunningCollectionPlanStatus> dunningCollectionPlanStatuses = new ArrayList<>();

    @OneToMany(mappedBy = "dunningSettings", fetch = FetchType.LAZY, cascade = CascadeType.ALL, orphanRemoval = true)
    private List<DunningPauseReason> dunningPauseReasons = new ArrayList<>();

    @OneToMany(mappedBy = "dunningSettings", fetch = FetchType.LAZY, cascade = CascadeType.ALL, orphanRemoval = true)
    private List<DunningPaymentRetry> dunningPaymentRetries = new ArrayList<>();

    @OneToMany(mappedBy = "dunningSettings", fetch = FetchType.LAZY, cascade = CascadeType.ALL, orphanRemoval = true)
    private List<DunningStopReason> dunningStopReasons = new ArrayList<>();

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "customer_balance_id")
    private CustomerBalance customerBalance;

    public DunningModeEnum getDunningMode() {
        return dunningMode;
    }

    public void setDunningMode(DunningModeEnum dunningMode) {
        this.dunningMode = dunningMode;
    }

    public Integer getMaxDunningLevels() {
        return maxDunningLevels;
    }

    public void setMaxDunningLevels(Integer maxDunningLevels) {
        this.maxDunningLevels = maxDunningLevels;
    }

    public int getMaxDaysOutstanding() {
        return maxDaysOutstanding;
    }

    public void setMaxDaysOutstanding(int maxDaysOutstanding) {
        this.maxDaysOutstanding = maxDaysOutstanding;
    }

    public boolean isAllowInterestForDelay() {
        return allowInterestForDelay;
    }

    public void setAllowInterestForDelay(boolean allowInterestForDelay) {
        this.allowInterestForDelay = allowInterestForDelay;
    }

    public BigDecimal getInterestForDelayRate() {
        return interestForDelayRate;
    }

    public void setInterestForDelayRate(BigDecimal interestForDelayRate) {
        this.interestForDelayRate = interestForDelayRate;
    }

    public boolean isAllowDunningCharges() {
        return allowDunningCharges;
    }

    public void setAllowDunningCharges(boolean allowDunningCharges) {
        this.allowDunningCharges = allowDunningCharges;
    }

    public boolean isApplyDunningChargeFxExchangeRate() {
        return applyDunningChargeFxExchangeRate;
    }

    public void setApplyDunningChargeFxExchangeRate(boolean applyDunningChargeFxExchangeRate) {
        this.applyDunningChargeFxExchangeRate = applyDunningChargeFxExchangeRate;
    }

    public AccountingArticle getAccountingArticle() {
        return accountingArticle;
    }

    public void setAccountingArticle(AccountingArticle accountingArticle) {
        this.accountingArticle = accountingArticle;
    }

    public List<DunningAgent> getDunningAgents() {
        return dunningAgents;
    }

    public void setDunningAgents(List<DunningAgent> dunningAgents) {
        this.dunningAgents = dunningAgents;
    }
    public List<DunningCollectionPlanStatus> getDunningCollectionPlanStatuses() {
        return dunningCollectionPlanStatuses;
    }

    public void setDunningCollectionPlanStatuses(List<DunningCollectionPlanStatus> dunningCollectionPlanStatuses) {
        this.dunningCollectionPlanStatuses = dunningCollectionPlanStatuses;
    }

    public List<DunningPauseReason> getDunningPauseReasons() {
        return dunningPauseReasons;
    }

    public void setDunningPauseReasons(List<DunningPauseReason> dunningPauseReasons) {
        this.dunningPauseReasons = dunningPauseReasons;
    }

    public List<DunningPaymentRetry> getDunningPaymentRetries() {
        return dunningPaymentRetries;
    }

    public void setDunningPaymentRetries(List<DunningPaymentRetry> dunningPaymentRetries) {
        this.dunningPaymentRetries = dunningPaymentRetries;
    }

    public List<DunningStopReason> getDunningStopReasons() {
        return dunningStopReasons;
    }

    public void setDunningStopReasons(List<DunningStopReason> dunningStopReasons) {
        this.dunningStopReasons = dunningStopReasons;
    }

    public CustomerBalance getCustomerBalance() {
        return customerBalance;
    }

    public void setCustomerBalance(CustomerBalance customerBalance) {
        this.customerBalance = customerBalance;
    }
}
