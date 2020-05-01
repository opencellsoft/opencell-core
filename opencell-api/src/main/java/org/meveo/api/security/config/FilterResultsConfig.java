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

import java.util.Arrays;

/**
 * POJO to configure results filtering for secured entities
 *
 * @author Mounir Boukayoua
 * @since 10.0
 */
public class FilterResultsConfig {

    private String propertyToFilter = "";

    private FilterPropertyConfig[] itemPropertiesToFilter;

    private String totalRecords = "";

    /**
     * Identifies the result property that returns the child entities to be iterated for filtering.
     * e.g. if "customerAccounts.customerAccount" is passed into this attribute, then the
     * value of "dto.customerAccounts.customerAccount" will be parsed and filtered.
     *
     * If not specified - an object itself will be filtered
     *
     * @return name of property to be filtered
     */
    public String getPropertyToFilter() {
        return propertyToFilter;
    }

    public void setPropertyToFilter(String propertyToFilter) {
        this.propertyToFilter = propertyToFilter;
    }

    /**
     * Identifies the filtering rule to apply to items selected for filtering.
     *
     * @return array of property to be filtered.
     */
    public FilterPropertyConfig[] getItemPropertiesToFilter() {
        return itemPropertiesToFilter;
    }

    public void setItemPropertiesToFilter(FilterPropertyConfig[] itemPropertiesToFilter) {
        this.itemPropertiesToFilter = itemPropertiesToFilter;
    }

    /**
     * Identifies the result property containing the number of records returned to refresh after applying filter
     *
     */
    public String getTotalRecords() {
        return totalRecords;
    }

    public void setTotalRecords(String totalRecords) {
        this.totalRecords = totalRecords;
    }

    @Override
    public String toString() {
        return "FilterResultsConfig{" +
                "propertyToFilter='" + propertyToFilter + '\'' +
                ", itemPropertiesToFilter=" + Arrays.toString(itemPropertiesToFilter) +
                ", totalRecords='" + totalRecords + '\'' +
                '}';
    }
}
