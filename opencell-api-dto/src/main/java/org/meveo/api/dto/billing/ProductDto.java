package org.meveo.api.dto.billing;

import java.math.BigDecimal;
import java.util.Date;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;

import org.meveo.api.dto.BaseDto;
import org.meveo.api.dto.CustomFieldsDto;
import org.meveo.model.billing.ProductChargeInstance;

@XmlAccessorType(XmlAccessType.FIELD)
public class ProductDto extends BaseDto {

    private static final long serialVersionUID = -4084004747483067153L;

    @XmlAttribute(required = true)
    private String code;

    @XmlAttribute()
    private String description;

    private Date chargeDate;

    private BigDecimal quantity;

	private BigDecimal amountWithoutTax;
	private BigDecimal amountWithTax;
	private String criteria1;
	private String criteria2;
	private String criteria3;

    private CustomFieldsDto customFields;

    public ProductDto() {

    }

    public ProductDto(ProductChargeInstance e, CustomFieldsDto customFieldInstances) {
        code = e.getCode();
        description = e.getDescription();
        chargeDate = e.getChargeDate();
        quantity = e.getQuantity();
        customFields = customFieldInstances;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public Date getChargeDate() {
		return chargeDate;
	}

	public void setChargeDate(Date chargeDate) {
		this.chargeDate = chargeDate;
	}

    public BigDecimal getQuantity() {
        return quantity;
    }

    public void setQuantity(BigDecimal quantity) {
        this.quantity = quantity;
    }

    @Override
    public String toString() {
        return "ServiceInstanceDto [code=" + code + ", description=" + description + ", chargeDate=" + chargeDate
                 + ", quantity=" + quantity  +"]";
    }

    public String getDescription() {
        return description;
    }

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

	public String getCriteria1() {
		return criteria1;
	}

	public void setCriteria1(String criteria1) {
		this.criteria1 = criteria1;
	}

	public String getCriteria2() {
		return criteria2;
	}

	public void setCriteria2(String criteria2) {
		this.criteria2 = criteria2;
	}

	public String getCriteria3() {
		return criteria3;
	}

	public void setCriteria3(String criteria3) {
		this.criteria3 = criteria3;
	}

	public CustomFieldsDto getCustomFields() {
        return customFields;
    }

    public void setCustomFields(CustomFieldsDto customFields) {
        this.customFields = customFields;
    }
}