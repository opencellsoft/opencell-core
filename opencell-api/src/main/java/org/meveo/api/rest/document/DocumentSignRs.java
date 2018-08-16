package org.meveo.api.rest.document;

import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;

import org.meveo.api.dto.document.sign.CreateProcedureRequestDto;
import org.meveo.api.dto.document.sign.SignProcedureResponseDto;
import org.meveo.api.dto.response.RawResponseDto;
import org.meveo.api.dto.document.sign.SignFileResponseDto;
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
    
    @POST 
    @Path("/procedures") 
    public SignProcedureResponseDto createProcedure(CreateProcedureRequestDto postData); 
    
    @GET 
    @Path("/procedures/{id}") 
    public SignProcedureResponseDto getProcedureById(@PathParam("id") String id); 
    
    @GET 
    @Path("/procedures/{id}/status") 
    public RawResponseDto<String> getProcedureStatusById(@PathParam("id") String id); 
    
    @GET 
    @Path("/files/{id}/download") 
    public SignFileResponseDto downloadFileById(@PathParam("id") String id); 

}
