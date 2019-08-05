package org.meveo.api.rest.admin.impl;

import org.meveo.api.admin.FileFormatApi;
import org.meveo.api.dto.ActionStatus;
import org.meveo.api.dto.ActionStatusEnum;
import org.meveo.api.dto.admin.FileFormatDto;
import org.meveo.api.logging.WsRestApiInterceptor;
import org.meveo.api.rest.admin.FileFormatRs;
import org.meveo.api.rest.impl.BaseRs;

import javax.enterprise.context.RequestScoped;
import javax.inject.Inject;
import javax.interceptor.Interceptors;

/**
 * File format resource
 *
 * @author Abdellatif BARI
 * @since 8.0.0
 */
@RequestScoped
@Interceptors({ WsRestApiInterceptor.class })
public class FileFormatRsImpl extends BaseRs implements FileFormatRs {

    @Inject
    private FileFormatApi fileFormatApi;

    @Override
    public ActionStatus create(FileFormatDto postData) {
        ActionStatus result = new ActionStatus(ActionStatusEnum.SUCCESS, "");

        try {
            fileFormatApi.create(postData);
        } catch (Exception e) {
            processException(e, result);
        }

        return result;
    }

    @Override
    public ActionStatus remove(String code) {
        ActionStatus result = new ActionStatus(ActionStatusEnum.SUCCESS, "");

        try {
            fileFormatApi.remove(code);
        } catch (Exception e) {
            processException(e, result);
        }

        return result;
    }
}
