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

import java.util.Date;

import javax.persistence.Cacheable;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import javax.persistence.Transient;
import javax.persistence.UniqueConstraint;
import javax.validation.constraints.Size;

import org.hibernate.annotations.GenericGenerator;
import org.hibernate.annotations.Parameter;
import org.meveo.model.AuditableEntity;
import org.meveo.model.ExportIdentifier;
import org.meveo.model.shared.DateUtils;

/**
 * InvoiceSubcategoryCountry entity.
 */
@Entity
@Cacheable
@ExportIdentifier({ "invoiceSubCategory.code", "tradingCountry.country.countryCode", "tax.code", "startValidityDate", "endValidityDate" })
@Table(name = "billing_inv_sub_cat_country", uniqueConstraints = @UniqueConstraint(columnNames = { "invoice_sub_category_id", "selling_country_id", "trading_country_id",
        "start_validity_date", "end_validity_date" }))
@GenericGenerator(name = "ID_GENERATOR", strategy = "org.hibernate.id.enhanced.SequenceStyleGenerator", parameters = {
        @Parameter(name = "sequence_name", value = "billing_inv_sub_cat_country_seq"), })
@NamedQueries({
        @NamedQuery(name = "InvoiceSubcategoryCountry.findByInvoiceSubCategoryAndCountry", query = "select i from InvoiceSubcategoryCountry i where i.invoiceSubCategory=:invoiceSubCategory and (i.sellingCountry=:sellingCountry or i.sellingCountry is null) and (i.tradingCountry=:tradingCountry or i.tradingCountry is null) and ((i.startValidityDate is null or i.startValidityDate<=:applicationDate) AND (:applicationDate<i.endValidityDate or i.endValidityDate is null)) ORDER BY sellingCountry asc, tradingCountry asc, priority DESC") })
public class InvoiceSubcategoryCountry extends AuditableEntity {
    private static final long serialVersionUID = 1L;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "invoice_sub_category_id")
    private InvoiceSubCategory invoiceSubCategory;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "selling_country_id")
    private TradingCountry sellingCountry;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "trading_country_id")
    private TradingCountry tradingCountry;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "tax_id")
    private Tax tax;

    @Column(name = "filter_el", length = 2000)
    @Size(max = 2000)
    private String filterEL;

    @Column(name = "tax_code_el", length = 2000)
    @Size(max = 2000)
    private String taxCodeEL;

    @Column(name = "tax_code_el_sp", length = 2000)
    @Size(max = 2000)
    private String taxCodeELSpark;

    @Column(name = "start_validity_date")
    @Temporal(TemporalType.DATE)
    private Date startValidityDate;

    @Column(name = "end_validity_date")
    @Temporal(TemporalType.DATE)
    private Date endValidityDate;

    @Column(name = "priority")
    private int priority;

    @Transient
    private Boolean strictMatch;

    @Transient
    private Date startValidityDateMatch;

    @Transient
    private Date endValidityDateMatch;

    public InvoiceSubCategory getInvoiceSubCategory() {
        return invoiceSubCategory;
    }

    public void setInvoiceSubCategory(InvoiceSubCategory invoiceSubCategory) {
        this.invoiceSubCategory = invoiceSubCategory;
    }

    public TradingCountry getSellingCountry() {
        return sellingCountry;
    }

    public void setSellingCountry(TradingCountry sellingCountry) {
        this.sellingCountry = sellingCountry;
    }

    public TradingCountry getTradingCountry() {
        return tradingCountry;
    }

    public void setTradingCountry(TradingCountry tradingCountry) {
        this.tradingCountry = tradingCountry;
    }

    public Tax getTax() {
        return tax;
    }

    public void setTax(Tax tax) {
        this.tax = tax;
    }

    /**
     * @return Expression to determine tax code
     */
    public String getTaxCodeEL() {
        return taxCodeEL;
    }

    /**
     * @param taxCodeEL Expression to determine tax code
     */
    public void setTaxCodeEL(String taxCodeEL) {
        this.taxCodeEL = taxCodeEL;
    }

    /**
     * @return Expression to determine tax code - for Spark
     */
    public String getTaxCodeELSpark() {
        return taxCodeELSpark;
    }

    /**
     * @param taxCodeELSpark Expression to determine tax code - for Spark
     */
    public void setTaxCodeELSpark(String taxCodeELSpark) {
        this.taxCodeELSpark = taxCodeELSpark;
    }

    public String getFilterEL() {
        return filterEL;
    }

    public void setFilterEL(String filterEL) {
        this.filterEL = filterEL;
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = super.hashCode();
        result = prime * result + ((getId() == null) ? 0 : getId().hashCode());
        return result;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        } else if (obj == null) {
            return false;
        } else if (!(obj instanceof InvoiceSubcategoryCountry)) {
            return false;
        }

        InvoiceSubcategoryCountry other = (InvoiceSubcategoryCountry) obj;
        if (getId() == null) {
            if (other.getId() != null) {
                return false;
            }
        } else if (!getId().equals(other.getId())) {
            return false;
        }
        return true;
    }

    public Date getStartValidityDate() {
        return startValidityDate;
    }

    public void setStartValidityDate(Date startValidityDate) {
        this.startValidityDate = startValidityDate;
    }

    public Date getEndValidityDate() {
        return endValidityDate;
    }

    public void setEndValidityDate(Date endValidityDate) {
        this.endValidityDate = endValidityDate;
    }

    public int getPriority() {
        return priority;
    }

    public void setPriority(int priority) {
        this.priority = priority;
    }

    public boolean isCorrespondsToValidityDate(Date startValidityDate, Date endValidityDate, boolean strictMatch) {
        if (strictMatch) {
            boolean match = (startValidityDate == null && this.startValidityDate == null)
                    || (startValidityDate != null && this.startValidityDate != null && startValidityDate.equals(this.startValidityDate));
            match = match && ((endValidityDate == null && this.endValidityDate == null)
                    || (endValidityDate != null && this.endValidityDate != null && endValidityDate.equals(this.endValidityDate)));
            return match;
        }

        return DateUtils.isPeriodsOverlap(this.startValidityDate, this.endValidityDate, startValidityDate, endValidityDate);
    }

    public Date getStartValidityDateMatch() {
        return startValidityDateMatch;
    }

    public void setStartValidityDateMatch(Date startValidityDateMatch) {
        this.startValidityDateMatch = startValidityDateMatch;
    }

    public Date getEndValidityDateMatch() {
        return endValidityDateMatch;
    }

    public void setEndValidityDateMatch(Date endValidityDateMatch) {
        this.endValidityDateMatch = endValidityDateMatch;
    }

    public Boolean isStrictMatch() {
        return strictMatch;
    }

    public void setStrictMatch(Boolean strictMatch) {
        this.strictMatch = strictMatch;
    }
}