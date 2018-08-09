package org.meveo.api.rest.document.impl;

import javax.inject.Inject;

import org.meveo.api.YouSignApi;
import org.meveo.api.dto.ActionStatusEnum;
import org.meveo.api.dto.document.sign.CreateProcedureRequestDto;
import org.meveo.api.dto.document.sign.CreateProcedureResponseDto;
import org.meveo.api.rest.document.DocumentSignRs;
import org.meveo.api.rest.impl.BaseRs;

/**
 * The default Implementation of DocumentSignRs.
 */
public class DocumentSignRsImpl extends BaseRs implements DocumentSignRs {
    
    /** The you sign api. */
    @Inject
    private YouSignApi youSignApi;

    @Override
    public CreateProcedureResponseDto createProcedure(CreateProcedureRequestDto postData) {
        CreateProcedureResponseDto result = new CreateProcedureResponseDto(); 
        try { 
            result = youSignApi.createProcedure(postData); 
            result.getActionStatus().setStatus(ActionStatusEnum.SUCCESS);  
        } catch (Exception e) { 
            processException(e, result.getActionStatus()); 
        } 
        return result; 
    } 

}
