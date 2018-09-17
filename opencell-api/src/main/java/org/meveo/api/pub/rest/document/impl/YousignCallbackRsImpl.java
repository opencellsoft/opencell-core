package org.meveo.api.pub.rest.document.impl;

import javax.enterprise.context.RequestScoped;
import javax.inject.Inject;
import javax.interceptor.Interceptors;

import org.meveo.api.YouSignApi;
import org.meveo.api.dto.ActionStatus;
import org.meveo.api.dto.ActionStatusEnum;
import org.meveo.api.dto.document.sign.SignCallbackDto;
import org.meveo.api.dto.document.sign.SignFileResponseDto;
import org.meveo.api.dto.document.sign.YousignEventEnum;
import org.meveo.api.logging.WsRestApiInterceptor;
import org.meveo.api.pub.rest.document.YousignCallbackRs;
import org.meveo.api.rest.impl.BaseRs;

/**
 * The default Implementation of YousignCallbackRs.
 */
@RequestScoped
@Interceptors({ WsRestApiInterceptor.class })
public class YousignCallbackRsImpl extends BaseRs implements YousignCallbackRs {

    @Inject
    private YouSignApi youSignApi;

    @Override
    public ActionStatus youSignCallback(SignCallbackDto callbackDto) {
        ActionStatus result = new ActionStatus(ActionStatusEnum.SUCCESS, "");
        SignFileResponseDto fileResponseDto = callbackDto.getProcedure().getFiles().get(0); // Managing the case of many files is not yet required for the moment
        try {
            YousignEventEnum event = callbackDto.getEventName();
            switch (event) {
            case PROCEDURE_FINISHED:
                youSignApi.downloadFileByIdAndSaveInServer(fileResponseDto.getId().substring(7), fileResponseDto.getName());
                break;
            case PROCEDURE_STARTED: // Not yet needed
            default:
                result = new ActionStatus(ActionStatusEnum.FAIL, " Event not supported : " + event);
                break;
            }
        } catch (Exception e) {
            processException(e, new ActionStatus(ActionStatusEnum.FAIL, e.getMessage()));
        }

        return result;
    }

}
