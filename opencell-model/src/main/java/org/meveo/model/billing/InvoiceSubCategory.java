/*
 * (C) Copyright 2015-2016 Opencell SAS (http://opencellsoft.com/) and contributors.
 * (C) Copyright 2009-2014 Manaty SARL (http://manaty.net/) and contributors.
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Affero General Public License as
 * published by the Free Software Foundation, either version 3 of the
 * License, or (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  
 * This program is not suitable for any direct or indirect application in MILITARY industry
 * See the GNU Affero General Public License for more details.
 *
 * You should have received a copy of the GNU Affero General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package org.meveo.model.billing;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.persistence.Cacheable;
import javax.persistence.CascadeType;
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
import org.meveo.model.ICustomFieldEntity;
import org.meveo.model.scripts.ScriptInstance;

/**
 * @author Edward P. Legaspi
 * @lastModifiedVersion 5.0
 */
@Entity
@Cacheable
@ExportIdentifier({ "code" })
@Table(name = "billing_invoice_sub_cat", uniqueConstraints = @UniqueConstraint(columnNames = { "code" }))
@GenericGenerator(name = "ID_GENERATOR", strategy = "org.hibernate.id.enhanced.SequenceStyleGenerator", parameters = {
        @Parameter(name = "sequence_name", value = "billing_invoice_sub_cat_seq"), })
@CustomFieldEntity(cftCodePrefix = "INV_SUB_CAT")
@NamedQueries({
        @NamedQuery(name = "invoiceSubCategory.getNbrInvoiceSubCatNotAssociated", query = "select count(*) from InvoiceSubCategory v where v.id not in (select c.invoiceSubCategory.id from ChargeTemplate c where c.invoiceSubCategory.id is not null)"
                + " and v.id not in (select inv.invoiceSubCategory.id from InvoiceSubcategoryCountry inv where inv.invoiceSubCategory.id is not null)", hints = {
                        @QueryHint(name = "org.hibernate.cacheable", value = "true") }),

        @NamedQuery(name = "invoiceSubCategory.getInvoiceSubCatNotAssociated", query = "from InvoiceSubCategory v where v.id not in (select c.invoiceSubCategory.id from ChargeTemplate c where c.invoiceSubCategory.id is not null) "
                + " and v.id not in (select inv.invoiceSubCategory.id from InvoiceSubcategoryCountry inv where inv.invoiceSubCategory.id is not null)") })
public class InvoiceSubCategory extends BusinessCFEntity {

    private static final long serialVersionUID = 1L;

    @Column(name = "discount")
    private BigDecimal discount;

    @Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
    @OneToMany(mappedBy = "invoiceSubCategory", fetch = FetchType.LAZY, cascade = { CascadeType.ALL })
    private List<InvoiceSubcategoryCountry> invoiceSubcategoryCountries = new ArrayList<InvoiceSubcategoryCountry>();

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "invoice_category_id")
    private InvoiceCategory invoiceCategory;

    @Type(type = "json")
    @Column(name = "description_i18n", columnDefinition = "text")
    private Map<String, String> descriptionI18n;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "accounting_code_id")
    private AccountingCode accountingCode;
    
    @ManyToOne()
    @JoinColumn(name = "tax_script_instance_id")
    private ScriptInstance taxScript;

    public List<InvoiceSubcategoryCountry> getInvoiceSubcategoryCountries() {
        return invoiceSubcategoryCountries;
    }

    public void setInvoiceSubcategoryCountries(List<InvoiceSubcategoryCountry> invoiceSubcategoryCountries) {
        this.invoiceSubcategoryCountries = invoiceSubcategoryCountries;
    }

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
        return new ICustomFieldEntity[] { invoiceCategory };
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

    public ScriptInstance getTaxScript() {
        return taxScript;
    }

    public void setTaxScript(ScriptInstance taxScript) {
        this.taxScript = taxScript;
    }
}