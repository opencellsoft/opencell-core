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

package org.meveo.apiv2;

import org.junit.Test;
import org.meveo.api.exception.EntityDoesNotExistsException;
import org.meveo.api.exception.InvalidParameterException;
import org.meveo.apiv2.ordering.ValidationUtils;
import org.meveo.commons.utils.StringUtils;
import org.meveo.model.crm.Customer;

import static org.assertj.core.api.Assertions.assertThat;

public class ValidationUtilsTest {
    
    @Test
    public void given_null_entity_name_when_check_entity_name_then_throw_entity_does_not_exist_exception_and_expect_default_message() {
        try {
            ValidationUtils.checkEntityName(null);
        } catch (Exception ex) {
            assertThat(ex).isInstanceOf(EntityDoesNotExistsException.class);
            assertThat(ex.getMessage()).isEqualTo("The entityName should not be null or empty");
        }
    }
    
    @Test
    public void given_empty_entity_name_when_check_entity_name_then_should_throw_entity_does_not_exist_exception_and_expect_default_message() {
        try {
            ValidationUtils.checkEntityName(StringUtils.EMPTY);
        } catch (Exception ex) {
            assertThat(ex).isInstanceOf(EntityDoesNotExistsException.class);
            assertThat(ex.getMessage()).isEqualTo("The entityName should not be null or empty");
        }
    }
    
    @Test
    public void given_null_id_when_check_id_then_should_throw_invalid_parameter_exception_and_expect_default_message() {
        try {
            ValidationUtils.checkId(null);
        } catch (Exception ex) {
            assertThat(ex).isInstanceOf(InvalidParameterException.class);
            assertThat(ex.getMessage()).isEqualTo("The requested id should not be null");
        }
    }
    
    @Test
    public void given_null_dto_when_check_dto_then_throw_invalid_parameter_exception_and_expect_default_message() {
        try {
            ValidationUtils.checkDto(null);
        } catch (Exception ex) {
            assertThat(ex).isInstanceOf(InvalidParameterException.class);
            assertThat(ex.getMessage()).isEqualTo("The given json dto representation should not be null or empty");
        }
    }
    
    @Test
    public void given_empty_dto_when_check_dto_then_throw_invalid_parameter_exception_and_expect_default_message() {
        try {
            ValidationUtils.checkDto(StringUtils.EMPTY);
        } catch (Exception ex) {
            assertThat(ex).isInstanceOf(InvalidParameterException.class);
            assertThat(ex.getMessage()).isEqualTo("The given json dto representation should not be null or empty");
        }
    }
    
    @Test
    public void given_null_entity_class_when_check_entity_class_then_throw_entity_does_not_exist_exception_and_expect_default_message() {
        try {
            ValidationUtils.checkEntityClass(null);
        } catch (Exception ex) {
            assertThat(ex).isInstanceOf(EntityDoesNotExistsException.class);
            assertThat(ex.getMessage()).isEqualTo("The requested entity does not exist");
        }
    }
    
    @Test
    public void given_null_record_when_require_record_non_null_then_throw_entity_does_not_exist_exception_and_expect_default_message() {
        try {
            ValidationUtils.checkRecord(null, "Customer", 13L);
        } catch (Exception ex) {
            assertThat(ex).isInstanceOf(EntityDoesNotExistsException.class);
            assertThat(ex.getMessage()).isEqualTo("Customer with code=13 does not exists.");
        }
    }
    
    @Test
    public void given_null_list_of_records_when_require_record_non_null_then_throw_entity_does_not_exist_exception_and_expect_default_message() {
        try {
            ValidationUtils.checkRecords(null, "Customer");
        } catch (Exception ex) {
            assertThat(ex).isInstanceOf(EntityDoesNotExistsException.class);
            assertThat(ex.getMessage()).isEqualTo("Unable to find records fo type Customer");
        }
    }

    
    @Test
    public void should_return_record_when_is_valid() {
        Customer record = new Customer();
        Customer customer = ValidationUtils.checkRecord(record, "Customer", 13L);
        assertThat(customer).isEqualTo(record);
    }
    
}