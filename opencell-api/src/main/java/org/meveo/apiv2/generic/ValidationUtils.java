package org.meveo.apiv2.generic;

import org.meveo.api.exception.InvalidParameterException;
import org.meveo.commons.utils.StringUtils;

import javax.ws.rs.NotFoundException;
import java.util.List;
import java.util.Objects;
import java.util.function.Predicate;
import java.util.function.Supplier;

public class ValidationUtils {
    private static ValidationUtils INSTANCE = new ValidationUtils();
    
    public static ValidationUtils checkEntityName(String entityName) {
        return check(entityName, StringUtils::isBlank, () -> new NotFoundException("The entityName should not be null or empty"));
    }
    
    public static ValidationUtils checkId(Long id) {
        return check(id, Objects::isNull, () -> new InvalidParameterException("The requested id should not be null"));
    }
    
    public static ValidationUtils checkDto(String dto) {
        return check(dto, StringUtils::isBlank, () -> new InvalidParameterException("The given json dto representation should not be null or empty"));
    }
    
    public static ValidationUtils checkEntityClass(Class entityClass) {
        return check(entityClass, Objects::isNull, () -> new NotFoundException("The requested entity does not exist"));
    }
    
    public static <T> List<T> checkRecords(List<T> records, String className) {
        check(records, Objects::isNull, () -> new NotFoundException(String.format("Unable to find records fo type %s", className)));
        return records;
    }
    
    public static <T> T checkRecord(T record, String className, Long id) {
        check(record, Objects::isNull, () -> new NotFoundException(String.format("%s with code=%s does not exists.", className, id.toString())));
        return record;
    }
    
    private static <T> ValidationUtils check(T object, Predicate<T> condition, Supplier<? extends RuntimeException> ex) {
        if (condition.test(object)) {
            throw ex.get();
        }
        return INSTANCE;
    }
}
