package org.meveo.api.dto.catalog;

import java.math.BigDecimal;

import javax.validation.constraints.NotNull;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

import org.meveo.api.dto.BaseEntityDto;
import org.meveo.api.dto.CustomFieldsDto;
import org.meveo.api.dto.IEnableDto;
import org.meveo.model.catalog.DiscountPlanItem;
import org.meveo.model.catalog.DiscountPlanItemTypeEnum;

/**
 * Discount plan item
 *
 * @author Tyshan Shi(tyshan@manaty.net)
 * @author Edward P. Legaspi
 * @lastModifiedVersion 5.3
 */
@XmlRootElement(name = "DiscountPlanItem")
@XmlAccessorType(XmlAccessType.FIELD)
public class DiscountPlanItemDto extends BaseEntityDto implements IEnableDto {

    /** The Constant serialVersionUID. */
    private static final long serialVersionUID = -4512584223794507921L;

    /**
     * Code
     */
    @NotNull
    @XmlAttribute(required = true)
    private String code;

    /**
     * Discount plan code
     */
    @NotNull
    @XmlElement(required = true)
    private String discountPlanCode;

    /**
     * Invoice category code
     */
    private String invoiceCategoryCode;

    /**
     * Invoice sub category code
     */
    private String invoiceSubCategoryCode;

    /**
     * Accounting code
     */
    @Deprecated // until further analysis
    private String accountingCode;

    /**
     * Expression to determine if discount applies
     */
    private String expressionEl;

    /**
     * Expression to determine if discount applies - for Spark
     */
    private String expressionElSpark;

    /**
     * Is entity disabled. Value is ignored in Update action - use enable/disable API instead.
     */
    private Boolean disabled;
	
	/** Type of discount, whether absolute or percentage. */
	private DiscountPlanItemTypeEnum discountPlanItemType = DiscountPlanItemTypeEnum.PERCENTAGE;
    
	/**
     * The absolute or percentage discount amount.
     */
	private BigDecimal discountValue;
	
	/**
     * The absolute or percentage discount amount EL.
     */
	private String discountValueEL;
	
	/**
     * Expression to calculate discount percentage - for Spark
     */
	private String discountValueElSpark;
	
	/** The custom fields. */
    @XmlElement(required = false)
    private CustomFieldsDto customFields;

    /**
     * Instantiates a new discount plan item dto.
     */
    public DiscountPlanItemDto() {
    }

    /**
     * Convert discount plan item entity to DTO
     *
     * @param discountPlanItem Entity to convert
     */
    public DiscountPlanItemDto(DiscountPlanItem discountPlanItem) {
        this.code = discountPlanItem.getCode();
        this.discountPlanCode = discountPlanItem.getDiscountPlan().getCode();
        this.invoiceCategoryCode = discountPlanItem.getInvoiceCategory() != null ? discountPlanItem.getInvoiceCategory().getCode() : null;
        this.invoiceSubCategoryCode = discountPlanItem.getInvoiceSubCategory() != null ? discountPlanItem.getInvoiceSubCategory().getCode() : null;
        this.accountingCode = discountPlanItem.getAccountingCode();
        this.expressionEl = discountPlanItem.getExpressionEl();
        this.expressionElSpark = discountPlanItem.getExpressionElSpark();
        this.disabled = discountPlanItem.isDisabled();
		this.discountPlanItemType = discountPlanItem.getDiscountPlanItemType();
		this.discountValue = discountPlanItem.getDiscountValue();
		this.discountValueEL = discountPlanItem.getDiscountValueEL();
		this.discountValueElSpark = discountPlanItem.getDiscountValueElSpark();
    }

    /**
     * Gets the code.
     *
     * @return the code
     */
    public String getCode() {
        return code;
    }

    /**
     * Sets the code.
     *
     * @param code the new code
     */
    public void setCode(String code) {
        this.code = code;
    }

    /**
     * Gets the discount plan code.
     *
     * @return the discount plan code
     */
    public String getDiscountPlanCode() {
        return discountPlanCode;
    }

