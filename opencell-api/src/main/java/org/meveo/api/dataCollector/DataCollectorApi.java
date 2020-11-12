package org.meveo.api.dataCollector;

import static java.util.Optional.empty;
import static java.util.Optional.of;
import static java.util.Optional.ofNullable;

import org.meveo.admin.exception.BusinessException;
import org.meveo.api.BaseApi;
import org.meveo.api.CustomEntityTemplateApi;
import org.meveo.api.dto.ActionStatus;
import org.meveo.api.dto.ActionStatusEnum;
import org.meveo.api.dto.AggregatedDataDto;
import org.meveo.api.dto.CustomEntityTemplateDto;
import org.meveo.api.dto.DataCollectorDto;
import org.meveo.api.dto.response.AggregatedDataResponseDto;
import org.meveo.api.exception.MeveoApiException;
import org.meveo.model.customEntities.CustomEntityTemplate;
import org.meveo.model.bi.DataCollector;
import org.meveo.service.dataCollector.DataCollectorService;

import javax.ejb.Stateless;
import javax.ejb.TransactionAttribute;
import javax.ejb.TransactionAttributeType;
import javax.inject.Inject;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@Stateless
public class DataCollectorApi extends BaseApi {

    @Inject
    private CustomEntityTemplateApi customEntityTemplateApi;
    @Inject
    private DataCollectorService dataCollectorService;

    public DataCollectorDto create(DataCollectorDto postData) throws MeveoApiException, BusinessException {
        validateInputs(postData);
        String customTableCode = customTableCode(postData);
        DataCollector dataCollector = from(postData, customTableCode);
        dataCollectorService.create(dataCollector);
        return DataCollectorDto.from(dataCollector);
    }

    private void validateInputs(DataCollectorDto postData) {
        if(postData.getEntityTemplateDto() == null && postData.getCustomTableCode() == null) {
            throw new MeveoApiException("Custom table is missing");
        }
        if (postData.getAliases() == null || postData.getAliases().isEmpty()) {
            throw new MeveoApiException("Data collector aliases are missing");
        }
    }

    private String customTableCode(DataCollectorDto postData) {
        if(postData.getCustomTableCode() != null) {
            CustomEntityTemplateDto customTable = customEntityTemplateApi.find(postData.getCustomTableCode());
            return customTable.getCode();
        } else {
            CustomEntityTemplate customTable = customEntityTemplateApi.create(postData.getEntityTemplateDto());
            return customTable.getCode();
        }
    }

    public Optional<DataCollectorDto> find(String code) {
        return ofNullable(dataCollectorService.findByCode(code))
                .map(dc -> of(DataCollectorDto.from(dc)))
                .orElse(empty());
    }

    private DataCollector from(DataCollectorDto postData, String tableCode) {
        DataCollector dataCollector = new DataCollector();
        dataCollector.setCode(postData.getCode());
        ofNullable(postData.getDescription())
                .ifPresent(description -> dataCollector.setDescription(description));
        dataCollector.setSqlQuery(postData.buildQuery());
        dataCollector.setCustomTableCode(tableCode);
        dataCollector.setAliases(postData.getAliases());
        ofNullable(postData.getParameters())
                .ifPresent(params -> dataCollector.setParameters(params));
        return dataCollector;
    }

    @TransactionAttribute(TransactionAttributeType.NEVER)
    public ActionStatus execute(String dataCollectorCode) {
        ActionStatus result = new ActionStatus(ActionStatusEnum.SUCCESS, "");
        int importedRecord = dataCollectorService.executeQuery(dataCollectorCode);
        result.setMessage("Number of record imported : " + importedRecord);
        result.setEntityCode(dataCollectorCode);
        return result;
    }

    public AggregatedDataResponseDto aggregatedData(AggregatedDataDto aggregationFields) {
        List<Map<String, Object>> queryResult =  dataCollectorService.aggregatedData(aggregationFields.getCustomTableCode(),
                aggregationFields.getDataCollectorCode(),
                aggregationFields.getAggregatedFields(),
                aggregationFields.getFields());
        AggregatedDataResponseDto response = new AggregatedDataResponseDto();
        response.setQueryResult(queryResult);
        return response;
    }

    public void updateLastRunDate(String code, Date lastDateRun) {
        DataCollector dataCollector = dataCollectorService.findByCode(code);
        dataCollector.setLastRunDate(lastDateRun);
        dataCollectorService.update(dataCollector);
    }
}