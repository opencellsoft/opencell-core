package org.meveo.model.securityDeposit;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

import javax.persistence.*;
import javax.validation.constraints.Digits;

import org.hibernate.annotations.GenericGenerator;
import org.hibernate.annotations.Parameter;
import org.hibernate.annotations.Type;
import org.meveo.model.BusinessEntity;
import org.meveo.model.settings.OpenOrderSetting;

@Entity
@Table(name = "finance_settings")
@GenericGenerator(name = "ID_GENERATOR", strategy = "org.hibernate.id.enhanced.SequenceStyleGenerator", parameters = {
        @Parameter(name = "sequence_name", value = "finance_settings_seq") })
public class FinanceSettings extends BusinessEntity {

    /**
     * 
     */
    private static final long serialVersionUID = -7662503000202423539L;

    @Type(type = "numeric_boolean")
    @Column(name = "use_security_deposit")
    private boolean useSecurityDeposit = true;

    @Column(name = "max_amount_security_deposit", precision = NB_PRECISION, scale = NB_DECIMALS)
    @Digits(integer = NB_PRECISION, fraction = NB_DECIMALS)
    private BigDecimal maxAmountPerSecurityDeposit;

    @Column(name = "max_amount_consumer", precision = NB_PRECISION, scale = NB_DECIMALS)
    @Digits(integer = NB_PRECISION, fraction = NB_DECIMALS)
    private BigDecimal maxAmountPerCustomer;

    @Type(type = "numeric_boolean")
    @Column(name = "auto_refund")
    private boolean autoRefund = false;

    @OneToOne(fetch = FetchType.LAZY, cascade = CascadeType.ALL, orphanRemoval = true)
    @JoinColumn(name = "open_order_settings_id")
    private OpenOrderSetting openOrderSetting;

    @Type(type = "numeric_boolean")
    @Column(name = "activate_dunning")
    private boolean activateDunning = false;

    @Type(type = "numeric_boolean")
    @Column(name = "enable_billing_redirection_rules")
    private boolean enableBillingRedirectionRules = false;

    @Type(type = "numeric_boolean")
    @Column(name = "discount_advanced_mode")
    private boolean discountAdvancedMode = false;

    @Type(type = "numeric_boolean")
    @Column(name = "enable_price_list")
    private boolean enablePriceList = false;

	@Column(name = "article_selection_mode")
	@Enumerated(EnumType.STRING)
	private ArticleSelectionModeEnum articleSelectionMode = ArticleSelectionModeEnum.AFTER_PRICING;

	@Type(type = "json")
    @Column(name = "entities_with_huge_volume", columnDefinition = "jsonb")
    private Map<String, HugeEntity> entitiesWithHugeVolume;

    @Column(name = "nb_partitions_keep")
    private Integer nbPartitionsToKeep;

   @Column(name = "wo_partition_range_months")
   private Integer woPartitionPeriod;

    @Column(name = "rt_partition_range_months")
    private Integer rtPartitionPeriod;

    @Column(name = "edr_partition_range_months")
    private Integer edrPartitionPeriod;

    @Embedded
    private AuxiliaryAccounting auxiliaryAccounting;

    @Type(type = "numeric_boolean")
    @Column(name = "billing_run_process_warning")
    private boolean billingRunProcessWarning;

    public FinanceSettings() {
        super();
    }

    public FinanceSettings(boolean useSecurityDeposit, BigDecimal maxAmountPerSecurityDeposit, BigDecimal maxAmountPerCustomer, boolean autoRefund, boolean activateDunning) {
        super();
        this.useSecurityDeposit = useSecurityDeposit;
        this.maxAmountPerSecurityDeposit = maxAmountPerSecurityDeposit;
        this.maxAmountPerCustomer = maxAmountPerCustomer;
        this.autoRefund = autoRefund;
        this.activateDunning = activateDunning;
    }

    public boolean isUseSecurityDeposit() {
        return useSecurityDeposit;
    }

    public void setUseSecurityDeposit(boolean useSecurityDeposit) {
        this.useSecurityDeposit = useSecurityDeposit;
    }

