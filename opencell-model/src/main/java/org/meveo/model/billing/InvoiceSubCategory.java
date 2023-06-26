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
package org.meveo.model.billing;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.Map;

import javax.persistence.Cacheable;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.Table;
import javax.persistence.UniqueConstraint;

import org.hibernate.annotations.GenericGenerator;
import org.hibernate.annotations.Parameter;
import org.hibernate.annotations.Type;
import org.meveo.model.BusinessCFEntity;
import org.meveo.model.CustomFieldEntity;
import org.meveo.model.ExportIdentifier;
import org.meveo.model.I18nDescripted;
import org.meveo.model.ICustomFieldEntity;
import org.meveo.model.payments.OCCTemplate;

/**
 * @author Edward P. Legaspi
 * @author Abdellatif BARI
 * @lastModifiedVersion 7.0
 */
@Entity
@Cacheable
@ExportIdentifier({ "code" })
@Table(name = "billing_invoice_sub_cat", uniqueConstraints = @UniqueConstraint(columnNames = { "code" }))
@GenericGenerator(name = "ID_GENERATOR", strategy = "org.hibernate.id.enhanced.SequenceStyleGenerator", parameters = { @Parameter(name = "sequence_name", value = "billing_invoice_sub_cat_seq"), })
@CustomFieldEntity(cftCodePrefix = "InvoiceSubCategory", inheritCFValuesFrom = "invoiceCategory")
@NamedQueries({ @NamedQuery(name = "InvoiceSubCategory.listWithCategory", query = "from InvoiceSubCategory sc left join fetch sc.invoiceCategory left join fetch sc.accountingCode")})
public class InvoiceSubCategory extends BusinessCFEntity implements I18nDescripted{

    private static final long serialVersionUID = 1L;

    @Column(name = "discount")
    private BigDecimal discount;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "invoice_category_id")
    private InvoiceCategory invoiceCategory;

    /**
     * Translated descriptions in JSON format with language code as a key and translated description as a value
     */
    @Type(type = "json")
    @Column(name = "description_i18n", columnDefinition = "jsonb")
    private Map<String, String> descriptionI18n;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "accounting_code_id")
    private AccountingCode accountingCode;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "occ_template_id")
    private OCCTemplate occTemplate;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "occ_templ_negative_id")
    private OCCTemplate occTemplateNegative;

    /**
     * Sorting index
     */
    @Column(name = "sort_index")
    private Integer sortIndex;

    public AccountingCode getAccountingCode() {
        return accountingCode;
    }

    public void setAccountingCode(AccountingCode accountingCode) {
        this.accountingCode = accountingCode;
    }

    public InvoiceCategory getInvoiceCategory() {
        return invoiceCategory;
    }

    public void setInvoiceCategory(InvoiceCategory invoiceCategory) {
        this.invoiceCategory = invoiceCategory;
    }

    public BigDecimal getDiscount() {
        return discount;
    }

    public void setDiscount(BigDecimal discount) {
        this.discount = discount;
    }

    @Override
    public ICustomFieldEntity[] getParentCFEntities() {
        if (invoiceCategory != null) {
            return new ICustomFieldEntity[] { invoiceCategory };
        }
        return null;
    }

    public Map<String, String> getDescriptionI18n() {
        return descriptionI18n;
    }

    public void setDescriptionI18n(Map<String, String> descriptionI18n) {
        this.descriptionI18n = descriptionI18n;
    }

    /**
     * Instantiate descriptionI18n field if it is null. NOTE: do not use this method unless you have an intention to modify it's value, as entity will be marked dirty and record
     * will be updated in DB
     *
     * @return descriptionI18n value or instantiated descriptionI18n field value
     */
    public Map<String, String> getDescriptionI18nNullSafe() {
        if (descriptionI18n == null) {
            descriptionI18n = new HashMap<>();
        }
        return descriptionI18n;
    }

    /**
     * @return the occTemplate
     */
    public OCCTemplate getOccTemplate() {
        return occTemplate;
    }

    /**
     * @param occTemplate the occTemplate to set
     */
    public void setOccTemplate(OCCTemplate occTemplate) {
        this.occTemplate = occTemplate;
    }

    /**
     * @return the occTemplateNegative
     */
    public OCCTemplate getOccTemplateNegative() {
        return occTemplateNegative;
    }

    /**
     * @param occTemplateNegative the occTemplateNegative to set
     */
    public void setOccTemplateNegative(OCCTemplate occTemplateNegative) {
        this.occTemplateNegative = occTemplateNegative;
    }

    /**
     * Gets the sorting index.
     *
     * @return the sorting index
     */
    public Integer getSortIndex() {
        return sortIndex;
    }

    /**
     * Sets the sorting index.
     *
     * @param sortIndex the sorting index
     */
    public void setSortIndex(Integer sortIndex) {
        this.sortIndex = sortIndex;
    }
}