package org.meveo.api.rest.document;

import javax.ws.rs.Consumes;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;

import org.meveo.api.dto.document.sign.CreateProcedureRequestDto;
import org.meveo.api.dto.document.sign.CreateProcedureResponseDto;
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
    public CreateProcedureResponseDto createProcedure(CreateProcedureRequestDto postData); 

}
