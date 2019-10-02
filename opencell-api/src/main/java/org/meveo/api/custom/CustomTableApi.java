package org.meveo.api.custom;

import static java.util.stream.Collectors.toList;
import static org.meveo.service.base.NativePersistenceService.FIELD_ID;

import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;
import java.util.stream.Collectors;

import javax.ejb.Stateless;
import javax.ejb.TransactionAttribute;
import javax.ejb.TransactionAttributeType;
import javax.inject.Inject;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.meveo.admin.exception.BusinessException;
import org.meveo.admin.exception.ValidationException;
import org.meveo.admin.util.pagination.PaginationConfiguration;
import org.meveo.api.BaseApi;
import org.meveo.api.dto.custom.CustomTableDataDto;
import org.meveo.api.dto.custom.CustomTableDataResponseDto;
import org.meveo.api.dto.custom.CustomTableRecordDto;
import org.meveo.api.dto.custom.UnitaryCustomTableDataDto;
import org.meveo.api.dto.response.PagingAndFiltering;
import org.meveo.api.exception.EntityDoesNotExistsException;
import org.meveo.api.exception.InvalidParameterException;
import org.meveo.api.exception.MeveoApiException;
import org.meveo.api.exception.MissingParameterException;
import org.meveo.model.crm.CustomFieldTemplate;
import org.meveo.model.customEntities.CustomEntityTemplate;
import org.meveo.service.custom.CustomTableService;
import org.primefaces.model.SortOrder;

/**
 * @author Andrius Karpavicius
 * @author Mohammed ELAZZOUZI
 * @lastModifiedVersion 7.0
 **/
@Stateless
@SuppressWarnings("serial")
public class CustomTableApi extends BaseApi {

    @Inject
    private CustomTableService customTableService;

    /**
     * Create new records in a custom table with an option of deleting existing data first
     *
     * @param dto Values to add
     * @throws MeveoApiException API exception
     * @throws BusinessException General exception
     */
    @TransactionAttribute(TransactionAttributeType.NEVER)
    public void create(CustomTableDataDto dto) throws MeveoApiException, BusinessException {
    	Map<String, Object> toValidate = new TreeMap<String, Object>() {{put("customTableCode", dto.getCustomTableCode());  put("values", dto.getValues());}};
    	validateParams(toValidate);
    	CustomEntityTemplate cet = customTableService.getCET(dto.getCustomTableCode());
        if (dto.getOverwrite() == null) {
            dto.setOverwrite(false);
        }
        customTableService.importData(cet, dto.getValues().stream().map(x->x.getValues()).collect(toList()), !dto.getOverwrite());
    }

    @TransactionAttribute(TransactionAttributeType.REQUIRED)
    public void create(UnitaryCustomTableDataDto dto) throws MeveoApiException, BusinessException {
    	Map<String, Object> toValidate = new TreeMap<String, Object>() {{put("customTableCode", dto.getCustomTableCode());  put("value", dto.getValue());}};
    	validateParams(toValidate);
    	CustomEntityTemplate cet = customTableService.getCET(dto.getCustomTableCode());
        Map<String, CustomFieldTemplate> cfts = customTableService.validateCfts(cet,false);
        Map<String, Object> values = customTableService.convertValue(dto.getRowValues(), cfts.values(), false,null);
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
    	Map<String, Object> toValidate = new TreeMap<String, Object>() {{put("customTableCode", dto.getCustomTableCode());  put("values", dto.getValues());}};
    	validateParams(toValidate);
    	CustomEntityTemplate cet = customTableService.getCET(dto.getCustomTableCode());
        Map<String, CustomFieldTemplate> cfts = customTableService.validateCfts(cet, false);
        
        Map<Boolean, List<CustomTableRecordDto>> partitionedById = dto.getValues().stream().collect(Collectors.partitioningBy(x->x.getValues().get(FIELD_ID)!=null));
        List<CustomTableRecordDto> valuesWithIds = partitionedById.get(true);
        List<CustomTableRecordDto> valuesWithoutIds = partitionedById.get(false);
        if (!valuesWithoutIds.isEmpty()) {
            throw new ValidationException(valuesWithoutIds.size() + " record(s) for update are missing the IDs.");
        }
        customTableService.updateRecords(cet.getDbTablename(), cfts.values(), valuesWithIds);
    }

