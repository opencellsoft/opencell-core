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

package org.meveo.api.custom;

import static java.util.stream.Collectors.toList;
import static org.meveo.service.base.NativePersistenceService.FIELD_ID;

import java.math.BigInteger;
import java.util.Arrays;
import java.util.Collection;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import javax.ejb.Stateless;
import javax.ejb.TransactionAttribute;
import javax.ejb.TransactionAttributeType;
import javax.inject.Inject;
import javax.persistence.Entity;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.meveo.admin.exception.BusinessException;
import org.meveo.admin.exception.ValidationException;
import org.meveo.admin.util.pagination.PaginationConfiguration;
import org.meveo.api.BaseApi;
import org.meveo.api.dto.custom.CustomTableDataDto;
import org.meveo.api.dto.custom.CustomTableDataResponseDto;
import org.meveo.api.dto.custom.CustomTableRecordDto;
import org.meveo.api.dto.custom.CustomTableWrapperDto;
import org.meveo.api.dto.custom.UnitaryCustomTableDataDto;
import org.meveo.api.dto.response.PagingAndFiltering;
import org.meveo.api.exception.EntityDoesNotExistsException;
import org.meveo.api.exception.InvalidParameterException;
import org.meveo.api.exception.MeveoApiException;
import org.meveo.api.exception.MissingParameterException;
import org.meveo.commons.utils.JsonUtils;
import org.meveo.commons.utils.ReflectionUtils;
import org.meveo.jpa.EntityManagerWrapper;
import org.meveo.jpa.MeveoJpa;
import org.meveo.model.ICustomFieldEntity;
import org.meveo.model.crm.CustomFieldTemplate;
import org.meveo.model.customEntities.CustomEntityTemplate;
import org.meveo.service.base.ValueExpressionWrapper;
import org.meveo.service.crm.impl.CustomFieldTemplateService;
import org.meveo.service.custom.CustomTableService;
import org.primefaces.model.SortOrder;

/**
 * @author Andrius Karpavicius
 * @author Mohammed ELAZZOUZI
 * @lastModifiedVersion 7.0
 **/
@Stateless
public class CustomTableApi extends BaseApi {

    @Inject
    private CustomTableService customTableService;

    @Inject
    private CustomFieldTemplateService customFieldTemplateService;

    @Inject
    @MeveoJpa
    private EntityManagerWrapper emWrapper;

    /**
     * Create new records in a custom table with an option of deleting existing data first
     *
     * @param dto Values to add
     * @throws MeveoApiException API exception
     * @throws BusinessException General exception
     */
    @TransactionAttribute(TransactionAttributeType.NEVER)
    public void create(CustomTableDataDto dto) throws MeveoApiException, BusinessException {

        validateParams("customTableCode", dto.getCustomTableCode(), "values", dto.getValues());

        CustomEntityTemplate cet = customTableService.getCET(dto.getCustomTableCode());
        if (dto.getOverwrite() == null) {
            dto.setOverwrite(false);
        }
        customTableService.importData(cet, dto.getValues().stream().map(x -> x.getValues()).collect(toList()), !dto.getOverwrite());
    }

    @TransactionAttribute(TransactionAttributeType.REQUIRED)
    public void create(UnitaryCustomTableDataDto dto) throws MeveoApiException, BusinessException {

        validateParams("customTableCode", dto.getCustomTableCode(), "value", dto.getValue());

        CustomEntityTemplate cet = customTableService.getCET(dto.getCustomTableCode());
        Map<String, CustomFieldTemplate> cfts = customTableService.retrieveAndValidateCfts(cet, false);
        Map<String, Object> values = customTableService.convertValue(dto.getRowValues(), cfts.values(), false, null);
        Long id = customTableService.create(cet.getDbTablename(), values);
        dto.getValue().setId(id);
    }

