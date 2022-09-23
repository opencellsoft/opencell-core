package org.meveo.model.securityDeposit;

import java.math.BigDecimal;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.Table;

import org.hibernate.annotations.GenericGenerator;
import org.hibernate.annotations.Parameter;
import org.hibernate.annotations.Type;
import org.meveo.model.BusinessEntity;
import org.meveo.model.admin.Currency;

@Table(name = "security_deposit_templat")
@Entity
@GenericGenerator(name = "ID_GENERATOR", strategy = "org.hibernate.id.enhanced.SequenceStyleGenerator", parameters = {
        @Parameter(name = "sequence_name", value = "security_deposit_templat_seq"), })
@NamedQueries({
    @NamedQuery(name = "SecurityDepositTemplate.findByTemplateName", query = "SELECT sdt FROM SecurityDepositTemplate sdt where sdt.templateName==:templateName")
})
public class SecurityDepositTemplate extends BusinessEntity {

    /**
     * 
     */
    private static final long serialVersionUID = -8231082352583077713L;

    @Column(name = "template_name", nullable = false)
    private String templateName;

    @ManyToOne
    @JoinColumn(name = "currency_id")
    private Currency currency;

    @Type(type = "numeric_boolean")
    @Column(name = "allow_validity_date")
    private boolean allowValidityDate;

    @Type(type = "numeric_boolean")
    @Column(name = "allow_validity_period")
    private boolean allowValidityPeriod;

    @Column(name = "min_amount")
    private BigDecimal minAmount;

    @Column(name = "max_amount")
    private BigDecimal maxAmount;

    @Enumerated(value = EnumType.STRING)
    @Column(name = "status")
    private SecurityTemplateStatusEnum status;

    @Column(name = "number_instantiation")
    private Integer numberOfInstantiation;

    public String getTemplateName() {
        return templateName;
    }

    public void setTemplateName(String templateName) {
        this.templateName = templateName;
    }

    public Currency getCurrency() {
        return currency;
    }

    public void setCurrency(Currency currency) {
        this.currency = currency;
    }

    public boolean isAllowValidityDate() {
        return allowValidityDate;
    }

    public void setAllowValidityDate(boolean allowValidityDate) {
        this.allowValidityDate = allowValidityDate;
    }

    public boolean isAllowValidityPeriod() {
        return allowValidityPeriod;
    }

    public void setAllowValidityPeriod(boolean allowValidityPeriod) {
        this.allowValidityPeriod = allowValidityPeriod;
    }

    public BigDecimal getMinAmount() {
        return minAmount;
    }

    public void setMinAmount(BigDecimal minAmount) {
        this.minAmount = minAmount;
    }

    public BigDecimal getMaxAmount() {
        return maxAmount;
    }

    public void setMaxAmount(BigDecimal maxAmount) {
        this.maxAmount = maxAmount;
    }

    public SecurityTemplateStatusEnum getStatus() {
        return status;
    }

    public void setStatus(SecurityTemplateStatusEnum status) {
        this.status = status;
    }

    public Integer getNumberOfInstantiation() {
        return numberOfInstantiation;
    }

    public void setNumberOfInstantiation(Integer numberOfInstantiation) {
        this.numberOfInstantiation = numberOfInstantiation;
    }
}
