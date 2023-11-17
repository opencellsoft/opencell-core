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

package org.meveo.model.tax;

import java.io.Serializable;

import javax.persistence.AttributeOverride;
import javax.persistence.AttributeOverrides;
import javax.persistence.Cacheable;
import javax.persistence.Column;
import javax.persistence.Embedded;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.QueryHint;
import javax.persistence.Table;
import javax.persistence.UniqueConstraint;
import javax.validation.constraints.Size;

import org.hibernate.annotations.GenericGenerator;
import org.hibernate.annotations.Parameter;
import org.meveo.model.AuditableEntity;
import org.meveo.model.DatePeriod;
import org.meveo.model.ExportIdentifier;
import org.meveo.model.billing.Tax;
import org.meveo.model.billing.TradingCountry;
import org.meveo.model.scripts.ScriptInstance;

/**
 * Tax mapping
 * 
 * @author Andrius Karpavicius
 *
 */
@Entity
@Cacheable
@ExportIdentifier({ "accountTaxCategory.code", "chargeTaxClass.code", "valid.from", "valid.to", "sellerCountry.country.countryCode", "buyerCountry.country.countryCode", "tax.code" })
@Table(name = "billing_tax_mapping", uniqueConstraints = @UniqueConstraint(columnNames = { "tax_category_id", "tax_class_id", "seller_country_id", "buyer_country_id", "valid_from", "valid_to" }))
@GenericGenerator(name = "ID_GENERATOR", strategy = "org.hibernate.id.enhanced.SequenceStyleGenerator", parameters = { @Parameter(name = "sequence_name", value = "billing_tax_mapping_seq"), })
@NamedQueries({
        @NamedQuery(name = "TaxMapping.findApplicableTax", query = "select m from TaxMapping m where m.accountTaxCategory=:taxCategory and (m.chargeTaxClass=:taxClass or m.chargeTaxClass is null) and (m.sellerCountry=:sellerCountry or m.sellerCountry is null) and (m.buyerCountry=:buyerCountry or m.buyerCountry is null) and ((m.valid.from is null or m.valid.from<=:applicationDate) AND (:applicationDate<m.valid.to or m.valid.to is null)) ORDER BY m.chargeTaxClass asc NULLS LAST, m.sellerCountry asc NULLS LAST, m.buyerCountry asc NULLS LAST, priority DESC", hints = {
                @QueryHint(name = "org.hibernate.cacheable", value = "TRUE"), @QueryHint(name = "org.hibernate.readOnly", value = "true") }),
        @NamedQuery(name = "TaxMapping.findApplicableTaxByIds", query = "select m from TaxMapping m where m.accountTaxCategory.id=:taxCategoryId and (m.chargeTaxClass.id=:taxClassId or m.chargeTaxClass is null) and (m.sellerCountry.id=:sellerCountryId or m.sellerCountry.id is null) and (m.buyerCountry.id=:buyerCountryId or m.buyerCountry is null) and ((m.valid.from is null or m.valid.from<=:applicationDate) AND (:applicationDate<m.valid.to or m.valid.to is null)) ORDER BY m.chargeTaxClass asc NULLS LAST, m.sellerCountry asc NULLS LAST, m.buyerCountry asc NULLS LAST, priority DESC"),
})
public class TaxMapping extends AuditableEntity implements Serializable {
    private static final long serialVersionUID = 1L;

