/*
 * (C) Copyright 2015-2020 Opencell SAS (https://opencellsoft.com/) and contributors.
 *
 * This program is free software: you can redistribute it and/or modify it under the terms of the GNU Affero General
 * Public License as published by the Free Software Foundation, either version 3 of the License, or (at your option)
 * any later version.
 *
 * THERE IS NO WARRANTY FOR THE PROGRAM, TO THE EXTENT PERMITTED BY APPLICABLE LAW. EXCEPT WHEN
 * OTHERWISE STATED IN WRITING THE COPYRIGHT HOLDERS AND/OR OTHER PARTIES PROVIDE THE PROGRAM "AS
 * IS" WITHOUT WARRANTY OF ANY KIND, EITHER EXPRESSED OR IMPLIED, INCLUDING, BUT NOT LIMITED TO, THE
 * IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE. THE ENTIRE RISK AS TO
 * THE QUALITY AND PERFORMANCE OF THE PROGRAM IS WITH YOU. SHOULD THE PROGRAM PROVE DEFECTIVE,
 * YOU ASSUME THE COST OF ALL NECESSARY SERVICING, REPAIR OR CORRECTION.
 *
 * For more information on the GNU Affero General Public License, please consult
 * <https://www.gnu.org/licenses/agpl-3.0.en.html>.
 */
package org.meveo.model.crm;

import javax.persistence.Cacheable;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.persistence.UniqueConstraint;
import javax.validation.constraints.Size;

import org.hibernate.annotations.GenericGenerator;
import org.hibernate.annotations.Parameter;
import org.hibernate.annotations.Type;
import org.meveo.model.BusinessCFEntity;
import org.meveo.model.CustomFieldEntity;
import org.meveo.model.ExportIdentifier;
import org.meveo.model.ISearchable;
import org.meveo.model.billing.AccountingCode;
import org.meveo.model.tax.TaxCategory;

import java.util.Map;

/**
 * Customer category
 * 
 * @author Andrius Karpavicius
 */
@Entity
@Cacheable
@CustomFieldEntity(cftCodePrefix = "CustomerCategory")
@ExportIdentifier({ "code" })
@Table(name = "crm_customer_category", uniqueConstraints = @UniqueConstraint(columnNames = { "code" }))
@GenericGenerator(name = "ID_GENERATOR", strategy = "org.hibernate.id.enhanced.SequenceStyleGenerator", parameters = { @Parameter(name = "sequence_name", value = "crm_customer_category_seq"), })
public class CustomerCategory extends BusinessCFEntity implements ISearchable {

    private static final long serialVersionUID = 1L;

    /**
     * Is account exonerated from taxes
     */
    @Type(type = "numeric_boolean")
    @Column(name = "exonerated_from_taxes")
    private boolean exoneratedFromTaxes = false;

    /**
     * Expression to determine if account is exonerated from taxes
     */
    @Column(name = "exoneration_tax_el", length = 2000)
    @Size(max = 2000)
    private String exonerationTaxEl;

    /**
     * Expression to determine if account is exonerated from taxes - for Spark
     */
    @Column(name = "exoneration_tax_el_sp", length = 2000)
    @Size(max = 2000)
    private String exonerationTaxElSpark;

    /**
     * Exoneration reason
     */
    @Column(name = "exoneration_reason")
    @Size(max = 255)
    private String exonerationReason;

    /**
     * related Accounting Code
     */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "accounting_code_id")
    private AccountingCode accountingCode;

    /**
     * Account tax category
     **/
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "tax_category_id")
    private TaxCategory taxCategory;

    /**
     * Expression to determine tax category
     */
    @Column(name = "tax_category_el", length = 2000)
    @Size(max = 2000)
    private String taxCategoryEl;

    /**
     * Expression to determine tax category - for Spark
     */
    @Column(name = "tax_category_el_sp", length = 2000)
    @Size(max = 2000)
    private String taxCategoryElSpark;

    /**
     * Translated descriptions in JSON format with language code as a key and translated description as a value
     */
    @Type(type = "json")
    @Column(name = "description_i18n", columnDefinition = "text")
    private Map<String, String> descriptionI18n;

    /**
     * @return True if account is exonerated from taxes
     */
    public boolean getExoneratedFromTaxes() {
        return exoneratedFromTaxes;
    }

    /**
     * @param exoneratedFromTaxes True if account is exonerated from taxes
     */
    public void setExoneratedFromTaxes(boolean exoneratedFromTaxes) {
        this.exoneratedFromTaxes = exoneratedFromTaxes;
    }

    /**
     * @return Expression to determine if account is exonerated from taxes
     */
    public String getExonerationTaxEl() {
        return exonerationTaxEl;
    }

    /**
     * @param exonerationTaxEl Expression to determine if account is exonerated from taxes
     */
    public void setExonerationTaxEl(String exonerationTaxEl) {
        this.exonerationTaxEl = exonerationTaxEl;
    }

    /**
     * @return Expression to determine if account is exonerated from taxes - for Spark
     */
    public String getExonerationTaxElSpark() {
        return exonerationTaxElSpark;
    }

    /**
     * @param exonerationTaxElSpark Expression to determine if account is exonerated from taxes - for Spark
     */
    public void setExonerationTaxElSpark(String exonerationTaxElSpark) {
        this.exonerationTaxElSpark = exonerationTaxElSpark;
    }

    /**
     * @return the exonerationReason
     */
    public String getExonerationReason() {
        return exonerationReason;
    }

    /**
     * @param exonerationReason the exonerationReason to set
     */
    public void setExonerationReason(String exonerationReason) {
        this.exonerationReason = exonerationReason;
    }

    /**
     * @return the accounting code
     */
    public AccountingCode getAccountingCode() {
        return accountingCode;
    }

    /**
     * @param accountingCode the accounting code to set
     */
    public void setAccountingCode(AccountingCode accountingCode) {
        this.accountingCode = accountingCode;
    }

    /**
     * @return Account tax category
     */
    public TaxCategory getTaxCategory() {
        return taxCategory;
    }

    /**
     * @param taxCategory Account tax category
     */
    public void setTaxCategory(TaxCategory taxCategory) {
        this.taxCategory = taxCategory;
    }

    /**
     * @return Expression to determine tax category
     */
    public String getTaxCategoryEl() {
        return taxCategoryEl;
    }

    /**
     * @param taxCategoryEl Expression to determine tax category
     */
    public void setTaxCategoryEl(String taxCategoryEl) {
        this.taxCategoryEl = taxCategoryEl;
    }

    /**
     * @return Expression to determine tax category - for Spark
     */
    public String getTaxCategoryElSpark() {
        return taxCategoryElSpark;
    }

    /**
     * @param taxCategorySpark Expression to determine tax category - for Spark
     */
    public void setTaxCategoryElSpark(String taxCategoryElSpark) {
        this.taxCategoryElSpark = taxCategoryElSpark;
    }

    public Map<String, String> getDescriptionI18n() {
        return descriptionI18n;
    }

    public void setDescriptionI18n(Map<String, String> descriptionI18n) {
        this.descriptionI18n = descriptionI18n;
    }

    public String getLocalizedDescription(String lang) {
        if(descriptionI18n != null) {
            return descriptionI18n.getOrDefault(lang, this.description);
        } else {
            return this.description;
        }
    }
}