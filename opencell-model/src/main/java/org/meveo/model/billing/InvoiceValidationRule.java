package org.meveo.model.billing;

import java.util.Date;
import java.util.Map;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.FetchType;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.persistence.Transient;
import javax.persistence.UniqueConstraint;
import javax.validation.constraints.NotNull;

import org.hibernate.annotations.GenericGenerator;
import org.hibernate.annotations.Parameter;
import org.hibernate.annotations.Type;
import org.meveo.model.BusinessEntity;
import org.meveo.model.scripts.ScriptInstance;


@Entity
@Table(name = "billing_invoice_validation_rule", uniqueConstraints = @UniqueConstraint(columnNames = { "code" }))
@GenericGenerator(name = "ID_GENERATOR", strategy = "org.hibernate.id.enhanced.SequenceStyleGenerator", parameters = {
        @Parameter(name = "sequence_name", value = "billing_invoice_validation_rule_seq"), })
public class InvoiceValidationRule extends BusinessEntity {

    @ManyToOne(fetch = FetchType.EAGER)
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

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "script_instance_id")
    private ScriptInstance validationScript;

    @Column(name = "validation_el")
    private String validationEL;
    
    @Type(type = "json")
    @Column(name = "rule_values", columnDefinition = "jsonb")
    private Map<String, String> ruleValues;

    @Transient
    private boolean toReorder;

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

    public ScriptInstance getValidationScript() {
        return validationScript;
    }

    public void setValidationScript(ScriptInstance validationScript) {
        this.validationScript = validationScript;
    }

    public String getValidationEL() {
        return validationEL;
    }

    public void setValidationEL(String validationEL) {
        this.validationEL = validationEL;
    }

    public InvoiceValidationStatusEnum getFailStatus() {
        return failStatus;
    }

    public void setFailStatus(InvoiceValidationStatusEnum failStatus) {
        this.failStatus = failStatus;
    }

	public Map<String, String> getRuleValues() {
		return ruleValues;
	}

	public void setRuleValues(Map<String, String> ruleValues) {
		this.ruleValues = ruleValues;
	}

	public boolean isToReorder() {
		return toReorder;
	}

	public void setToReorder(boolean toReorder) {
		this.toReorder = toReorder;
	}
}
