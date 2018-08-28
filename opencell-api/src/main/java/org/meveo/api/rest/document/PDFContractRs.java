package org.meveo.api.rest.document;

import javax.ws.rs.Consumes;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;

import org.meveo.api.dto.document.PDFContractRequestDto;
import org.meveo.api.dto.response.document.PDFContractResponseDto;
import org.meveo.api.rest.IBaseRs;

/**
 * Rest services to handle PDF Contract files.
 *
 * @author Said Ramli
 */
@Path("/document/pdfcontract")
@Consumes({ MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML })
@Produces({ MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML })
public interface PDFContractRs extends IBaseRs {
    
    /**
     * Generate PDF contract.
     *
     * @param postData the post data
     * @return the PDF contarct response dto
     */
    @POST
    @Path("/")
    public PDFContractResponseDto generatePDFContract(PDFContractRequestDto postData);
}
