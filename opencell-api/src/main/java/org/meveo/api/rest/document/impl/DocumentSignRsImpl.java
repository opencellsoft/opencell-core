package org.meveo.api.rest.document.impl;

import javax.enterprise.context.RequestScoped;
import javax.inject.Inject;
import javax.interceptor.Interceptors;

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
