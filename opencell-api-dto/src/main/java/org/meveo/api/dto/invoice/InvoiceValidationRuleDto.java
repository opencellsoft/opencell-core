package org.meveo.api.dto.invoice;


import org.meveo.api.dto.BaseEntityDto;
import org.meveo.api.dto.billing.InvoiceTypeDto;
import org.meveo.model.billing.InvoiceValidationStatusEnum;
import org.meveo.model.billing.ValidationRuleTypeEnum;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlRootElement;
import java.util.Date;

@XmlRootElement(name = "CreateInvoiceValidationRuleRequestDto")
@XmlAccessorType(XmlAccessType.FIELD)
public class InvoiceValidationRuleDto extends BaseEntityDto {

    /** The Constant serialVersionUID. */
    private static final long serialVersionUID = 1L;

    private InvoiceTypeDto invoiceTypeDto;

    private String code;

    private String description;

    private Integer priority;

    private Date validFrom;

    private Date validTo;

    private ValidationRuleTypeEnum type;

    private InvoiceValidationStatusEnum failStatus;

    private String validationScript;

    private String validationEL;


    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
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

    public InvoiceValidationStatusEnum getFailStatus() {
        return failStatus;
    }

    public void setFailStatus(InvoiceValidationStatusEnum failStatus) {
        this.failStatus = failStatus;
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

    public InvoiceTypeDto getInvoiceTypeDto() {
        return invoiceTypeDto;
    }

    public void setInvoiceTypeDto(InvoiceTypeDto invoiceTypeDto) {
        this.invoiceTypeDto = invoiceTypeDto;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

}
