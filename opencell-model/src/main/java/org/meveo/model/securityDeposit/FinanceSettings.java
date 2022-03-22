package org.meveo.model.securityDeposit;

import java.math.BigDecimal;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;
import javax.validation.constraints.Digits;

import org.hibernate.annotations.GenericGenerator;
import org.hibernate.annotations.Parameter;
import org.hibernate.annotations.Type;
import org.meveo.model.BusinessEntity;

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
    private boolean useSecurityDeposit = false;

    @Column(name = "max_amount_security_deposit", precision = NB_PRECISION, scale = NB_DECIMALS)
    @Digits(integer = NB_PRECISION, fraction = NB_DECIMALS)
    private BigDecimal maxAmountPerSecurityDeposit;

    @Column(name = "max_amount_consumer", precision = NB_PRECISION, scale = NB_DECIMALS)
    @Digits(integer = NB_PRECISION, fraction = NB_DECIMALS)
    private BigDecimal maxAmountPerCustomer;

    @Type(type = "numeric_boolean")
    @Column(name = "auto_refund")
    private boolean autoRefund = false;

    public FinanceSettings() {
        super();
    }

    public FinanceSettings(boolean useSecurityDeposit, BigDecimal maxAmountPerSecurityDeposit, BigDecimal maxAmountPerCustomer, boolean autoRefund, boolean allowRenew,
            boolean allowTransfer) {
        super();
        this.useSecurityDeposit = useSecurityDeposit;
        this.maxAmountPerSecurityDeposit = maxAmountPerSecurityDeposit;
        this.maxAmountPerCustomer = maxAmountPerCustomer;
        this.autoRefund = autoRefund;
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

    }
