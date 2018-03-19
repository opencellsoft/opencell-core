package org.meveo.api.dto.catalog;

import java.math.BigDecimal;

import javax.validation.constraints.NotNull;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

import org.meveo.api.dto.BaseDto;
import org.meveo.model.catalog.DiscountPlanItem;

/**
 * 
 * @author Tyshan Shi(tyshan@manaty.net)
 * @since Aug 1, 2016 9:34:34 PM
 *
 */
@XmlRootElement(name = "DiscountPlanItem")
@XmlAccessorType(XmlAccessType.FIELD)
public class DiscountPlanItemDto extends BaseDto {

    private static final long serialVersionUID = -4512584223794507921L;

    @NotNull
    @XmlAttribute(required = true)
    private String code;
    @NotNull
    @XmlElement(required = true)
    private String discountPlanCode;
    private String invoiceCategoryCode;
    private String invoiceSubCategoryCode;
    private BigDecimal percent;
    @Deprecated // until further analysis
    private String accountingCode;
    private String expressionEl;
    private String discountPercentEl;

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
                + invoiceSubCategoryCode + ", percent=" + percent + ", accountingCode=" + accountingCode + ", expressionEl=" + expressionEl + "]";
    }

    public String getDiscountPercentEl() {
        return discountPercentEl;
    }

    public void setDiscountPercentEl(String discountPercentEl) {
        this.discountPercentEl = discountPercentEl;
    }

}
