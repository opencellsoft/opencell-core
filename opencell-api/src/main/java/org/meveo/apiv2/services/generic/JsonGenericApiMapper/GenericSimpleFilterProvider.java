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

package org.meveo.apiv2.services.generic.JsonGenericApiMapper;

import com.fasterxml.jackson.databind.ser.impl.SimpleBeanPropertyFilter;
import com.fasterxml.jackson.databind.ser.impl.SimpleFilterProvider;

class GenericSimpleFilterProvider extends SimpleFilterProvider {
    private static final String[] forbiddenFieldNames = {
            "NB_DECIMALS", "historized", "notified", "NB_PRECISION", "appendGeneratedCode", "serialVersionUID", "transient", "codeChanged",
            "version", "uuid", "cfValuesNullSafe", "cfAccumulatedValuesNullSafe", "descriptionOrCode", "descriptionAndCode", "referenceCode",
            "referenceDescription", "cfAccumulatedValues"
    };
    public GenericSimpleFilterProvider() {
        addFilter("EntityForbiddenFieldsFilter", SimpleBeanPropertyFilter.serializeAllExcept(forbiddenFieldNames));
        addFilter("GenericPaginatedResourceFilter", SimpleBeanPropertyFilter.filterOutAllExcept("data","total","offset","limit"));
        addFilter("EntityCustomFieldValuesFilter", SimpleBeanPropertyFilter.filterOutAllExcept("valuesByCode"));
        addFilter("EntityCustomFieldValueFilter", SimpleBeanPropertyFilter.filterOutAllExcept("value", "priority", "from", "to"));
    }
}
