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

    private ImportResultService() {
        throw new UnsupportedOperationException("This class cannot be instantiated");
    }

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
        List<Field> fields = getAllFields(new LinkedList<>(), clazz);

        for (Field field : fields) {
            try {
                getCode(entityToReturn, importResultDto, field);
                getName(entityToReturn, importResultDto, field);
                getStatus(entityToReturn, importResultDto, field);
            } catch (IllegalAccessException e) {
                throw new RuntimeException("Failed to access field " + entityToReturn.getClass().getName() + "." + field.getName(), e);
            }
        }

        if(importResultDto.getCode() != null && !importResultDto.getCode().isEmpty() && importResultDto.getName() == null) {
            importResultDto.setName("N/A");
        }

        addImportResultDtoToList(importResultDtoList, importResultDto);
    }

    /**
     * Get Status
     * @param entityToReturn {@link IEntity}
     * @param importResultDto {@link ImportResultDto}
     * @param field {@link Field}
     * @throws IllegalAccessException {@link IllegalAccessException}
     */
    private static void getStatus(IEntity entityToReturn, ImportResultDto importResultDto, Field field) throws IllegalAccessException {
        if (field.getName().equals("lifeCycleStatus") || field.getName().equals("status")) {
            Object fieldValue = FieldUtils.readField(field, entityToReturn, true);
            if (fieldValue != null) {
                importResultDto.setStatus(fieldValue.toString());
            }
        }
    }

    /**
     * Get Name information
     * @param entityToReturn {@link IEntity}
     * @param importResultDto {@link ImportResultDto}
     * @param field {@link Field}
     * @throws IllegalAccessException {@link IllegalAccessException}
     */
    private static void getName(IEntity entityToReturn, ImportResultDto importResultDto, Field field) throws IllegalAccessException {
        if (field.getName().equals("description")) {
            Object fieldValue = FieldUtils.readField(field, entityToReturn, true);
            if (fieldValue != null) {
                importResultDto.setName((String) fieldValue);
            }
        }
    }

    /**
     * Get Code information
     * @param entityToReturn {@link IEntity}
     * @param importResultDto {@link ImportResultDto}
     * @param field {@link Field}
     * @throws IllegalAccessException {@link IllegalAccessException}
     */
    private static void getCode(IEntity entityToReturn, ImportResultDto importResultDto, Field field) throws IllegalAccessException {
        if (field.getName().equals("code")) {
            Object fieldValue = FieldUtils.readField(field, entityToReturn, true);
            if (fieldValue != null) {
                importResultDto.setCode((String) fieldValue);
            }
        }
    }

    /**
     * Add importResultDto to the list
     * @param importResultDtoList List of {@link ImportResultDto}
     * @param importResultDto {@link ImportResultDto}
     */
    private static void addImportResultDtoToList(List<ImportResultDto> importResultDtoList, ImportResultDto importResultDto) {
        boolean found = false;
        for(ImportResultDto im : list) {
            if(im.getName() != null && im.getName().equals(importResultDto.getName()) && im.getCode().equals(importResultDto.getCode())) {
                found = true;
                break;
            }
        }

        if(!found && importResultDto.getName() != null && !importResultDto.getName().isEmpty() && importResultDto.getCode() != null && !importResultDto.getCode().isEmpty()) {
            importResultDtoList.add(importResultDto);
            list.add(importResultDto);
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