    /**
     * Account tax category
     **/
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "tax_category_id", nullable = false)
    private TaxCategory accountTaxCategory;

    /**
     * Charge tax class
     **/
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "tax_class_id")
    private TaxClass chargeTaxClass;

    /**
     * Tax mapping validity
     **/
    @Embedded
    @AttributeOverrides(value = { @AttributeOverride(name = "from", column = @Column(name = "valid_from")), @AttributeOverride(name = "to", column = @Column(name = "valid_to")) })
    private DatePeriod valid;

    /**
     * Seller's country
     **/
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "seller_country_id")
    private TradingCountry sellerCountry;

    /**
     * Buyer's country
     **/
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "buyer_country_id")
    private TradingCountry buyerCountry;

    /**
     * Filter expression
     **/
    @Column(name = "filter_el", length = 2000)
    @Size(max = 2000)
    private String filterEL;

    /**
     * Tax to apply
     **/
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "tax_id")
    private Tax tax;

    /**
     * Tax expression
     **/
    @Column(name = "tax_el", length = 2000)
    @Size(max = 2000)
    private String taxEL;

    /**
     * Script to determine tax
     **/
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "tax_script_id")
    private ScriptInstance taxScript;

    /**
     * Priority. Higher value, higher the priority
     */
    @Column(name = "priority")
    private int priority = 0;

    /**
     * Record's data source
     **/
    @Column(name = "source")
    private String source;

    /**
     * Original record that this record overrides (identifier)
     **/
    @Column(name = "origin_id")
    private Long originId;

    /**
     * @param accountTaxCategory Account tax category
     */
    public void setAccountTaxCategory(TaxCategory accountTaxCategory) {
        this.accountTaxCategory = accountTaxCategory;
    }

    /**
     * @return Account tax category
     */
    public TaxCategory getAccountTaxCategory() {
        return this.accountTaxCategory;
    }

    /**
     * @param chargeTaxClass Charge tax class
     */
    public void setChargeTaxClass(TaxClass chargeTaxClass) {
        this.chargeTaxClass = chargeTaxClass;
    }

    /**
     * @return Charge tax class
     */
    public TaxClass getChargeTaxClass() {
        return this.chargeTaxClass;
    }

    /**
     * @param valid Tax mapping validity
     */
    public void setValid(DatePeriod valid) {
        this.valid = valid;
    }

    /**
     * @return Tax mapping validity
     */
    public DatePeriod getValid() {
        return this.valid;
    }

    /**
     * @param sellerCountry Seller's country
     */
    public void setSellerCountry(TradingCountry sellerCountry) {
        this.sellerCountry = sellerCountry;
    }

    /**
     * @return Seller's country
     */
    public TradingCountry getSellerCountry() {
        return this.sellerCountry;
    }

    /**
     * @param buyerCountry Buyer's country
     */
    public void setBuyerCountry(TradingCountry buyerCountry) {
        this.buyerCountry = buyerCountry;
    }

    /**
     * @return Buyer's country
     */
    public TradingCountry getBuyerCountry() {
        return this.buyerCountry;
    }

    /**
     * @param filterEL Filter expression
     */
    public void setFilterEL(String filterEL) {
        this.filterEL = filterEL;
    }

    /**
     * @return Filter expression
     */
    public String getFilterEL() {
        return this.filterEL;
    }


    /**
     * @param tax Tax to apply
     */
    public void setTax(Tax tax) {
        this.tax = tax;
    }

    /**
     * @return Tax to apply
     */
    public Tax getTax() {
        return this.tax;
    }

    /**
     * @param taxEL Tax expression
     */
    public void setTaxEL(String taxEL) {
        this.taxEL = taxEL;
    }

    /**
     * @return Tax expression
     */
    public String getTaxEL() {
        return this.taxEL;
    }

    /**
     * @param taxScript Script to determine tax
     */
    public void setTaxScript(ScriptInstance taxScript) {
        this.taxScript = taxScript;
    }

    /**
     * @return Script to determine tax
     */
    public ScriptInstance getTaxScript() {
        return this.taxScript;
    }

    /**
     * @return Priority. Higher value, higher the priority
     */
    public int getPriority() {
        return priority;
    }

    /**
     * @param priority Priority. Higher value, higher the priority
     */
    public void setPriority(int priority) {
        this.priority = priority;
    }

    /**
     * @param source Record's data source
     */
    public void setSource(String source) {
        this.source = source;
    }

    /**
     * @return Record's data source
     */
    public String getSource() {
        return this.source;
    }

    /**
     * @param originId Original record that this record overrides (identifier)
     */
    public void setOriginId(Long originId) {
        this.originId = originId;
    }

    /**
     * @return Original record that this record overrides (identifier)
     */
    public Long getOriginId() {
        return this.originId;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        } else if (obj == null) {
            return false;
        } else if (!(obj instanceof TaxMapping)) {
            return false;
        }

        TaxMapping other = (TaxMapping) obj;
        if (getId() == null) {
            if (other.getId() != null) {
                return false;
            }
        } else if (!getId().equals(other.getId())) {
            return false;
        }
        return true;
    }
}