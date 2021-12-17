package org.meveo.model.securityDeposit;

import org.hibernate.annotations.GenericGenerator;
import org.hibernate.annotations.Parameter;
import org.hibernate.annotations.Type;
import org.meveo.model.BusinessEntity;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;
import javax.validation.constraints.Digits;
import java.math.BigDecimal;

@Entity
@Table(name = "security_deposit_settings")
@GenericGenerator(name = "ID_GENERATOR", strategy = "org.hibernate.id.enhanced.SequenceStyleGenerator", parameters = {
        @Parameter(name = "sequence_name", value = "security_deposit_settings_seq") })
public class SecurityDepositSettings extends BusinessEntity {
    @Type(type = "numeric_boolean")
    @Column(name = "use_security_deposit")
    private boolean useSecurityDeposit=false;
    @Column(name = "max_amount_security_deposit" , precision = NB_PRECISION, scale = NB_DECIMALS)
    @Digits(integer = NB_PRECISION, fraction = NB_DECIMALS)
    private BigDecimal maxAmountPerSecurityDeposit;
    @Column(name = "max_amount_consumer" , precision = NB_PRECISION, scale = NB_DECIMALS)
    @Digits(integer = NB_PRECISION, fraction = NB_DECIMALS)
    private BigDecimal maxAmountPerCustomer;
    @Type(type = "numeric_boolean")
    @Column(name = "auto_refund")
    private boolean autoRefund = false;
    @Type(type = "numeric_boolean")
    @Column(name = "allow_renew")
    private boolean allowRenew = false;
    @Type(type = "numeric_boolean")
    @Column(name = "allow_transfer")
    private boolean allowTransfer = false;

    public SecurityDepositSettings() {
        super();
    }

    public SecurityDepositSettings(boolean useSecurityDeposit, BigDecimal maxAmountPerSecurityDeposit, BigDecimal maxAmountPerCustomer, boolean autoRefund, boolean allowRenew,
            boolean allowTransfer) {
        super();
        this.useSecurityDeposit = useSecurityDeposit;
        this.maxAmountPerSecurityDeposit = maxAmountPerSecurityDeposit;
        this.maxAmountPerCustomer = maxAmountPerCustomer;
        this.autoRefund = autoRefund;
        this.allowRenew = allowRenew;
        this.allowTransfer = allowTransfer;
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

    public boolean isAllowRenew() {
        return allowRenew;
    }

    public void setAllowRenew(boolean allowRenew) {
        this.allowRenew = allowRenew;
    }

    public boolean isAllowTransfer() {
        return allowTransfer;
    }

    public void setAllowTransfer(boolean allowTransfer) {
        this.allowTransfer = allowTransfer;
    }
}
