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

package org.meveo.api.security.config;

import org.meveo.model.BusinessEntity;

/**
 * POJO to configure property to check on filtered items
 *
 * @author Mounir Boukayoua
 * @since 10.0
 */
public class FilterPropertyConfig {

    /**
     * property to use to extract code value from the filtered item
     */
    private String property;

    /**
     * Secured Entity class to check
     */
    private Class<? extends BusinessEntity> entityClass;

    /**
     * allow filtered item if its checked property is null
     */
    private boolean allowAccessIfNull = false;

    public String getProperty() {
        return property;
    }

    public void setProperty(String property) {
        this.property = property;
    }

    public Class<? extends BusinessEntity> getEntityClass() {
        return entityClass;
    }

    public void setEntityClass(Class<? extends BusinessEntity> entityClass) {
        this.entityClass = entityClass;
    }

    public boolean isAllowAccessIfNull() {
        return allowAccessIfNull;
    }

    public void setAllowAccessIfNull(boolean allowAccessIfNull) {
        this.allowAccessIfNull = allowAccessIfNull;
    }

    @Override
    public String toString() {
        return "FilterPropertyConfig{" +
                "property='" + property + '\'' +
                ", entityClass=" + entityClass +
                ", allowAccessIfNull=" + allowAccessIfNull +
                '}';
    }
}