    /**
     * Update existing records in a custom table. Values must contain an 'id' field value, to identify an existing record.
     *
     * @param dto Values to update
     * @throws MeveoApiException API exception
     * @throws BusinessException General exception
     */
    @TransactionAttribute(TransactionAttributeType.NEVER)
    public void update(CustomTableDataDto dto) throws MeveoApiException, BusinessException {

        validateParams("customTableCode", dto.getCustomTableCode(), "values", dto.getValues());

        CustomEntityTemplate cet = customTableService.getCET(dto.getCustomTableCode());
        Map<String, CustomFieldTemplate> cfts = customTableService.retrieveAndValidateCfts(cet, false);

        Map<Boolean, List<CustomTableRecordDto>> partitionedById = dto.getValues().stream().collect(Collectors.partitioningBy(x -> x.getValues().get(FIELD_ID) != null));
        List<CustomTableRecordDto> valuesWithIds = partitionedById.get(true);
        List<CustomTableRecordDto> valuesWithoutIds = partitionedById.get(false);
        if (!valuesWithoutIds.isEmpty()) {
            throw new ValidationException(valuesWithoutIds.size() + " record(s) for update are missing the IDs.");
        }

        customTableService.updateRecords(cet.getDbTablename(), cfts.values(), valuesWithIds);
    }

    @TransactionAttribute(TransactionAttributeType.REQUIRED)
    public void update(UnitaryCustomTableDataDto dto) throws MeveoApiException, BusinessException {
        Long id = dto.getValue().getId();

        validateParams("customTableCode", dto.getCustomTableCode(), "value", dto.getValue(), "id", id);

        CustomEntityTemplate cet = customTableService.getCET(dto.getCustomTableCode());
        Map<String, CustomFieldTemplate> cfts = customTableService.retrieveAndValidateCfts(cet, false);
        LinkedHashMap<String, Object> rowValues = dto.getRowValues();
        rowValues.put(FIELD_ID, id);
        List<Map<String, Object>> values = customTableService.convertValues(Arrays.asList(rowValues), cfts.values(), false);
        customTableService.update(cet.getDbTablename(), values.get(0));
    }

    /**
     * Create new records or update existing ones in a custom table, depending if 'id' value is present
     *
     * @param dto Values to add or update
     * @throws MeveoApiException API exception
     * @throws BusinessException General exception
     */
    @TransactionAttribute(TransactionAttributeType.NEVER)
    public void createOrUpdate(CustomTableDataDto dto) throws MeveoApiException, BusinessException {

        validateParams("customTableCode", dto.getCustomTableCode(), "values", dto.getValues());

        if (dto.getOverwrite() == null) {
            dto.setOverwrite(false);
        }
        CustomEntityTemplate cet = customTableService.getCET(dto.getCustomTableCode());
        Map<String, CustomFieldTemplate> cfts = customTableService.retrieveAndValidateCfts(cet, false);
        Map<Boolean, List<CustomTableRecordDto>> partitionedById = dto.getValues().stream().collect(Collectors.partitioningBy(x -> x.getValues().get(FIELD_ID) != null));

        // create records without ids
        List<CustomTableRecordDto> valuesWithoutIds = partitionedById.get(false);
        if (!valuesWithoutIds.isEmpty()) {
            customTableService.importData(cet, valuesWithoutIds.stream().map(x -> x.getValues()).collect(toList()), !dto.getOverwrite());
        }
        // update records with ids
        List<CustomTableRecordDto> valuesWithIds = partitionedById.get(true);
        if (!valuesWithIds.isEmpty()) {
            customTableService.updateRecords(cet.getDbTablename(), cfts.values(), valuesWithIds);
        }
    }

