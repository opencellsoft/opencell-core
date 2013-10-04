package org.meveo.rest.environment;

import javax.ejb.Stateless;
import javax.inject.Inject;
import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;

import org.meveo.api.CountryServiceApi;
import org.meveo.commons.utils.ParamBean;
import org.meveo.rest.ActionStatus;
import org.meveo.rest.ActionStatusEnum;
import org.meveo.util.MeveoParamBean;

/**
 * @author Edward P. Legaspi
 * @since Oct 2, 2013
 **/
@Stateless
@Path("/environment")
@Consumes({ MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML })
@Produces({ MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML })
public class Environment {

	@Inject
	private CountryServiceApi countryServiceApi;

	@Inject
	@MeveoParamBean
	private ParamBean paramBean;

	@GET
	@Path("/index")
	public ActionStatus index() {
		ActionStatus result = new ActionStatus(ActionStatusEnum.SUCCESS,
				"MEVEO API Rest Web Service");

		return result;
	}

	@POST
	@Path("/createCountry")
	public ActionStatus createCountry(CountryDto countryDto) {
		ActionStatus result = new ActionStatus(ActionStatusEnum.SUCCESS, "");

		try {
			countryDto.setUserId(Long.valueOf(paramBean.getProperty(
					"asp.api.userId", "1")));
			countryServiceApi.create(countryDto);
		} catch (Exception e) {
			result.setStatus(ActionStatusEnum.FAIL);
			result.setMessage(e.getMessage());
		}

		return result;
	}
}
