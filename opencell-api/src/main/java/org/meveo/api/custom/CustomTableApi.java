package org.meveo.api.custom;

import java.math.BigInteger;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.ejb.Stateless;
import javax.inject.Inject;

import org.apache.commons.lang3.StringUtils;
import org.meveo.admin.exception.BusinessException;
import org.meveo.admin.exception.ValidationException;
import org.meveo.admin.util.pagination.PaginationConfiguration;
import org.meveo.api.BaseApi;
import org.meveo.api.dto.custom.CustomTableDataDto;
import org.meveo.api.dto.custom.CustomTableDataResponseDto;
import org.meveo.api.dto.custom.CustomTableRecordDto;
import org.meveo.api.dto.response.PagingAndFiltering;
import org.meveo.api.exception.EntityDoesNotExistsException;
import org.meveo.api.exception.InvalidParameterException;
import org.meveo.api.exception.MeveoApiException;
import org.meveo.api.exception.MissingParameterException;
import org.meveo.model.crm.CustomFieldTemplate;
import org.meveo.model.customEntities.CustomEntityTemplate;
import org.meveo.service.base.NativePersistenceService;
import org.meveo.service.crm.impl.CustomFieldTemplateService;
import org.meveo.service.custom.CustomEntityTemplateService;
import org.meveo.service.custom.CustomTableService;
import org.primefaces.model.SortOrder;

/**
 * @author Andrius Karpavicius
 * @lastModifiedVersion 7.0
 **/
@Stateless
public class CustomTableApi extends BaseApi {

    @Inject
    private CustomEntityTemplateService customEntityTemplateService;

    @Inject
    private CustomTableService customTableService;

    @Inject
    private CustomFieldTemplateService customFieldTemplateService;

    /**
     * Create new records in a custom table
     * 
     * @param dto Values to add
     * @throws MeveoApiException API exception
     * @throws BusinessException General exception
     */
    public void append(CustomTableDataDto dto) throws MeveoApiException, BusinessException {

        if (StringUtils.isBlank(dto.getCustomTableCode())) {
            missingParameters.add("customTableCode");
        }
        if (dto.getValues() == null || dto.getValues().isEmpty()) {
            missingParameters.add("values");
        }

        handleMissingParameters();

        CustomEntityTemplate cet = customEntityTemplateService.findByCode(dto.getCustomTableCode());
        if (cet == null) {
            throw new EntityDoesNotExistsException(CustomEntityTemplate.class, dto.getCustomTableCode());
        }
        Map<String, CustomFieldTemplate> cfts = customFieldTemplateService.findByAppliesTo(cet.getAppliesTo());
        if (cfts == null || cfts.isEmpty()) {
            throw new ValidationException("No fields are defined for custom table", "customTable.noFields");
        }

        List<Map<String, Object>> values = new ArrayList<>();
        dto.getValues().forEach(record -> values.add(record.getValues()));

        customTableService.create(cet.getDbTablename(), customTableService.convertValues(values, cfts.values(), false));

    }

    /**
     * Update existing records in a custom table. Values must contain an 'id' field value, to identify an existing record.
     * 
     * @param dto Values to update
     * @throws MeveoApiException API exception
     * @throws BusinessException General exception
     */
    public void update(CustomTableDataDto dto) throws MeveoApiException, BusinessException {

        if (StringUtils.isBlank(dto.getCustomTableCode())) {
            missingParameters.add("customTableCode");
        }
        if (dto.getValues() == null || dto.getValues().isEmpty()) {
            missingParameters.add("values");
        }

        handleMissingParameters();

        CustomEntityTemplate cet = customEntityTemplateService.findByCode(dto.getCustomTableCode());
        if (cet == null) {
            throw new EntityDoesNotExistsException(CustomEntityTemplate.class, dto.getCustomTableCode());
        }
        Map<String, CustomFieldTemplate> cfts = customFieldTemplateService.findByAppliesTo(cet.getAppliesTo());
        if (cfts == null || cfts.isEmpty()) {
            throw new ValidationException("No fields are defined for custom table", "customTable.noFields");
        }

        List<Map<String, Object>> values = new ArrayList<>();
        dto.getValues().forEach(record -> values.add(record.getValues()));

        customTableService.update(cet.getDbTablename(), customTableService.convertValues(values, cfts.values(), false));
    }