    /**
     * Sets the discount plan code.
     *
     * @param discountPlanCode the new discount plan code
     */
    public void setDiscountPlanCode(String discountPlanCode) {
        this.discountPlanCode = discountPlanCode;
    }

    /**
     * Gets the invoice category code.
     *
     * @return the invoice category code
     */
    public String getInvoiceCategoryCode() {
        return invoiceCategoryCode;
    }

    /**
     * Sets the invoice category code.
     *
     * @param invoiceCategoryCode the new invoice category code
     */
    public void setInvoiceCategoryCode(String invoiceCategoryCode) {
        this.invoiceCategoryCode = invoiceCategoryCode;
    }

    /**
     * Gets the invoice sub category code.
     *
     * @return the invoice sub category code
     */
    public String getInvoiceSubCategoryCode() {
        return invoiceSubCategoryCode;
    }

    /**
     * Sets the invoice sub category code.
     *
     * @param invoiceSubCategoryCode the new invoice sub category code
     */
    public void setInvoiceSubCategoryCode(String invoiceSubCategoryCode) {
        this.invoiceSubCategoryCode = invoiceSubCategoryCode;
    }

    /**
     * Gets the accounting code.
     *
     * @return the accounting code
     */
    public String getAccountingCode() {
        return accountingCode;
    }

    /**
     * Sets the accounting code.
     *
     * @param accountingCode the new accounting code
     */
    public void setAccountingCode(String accountingCode) {
        this.accountingCode = accountingCode;
    }

    /**
     * @return Expression to determine if discount applies
     */
    public String getExpressionEl() {
        return expressionEl;
    }

    /**
     * @param expressionEl Expression to determine if discount applies
     */
    public void setExpressionEl(String expressionEl) {
        this.expressionEl = expressionEl;
    }

    /**
     * @return Expression to determine if discount applies - for Spark
     */
    public String getExpressionElSpark() {
        return expressionElSpark;
    }

    /**
     * @param expressionElSpark Expression to determine if discount applies - for Spark
     */
    public void setExpressionElSpark(String expressionElSpark) {
        this.expressionElSpark = expressionElSpark;
    }

    @Override
    public void setDisabled(Boolean disabled) {
        this.disabled = disabled;
    }

    @Override
    public Boolean isDisabled() {
        return disabled;
    }

	public DiscountPlanItemTypeEnum getDiscountPlanItemType() {
		return discountPlanItemType;
	}

	public void setDiscountPlanItemType(DiscountPlanItemTypeEnum discountPlanItemType) {
		this.discountPlanItemType = discountPlanItemType;
	}

	public BigDecimal getDiscountValue() {
		return discountValue;
	}

	public void setDiscountValue(BigDecimal discountValue) {
		this.discountValue = discountValue;
	}

	public String getDiscountValueElSpark() {
		return discountValueElSpark;
	}

	public void setDiscountValueElSpark(String discountValueElSpark) {
		this.discountValueElSpark = discountValueElSpark;
	}

	@Override
	public String toString() {
		return "DiscountPlanItemDto [code=" + code + ", discountPlanCode=" + discountPlanCode + ", invoiceCategoryCode="
				+ invoiceCategoryCode + ", invoiceSubCategoryCode=" + invoiceSubCategoryCode + ", accountingCode="
				+ accountingCode + ", expressionEl=" + expressionEl + ", expressionElSpark=" + expressionElSpark
				+ ", disabled=" + disabled + ", discountPlanItemType=" + discountPlanItemType + ", discountValue="
				+ discountValue + ", discountValueEL=" + discountValueEL + ", discountValueElSpark="
				+ discountValueElSpark + "]";
	}

	public void setDiscountValueEL(String discountValueEL) {
		this.discountValueEL = discountValueEL;
	}

	public String getDiscountValueEL() {
		return discountValueEL;
	}

	public CustomFieldsDto getCustomFields() {
		return customFields;
	}

	public void setCustomFields(CustomFieldsDto customFields) {
		this.customFields = customFields;
	}
}
