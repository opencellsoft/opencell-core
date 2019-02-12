package org.meveo.api.rest.custom.impl;

import javax.enterprise.context.RequestScoped;
import javax.inject.Inject;
import javax.interceptor.Interceptors;

import org.meveo.api.custom.CustomTableApi;
import org.meveo.api.dto.ActionStatus;
import org.meveo.api.dto.ActionStatusEnum;
import org.meveo.api.dto.custom.CustomTableDataDto;
import org.meveo.api.dto.custom.CustomTableDataResponseDto;
import org.meveo.api.dto.response.PagingAndFiltering;
import org.meveo.api.logging.WsRestApiInterceptor;
import org.meveo.api.rest.custom.CustomTableRs;
import org.meveo.api.rest.impl.BaseRs;

/**
 * Rest API implementation for custom table data management
 * 
 * @author Andrius Karpavicius
 **/
@RequestScoped
@Interceptors({ WsRestApiInterceptor.class })
public class CustomTableRsImpl extends BaseRs implements CustomTableRs {

    @Inject
    private CustomTableApi customTableApi;

    @Override
    public ActionStatus append(CustomTableDataDto dto) {

        ActionStatus result = new ActionStatus(ActionStatusEnum.SUCCESS, "");

        try {

            customTableApi.append(dto);

        } catch (Exception e) {
            processException(e, result);
        }

        return result;
    }

    @Override
    public ActionStatus update(CustomTableDataDto dto) {

        ActionStatus result = new ActionStatus(ActionStatusEnum.SUCCESS, "");

        try {

            customTableApi.update(dto);

        } catch (Exception e) {
            processException(e, result);
        }

        return result;
    }

    @Override
    public ActionStatus remove(CustomTableDataDto dto) {

        ActionStatus result = new ActionStatus(ActionStatusEnum.SUCCESS, "");

        try {

            customTableApi.remove(dto);

        } catch (Exception e) {
            processException(e, result);
        }

        return result;
    }

    @Override
    public CustomTableDataResponseDto list(String customTableCode, PagingAndFiltering pagingAndFiltering) {

        CustomTableDataResponseDto result = new CustomTableDataResponseDto();

        try {

            return customTableApi.list(customTableCode, pagingAndFiltering);

        } catch (Exception e) {
            processException(e, result.getActionStatus());
        }

        return result;

    }

    @Override
    public ActionStatus createOrUpdate(CustomTableDataDto dto) {

        ActionStatus result = new ActionStatus(ActionStatusEnum.SUCCESS, "");

        try {

            customTableApi.createOrUpdate(dto);

        } catch (Exception e) {
            processException(e, result);
        }

        return result;
    }

    @Override
    public ActionStatus enable(CustomTableDataDto dto) {

        ActionStatus result = new ActionStatus(ActionStatusEnum.SUCCESS, "");

        try {
            customTableApi.enableDisable(dto, true);

        } catch (Exception e) {
            processException(e, result);
        }

        return result;
    }

    @Override
    public ActionStatus disable(CustomTableDataDto dto) {

        ActionStatus result = new ActionStatus(ActionStatusEnum.SUCCESS, "");

        try {
            customTableApi.enableDisable(dto, false);

        } catch (Exception e) {
            processException(e, result);
        }

        return result;
    }
}