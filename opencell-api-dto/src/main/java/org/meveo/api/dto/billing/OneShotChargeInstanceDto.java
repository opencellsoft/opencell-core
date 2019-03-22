package org.meveo.api.dto.billing;

import org.meveo.api.dto.BusinessEntityDto;
import org.meveo.model.billing.InstanceStatusEnum;
import org.meveo.model.billing.OneShotChargeInstance;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import java.math.BigDecimal;
import java.util.Date;

/**
 * @author Abdellatif BARI
 * @lastModifiedVersion 5.4
 */
@XmlAccessorType(XmlAccessType.FIELD)
public class OneShotChargeInstanceDto extends BusinessEntityDto {

    private Date operationDate;
    private Date terminationDate;
    private InstanceStatusEnum status;
    private BigDecimal quantity;
    private BigDecimal amountWithoutTax;
    private BigDecimal amountWithTax;

    public OneShotChargeInstanceDto() {
        super();
    }

    /**
     * Instantiates a new OneShotChargeInstanceDto.
     */
    public OneShotChargeInstanceDto(OneShotChargeInstance oneShotChargeInstance, BigDecimal amountWithoutTax, BigDecimal amountWithTax) {
        this(oneShotChargeInstance);
        this.amountWithoutTax = amountWithoutTax;
        this.amountWithTax = amountWithTax;
    }

    public OneShotChargeInstanceDto(OneShotChargeInstance oneShotChargeInstance) {
        this.id = oneShotChargeInstance.getId();
        this.code = oneShotChargeInstance.getCode();
        this.operationDate = oneShotChargeInstance.getChargeDate();
        this.terminationDate = oneShotChargeInstance.getTerminationDate();
        this.quantity = oneShotChargeInstance.getQuantity();
        this.description = oneShotChargeInstance.getDescription();
        this.amountWithoutTax = oneShotChargeInstance.getAmountWithoutTax();
        this.amountWithTax = oneShotChargeInstance.getAmountWithTax();
        this.status = oneShotChargeInstance.getStatus();
    }

    public Date getOperationDate() {
        return operationDate;
    }

    public void setOperationDate(Date operationDate) {
        this.operationDate = operationDate;
    }

    public Date getTerminationDate() {
        return terminationDate;
    }

    public void setTerminationDate(Date terminationDate) {
        this.terminationDate = terminationDate;
    }

    public BigDecimal getQuantity() {
        return quantity;
    }

    public void setQuantity(BigDecimal quantity) {
        this.quantity = quantity;
    }

    @Override
    public String getDescription() {
        return description;
    }

    @Override
    public void setDescription(String description) {
        this.description = description;
    }

    public BigDecimal getAmountWithoutTax() {
        return amountWithoutTax;
    }

    public void setAmountWithoutTax(BigDecimal amountWithoutTax) {
        this.amountWithoutTax = amountWithoutTax;
    }

    public BigDecimal getAmountWithTax() {
        return amountWithTax;
    }

    public void setAmountWithTax(BigDecimal amountWithTax) {
        this.amountWithTax = amountWithTax;
    }

    public InstanceStatusEnum getStatus() {
        return status;
    }

    public void setStatus(InstanceStatusEnum status) {
        this.status = status;
    }

    @Override
    public String toString() {
        return "OneShotChargeInstanceDto{" +
                "operationDate=" + operationDate +
                ", terminationDate=" + terminationDate +
                ", status=" + status +
                ", quantity=" + quantity +
                ", amountWithoutTax=" + amountWithoutTax +
                ", amountWithTax=" + amountWithTax +
                '}';
    }
}