package org.meveo.api.rest.document.impl;

import javax.inject.Inject;

import org.meveo.api.document.PDFContractApi;
import org.meveo.api.dto.ActionStatusEnum;
import org.meveo.api.dto.document.PDFContractRequestDto;
import org.meveo.api.dto.response.document.PDFContractResponseDto;
import org.meveo.api.rest.document.PDFContractRs;
import org.meveo.api.rest.impl.BaseRs;


/**
 * The Default implementation of PDFContractRs.
 */
public class PDFContractRsImpl extends BaseRs implements PDFContractRs {

    /** The pdf contract api. */
    @Inject
    PDFContractApi pdfContractApi;
    
    @Override
    public PDFContractResponseDto generatePDFContract(PDFContractRequestDto postData) {
        PDFContractResponseDto result = new PDFContractResponseDto();
        try {
            result = pdfContractApi.generatePDFContract(postData);
            result.getActionStatus().setStatus(ActionStatusEnum.SUCCESS); 
        } catch (Exception e) {
            processException(e, result.getActionStatus());
        }
        return result;
    }

}
