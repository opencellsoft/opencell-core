package org.meveo.apiv2;

import org.meveo.api.exception.EntityDoesNotExistsException;
import org.meveo.api.exception.InvalidParameterException;
import org.meveo.commons.utils.StringUtils;

public class ValidationUtils {
    private static ValidationUtils INSTANCE = new ValidationUtils();
    
    public static ValidationUtils checkEntityName(String entityName){
        if(StringUtils.isBlank(entityName)){
            throw new EntityDoesNotExistsException("The entityName should not be null or empty");
        }
        return INSTANCE;
    }
    
    public static ValidationUtils checkId(Long id){
        if(id == null){
            throw new InvalidParameterException("The requested id should not be null");
        }
        return INSTANCE;
    }
    
    public static ValidationUtils checkDto(String dto){
        if(StringUtils.isBlank(dto)){
            throw new InvalidParameterException("The given json dto representation should not be null or empty");
        }
        return INSTANCE;
    }
    
   public static ValidationUtils checkEntityClass(Class entityClass){
        if(entityClass == null){
            throw new EntityDoesNotExistsException("The requested entity does not exist");
        }
        return INSTANCE;
    }
    
    public static <T> T checkRecord(T record, String className, Long id){
       if(record == null){
           throw new EntityDoesNotExistsException(className, id.toString());
       }
       return record;
    }
}
