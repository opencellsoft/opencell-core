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

package org.meveo.model.customEntities;

import java.io.Serializable;
import java.util.Map;

import org.meveo.model.IEntity;
import org.meveo.model.ISearchable;

public class CustomTableRecord implements Serializable, IEntity, ISearchable {

    private static final long serialVersionUID = 6342962203104643392L;

    /**
     * Identifier
     */
    private Long id;

    /**
     * Custom entity template code
     */
    private String cetCode;

    /**
     * Field values with field name as map key and field value as map value
     */
    private Map<String, Object> values;

    @Override
    public Long getId() {
        return id;
    }

    @Override
    public void setId(Long id) {
        this.id = id;
    }

    /**
     * @return Custom entity template code
     */
    public String getCetCode() {
        return cetCode;
    }

    /**
     * @param cetCode Custom entity template code
     */
    public void setCetCode(String cetCode) {
        this.cetCode = cetCode;
    }

    /**
     * @return Field values with field name as map key and field value as map value
     */
    public Map<String, Object> getValues() {
        return values;
    }

    /**
     * @param values Field values with field name as map key and field value as map value
     */
    public void setValues(Map<String, Object> values) {
        this.values = values;
    }

    @Override
    public boolean isTransient() {
        return id == null;
    }

    @Override
    public String getCode() {
        return id != null ? id.toString() : null;
    }

    @Override
    public void setCode(String code) {

    }

    @Override
    public String getDescription() {
        return null;
    }

    @Override
    public void setDescription(String description) {
    }
}