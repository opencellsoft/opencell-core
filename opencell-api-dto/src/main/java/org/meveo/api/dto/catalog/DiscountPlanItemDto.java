package org.meveo.api.dto.catalog;

import java.math.BigDecimal;

import javax.validation.constraints.NotNull;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

import org.meveo.api.dto.BaseEntityDto;
import org.meveo.api.dto.IEnableDto;
import org.meveo.model.catalog.DiscountPlanItem;

/**
 * Discount plan item
 *
 * @author Tyshan Shi(tyshan@manaty.net)
 * @author Edward P. Legaspi
 * @since Aug 1, 2016 9:34:34 PM
 * @lastModifiedVersion 5.0
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
     * Discount percent
     */
    private BigDecimal percent;

    /**
     * Accounting code
     */
    @Deprecated // until further analysis
    private String accountingCode;

    /**
     * EL expression to determine if discount plan item applies
     */
    private String expressionEl;

    /**
     * EL expression to determine discount percentage
     */
    private String discountPercentEl;

    /**
     * Is entity disabled. Value is ignored in Update action - use enable/disable API instead.
     */
    private Boolean disabled;

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
        this.percent = discountPlanItem.getPercent();
        this.accountingCode = discountPlanItem.getAccountingCode();
        this.expressionEl = discountPlanItem.getExpressionEl();
        this.discountPercentEl = discountPlanItem.getDiscountPercentEl();
        this.disabled = discountPlanItem.isDisabled();
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
     * Gets the percent.
     *
     * @return the percent
     */
    public BigDecimal getPercent() {
        return percent;
    }

    /**
     * Sets the percent.
     *
     * @param percent the new percent
     */
    public void setPercent(BigDecimal percent) {
        this.percent = percent;
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
     * Gets the expression el.
     *
     * @return the expression el
     */
    public String getExpressionEl() {
        return expressionEl;
    }

    /**
     * Sets the expression el.
     *
     * @param expressionEl the new expression el
     */
    public void setExpressionEl(String expressionEl) {
        this.expressionEl = expressionEl;
    }

    /**
     * Gets the discount percent el.
     *
     * @return the discount percent el
     */
    public String getDiscountPercentEl() {
        return discountPercentEl;
    }

    /**
     * Sets the discount percent el.
     *
     * @param discountPercentEl the new discount percent el
     */
    public void setDiscountPercentEl(String discountPercentEl) {
        this.discountPercentEl = discountPercentEl;
    }

    @Override
    public void setDisabled(Boolean disabled) {
        this.disabled = disabled;
    }

    @Override
    public Boolean isDisabled() {
        return disabled;
    }

    @Override
    public String toString() {
        return "DiscountPlanItemDto [code=" + code + ", discountPlanCode=" + discountPlanCode + ", invoiceCategoryCode=" + invoiceCategoryCode + ", invoiceSubCategoryCode="
                + invoiceSubCategoryCode + ", percent=" + percent + ", accountingCode=" + accountingCode + ", expressionEl=" + expressionEl + "]";
    }
}
