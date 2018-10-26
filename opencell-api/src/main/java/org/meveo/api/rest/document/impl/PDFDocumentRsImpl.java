package org.meveo.api.rest.document.impl;

import javax.enterprise.context.RequestScoped;
import javax.inject.Inject;
import javax.interceptor.Interceptors;

import org.meveo.api.document.PDFDocumentApi;
import org.meveo.api.dto.ActionStatusEnum;
import org.meveo.api.dto.document.PDFDocumentRequestDto;
import org.meveo.api.dto.response.document.PDFDocumentResponseDto;
import org.meveo.api.logging.WsRestApiInterceptor;
import org.meveo.api.rest.document.PDFDocumentRs;
import org.meveo.api.rest.impl.BaseRs;


/**
 * The Default implementation of PDFDocumentRs.
 */
@RequestScoped
@Interceptors({ WsRestApiInterceptor.class })
public class PDFDocumentRsImpl extends BaseRs implements PDFDocumentRs {

    /** The pdf document api. */
    @Inject
    PDFDocumentApi pdfDocumentApi;
    
    @Override
    public PDFDocumentResponseDto generatePDF(PDFDocumentRequestDto postData) {
        PDFDocumentResponseDto result = new PDFDocumentResponseDto();
        try {
            result = pdfDocumentApi.generatePDF(postData);
            result.getActionStatus().setStatus(ActionStatusEnum.SUCCESS); 
        } catch (Exception e) {
            processException(e, result.getActionStatus());
        }
        return result;
    }

}
