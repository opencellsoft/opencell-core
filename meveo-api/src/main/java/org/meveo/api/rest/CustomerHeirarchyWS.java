package org.meveo.api.rest;

import javax.ejb.Stateless;
import javax.inject.Inject;
import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;

import org.meveo.api.ActionStatus;
import org.meveo.api.ActionStatusEnum;
import org.meveo.api.CustomerHeirarchyApi;
import org.meveo.api.dto.CustomerHeirarchyDto;
import org.meveo.commons.utils.ParamBean;
import org.slf4j.Logger;

@Stateless
@Path("/customer")
@Consumes({ MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML })
@Produces({ MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML })
public class CustomerHeirarchyWS {

	@Inject
	private Logger log;

	private ParamBean paramBean = ParamBean
			.getInstance("meveo-admin.properties");

	@Inject
	private CustomerHeirarchyApi customerHeirarchyApi;

	@GET
	@Path("/index")
	public ActionStatus index() {
		ActionStatus result = new ActionStatus(ActionStatusEnum.SUCCESS,
				"MEVEO API Rest Web Service");

		return result;
	}

	@POST
	@Path("/create")
	public ActionStatus create(CustomerHeirarchyDto customerHeirarchyDto) {
		log.info("Creating Customer Heirarchy...");
		log.debug("customerHeirarchy.create={}", customerHeirarchyDto);

		ActionStatus result = new ActionStatus(ActionStatusEnum.SUCCESS, "");

		try {
			customerHeirarchyDto.setCurrentUserId(Long.valueOf(paramBean
					.getProperty("asp.api.userId", "1")));
			customerHeirarchyDto.setProviderId(Long.valueOf(paramBean
					.getProperty("asp.api.providerId", "1")));

			customerHeirarchyApi.createCustomerHeirarchy(customerHeirarchyDto);

		} catch (Exception e) {
			result.setStatus(ActionStatusEnum.FAIL);
			result.setMessage(e.getMessage());
			e.printStackTrace();
		}

		return result;
	}

}
