package org.meveo.api.rest.dunning.impl;

import javax.enterprise.context.RequestScoped;
import javax.inject.Inject;
import javax.interceptor.Interceptors;

import org.meveo.api.dto.ActionStatus;
import org.meveo.api.dto.ActionStatusEnum;
import org.meveo.api.dto.dunning.DunningDocumentDto;
import org.meveo.api.dto.dunning.DunningDocumentResponseDto;
import org.meveo.api.dto.dunning.DunningDocumentsListResponseDto;
import org.meveo.api.dto.response.PagingAndFiltering;
import org.meveo.api.dunning.DunningDocumentApi;
import org.meveo.api.logging.WsRestApiInterceptor;
import org.meveo.api.rest.dunning.DunningDocumentRs;
import org.meveo.api.rest.impl.BaseRs;
import org.meveo.model.dunning.DunningDocument;

@RequestScoped
@Interceptors({ WsRestApiInterceptor.class })
public class DunningDocumentRsImpl extends BaseRs implements DunningDocumentRs {

    @Inject
    private DunningDocumentApi dunningDocumentApi;

    public ActionStatus create(DunningDocumentDto dunningDocumentDto) {
        ActionStatus result = new ActionStatus(ActionStatusEnum.SUCCESS, "");
        log.info("createDunningDocument request={}", dunningDocumentDto);
        try {
            DunningDocument dunningDocument = dunningDocumentApi.create(dunningDocumentDto);
            result.setEntityCode(dunningDocument.getCode());
        } catch (Exception e) {
            processException(e, result);
        }

        return result;
    }

    public DunningDocumentsListResponseDto listPost(PagingAndFiltering pagingAndFiltering) {
        log.info("listPostDunningDocument request={}", pagingAndFiltering);
        try {
            return dunningDocumentApi.list(null, pagingAndFiltering);
        } catch (Exception e) {
            DunningDocumentsListResponseDto result = new DunningDocumentsListResponseDto();
            processException(e, result.getActionStatus());
            return result;
        }
    }

    public ActionStatus addPayments(DunningDocumentDto postData) {
        ActionStatus result = new ActionStatus(ActionStatusEnum.SUCCESS, "");
        log.info("updateDunningDocument request={}", postData);
        try {
            DunningDocument dunningDocument = dunningDocumentApi.addPayments(postData.getDunningDocumentId(), postData.getPayments());
            result.setEntityCode(dunningDocument.getCode());
        } catch (Exception e) {
            processException(e, result);
        }

        return result;
    }

    public DunningDocumentResponseDto find(String dunningDocumentCode) {
        log.info("findDunningDocument dunningDocumentCode={}", dunningDocumentCode);
        try {
            return dunningDocumentApi.find(dunningDocumentCode);
        } catch (Exception e) {
            DunningDocumentResponseDto result = new DunningDocumentResponseDto();
            processException(e, result.getActionStatus());
            return result;
        }
    }

}
