package org.meveo.api.dto.catalog;

import java.math.BigDecimal;

import javax.validation.constraints.NotNull;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

import org.meveo.api.dto.BaseDto;
import org.meveo.api.dto.IEnableDto;
import org.meveo.model.catalog.DiscountPlanItem;

/**
 * Discount plan item
 * 
 * @author Tyshan Shi(tyshan@manaty.net)
 * @since Aug 1, 2016 9:34:34 PM
 * @author Edward P. Legaspi
 * @lastModifiedVersion 5.0
 *
 */
@XmlRootElement(name = "DiscountPlanItem")
@XmlAccessorType(XmlAccessType.FIELD)
public class DiscountPlanItemDto extends BaseDto implements IEnableDto {

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

    public DiscountPlanItemDto() {
    }

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

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public String getDiscountPlanCode() {
        return discountPlanCode;
    }

    public void setDiscountPlanCode(String discountPlanCode) {
        this.discountPlanCode = discountPlanCode;
    }

    public String getInvoiceCategoryCode() {
        return invoiceCategoryCode;
    }

    public void setInvoiceCategoryCode(String invoiceCategoryCode) {
        this.invoiceCategoryCode = invoiceCategoryCode;
    }

    public String getInvoiceSubCategoryCode() {
        return invoiceSubCategoryCode;
    }

    public void setInvoiceSubCategoryCode(String invoiceSubCategoryCode) {
        this.invoiceSubCategoryCode = invoiceSubCategoryCode;
    }

    public BigDecimal getPercent() {
        return percent;
    }

    public void setPercent(BigDecimal percent) {
        this.percent = percent;
    }

    public String getAccountingCode() {
        return accountingCode;
    }

    public void setAccountingCode(String accountingCode) {
        this.accountingCode = accountingCode;
    }

    public String getExpressionEl() {
        return expressionEl;
    }

    public void setExpressionEl(String expressionEl) {
        this.expressionEl = expressionEl;
    }

    @Override
    public String toString() {
        return "DiscountPlanItemDto [code=" + code + ", discountPlanCode=" + discountPlanCode + ", invoiceCategoryCode=" + invoiceCategoryCode + ", invoiceSubCategoryCode="
                + invoiceSubCategoryCode + ", percent=" + percent + ", accountingCode=" + accountingCode + ", expressionEl=" + expressionEl + ", discountPercentEl="
                + discountPercentEl + ", disabled=" + disabled + "]";
    }

    public String getDiscountPercentEl() {
        return discountPercentEl;
    }

    public void setDiscountPercentEl(String discountPercentEl) {
        this.discountPercentEl = discountPercentEl;
    }

    public void setDisabled(Boolean disabled) {
        this.disabled = disabled;
    }

    public Boolean isDisabled() {
        return disabled;
    }
}