    /**
     * Retrieve custom table data based on a search criteria
     *
     * @param customTableCode Custom table/custom entity template code
     * @param pagingAndFiltering Search and pagination criteria
     * @return Values and pagination information
     * @throws MissingParameterException Missing parameters
     * @throws EntityDoesNotExistsException Custom table was not matched
     * @throws InvalidParameterException Invalid parameters passed
     * @throws ValidationException
     */
    public CustomTableDataResponseDto list(String customTableCode, PagingAndFiltering pagingAndFiltering) throws MissingParameterException, EntityDoesNotExistsException, InvalidParameterException, ValidationException {

        validateParams("customTableCode", customTableCode);
        if (pagingAndFiltering == null) {
            pagingAndFiltering = new PagingAndFiltering();
        }

        CustomEntityTemplate cet = customTableService.getCET(customTableCode);
        Map<String, CustomFieldTemplate> cfts = customTableService.retrieveAndValidateCfts(cet, false);
        CustomTableDataResponseDto result = new CustomTableDataResponseDto();
        result.setPaging(pagingAndFiltering);
        result.getCustomTableData().setCustomTableCode(customTableCode);
        List<String> fields = extractFields(pagingAndFiltering);
        PaginationConfiguration paginationConfig = toPaginationConfiguration(FIELD_ID, SortOrder.ASCENDING, fields, pagingAndFiltering, cfts);
//        try {
//            pagingAndFiltering.setFilters(customTableService.convertValue(pagingAndFiltering.getFilters(), cfts.values(), true, null));
//        } catch (ElementNotFoundException e) {
//            pagingAndFiltering.setTotalNumberOfRecords(0);
//            return result;
//        }
        Long totalCount = customTableService.count(cet.getDbTablename(), null);
        result.getPaging().setTotalNumberOfRecords(totalCount.intValue());
        if (totalCount > 0) {
            List<Map<String, Object>> list = customTableService.list(cet.getDbTablename(), paginationConfig);
            customTableService.completeWithEntities(list, cfts, pagingAndFiltering.getLoadReferenceDepth());
            result.getCustomTableData().setValuesFromListofMap(list);
        }
        return result;
    }

    private List<String> extractFields(PagingAndFiltering pagingAndFiltering) {
        return pagingAndFiltering.getFields() == null ? null : Stream.of((FIELD_ID + "," + pagingAndFiltering.getFields()).split(",")).distinct().collect(Collectors.toList());
    }

    /**
     * Remove records, identified by 'id' value, from a custom table. If no 'id' values are passed, will delete all the records in a table.<br/>
     * If a single ID is passed, an error will be thrown if record does not exist
     *
     * @param dto Values to remove. Should contain only 'id' field values
     * @throws MeveoApiException API exception
     * @return Number of records deleted
     * @throws BusinessException General exception
     */
    public int remove(CustomTableDataDto dto) throws MeveoApiException, BusinessException {

        validateParams("customTableCode", dto.getCustomTableCode());
        CustomEntityTemplate cet = customTableService.getCET(dto.getCustomTableCode());
        int nrDeleted = 0;
        if (dto.getValues() == null || dto.getValues().isEmpty()) {
            nrDeleted = customTableService.remove(cet.getDbTablename());
        } else {
            Map<Boolean, List<CustomTableRecordDto>> partitionedById = dto.getValues().stream().collect(Collectors.partitioningBy(x -> x.getId() != null || x.getValues().get(FIELD_ID) != null));
            List<CustomTableRecordDto> valuesWithoutIds = partitionedById.get(false);

            if (!valuesWithoutIds.isEmpty()) {
                throw new ValidationException(valuesWithoutIds.size() + " record(s) to remove are missing the IDs.");
            }
            Set<Long> ids = extractIds(dto);
            if (ids.size() == 1) {
                nrDeleted = customTableService.removeWithCheck(cet.getDbTablename(), ids);
            } else {
                nrDeleted = customTableService.remove(cet.getDbTablename(), ids);
            }
        }
        return nrDeleted;
    }

    /**
     * Remove a single record from a custom table identified by its 'id' value
     * 
     * @param customTableCode Custom table/custom entity template code
     * @param id
     * @throws MeveoApiException
     * @throws BusinessException
     */
    @TransactionAttribute(TransactionAttributeType.REQUIRED)
    public void remove(String customTableCode, Long id) throws MeveoApiException, BusinessException {

        validateParams("tableName", customTableCode, FIELD_ID, id);

        CustomEntityTemplate cet = customTableService.getCET(customTableCode);
        customTableService.remove(cet.getDbTablename(), id);
    }

