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
package org.meveo.model.admin;

import javax.persistence.Cacheable;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;
import javax.validation.constraints.Size;

import org.hibernate.annotations.GenericGenerator;
import org.hibernate.annotations.Parameter;
import org.hibernate.annotations.Type;
import org.meveo.model.AuditableEntity;
import org.meveo.model.ExportIdentifier;

import java.util.Map;

/**
 * Currency entity
 * @author Khalid HORRI
 * @lastModifiedVersion 5.3
 */
@Entity
@Cacheable
@ExportIdentifier("currencyCode")
@Table(name = "adm_currency")
@GenericGenerator(name = "ID_GENERATOR", strategy = "org.hibernate.id.enhanced.SequenceStyleGenerator", parameters = {
        @Parameter(name = "sequence_name", value = "adm_currency_seq"), })
public class Currency extends AuditableEntity {

    private static final long serialVersionUID = 1L;

    /** Currency code e.g. EUR for euros. */
    @Column(name = "currency_code", length = 3, unique = true, nullable=false)
    @Size(max = 3)
    private String currencyCode;

    /** Currency name. */
    @Column(name = "description_en", length = 255, unique = true, nullable=false)
    @Size(max = 255)
    private String descriptionEn;

    /** Flag field that indicates if it is system currency. */
    @Type(type = "numeric_boolean")
    @Column(name = "system_currency")
    private Boolean systemCurrency;

    /**
     * Translated descriptions in JSON format with language code as a key and translated description as a value
     */
    @Type(type = "json")
    @Column(name = "description_i18n", columnDefinition = "text")
    private Map<String, String> descriptionI18n;

    public String getCurrencyCode() {
        return currencyCode;
    }

    public void setCurrencyCode(String currencyCode) {
        this.currencyCode = currencyCode;
    }

    public String getDescriptionEn() {
        return descriptionEn;
    }

    public void setDescriptionEn(String descriptionEn) {
        this.descriptionEn = descriptionEn;
    }

    public Boolean getSystemCurrency() {
        return systemCurrency;
    }

    public Map<String, String> getDescriptionI18n() {
        return descriptionI18n;
    }

    public void setDescriptionI18n(Map<String, String> descriptionI18n) {
        this.descriptionI18n = descriptionI18n;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        } else if (obj == null) {
            return false;
        } else if (!(obj instanceof Currency)) {
            return false;
        }

        Currency other = (Currency) obj;
        if (id != null && other.getId() != null && id.equals(other.getId())) {
            return true;
        }

        if (currencyCode == null) {
            if (other.currencyCode != null) {
                return false;
            }
        } else if (!currencyCode.equals(other.currencyCode)) {
            return false;
        }
        return true;
    }

    public String toString() {
        return currencyCode;
    }

    public boolean isTransient() {
        // TODO Auto-generated method stub
        return false;
    }

    @Override
    public int hashCode() {
        return 961 + (("Currency" + (currencyCode == null ? "" : currencyCode)).hashCode());
    }

    public void setSystemCurrency(Boolean systemCurrency) {
        this.systemCurrency = systemCurrency;
    }

    public String getDescription() {
        return descriptionEn;
    }

    public String getLocalizedDescription(String lang) {
        if(descriptionI18n != null) {
            return descriptionI18n.getOrDefault(lang, this.descriptionEn);
        } else {
            return this.descriptionEn;
        }
    }

}
