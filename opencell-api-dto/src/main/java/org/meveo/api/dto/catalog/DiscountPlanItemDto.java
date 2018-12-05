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
     * @param customFieldsDto the custom fields
     */
    public DiscountPlanItemDto(DiscountPlanItem discountPlanItem, CustomFieldsDto customFieldInstances) {
        this.code = discountPlanItem.getCode();
        this.discountPlanCode = discountPlanItem.getDiscountPlan().getCode();
        this.invoiceCategoryCode = discountPlanItem.getInvoiceCategory() != null ? discountPlanItem.getInvoiceCategory().getCode() : null;
        this.invoiceSubCategoryCode = discountPlanItem.getInvoiceSubCategory() != null ? discountPlanItem.getInvoiceSubCategory().getCode() : null;
        this.accountingCode = discountPlanItem.getAccountingCode();
        this.expressionEl = discountPlanItem.getExpressionEl();
        this.disabled = discountPlanItem.isDisabled();
		this.discountPlanItemType = discountPlanItem.getDiscountPlanItemType();
		this.discountValue = discountPlanItem.getDiscountValue();
		this.discountValueEL = discountPlanItem.getDiscountValueEL();
		
		customFields = customFieldInstances;
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
     * Sets whether this entity is disabled or not.
     */
    @Override
    public void setDisabled(Boolean disabled) {
        this.disabled = disabled;
    }

    /**
     * Whether this entity is disabled.
     */
    @Override
    public Boolean isDisabled() {
        return disabled;
    }

    /**
     * Sets the discount plan item type.
     * @return item type
     */
	public DiscountPlanItemTypeEnum getDiscountPlanItemType() {
		return discountPlanItemType;
	}


    /**
     * Gets the discount plan item type.
     * @return item type
     */
	public void setDiscountPlanItemType(DiscountPlanItemTypeEnum discountPlanItemType) {
		this.discountPlanItemType = discountPlanItemType;
	}

	/**
	 * Gets the discount value. Can be either percentage or fixed. Depending on the item type.
	 * @return the discount value
	 */
	public BigDecimal getDiscountValue() {
		return discountValue;
	}

	/**
	 * Sets the discount value. Can be either percentage or fixed. Depending on the item type.
	 * @param discountValue the discount value
	 */
	public void setDiscountValue(BigDecimal discountValue) {
		this.discountValue = discountValue;
	}

	@Override
	public String toString() {
		return "DiscountPlanItemDto [code=" + code + ", discountPlanCode=" + discountPlanCode + ", invoiceCategoryCode="
				+ invoiceCategoryCode + ", invoiceSubCategoryCode=" + invoiceSubCategoryCode + ", accountingCode="
				+ accountingCode + ", expressionEl=" + expressionEl + ", disabled=" + disabled
				+ ", discountPlanItemType=" + discountPlanItemType + ", discountValue=" + discountValue
				+ ", discountValueEL=" + discountValueEL + ", customFields=" + customFields + "]";
	}

	/**
	 * Sets the discount value el.
	 * @param discountValueEL el expression
	 */
	public void setDiscountValueEL(String discountValueEL) {
		this.discountValueEL = discountValueEL;
	}

	/**
	 * Gets the discount value el.
	 * @return el expression
	 */
	public String getDiscountValueEL() {
		return discountValueEL;
	}

	/**
	 * Gets the custom fields.
	 * @return custom fields associated with this entity
	 */
	public CustomFieldsDto getCustomFields() {
		return customFields;
	}

	/**
	 * Sets the custom fields.
	 * @param customFields custom fields to be associated with this entity
	 */
	public void setCustomFields(CustomFieldsDto customFields) {
		this.customFields = customFields;
	}
}