    @TransactionAttribute(TransactionAttributeType.REQUIRED)
    public void update(UnitaryCustomTableDataDto dto) throws MeveoApiException, BusinessException {
    	Map<String, Object> toValidate = new TreeMap<String, Object>() {{put("customTableCode", dto.getCustomTableCode());  put("value", dto.getValue());}};
    	validateParams(toValidate);
        CustomEntityTemplate cet = customTableService.getCET(dto.getCustomTableCode());
        Map<String, CustomFieldTemplate> cfts = customTableService.validateCfts(cet, false);
        List<Map<String, Object>> values = customTableService.convertValues(Arrays.asList(dto.getRowValues()), cfts.values(), false);
        customTableService.update(cet.getDbTablename(), values);
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
    	Map<String, Object> toValidate = new TreeMap<String, Object>() {{put("customTableCode", dto.getCustomTableCode());  put("values", dto.getValues());}};
    	validateParams(toValidate);
        CustomEntityTemplate cet = customTableService.getCET(dto.getCustomTableCode());
        Map<String, CustomFieldTemplate> cfts = customTableService.validateCfts(cet, false);
        Map<Boolean, List<CustomTableRecordDto>> partitionedById = dto.getValues().stream().collect(Collectors.partitioningBy(x->x.getValues().get(FIELD_ID)!=null));
        //create records without ids
        List<CustomTableRecordDto> valuesWithoutIds = partitionedById.get(false);
        customTableService.importData(cet, valuesWithoutIds.stream().map(x->x.getValues()).collect(toList()), !dto.getOverwrite());
        //update records with ids
        List<CustomTableRecordDto> valuesWithIds = partitionedById.get(true);
        customTableService.updateRecords(cet.getDbTablename(), cfts.values(), valuesWithIds);
    }

    /**
     * Retrieve custom table data based on a search criteria
     *
     * @param customTableCode    Custom table/custom entity template code
     * @param pagingAndFiltering Search and pagination criteria
     * @return Values and pagination information
     * @throws MissingParameterException    Missing parameters
     * @throws EntityDoesNotExistsException Custom table was not matched
     * @throws InvalidParameterException    Invalid parameters passed
     * @throws ValidationException
     */
    public CustomTableDataResponseDto list(String customTableCode, PagingAndFiltering pagingAndFiltering)
            throws MissingParameterException, EntityDoesNotExistsException, InvalidParameterException, ValidationException {
    	Map<String, Object> toValidate = new TreeMap<String, Object>() {{put("customTableCode", customTableCode);}};
    	validateParams(toValidate);
        if (pagingAndFiltering == null) {
            pagingAndFiltering = new PagingAndFiltering();
        }
        CustomEntityTemplate cet = customTableService.getCET(customTableCode);
        Map<String, CustomFieldTemplate> cfts = customTableService.validateCfts(cet, false);
        pagingAndFiltering.setFilters(customTableService.convertValue(pagingAndFiltering.getFilters(), cfts.values(), true, null));
        PaginationConfiguration paginationConfig = toPaginationConfiguration(FIELD_ID, SortOrder.ASCENDING, null, pagingAndFiltering, null);
        Long totalCount = customTableService.count(cet.getDbTablename(), paginationConfig);
        CustomTableDataResponseDto result = new CustomTableDataResponseDto();
        result.setPaging(pagingAndFiltering);
        result.getPaging().setTotalNumberOfRecords(totalCount.intValue());
        result.getCustomTableData().setCustomTableCode(customTableCode);
        List<Map<String, Object>> list = customTableService.list(cet.getDbTablename(), paginationConfig);
        customTableService.completeWithEntities(list, cfts, pagingAndFiltering.getLoadReferenceDepth());
        result.getCustomTableData().setValuesFromListofMap(list);
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
    	Map<String, Object> toValidate = new TreeMap<String, Object>() {{put("customTableCode", dto.getCustomTableCode());}};
    	validateParams(toValidate);
    	CustomEntityTemplate cet = customTableService.getCET(dto.getCustomTableCode());
        if (dto.getValues() == null || dto.getValues().isEmpty()) {
            customTableService.remove(cet.getDbTablename());
        } else {
            Set<Long> ids = extractIds(dto);
            customTableService.remove(cet.getDbTablename(), ids);
        }
    }

    
	@TransactionAttribute(TransactionAttributeType.REQUIRED)
    public void remove(String tableName, Long id) throws MeveoApiException, BusinessException {
    	Map<String, Object> toValidate = new TreeMap<String, Object>() {{put("tableName", tableName);  put(FIELD_ID, id);}};
    	validateParams(toValidate);
    	CustomEntityTemplate cet = customTableService.getCET(tableName);
        customTableService.remove(cet.getDbTablename(), id);
    }