    /**
     * Remove custom table data based on a search criteria
     *
     * @param customTableCode Custom table/custom entity template code
     * @param pagingAndFiltering Search and pagination criteria
     * @return Number of records deleted
     * @throws MissingParameterException Missing parameters
     * @throws EntityDoesNotExistsException Custom table was not matched
     * @throws InvalidParameterException Invalid parameters passed
     * @throws ValidationException
     */
    @SuppressWarnings("unchecked")
    public int remove(String customTableCode, PagingAndFiltering pagingAndFiltering) throws MissingParameterException, EntityDoesNotExistsException, InvalidParameterException, ValidationException {

        validateParams("customTableCode", customTableCode);

        if (pagingAndFiltering == null) {
            pagingAndFiltering = new PagingAndFiltering();
        }

        CustomEntityTemplate cet = customTableService.getCET(customTableCode);
        Map<String, CustomFieldTemplate> cfts = customTableService.retrieveAndValidateCfts(cet, false);
        PaginationConfiguration paginationConfig = toPaginationConfiguration(FIELD_ID, SortOrder.ASCENDING, Arrays.asList("id"), pagingAndFiltering, cfts);

        List<BigInteger> ids = customTableService.listAsObjects(cet.getDbTablename(), paginationConfig);
        if (ids == null || ids.isEmpty()) {
            return 0;
        }
        Set<Long> idsToRemove = ids.stream().map(id -> id.longValue()).collect(Collectors.toSet());

        return customTableService.remove(cet.getDbTablename(), idsToRemove);
    }

    @TransactionAttribute(TransactionAttributeType.REQUIRED)
    public void enableOrDisble(String tableName, Long id, boolean enable) {

        validateParams("tableName", tableName, FIELD_ID, id);

        CustomEntityTemplate cet = customTableService.getCET(tableName);
        customTableService.retrieveAndValidateCfts(cet, true);
        if (enable) {
            customTableService.enable(cet.getDbTablename(), id);
        } else {
            customTableService.disable(cet.getDbTablename(), id);
        }
    }

    /**
     * Enable or disable records, identified by 'id' value, in a custom table. Applies only to tables that contain field 'disabled'.
     *
     * @param dto Values to enable or disable. Should contain only 'id' field values
     * @param enable True to enable records, False to disable records.
     * @throws MeveoApiException API exception
     * @throws BusinessException General exception
     */
    public void enableDisable(CustomTableDataDto dto, boolean enable) throws MeveoApiException, BusinessException {

        validateParams("customTableCode", dto.getCustomTableCode(), "values", dto.getValues());

        CustomEntityTemplate cet = customTableService.getCET(dto.getCustomTableCode());
        customTableService.retrieveAndValidateCfts(cet, true);
        Set<Long> ids = extractIds(dto);
        if (enable) {
            customTableService.enable(cet.getDbTablename(), ids);
        } else {
            customTableService.disable(cet.getDbTablename(), ids);
        }
    }

    /**
     * Extract id field or value.id field value from DTO
     * 
     * @param dto Custom Table data dto
     * @return A list of ids
     */
    private Set<Long> extractIds(CustomTableDataDto dto) {
        return dto.getValues().stream().map(x -> x.getId() != null ? x.getId() : (castToLong(x.getValues().get(FIELD_ID))).longValue()).collect(Collectors.toSet());
    }

    private Long castToLong(Object id) {
        if (id != null) {
            if (id instanceof String) {
                return Long.parseLong((String) id);
            } else if (id instanceof Number) {
                return ((Number) id).longValue();
            }
            throw new InvalidParameterException("Invalid id value found: " + id);
        } else {
            throw new InvalidParameterException("Not all values have an 'id' field specified");
        }
    }

    @SuppressWarnings({ "rawtypes", "unchecked" })
    private void validateParams(Object... toValidate) {

        if (toValidate.length == 1 && toValidate[0] instanceof Map) {

            for (Entry<String, Object> param : ((Map<String, Object>) toValidate[0]).entrySet()) {
                Object value = param.getValue();
                if (value == null || (value instanceof String && StringUtils.isBlank((String) value)) || (value instanceof Collection && CollectionUtils.isEmpty((Collection) value))) {
                    missingParameters.add(param.getKey());
                }
            }
        } else {
            for (int i = 0; i < toValidate.length; i = i + 2) {
                String paramName = (String) toValidate[i];
                Object value = toValidate[i + 1];
                if (value == null || (value instanceof String && StringUtils.isBlank((String) value)) || (value instanceof Collection && CollectionUtils.isEmpty((Collection) value))) {
                    missingParameters.add(paramName);
                }
            }
        }
        handleMissingParameters();
    }

