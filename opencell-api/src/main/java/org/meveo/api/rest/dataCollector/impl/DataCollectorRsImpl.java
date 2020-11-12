package org.meveo.api.rest.dataCollector.impl;

import static org.meveo.api.dto.ActionStatusEnum.FAIL;
import static org.meveo.api.dto.ActionStatusEnum.SUCCESS;
import static org.meveo.api.MeveoApiErrorCodeEnum.ENTITY_DOES_NOT_EXISTS_EXCEPTION;

import org.meveo.api.dto.ActionStatus;
import org.meveo.api.dto.DataCollectorDto;
import org.meveo.api.dto.AggregatedDataDto;
import org.meveo.api.dto.response.AggregatedDataResponseDto;
import org.meveo.api.dataCollector.DataCollectorApi;
import org.meveo.api.dto.response.DataCollectorResponse;
import org.meveo.api.logging.WsRestApiInterceptor;
import org.meveo.api.rest.dataCollector.DataCollectorRs;
import org.meveo.api.rest.impl.BaseRs;

import javax.enterprise.context.RequestScoped;
import javax.inject.Inject;
import javax.interceptor.Interceptors;
import java.util.Date;
import java.util.Optional;

@RequestScoped
@Interceptors({ WsRestApiInterceptor.class })
public class DataCollectorRsImpl  extends BaseRs implements DataCollectorRs {

    @Inject
    private DataCollectorApi dataCollectorApi;

    @Override
    public ActionStatus create(DataCollectorDto postData) {
        ActionStatus result = new ActionStatus(SUCCESS, "");
        try {
            DataCollectorDto dto = dataCollectorApi.create(postData);
            result.setEntityCode(dto.getCode());
        } catch (Exception exception) {
            result.setStatus(FAIL);
            processException(exception, result);
        }
        return result;
    }

    @Override
    public DataCollectorResponse find(String code) {
        DataCollectorResponse response = new DataCollectorResponse();
        try {
            response = buildResponse(dataCollectorApi.find(code));
        } catch (Exception exception) {
            processException(exception,  response.getActionStatus());
        }
        return response;
    }

    private DataCollectorResponse buildResponse(Optional<DataCollectorDto> dataCollectorDto) {
        DataCollectorResponse response = new DataCollectorResponse();
        if(dataCollectorDto.isPresent()) {
            response.setDataCollectorDto(dataCollectorDto.get());
        } else {
            ActionStatus actionStatus = new ActionStatus();
            actionStatus.setStatus(FAIL);
            actionStatus.setErrorCode(ENTITY_DOES_NOT_EXISTS_EXCEPTION);
            actionStatus.setMessage("Data collector does not exists");
            response.setActionStatus(actionStatus);
        }
        return response;
    }

    @Override
    public ActionStatus execute(String dataCollectorCode) {
        ActionStatus result = new ActionStatus(SUCCESS, "");
        try {
            result = dataCollectorApi.execute(dataCollectorCode);
            dataCollectorApi.updateLastRunDate(result.getEntityCode(), new Date());
        } catch (Exception exception) {
            result.setStatus(FAIL);
            processException(exception, result);
        }
        return result;
    }

    @Override
    public AggregatedDataResponseDto aggregatedData(AggregatedDataDto aggregationFields) {
        AggregatedDataResponseDto response;
        try {
            response = dataCollectorApi.aggregatedData(aggregationFields);
            response.setStatus(SUCCESS);
        } catch (Exception exception) {
            response = new AggregatedDataResponseDto();
            response.setStatus(FAIL);
        }
        return response;
    }
}