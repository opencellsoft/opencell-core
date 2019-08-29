package org.meveo.api.rest.document;

import javax.ws.rs.Consumes;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;

import org.meveo.api.dto.document.PDFDocumentRequestDto;
import org.meveo.api.dto.response.document.PDFDocumentResponseDto;
import org.meveo.api.rest.IBaseRs;

/**
 * Rest services to handle PDF document files.
 *
 * @author Said Ramli
 */
@Path("/document/pdf")
@Consumes({ MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML })
@Produces({ MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML })
public interface PDFDocumentRs extends IBaseRs {
    
    /**
     * Generate PDF document.
     *
     * @param postData the post data
     * @return the PDF contarct response dto
     */
    @POST
    @Path("/")
    public PDFDocumentResponseDto generatePDF(PDFDocumentRequestDto postData);
}
