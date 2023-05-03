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
import java.util.List;
import java.util.Map;

import javax.persistence.Cacheable;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.JoinColumn;
import javax.persistence.JoinTable;
import javax.persistence.ManyToMany;
import javax.persistence.ManyToOne;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.QueryHint;
import javax.persistence.Table;
import javax.persistence.UniqueConstraint;
import javax.validation.constraints.NotNull;

import org.hibernate.annotations.GenericGenerator;
import org.hibernate.annotations.Parameter;
import org.hibernate.annotations.Type;
import org.meveo.commons.utils.StringUtils;
import org.meveo.model.BusinessCFEntity;
import org.meveo.model.CustomFieldEntity;
import org.meveo.model.ExportIdentifier;
import org.meveo.model.I18nDescripted;
import org.meveo.model.ObservableEntity;

/**
 * Tax
 * 
 * @author Edward P. Legaspi
 * @author Abdellatif BARI
 * @lastModifiedVersion 7.0
 */
@Entity
@ObservableEntity
@Cacheable
@CustomFieldEntity(cftCodePrefix = "Tax")
@ExportIdentifier({ "code" })
@Table(name = "billing_tax", uniqueConstraints = @UniqueConstraint(columnNames = { "code" }))
@GenericGenerator(name = "ID_GENERATOR", strategy = "org.hibernate.id.enhanced.SequenceStyleGenerator", parameters = { @Parameter(name = "sequence_name", value = "billing_tax_seq"), })
@NamedQueries({
        @NamedQuery(name = "Tax.getNbTaxesNotAssociated", query = "select count(*) from Tax t where t.id not in (select tm.tax.id from TaxMapping tm where tm.tax.id is not null)", hints = {
                @QueryHint(name = "org.hibernate.cacheable", value = "TRUE") }),
        @NamedQuery(name = "Tax.getTaxesNotAssociated", query = "from Tax t where t.id not in (select tm.tax.id from TaxMapping tm where tm.tax.id is not null)"),
        @NamedQuery(name = "Tax.getZeroTax", query = "from Tax t where t.percent=0 ", hints = { @QueryHint(name = "org.hibernate.cacheable", value = "TRUE") }),
        @NamedQuery(name = "Tax.getTaxByCode", query = "from Tax t where t.code=:code ", hints = { @QueryHint(name = "org.hibernate.cacheable", value = "TRUE") }),
        @NamedQuery(name = "Tax.getTaxByPercent", query = "from Tax t where t.percent=:percent "),
        @NamedQuery(name = "Tax.getTaxByRateAndAccountingCodeNull", query = "from Tax t where t.percent=:percent  and t.accountingCode is null"),
        @NamedQuery(name = "Tax.getAllTaxes", query = "from Tax t left join fetch t.accountingCode"),
        @NamedQuery(name = "Tax.getTaxByRateAndAccountingCode", query = "from Tax t where t.percent=:percent and t.accountingCode=:accountingCode ")})
public class Tax extends BusinessCFEntity implements I18nDescripted {
    private static final long serialVersionUID = 1L;

    /**
     * Accounting code
     */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "accounting_code_id")
    private AccountingCode accountingCode;

    /**
     * Tax percent
     */
    @Column(name = "tax_percentage", precision = NB_PRECISION, scale = NB_DECIMALS)
    @NotNull
    private BigDecimal percent;

    /**
     * Translated descriptions in JSON format with language code as a key and translated description as a value
     */
    @Type(type = "json")
    @Column(name = "description_i18n", columnDefinition = "jsonb")
    private Map<String, String> descriptionI18n;

    /**
     * Tax is a composition of other taxes
     */
    @Column(name = "composite")
    @Type(type = "numeric_boolean")
    private boolean composite;

    /**
     * Main taxes
     */
    @ManyToMany(fetch = FetchType.LAZY)
    @JoinTable(name = "billing_tax_composition",
            joinColumns = @JoinColumn(name = "sub_tax_id"), inverseJoinColumns = @JoinColumn(name = "main_tax_id"))
    private List<Tax> mainTaxes;

    /**
     * Sub taxes
     */
    @ManyToMany(fetch = FetchType.LAZY)
    @JoinTable(name = "billing_tax_composition",
            joinColumns = @JoinColumn(name = "main_tax_id"), inverseJoinColumns = @JoinColumn(name = "sub_tax_id"))
    private List<Tax> subTaxes;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "taxation_category_id")
    private UntdidTaxationCategory untdidTaxationCategory;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "vatex_id")
    private UntdidVatex untdidVatex;

    public Tax() {

    }

    public Tax(Tax tax) {
        this.code = tax.getCode();
        this.description = tax.getDescription();
        this.setAuditable(tax.getAuditable());
        this.accountingCode = tax.getAccountingCode();
        this.percent = tax.getPercent();
    }

    public AccountingCode getAccountingCode() {
        return accountingCode;
    }

    public void setAccountingCode(AccountingCode accountingCode) {
        this.accountingCode = accountingCode;
    }

    public BigDecimal getPercent() {
        return percent;
    }

    public void setPercent(BigDecimal percent) {
        this.percent = percent;
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

    public String getIdOrCode() {
        return StringUtils.isBlank(id) ? getCode() : String.valueOf(id);
    }

    public boolean isComposite() {
        return composite;
    }

    public void setComposite(boolean composite) {
        this.composite = composite;
    }

    public List<Tax> getMainTaxes() {
        return mainTaxes;
    }

    public void setMainTaxes(List<Tax> mainTaxes) {
        this.mainTaxes = mainTaxes;
    }

    public List<Tax> getSubTaxes() {
        return subTaxes;
    }

    public void setSubTaxes(List<Tax> subTaxes) {
        this.subTaxes = subTaxes;
    }

    @Override
    public boolean equals(Object obj) {

        if (this == obj) {
            return true;
        } else if (obj == null) {
            return false;
        } else if (!(obj instanceof Tax)) { // Fails with proxed objects: getClass() != obj.getClass()){
            return false;
        }

        Tax other = (Tax) obj;

        if (id != null && other.getId() != null && id.equals(other.getId())) {
            return true;
        }

        if (code == null) {
            if (other.getCode() != null) {
                return false;
            }
        } else if (!code.equals(other.getCode())) {
            return false;
        }
        return true;
    }
    
    public UntdidTaxationCategory getUntdidTaxationCategory() {
		return untdidTaxationCategory;
	}

	public void setUntdidTaxationCategory(UntdidTaxationCategory untdidTaxationCategory) {
		this.untdidTaxationCategory = untdidTaxationCategory;
	}

	public UntdidVatex getUntdidVatex() {
		return untdidVatex;
	}

	public void setUntdidVatex(UntdidVatex untdidVatex) {
		this.untdidVatex = untdidVatex;
	}
}