package org.meveo.model.billing;

import org.hibernate.annotations.GenericGenerator;
import org.hibernate.annotations.Parameter;
import org.meveo.model.BusinessEntity;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import java.util.Date;

@Entity
@Table(name = "billing_invoice_validation_rule", uniqueConstraints = @UniqueConstraint(columnNames = { "code" }))
@GenericGenerator(name = "ID_GENERATOR", strategy = "org.hibernate.id.enhanced.SequenceStyleGenerator", parameters = {
        @Parameter(name = "sequence_name", value = "billing_invoice_validation_rule_seq"), })
@NamedQueries({@NamedQuery(name = "InvoiceValidationRule.findByCodeAndInvoiceType", query = "select validationRule from InvoiceValidationRule validationRule where validationRule.code =:code and validationRule.invoiceType.code =: invoiceTypeCode")})
public class InvoiceValidationRule extends BusinessEntity {


    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "invoice_type_id", nullable = false)
    @NotNull
    private InvoiceType invoiceType;

    @Column(name = "priority")
    @NotNull
    private Integer priority;

    @Column(name = "valid_from")
    private Date validFrom;

    @Column(name = "valid_to")
    private Date validTo;

    @Column(name = "type")
    @Enumerated(EnumType.STRING)
    @NotNull
    private ValidationRuleTypeEnum type;

    @Column(name = "fail_status")
    @Enumerated(EnumType.STRING)
    private InvoiceValidationStatusEnum failStatus = InvoiceValidationStatusEnum.REJECTED;

    @Column(name = "validation_script")
    private String validationScript;

    @Column(name = "validation_el")
    private String validationEL;

    public InvoiceType getInvoiceType() {
        return invoiceType;
    }

    public void setInvoiceType(InvoiceType invoiceType) {
        this.invoiceType = invoiceType;
    }

    public Integer getPriority() {
        return priority;
    }

    public void setPriority(Integer priority) {
        this.priority = priority;
    }

    public Date getValidFrom() {
        return validFrom;
    }

    public void setValidFrom(Date validFrom) {
        this.validFrom = validFrom;
    }

    public Date getValidTo() {
        return validTo;
    }

    public void setValidTo(Date validTo) {
        this.validTo = validTo;
    }

    public ValidationRuleTypeEnum getType() {
        return type;
    }

    public void setType(ValidationRuleTypeEnum type) {
        this.type = type;
    }

    public String getValidationScript() {
        return validationScript;
    }

    public void setValidationScript(String validationScript) {
        this.validationScript = validationScript;
    }

    public String getValidationEL() {
        return validationEL;
    }

    public void setValidationEL(String validationEL) {
        this.validationEL = validationEL;
    }
}
