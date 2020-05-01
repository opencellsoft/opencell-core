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

import org.meveo.api.security.parameter.SecureMethodParameterParser;
import org.meveo.model.BusinessEntity;

/**
 * POJO to configure an API method's parameter that should be used to checked
 * if a secured entity is allowed or not for the current user
 * @author Mounir Boukayoua
 * @since 10.0
 */

public class SecureMethodParameterConfig {

    private int index = -1;

    private String property = "";

    private Class<? extends BusinessEntity> entityClass;

    private Class<? extends SecureMethodParameterParser<?>> parser;

    /**
     * @return parameter's index in the method input params
     */
    public int getIndex() {
        return index;
    }

    public void setIndex(int index) {
        this.index = index;
    }

    /**
     * @return property to read from the parameter checked
     */
    public String getProperty() {
        return property;
    }

    public void setProperty(String property) {
        this.property = property;
    }

    /**
     * @return the secured entity class to build using the property value
     * and to check if it's allowed for current user
     */
    public Class<? extends BusinessEntity> getEntityClass() {
        return entityClass;
    }

    public void setEntityClass(Class<? extends BusinessEntity> entityClass) {
        this.entityClass = entityClass;
    }

    /**
     * @return the parser to use to parse the property's value
     * from the indicated parameter
     */
    public Class<? extends SecureMethodParameterParser<?>> getParser() {
        return parser;
    }

    public void setParser(Class<? extends SecureMethodParameterParser<?>> parser) {
        this.parser = parser;
    }

    @Override
    public String toString() {
        return "SecureMethodParameterConfig{" +
                "index=" + index +
                ", property='" + property + '\'' +
                ", entityClass=" + entityClass +
                ", parser=" + parser +
                '}';
    }
}
