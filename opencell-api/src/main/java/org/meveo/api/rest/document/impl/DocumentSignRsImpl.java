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

package org.meveo.api.rest.document.impl;

import jakarta.enterprise.context.RequestScoped;
import jakarta.inject.Inject;
import jakarta.interceptor.Interceptors;

import org.meveo.api.YouSignApi;
import org.meveo.api.dto.ActionStatusEnum;
import org.meveo.api.dto.document.sign.CreateProcedureRequestDto;
import org.meveo.api.dto.document.sign.SignFileResponseDto;
import org.meveo.api.dto.document.sign.SignProcedureResponseDto;
import org.meveo.api.dto.response.RawResponseDto;
import org.meveo.api.logging.WsRestApiInterceptor;
import org.meveo.api.rest.document.DocumentSignRs;
import org.meveo.api.rest.impl.BaseRs;

/**
 * The default Implementation of DocumentSignRs.
 */
@RequestScoped
@Interceptors({ WsRestApiInterceptor.class })
public class DocumentSignRsImpl extends BaseRs implements DocumentSignRs {
    
    @Inject
    private YouSignApi youSignApi;

    @Override
    public SignProcedureResponseDto createProcedure(CreateProcedureRequestDto postData) {
        SignProcedureResponseDto result = new SignProcedureResponseDto(); 
        try { 
            result = youSignApi.createProcedure(postData); 
            result.getActionStatus().setStatus(ActionStatusEnum.SUCCESS);  
        } catch (Exception e) { 
            processException(e, result.getActionStatus()); 
        } 
        return result; 
    }

    @Override
    public SignFileResponseDto downloadFileById(String id) {
        SignFileResponseDto result = new SignFileResponseDto(); 
        try { 
            result = youSignApi.downloadFileById(id); 
            result.getActionStatus().setStatus(ActionStatusEnum.SUCCESS);  
        } catch (Exception e) { 
            processException(e, result.getActionStatus()); 
        } 
        return result; 
    }

    @Override
    public SignProcedureResponseDto getProcedureById(String id) {
        SignProcedureResponseDto result = new SignProcedureResponseDto(); 
        try { 
            result = youSignApi.getProcedureById(id); 
            result.getActionStatus().setStatus(ActionStatusEnum.SUCCESS);  
        } catch (Exception e) { 
            processException(e, result.getActionStatus()); 
        } 
        return result; 
    }

    @Override
    public RawResponseDto<String> getProcedureStatusById(String id) {
        RawResponseDto<String> result = new RawResponseDto<>(); 
        try { 
          String  status = youSignApi.getProcedureStatusById(id);  
          result.setResponse(status);
        } catch (Exception e) { 
            processException(e, result.getActionStatus()); 
        } 
        return result; 
    }
}
