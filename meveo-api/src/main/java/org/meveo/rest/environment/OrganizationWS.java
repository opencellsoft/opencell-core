package org.meveo.rest.environment;

import javax.ejb.Stateless;
import javax.inject.Inject;
import javax.ws.rs.Consumes;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;

import org.meveo.api.OrganizationServiceApi;
import org.meveo.api.dto.OrganizationDto;
import org.meveo.api.exception.MeveoApiException;
import org.meveo.commons.utils.ParamBean;
import org.meveo.rest.ActionStatus;
import org.meveo.rest.ActionStatusEnum;
import org.meveo.util.MeveoParamBean;

/**
 * @author Edward P. Legaspi
 * @since Oct 11, 2013
 **/
@Stateless
@Path("/organization")
@Consumes({ MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML })
@Produces({ MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML })
public class OrganizationWS {

	@Inject
	@MeveoParamBean
	private ParamBean paramBean;

	@Inject
	private OrganizationServiceApi organizationServiceApi;

	@POST
	@Path("/")
	public ActionStatus create(OrganizationDto orgDto) {
		ActionStatus result = new ActionStatus(ActionStatusEnum.SUCCESS, "");

		try {
			orgDto.setCurrentUserId(Long.valueOf(paramBean.getProperty(
					"asp.api.userId", "1")));
			orgDto.setProviderId(Long.valueOf(paramBean.getProperty(
					"asp.api.providerId", "1")));

			organizationServiceApi.create(orgDto);
		} catch (MeveoApiException e) {
			result.setStatus(ActionStatusEnum.FAIL);
			result.setMessage(e.getMessage());
		}

		return result;
	}

}