    /**
     * Create new records or update existing ones in a custom table, depending if 'id' value is present
     * 
     * @param dto Values to add or update
     * @throws MeveoApiException API exception
     * @throws BusinessException General exception
     */
    public void createOrUpdate(CustomTableDataDto dto) throws MeveoApiException, BusinessException {

        if (StringUtils.isBlank(dto.getCustomTableCode())) {
            missingParameters.add("customTableCode");
        }
        if (dto.getValues() == null || dto.getValues().isEmpty()) {
            missingParameters.add("values");
        }

        handleMissingParameters();

        CustomEntityTemplate cet = customEntityTemplateService.findByCodeOrDbTablename(dto.getCustomTableCode());
        if (cet == null) {
            throw new EntityDoesNotExistsException(CustomEntityTemplate.class, dto.getCustomTableCode());
        }
        Map<String, CustomFieldTemplate> cfts = customFieldTemplateService.findByAppliesTo(cet.getAppliesTo());
        if (cfts == null || cfts.isEmpty()) {
            throw new ValidationException("No fields are defined for custom table", "customTable.noFields");
        }

        for (CustomTableRecordDto record : dto.getValues()) {

            if (record.getValues().containsKey(NativePersistenceService.FIELD_ID)) {
                customTableService.update(cet.getDbTablename(), customTableService.convertValues(record.getValues(), cfts.values(), false));
            } else {
                customTableService.create(cet.getDbTablename(), customTableService.convertValues(record.getValues(), cfts.values(), false));
            }
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
    public CustomTableDataResponseDto list(String customTableCode, PagingAndFiltering pagingAndFiltering)
            throws MissingParameterException, EntityDoesNotExistsException, InvalidParameterException, ValidationException {

        if (StringUtils.isBlank(customTableCode)) {
            missingParameters.add("customTableCode");
        }
        handleMissingParameters();

        if (pagingAndFiltering == null) {
            pagingAndFiltering = new PagingAndFiltering();
        }

        CustomEntityTemplate cet = customEntityTemplateService.findByCodeOrDbTablename(customTableCode);
        if (cet == null) {
            throw new EntityDoesNotExistsException(CustomEntityTemplate.class, customTableCode);
        }

        Map<String, CustomFieldTemplate> cfts = customFieldTemplateService.findByAppliesTo(cet.getAppliesTo());
        if (cfts == null || cfts.isEmpty()) {
            throw new ValidationException("No fields are defined for custom table", "customTable.noFields");
        }

        pagingAndFiltering.setFilters(customTableService.convertValues(pagingAndFiltering.getFilters(), cfts.values(), true));

        PaginationConfiguration paginationConfig = toPaginationConfiguration("id", SortOrder.ASCENDING, null, pagingAndFiltering, null);

        Long totalCount = customTableService.count(cet.getDbTablename(), paginationConfig);

        CustomTableDataResponseDto result = new CustomTableDataResponseDto();

        result.setPaging(pagingAndFiltering);
        result.getPaging().setTotalNumberOfRecords(totalCount.intValue());
        result.getCustomTableData().setCustomTableCode(customTableCode);

        result.getCustomTableData().setValuesFromListofMap(customTableService.list(cet.getDbTablename(), paginationConfig));

        return result;
    }

    /**
     * Remove records, identified by 'id' value, from a custom table. If no 'id' values are passed, will delete all the records in a table.
     * 
     * @param dto Values to remove. Should contain only 'id' field values
     * @throws MeveoApiException API exception
     * @throws BusinessException General exception
     */
    public void remove(CustomTableDataDto dto) throws MeveoApiException, BusinessException {

        if (StringUtils.isBlank(dto.getCustomTableCode())) {
            missingParameters.add("customTableCode");
        }

        handleMissingParameters();

        CustomEntityTemplate cet = customEntityTemplateService.findByCodeOrDbTablename(dto.getCustomTableCode());
        if (cet == null) {
            throw new EntityDoesNotExistsException(CustomEntityTemplate.class, dto.getCustomTableCode());
        }

        if (dto.getValues() == null || dto.getValues().isEmpty()) {
            customTableService.remove(cet.getDbTablename());
        } else {
            Set<Long> ids = new HashSet<>();

            for (CustomTableRecordDto record : dto.getValues()) {

                Object id = record.getValues().get(NativePersistenceService.FIELD_ID);
                if (id != null) {
                    // Convert to long
                    if (id instanceof String) {
                        id = Long.parseLong((String) id);
                    } else if (id instanceof BigInteger) {
                        id = ((BigInteger) id).longValue();
                    }
                    ids.add((Long) id);

                } else {
                    throw new InvalidParameterException("Not all values have an 'id' field specified");
                }
            }
            customTableService.remove(cet.getDbTablename(), ids);
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

        if (StringUtils.isBlank(dto.getCustomTableCode())) {
            missingParameters.add("customTableCode");
        }
        if (dto.getValues() == null || dto.getValues().isEmpty()) {
            missingParameters.add("values");
        }

        handleMissingParameters();

        CustomEntityTemplate cet = customEntityTemplateService.findByCodeOrDbTablename(dto.getCustomTableCode());
        if (cet == null) {
            throw new EntityDoesNotExistsException(CustomEntityTemplate.class, dto.getCustomTableCode());
        }

        Map<String, CustomFieldTemplate> cfts = customFieldTemplateService.findByAppliesTo(cet.getAppliesTo());
        if (cfts == null || cfts.isEmpty() || cfts.containsKey(NativePersistenceService.FIELD_DISABLED)) {
            throw new ValidationException("Custom table does not contain a field 'disabled'", "customTable.noDisabledField");
        }

        Set<Long> ids = new HashSet<>();

        for (CustomTableRecordDto record : dto.getValues()) {

            Object id = record.getValues().get(NativePersistenceService.FIELD_ID);
            if (id != null) {
                // Convert to long
                if (id instanceof String) {
                    id = Long.parseLong((String) id);
                } else if (id instanceof BigInteger) {
                    id = ((BigInteger) id).longValue();
                }
                ids.add((Long) id);

            } else {
                throw new InvalidParameterException("Not all values have an 'id' field specified");
            }
        }
        if (enable) {
            customTableService.enable(cet.getDbTablename(), ids);
        } else {
            customTableService.disable(cet.getDbTablename(), ids);
        }
    }
}