    @TransactionAttribute(TransactionAttributeType.REQUIRED)
    public void enableOrDisble(String tableName, Long id, boolean enable) {
    	Map<String, Object> toValidate = new TreeMap<String, Object>() {{put("tableName", tableName);  put(FIELD_ID, id);}};
    	validateParams(toValidate);
    	CustomEntityTemplate cet = customTableService.getCET(tableName);
    	customTableService.validateCfts(cet, true);
        if (enable) {
            customTableService.enable(cet.getDbTablename(), id);
        } else {
            customTableService.disable(cet.getDbTablename(), id);
        }
    }

    /**
     * Enable or disable records, identified by 'id' value, in a custom table. Applies only to tables that contain field 'disabled'.
     *
     * @param dto    Values to enable or disable. Should contain only 'id' field values
     * @param enable True to enable records, False to disable records.
     * @throws MeveoApiException API exception
     * @throws BusinessException General exception
     */
    public void enableDisable(CustomTableDataDto dto, boolean enable) throws MeveoApiException, BusinessException {
    	Map<String, Object> toValidate = new TreeMap<String, Object>() {{put("customTableCode", dto.getCustomTableCode());  put("values", dto.getValues());}};
    	validateParams(toValidate);
        CustomEntityTemplate cet = customTableService.getCET(dto.getCustomTableCode());
        customTableService.validateCfts(cet, true);
        Set<Long> ids = extractIds(dto);
        if (enable) {
            customTableService.enable(cet.getDbTablename(), ids);
        } else {
            customTableService.disable(cet.getDbTablename(), ids);
        }
    }

	private Set<Long> extractIds(CustomTableDataDto dto) {
		return dto.getValues().stream().map(x -> (castToLong(x.getValues().get(FIELD_ID))).longValue()).collect(Collectors.toSet());
	}
    
	private Long castToLong(Object id) {
		if (id != null) {
            if (id instanceof String) {
                return Long.parseLong((String) id);
            } else if (id instanceof Number) {
                return ((Number) id).longValue();
            }
            throw new InvalidParameterException("Invalid id value found: "+id );
        } else {
            throw new InvalidParameterException("Not all values have an 'id' field specified");
        }
	}

	@SuppressWarnings("rawtypes")
	private void validateParams(Map<String, Object> toValidate) {
		for(String paramName: toValidate.keySet()) {
			Object value=toValidate.get(paramName);
			if(value == null || (value instanceof String && StringUtils.isBlank((String) value)) 
					|| (value instanceof Collection && CollectionUtils.isEmpty((Collection) value))){
				missingParameters.add(paramName);
			}
		}
		handleMissingParameters();
	}

}