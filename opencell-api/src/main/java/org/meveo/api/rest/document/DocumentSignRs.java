package org.meveo.api.rest.document;

import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;

import org.meveo.api.dto.document.sign.CreateProcedureRequestDto;
import org.meveo.api.dto.document.sign.SignFileResponseDto;
import org.meveo.api.dto.document.sign.SignProcedureResponseDto;
import org.meveo.api.dto.response.RawResponseDto;
import org.meveo.api.rest.IBaseRs;

/** 
 * Rest services to handle Document signature. 
 * 
 * @author Said Ramli 
 */ 
@Path("/document/sign") 
@Consumes({ MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML }) 
@Produces({ MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML }) 
public interface DocumentSignRs extends IBaseRs { 
    
    /**
     * Creates the procedure.
     *
     * @param postData the post data
     * @return the sign procedure response dto
     */
    @POST 
    @Path("/procedures") 
    public SignProcedureResponseDto createProcedure(CreateProcedureRequestDto postData); 
    
    /**
     * Gets the procedure by id.
     *
     * @param id the id
     * @return the procedure by id
     */    
    @GET 
    @Path("/procedures/{id}") 
    public SignProcedureResponseDto getProcedureById(@PathParam("id") String id); 
    
    /**
     * Gets the procedure status by id.
     *
     * @param id the id
     * @return the procedure status by id
     */    
    @GET 
    @Path("/procedures/{id}/status") 
    public RawResponseDto<String> getProcedureStatusById(@PathParam("id") String id); 
    
    /**
     * Download the files with the given id
     *
     * @param id The id
     * @return the file by id
     */    
    @GET 
    @Path("/files/{id}/download") 
    public SignFileResponseDto downloadFileById(@PathParam("id") String id); 

}
