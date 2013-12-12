package org.meveo.rest.api;

import javax.ejb.Stateless;
import javax.inject.Inject;
import javax.ws.rs.Consumes;
import javax.ws.rs.DELETE;
import javax.ws.rs.POST;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;

import org.meveo.api.MeveoApiErrorCode;
import org.meveo.api.ServicePricePlanServiceApi;
import org.meveo.api.dto.ServicePricePlanDto;
import org.meveo.api.exception.MeveoApiException;
import org.meveo.api.exception.MissingParameterException;
import org.meveo.commons.utils.ParamBean;
import org.meveo.rest.ActionStatus;
import org.meveo.rest.ActionStatusEnum;
import org.meveo.util.MeveoParamBean;

/**
 * @author Edward P. Legaspi
 * @since Oct 11, 2013
 **/
@Stateless
@Path("/servicePricePlan")
@Consumes({ MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML })
@Produces({ MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML })
public class ServicePricePlanWS {

	@Inject
	private ServicePricePlanServiceApi servicePricePlanServiceApi;

	@Inject
	@MeveoParamBean
	private ParamBean paramBean;

	@POST
	@Path("/")
	public ActionStatus create(ServicePricePlanDto servicePricePlanDto) {
		ActionStatus result = new ActionStatus(ActionStatusEnum.SUCCESS, "");

		try {
			servicePricePlanDto.setCurrentUserId(Long.valueOf(paramBean
					.getProperty("asp.api.userId", "1")));
			servicePricePlanDto.setProviderId(Long.valueOf(paramBean
					.getProperty("asp.api.providerId", "1")));

			servicePricePlanServiceApi.create(servicePricePlanDto);
		} catch (MissingParameterException e) {
			result.setErrorCode(MeveoApiErrorCode.MISSING_PARAMETER);
			result.setStatus(ActionStatusEnum.FAIL);
			result.setMessage(e.getMessage());
		} catch (MeveoApiException e) {
			result.setStatus(ActionStatusEnum.FAIL);
			result.setMessage(e.getMessage());
		}

		return result;
	}

	@DELETE
	@Path("/{serviceId}/{organizationId}")
	public ActionStatus remove(@PathParam("serviceId") String serviceId,
			@PathParam("organizationId") String organizationId) {
		ActionStatus result = new ActionStatus(ActionStatusEnum.SUCCESS, "");
		try {
			servicePricePlanServiceApi.remove(serviceId, organizationId, Long
					.valueOf(paramBean.getProperty("asp.api.userId", "1")),
					Long.valueOf(paramBean.getProperty("asp.api.providerId",
							"1")));
		} catch (Exception e) {
			result.setStatus(ActionStatusEnum.FAIL);
			result.setMessage(e.getMessage());
		}

		return result;
	}

	@PUT
	@Path("/")
	public ActionStatus update(ServicePricePlanDto servicePricePlanDto) {
		ActionStatus result = new ActionStatus(ActionStatusEnum.SUCCESS, "");

		try {
			servicePricePlanDto.setCurrentUserId(Long.valueOf(paramBean
					.getProperty("asp.api.userId", "1")));
			servicePricePlanDto.setProviderId(Long.valueOf(paramBean
					.getProperty("asp.api.providerId", "1")));

			servicePricePlanServiceApi.update(servicePricePlanDto);
		} catch (MissingParameterException e) {
			result.setErrorCode(MeveoApiErrorCode.MISSING_PARAMETER);
			result.setStatus(ActionStatusEnum.FAIL);
			result.setMessage(e.getMessage());
		} catch (MeveoApiException e) {
			result.setStatus(ActionStatusEnum.FAIL);
			result.setMessage(e.getMessage());
		}

		return result;
	}

}
