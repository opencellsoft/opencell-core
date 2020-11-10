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

package org.meveo.apiv2.ordering;

import org.meveo.api.exception.EntityDoesNotExistsException;
import org.meveo.api.exception.InvalidParameterException;
import org.meveo.api.exception.MeveoApiException;
import org.meveo.commons.utils.StringUtils;

import java.util.List;
import java.util.Objects;
import java.util.function.BiConsumer;
import java.util.function.Consumer;
import java.util.function.Predicate;
import java.util.function.Supplier;

public class ValidationUtils {
    private static ValidationUtils INSTANCE = new ValidationUtils();
    
    public static ValidationUtils checkEntityName(String entityName) {
        return check(entityName, StringUtils::isBlank, () -> new EntityDoesNotExistsException("The entityName should not be null or empty"));
    }
    
    public static ValidationUtils checkId(Long id) {
        return check(id, Objects::isNull, () -> new InvalidParameterException("The requested id should not be null"));
    }
    
    public static ValidationUtils checkDto(String dto) {
        return check(dto, StringUtils::isBlank, () -> new InvalidParameterException("The given json dto representation should not be null or empty"));
    }
    
    public static ValidationUtils checkEntityClass(Class entityClass) {
        return check(entityClass, Objects::isNull, () -> new EntityDoesNotExistsException("The requested entity does not exist"));
    }
    
    public static <T> List<T> checkRecords(List<T> records, String className) {
        check(records, Objects::isNull, () -> new EntityDoesNotExistsException(String.format("Unable to find records fo type %s", className)));
        return records;
    }
    
    public static <T> T checkRecord(T record, String className, Long id) {
        check(record, Objects::isNull, () -> new EntityDoesNotExistsException(className, id.toString()));
        return record;
    }
    
    private static <T> ValidationUtils check(T object, Predicate<T> condition, Supplier<? extends MeveoApiException> ex) {
        if (condition.test(object)) {
            throw ex.get();
        }
        return INSTANCE;
    }
    
    public static <T> ValidationUtils performOperationOnCondition(T object, Predicate<T> condition, Consumer<T> operation) {
        if (condition.test(object)) {
            operation.accept(object);
        }
        return INSTANCE;
    }
    
    public static <T, E> ValidationUtils performOperationOnCondition(T object, E prop, Predicate<T> condition, BiConsumer<T, E> operation) {
        if (condition.test(object)) {
            operation.accept(object, prop);
        }
        return INSTANCE;
    }
}
