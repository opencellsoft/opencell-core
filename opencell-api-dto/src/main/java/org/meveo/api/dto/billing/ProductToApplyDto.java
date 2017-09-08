package org.meveo.api.dto.billing;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Date;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlTransient;

import org.meveo.api.dto.CustomFieldsDto;
import org.meveo.model.catalog.ProductTemplate;

@XmlRootElement(name = "ProductToApply")
@XmlAccessorType(XmlAccessType.FIELD)
public class ProductToApplyDto implements Serializable {

    private static final long serialVersionUID = -3815026205495621916L;

    @XmlAttribute(required = true)
    private String code;

    @XmlElement(required = true)
    private BigDecimal quantity;

    private Date applicationDate;

	@XmlElement(required = false)
	private String criteria1;

	@XmlElement(required = false)
	private String criteria2;

	@XmlElement(required = false)
	private String criteria3;
	
	@XmlElement(required = false)
	private BigDecimal amountWithoutTax;

	@XmlElement(required = false)
	private BigDecimal amountWithTax;

    private CustomFieldsDto customFields;

    @XmlTransient
    // @ApiModelProperty(hidden = true)
    private ProductTemplate productTemplate;

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public BigDecimal getQuantity() {
        if (quantity == null)
            return new BigDecimal(0);
        return quantity;
    }

    public void setQuantity(BigDecimal quantity) {
        this.quantity = quantity;
    }

    public Date getApplicationDate() {
		return applicationDate;
	}

	public void setApplicationDate(Date applicationDate) {
		this.applicationDate = applicationDate;
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

	public CustomFieldsDto getCustomFields() {
        return customFields;
    }

    public void setCustomFields(CustomFieldsDto customFields) {
        this.customFields = customFields;
    }

    public ProductTemplate getProductTemplate() {
		return productTemplate;
	}

	public void setProductTemplate(ProductTemplate productTemplate) {
		this.productTemplate = productTemplate;
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

	@Override
    public String toString() {
        return String.format("ServiceToInstantiateDto [code=%s, quantity=%s, applicationDate=%s,amountWithoutTax =%s, amountWithTax=%s, customFields=%s]", code, quantity, applicationDate,
        		amountWithoutTax,amountWithTax, customFields);
    }


}