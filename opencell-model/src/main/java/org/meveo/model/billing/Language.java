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
 * Language entity
 * @author Khalid HORRI
 * @lastModifiedVersion 5.3
 */
@Entity
@Cacheable
@ExportIdentifier("languageCode")
@Table(name = "adm_language")
@GenericGenerator(name = "ID_GENERATOR", strategy = "org.hibernate.id.enhanced.SequenceStyleGenerator", parameters = {
        @Parameter(name = "sequence_name", value = "adm_language_seq"), })
public class Language extends AuditableEntity {
    private static final long serialVersionUID = 1L;

    /**
     * Language 3 letter code
     */
    @Column(name = "language_code", length = 3, unique = true, nullable=false)
    @Size(max = 3)
    private String languageCode;

    /**
     * Description
     */
    @Column(name = "description_en", length = 100, unique = true, nullable=false)
    @Size(max = 100)
    private String descriptionEn;

    /**
     * Translated descriptions in JSON format with language code as a key and translated description as a value
     */
    @Type(type = "json")
    @Column(name = "description_i18n", columnDefinition = "text")
    private Map<String, String> descriptionI18n;

    public String getLanguageCode() {
        return languageCode;
    }

    public void setLanguageCode(String languageCode) {
        this.languageCode = languageCode;
    }

    public String getDescriptionEn() {
        return descriptionEn;
    }

    public void setDescriptionEn(String descriptionEn) {
        this.descriptionEn = descriptionEn;
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
        } else if (!(obj instanceof Language)) {
            return false;
        }

        Language other = (Language) obj;
        if (id != null && other.getId() != null && id.equals(other.getId())) {
            return true;
        }
        if (languageCode == null) {
            if (other.languageCode != null) {
                return false;
            }
        } else if (!languageCode.equals(other.languageCode)) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return languageCode;
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