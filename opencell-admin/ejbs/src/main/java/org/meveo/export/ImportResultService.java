package org.meveo.export;

import org.apache.commons.lang3.reflect.FieldUtils;
import org.meveo.api.dto.response.utilities.ImportResultDto;
import org.meveo.model.IEntity;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;

public class ImportResultService {

    static List<ImportResultDto> list = new ArrayList<>();

    /**
     * Create Generic Import Result Dto
     * @param importResultDtoList List {@link ImportResultDto}
     * @param entityToReturn {@link IEntity}
     * @throws IllegalAccessException {@link IllegalAccessException}
     */
    public static void createImportResultDtoGeneric(List<ImportResultDto> importResultDtoList, IEntity entityToReturn) {
        Class clazz = entityToReturn.getClass();
        ImportResultDto importResultDto = new ImportResultDto();
        List<Field> fields = getAllFields(new LinkedList<Field>(), clazz);

        for (Field field : fields) {
            try {
                if (field.getName().equals("code")) {
                    Object fieldValue = FieldUtils.readField(field, entityToReturn, true);
                    if (fieldValue != null && fieldValue instanceof String) {
                        importResultDto.setCode((String) fieldValue);
                    }
                }

                if (field.getName().equals("description")) {
                    Object fieldValue = FieldUtils.readField(field, entityToReturn, true);
                    if (fieldValue != null && fieldValue instanceof String) {
                        importResultDto.setName((String) fieldValue);
                    }
                }

                if (field.getName().equals("lifeCycleStatus") || field.getName().equals("status")) {
                    Object fieldValue = FieldUtils.readField(field, entityToReturn, true);
                    if (fieldValue != null) {
                        importResultDto.setStatus(fieldValue.toString());
                    }
                }
            } catch (IllegalAccessException e) {
                throw new RuntimeException("Failed to access field " + entityToReturn.getClass().getName() + "." + field.getName(), e);
            }
        }

        if(importResultDto.getCode() != null && !importResultDto.getCode().isEmpty()) {
            if(importResultDto.getName() == null) {
                importResultDto.setName("N/A");
            }

            boolean found = false;
            for(ImportResultDto im : list) {
                if(im.getName() != null && im.getName().equals(importResultDto.getName()) && im.getCode().equals(importResultDto.getCode())) {
                    found = true;
                    break;
                }
            }

            if(!found) {
                importResultDtoList.add(importResultDto);
                list.add(importResultDto);
            }
        }
    }

    /**
     * Get all fields of a class
     * @param fields List {@link Field}
     * @param type Type
     * @return List Of {@link Field}
     */
    public static List<Field> getAllFields(List<Field> fields, Class<?> type) {
        fields.addAll(Arrays.asList(type.getDeclaredFields()));

        if (type.getSuperclass() != null) {
            getAllFields(fields, type.getSuperclass());
        }

        return fields;
    }
}
