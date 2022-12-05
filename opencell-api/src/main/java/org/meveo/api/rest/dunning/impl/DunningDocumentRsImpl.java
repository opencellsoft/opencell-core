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

package org.meveo.api.rest.dunning.impl;

import jakarta.enterprise.context.RequestScoped;
import jakarta.inject.Inject;
import jakarta.interceptor.Interceptors;

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