    /**
     * Retrieve custom table data based on CustomTableWrapper and a search criteria
     *
     * @param customTableWrapperDto Custom table Wrapper dto
     * @return Values and pagination information
     * @throws MissingParameterException Missing parameters
     * @throws EntityDoesNotExistsException Custom table was not matched
     * @throws InvalidParameterException Invalid parameters passed
     * @throws ValidationException
     */
    public CustomTableDataResponseDto listFromWrapper(CustomTableWrapperDto customTableWrapperDto) throws MissingParameterException, EntityDoesNotExistsException, InvalidParameterException, ValidationException {

        CustomFieldTemplate cft = customFieldTemplateService.findByCode(customTableWrapperDto.getCtwCode());
        if (cft == null) {
            throw new EntityDoesNotExistsException("CustomFieldTemplate", customTableWrapperDto.getCtwCode());
        }
        ICustomFieldEntity entity = getEntity(customTableWrapperDto.getEntityClass(), Long.valueOf(customTableWrapperDto.getEntityId()));
        String customTableCode = ValueExpressionWrapper.evaluateToStringIgnoreErrors(cft.getCustomTableCodeEL(), "entity", entity);

        validateParams("customTableCode", customTableCode);

        PagingAndFiltering pagingAndFiltering = customTableWrapperDto.getPagingAndFiltering();
        if (pagingAndFiltering == null) {
            pagingAndFiltering = new PagingAndFiltering();
        }
        addCTWPagingAndFiltering(entity, cft, pagingAndFiltering);
        CustomEntityTemplate cet = customTableService.getCET(customTableCode);

        Map<String, CustomFieldTemplate> cfts = customTableService.retrieveAndValidateCfts(cet, false);
        pagingAndFiltering.setFilters(customTableService.convertValue(pagingAndFiltering.getFilters(), cfts.values(), true, null));
        List<String> fields = extractFields(pagingAndFiltering);
        PaginationConfiguration paginationConfig = toPaginationConfiguration(FIELD_ID, SortOrder.ASCENDING, fields, pagingAndFiltering, cfts);
        Long totalCount = customTableService.count(cet.getDbTablename(), null);
        CustomTableDataResponseDto result = new CustomTableDataResponseDto();
        result.setPaging(pagingAndFiltering);
        result.getPaging().setTotalNumberOfRecords(totalCount.intValue());
        result.getCustomTableData().setCustomTableCode(customTableCode);
        List<Map<String, Object>> list = customTableService.list(cet.getDbTablename(), paginationConfig);
        customTableService.completeWithEntities(list, cfts, pagingAndFiltering.getLoadReferenceDepth());
        result.getCustomTableData().setValuesFromListofMap(list);
        return result;

    }

    private ICustomFieldEntity getEntity(String entityClass, Long entityId) {
        Class clazz = ReflectionUtils.getClassBySimpleNameAndAnnotation(entityClass, Entity.class);
        Object entity = emWrapper.getEntityManager().getReference(clazz, entityId);
        return (ICustomFieldEntity) entity;
    }

    private PagingAndFiltering addCTWPagingAndFiltering(ICustomFieldEntity entity, CustomFieldTemplate cft, PagingAndFiltering pagingAndFiltering) {
        String filterString = ValueExpressionWrapper.evaluateToStringIgnoreErrors(cft.getDataFilterEL(), "entity", entity);
        String fieldsString = ValueExpressionWrapper.evaluateToStringIgnoreErrors(cft.getFieldsEL(), "entity", entity);
        if (filterString == null) {
            filterString = "";
        }
        String jsonFilter = "{\"filters\": {" + filterString + "}";

        if (fieldsString != null) {
            jsonFilter = jsonFilter + ",\"fields\":\"" + fieldsString + "\"}";
        }

        PagingAndFiltering fieldsAndFiltering = JsonUtils.toObject(jsonFilter, PagingAndFiltering.class);
        pagingAndFiltering.addFilters(fieldsAndFiltering.getFilters());
        pagingAndFiltering.addFields(fieldsAndFiltering.getFields());
        return pagingAndFiltering;
    }
}