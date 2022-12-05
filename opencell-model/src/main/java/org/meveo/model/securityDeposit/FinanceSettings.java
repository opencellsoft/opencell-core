package org.meveo.model.securityDeposit;

import java.math.BigDecimal;

import org.hibernate.annotations.GenericGenerator;
import org.hibernate.annotations.Parameter;
import org.hibernate.type.NumericBooleanConverter;
import org.meveo.model.BusinessEntity;
import org.meveo.model.settings.OpenOrderSetting;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Convert;
import jakarta.persistence.Embedded;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.OneToOne;
import jakarta.persistence.Table;
import jakarta.validation.constraints.Digits;

@Entity
@Table(name = "finance_settings")
@GenericGenerator(name = "ID_GENERATOR", strategy = "org.hibernate.id.enhanced.SequenceStyleGenerator", parameters = {
        @Parameter(name = "sequence_name", value = "finance_settings_seq") })
public class FinanceSettings extends BusinessEntity {

    /**
     * 
     */
    private static final long serialVersionUID = -7662503000202423539L;

    @Convert(converter = NumericBooleanConverter.class)
    @Column(name = "use_security_deposit")
    private boolean useSecurityDeposit = true;

    @Column(name = "max_amount_security_deposit", precision = NB_PRECISION, scale = NB_DECIMALS)
    @Digits(integer = NB_PRECISION, fraction = NB_DECIMALS)
    private BigDecimal maxAmountPerSecurityDeposit;

    @Column(name = "max_amount_consumer", precision = NB_PRECISION, scale = NB_DECIMALS)
    @Digits(integer = NB_PRECISION, fraction = NB_DECIMALS)
    private BigDecimal maxAmountPerCustomer;

    @Convert(converter = NumericBooleanConverter.class)
    @Column(name = "auto_refund")
    private boolean autoRefund = false;

    @OneToOne(fetch = FetchType.LAZY, cascade = CascadeType.ALL, orphanRemoval = true)
    @JoinColumn(name = "open_order_settings_id")
    private OpenOrderSetting openOrderSetting;

    @Convert(converter = NumericBooleanConverter.class)
    @Column(name = "activate_dunning")
    private boolean activateDunning = false;

    @Embedded
    private AuxiliaryAccounting auxiliaryAccounting;

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
}