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
package org.meveo.model.payments;

import java.util.Map;
import java.util.Objects;
import java.util.StringJoiner;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.JoinColumn;
import javax.persistence.OneToOne;
import javax.persistence.Table;
import javax.persistence.UniqueConstraint;
import javax.validation.constraints.Size;

import org.hibernate.annotations.GenericGenerator;
import org.hibernate.annotations.Parameter;
import org.hibernate.annotations.Type;
import org.meveo.model.BusinessEntity;
import org.meveo.model.scripts.ScriptInstance;

/**
 * AccountingScheme entity
 *
 * @author Mohammed STITANE
 * @since 13.0.0
 */
@Entity
@Table(name = "ar_accounting_scheme", uniqueConstraints = @UniqueConstraint(columnNames = { "code" }))
@GenericGenerator(name = "ID_GENERATOR", strategy = "org.hibernate.id.enhanced.SequenceStyleGenerator", parameters = {
        @Parameter(name = "sequence_name", value = "ar_accounting_scheme_seq"), })
public class AccountingScheme extends BusinessEntity {

    private static final long serialVersionUID = -4989724064567423956L;

    /**
     * Long Description
     */
    @Column(name = "long_description")
    @Size(max = 2000)
    private String longDescription;

    /**
     * i18n Long Description
     */
    @Type(type = "json")
    @Column(name = "long_description_i18n", columnDefinition = "jsonb")
    private Map<String, String> longDescriptionI18n;

    /**
     * The script instance.
     */
    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "script_instance_id")
    private ScriptInstance scriptInstance;

    /**
     * Gets script instance.
     *
     * @return the script instance
     */
    public ScriptInstance getScriptInstance() {
        return scriptInstance;
    }

    /**
     * Sets script instance.
     *
     * @param scriptInstance the script instance
     */
    public void setScriptInstance(ScriptInstance scriptInstance) {
        this.scriptInstance = scriptInstance;
    }

    /**
     * Gets long description.
     *
     * @return the long description
     */
    public String getLongDescription() {
        return longDescription;
    }

    /**
     * Sets long description.
     *
     * @param longDescription the long description
     */
    public void setLongDescription(String longDescription) {
        this.longDescription = longDescription;
    }

    /**
     * Get i18 long description values.
     *
     * @return Map with language
     */
    public Map<String, String> getLongDescriptionI18n() {
        return longDescriptionI18n;
    }

    /**
     * Set i18 long description values.
     *
     * @param longDescriptionI18n Map with language
     */
    public void setLongDescriptionI18n(Map<String, String> longDescriptionI18n) {
        this.longDescriptionI18n = longDescriptionI18n;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o)
            return true;
        if (!(o instanceof AccountingScheme))
            return false;
        if (!super.equals(o))
            return false;
        AccountingScheme that = (AccountingScheme) o;
        return Objects.equals(getCode(), that.getCode());
    }

    @Override
    public int hashCode() {
        return Objects.hash(super.hashCode(), getLongDescriptionI18n(), getScriptInstance());
    }

    @Override
    public String toString() {
        return new StringJoiner(", ", AccountingScheme.class.getSimpleName() + "[", "]")
                .add("code='" + code + "'")
                .add("description='" + description + "'")
                .add("longDescription='" + longDescription + "'")
                .add("longDescriptionI18n='" + longDescriptionI18n + "'")
                .add("scriptInstance=" + scriptInstance).toString();
    }

}
