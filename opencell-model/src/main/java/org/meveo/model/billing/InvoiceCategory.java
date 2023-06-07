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

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.persistence.Cacheable;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.OneToMany;
import javax.persistence.QueryHint;
import javax.persistence.Table;
import javax.persistence.UniqueConstraint;

import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;
import org.hibernate.annotations.GenericGenerator;
import org.hibernate.annotations.Parameter;
import org.hibernate.annotations.Type;
import org.meveo.model.BusinessCFEntity;
import org.meveo.model.CustomFieldEntity;
import org.meveo.model.ExportIdentifier;
import org.meveo.model.I18nDescripted;
import org.meveo.model.ObservableEntity;
import org.meveo.model.payments.OCCTemplate;

/**
 * Invoice category
 * 
 * @author anasseh
 * @author Abdellatif BARI
 * @lastModifiedVersion 7.0
 */
@Entity
@Cacheable
@ObservableEntity
@ExportIdentifier({ "code" })
@Table(name = "billing_invoice_cat", uniqueConstraints = @UniqueConstraint(columnNames = { "code" }))
@GenericGenerator(name = "ID_GENERATOR", strategy = "org.hibernate.id.enhanced.SequenceStyleGenerator", parameters = {
        @Parameter(name = "sequence_name", value = "billing_invoice_cat_seq"), })
@CustomFieldEntity(cftCodePrefix = "InvoiceCategory")
@NamedQueries({
        @NamedQuery(name = "invoiceCategory.getNbrInvoiceCatNotAssociated", query = "select count(*) from InvoiceCategory v where v.id not in (select sub.invoiceCategory.id from InvoiceSubCategory sub where sub.invoiceCategory.id is not null)", hints = {
                @QueryHint(name = "org.hibernate.cacheable", value = "TRUE") }),

        @NamedQuery(name = "invoiceCategory.getInvoiceCatNotAssociated", query = "from InvoiceCategory v where v.id not in (select sub.invoiceCategory.id from InvoiceSubCategory sub where sub.invoiceCategory.id is not null) ") })
public class InvoiceCategory extends BusinessCFEntity implements I18nDescripted {

    private static final long serialVersionUID = 1L;

    /**
     * A list of Invoice subcategories that make up this invoice category
     */
    @Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
    @OneToMany(mappedBy = "invoiceCategory", fetch = FetchType.LAZY)
    private List<InvoiceSubCategory> invoiceSubCategories;

    /**
     * Sorting index
     */
    @Column(name = "sort_index")
    private Integer sortIndex;

    /**
     * Translated descriptions in JSON format with language code as a key and translated description as a value
     */
    @Type(type = "json")
    @Column(name = "description_i18n", columnDefinition = "jsonb")
    private Map<String, String> descriptionI18n;

    /**
     * Account operation template
     */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "occ_template_id")
    private OCCTemplate occTemplate;

    /**
     * An opposite account operation template
     */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "occ_templ_negative_id")
    private OCCTemplate occTemplateNegative;

    public List<InvoiceSubCategory> getInvoiceSubCategories() {
        return invoiceSubCategories;
    }

    public void setInvoiceSubCategories(List<InvoiceSubCategory> invoiceSubCategories) {
        this.invoiceSubCategories = invoiceSubCategories;
    }

    public Integer getSortIndex() {
        return sortIndex;
    }

    public void setSortIndex(Integer sortIndex) {
        this.sortIndex = sortIndex;
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

}