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

import org.meveo.api.security.filter.NullFilter;
import org.meveo.api.security.filter.SecureMethodResultFilter;

import java.util.Arrays;

/**
 * POJO to identify and configure API's methods which need to be checked
 * against secured entities
 * @author Mounir Boukayoua
 * @since 10.0
 */
public class SecuredMethodConfig {

    private SecureMethodParameterConfig[]  validate;

    private Class<? extends SecureMethodResultFilter> resultFilter = NullFilter.class;

    /**
     * Validate indicate how secured method should be checked against secured entities
     * @return an array of $SecureMethodParameterConfig
     */
    public SecureMethodParameterConfig[] getValidate() {
        return validate;
    }

    public void setValidate(SecureMethodParameterConfig[] validate) {
        this.validate = validate;
    }

    /**
     * Result filter indicate the class to use to filter results
     * depending on the configured secured entities
     * @return class extending $SecureMethodResultFilter
     */
    public Class<? extends SecureMethodResultFilter> getResultFilter() {
        return resultFilter;
    }

    public void setResultFilter(Class<? extends SecureMethodResultFilter> resultFilter) {
        this.resultFilter = resultFilter;
    }

    @Override
    public String toString() {
        return "SecuredMethodConfig{" +
                "validate=" + Arrays.toString(validate) +
                ", resultFilter=" + resultFilter +
                '}';
    }
}
