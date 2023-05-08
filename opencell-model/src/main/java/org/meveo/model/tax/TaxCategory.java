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
import javax.persistence.QueryHint;
import javax.persistence.Table;
import javax.persistence.UniqueConstraint;

import org.hibernate.annotations.GenericGenerator;
import org.hibernate.annotations.Parameter;
import org.hibernate.annotations.Type;
import org.meveo.model.BusinessCFEntity;
import org.meveo.model.CustomFieldEntity;
import org.meveo.model.ExportIdentifier;
import org.meveo.model.ISearchable;
import org.meveo.model.billing.UntdidTaxationCategory;

/**
 * Tax category
 * 
 * @author Andrius Karpavicius
 *
 */
@Entity
@Cacheable
@ExportIdentifier({ "code" })
@CustomFieldEntity(cftCodePrefix = "TaxCategory")
@Table(name = "billing_tax_category", uniqueConstraints = @UniqueConstraint(columnNames = { "code" }))
@GenericGenerator(name = "ID_GENERATOR", strategy = "org.hibernate.id.enhanced.SequenceStyleGenerator", parameters = { @Parameter(name = "sequence_name", value = "billing_tax_category_seq"), })
@NamedQueries({ @NamedQuery(name = "TaxCategory.getByCode", query = "from TaxCategory t where t.code=:code ", hints = { @QueryHint(name = "org.hibernate.cacheable", value = "TRUE") }),
        @NamedQuery(name = "TaxCategory.getNbrTaxCatNotAssociated", query = "select count(*) from TaxCategory v where v.id not in (select tm.accountTaxCategory.id from TaxMapping tm where tm.accountTaxCategory.id is not null)", hints = {
                @QueryHint(name = "org.hibernate.cacheable", value = "true") }),
        @NamedQuery(name = "TaxCategory.getTaxCatNotAssociated", query = "from TaxCategory v where v.id not in (select tm.accountTaxCategory.id from TaxMapping tm where tm.accountTaxCategory.id is not null)") })
 public class TaxCategory extends BusinessCFEntity implements Serializable, ISearchable {
    private static final long serialVersionUID = 1L;

    /**
     * Translated descriptions in JSON format with language code as a key and translated description as a value
     **/
    @Type(type = "json")
    @Column(name = "description_i18n", columnDefinition = "jsonb")
    private Map<String, String> descriptionI18n;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "untdid_taxation_category_id")
    private UntdidTaxationCategory untdidTaxationCategory;

    /**
     * @param descriptionI18n Translated descriptions in JSON format with language code as a key and translated description as a value
     */
    public Map<String, String> getDescriptionI18n() {
        return this.descriptionI18n;
    }

    /**
     * @return Translated descriptions in JSON format with language code as a key and translated description as a value
     */
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
        if (this.descriptionI18n == null) {
            this.descriptionI18n = new HashMap<>();
        }
        return this.descriptionI18n;
    }

    public UntdidTaxationCategory getUntdidTaxationCategory() {
        return untdidTaxationCategory;
    }

    public void setUntdidTaxationCategory(UntdidTaxationCategory untdidTaxationCategory) {
        this.untdidTaxationCategory = untdidTaxationCategory;
    }
}