    public BigDecimal getMaxAmountPerSecurityDeposit() {
        return maxAmountPerSecurityDeposit;
    }

    public void setMaxAmountPerSecurityDeposit(BigDecimal maxAmountPerSecurityDeposit) {
        this.maxAmountPerSecurityDeposit = maxAmountPerSecurityDeposit;
    }

    public BigDecimal getMaxAmountPerCustomer() {
        return maxAmountPerCustomer;
    }

    public void setMaxAmountPerCustomer(BigDecimal maxAmountPerCustomer) {
        this.maxAmountPerCustomer = maxAmountPerCustomer;
    }

    public boolean isAutoRefund() {
        return autoRefund;
    }

    public void setAutoRefund(boolean autoRefund) {
        this.autoRefund = autoRefund;
    }

    public OpenOrderSetting getOpenOrderSetting() {
        return openOrderSetting;
    }

    public void setOpenOrderSetting(OpenOrderSetting openOrderSetting) {
        this.openOrderSetting = openOrderSetting;
    }

    public AuxiliaryAccounting getAuxiliaryAccounting() {
        return auxiliaryAccounting;
    }

    public void setAuxiliaryAccounting(AuxiliaryAccounting auxiliaryAccounting) {
        this.auxiliaryAccounting = auxiliaryAccounting;
    }

    public boolean isActivateDunning() {
        return activateDunning;
    }

    public void setActivateDunning(boolean activateDunning) {
        this.activateDunning = activateDunning;
    }

    public boolean isEnableBillingRedirectionRules() {
        return enableBillingRedirectionRules;
    }
    public void setEnableBillingRedirectionRules(boolean enableBillingRedirectionRules) {
        this.enableBillingRedirectionRules = enableBillingRedirectionRules;
    }

	public boolean isDiscountAdvancedMode() {
		return discountAdvancedMode;
	}

	public void setDiscountAdvancedMode(boolean discountAdvancedMode) {
		this.discountAdvancedMode = discountAdvancedMode;
	}

	public boolean isEnablePriceList() {
		return enablePriceList;
	}

	public void setEnablePriceList(boolean enablePriceList) {
		this.enablePriceList = enablePriceList;
	}

	public ArticleSelectionModeEnum getArticleSelectionMode() {
		return articleSelectionMode;
	}

	public void setArticleSelectionMode(ArticleSelectionModeEnum articleSelectionMode) {
		this.articleSelectionMode = articleSelectionMode;
	}

	public Map<String, HugeEntity> getEntitiesWithHugeVolume() {
		return entitiesWithHugeVolume;
	}

	public void setEntitiesWithHugeVolume(Map<String, HugeEntity> entitiesWithHugeVolume) {
		this.entitiesWithHugeVolume = entitiesWithHugeVolume;
	}

    public boolean isBillingRunProcessWarning() {
        return billingRunProcessWarning;
    }

    public void setBillingRunProcessWarning(boolean billingRunProcessWarning) {
        this.billingRunProcessWarning = billingRunProcessWarning;
    }

    public Integer getNbPartitionsToKeep() {
        return nbPartitionsToKeep;
    }

    public void setNbPartitionsToKeep(Integer nbPartitionsToKeep) {
        this.nbPartitionsToKeep = nbPartitionsToKeep;
    }

    public Integer getWoPartitionPeriod() {
        return woPartitionPeriod;
    }

    public void setWoPartitionPeriod(Integer woPartitionPeriod) {
        this.woPartitionPeriod = woPartitionPeriod;
    }

    public Integer getRtPartitionPeriod() {
        return rtPartitionPeriod;
    }

    public void setRtPartitionPeriod(Integer rtPartitionPeriod) {
        this.rtPartitionPeriod = rtPartitionPeriod;
    }

    public Integer getEdrPartitionPeriod() {
        return edrPartitionPeriod;
    }

    public void setEdrPartitionPeriod(Integer edrPartitionPeriod) {
        this.edrPartitionPeriod = edrPartitionPeriod;
    }
}