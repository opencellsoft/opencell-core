package org.meveo.api.rest.impl;

import javax.enterprise.context.RequestScoped;
import javax.inject.Inject;
import javax.interceptor.Interceptors;
import javax.ws.rs.QueryParam;

import org.meveo.api.dto.ActionStatusEnum;
import org.meveo.api.dto.response.PdfInvoiceResponse;
import org.meveo.api.invoice.PdfInvoiceApi;
import org.meveo.api.logging.WsRestApiInterceptor;
import org.meveo.api.rest.PdfInvoiceRs;

/**
 * @author R.AITYAAZZA
 * 
 */
@RequestScoped
@Interceptors({ WsRestApiInterceptor.class })
public class PdfInvoiceRsImpl extends BaseRs implements PdfInvoiceRs {

    @Inject
    private PdfInvoiceApi pdfInvoiceApi;

    @Override
    public PdfInvoiceResponse getPDFInvoice(@QueryParam("invoiceNumber") String invoiceNumber, @QueryParam("customerAccountCode") String customerAccountCode) {

        PdfInvoiceResponse result = new PdfInvoiceResponse();
        result.getActionStatus().setStatus(ActionStatusEnum.SUCCESS);

        try {
            result.setPdfInvoice(pdfInvoiceApi.getPDFInvoice(invoiceNumber, customerAccountCode));
        } catch (Exception e) {
            processException(e, result.getActionStatus());
        }

        return result;
    }

}
