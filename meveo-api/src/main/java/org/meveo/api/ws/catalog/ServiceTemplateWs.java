package org.meveo.api.ws.catalog;

import javax.jws.WebMethod;
import javax.jws.WebService;
import javax.ws.rs.POST;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;

import org.meveo.api.dto.ActionStatus;
import org.meveo.api.dto.catalog.ServiceTemplateDto;
import org.meveo.api.dto.response.catalog.GetServiceTemplateResponse;
import org.meveo.api.ws.IBaseWs;

/**
 * @author Edward P. Legaspi
 **/
@WebService
public interface ServiceTemplateWs extends IBaseWs {

	@Path("/")
	@POST
	ActionStatus create(ServiceTemplateDto postData);

	@Path("/")
	@PUT
	ActionStatus update(ServiceTemplateDto postData);

	@WebMethod
	GetServiceTemplateResponse find(String serviceTemplateCode);

	@WebMethod
	ActionStatus remove(String serviceTemplateCode);

}
