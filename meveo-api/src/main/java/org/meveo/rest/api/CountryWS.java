package org.meveo.rest.api;

import javax.ejb.Stateless;
import javax.inject.Inject;
import javax.ws.rs.Consumes;
import javax.ws.rs.DELETE;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.MediaType;

import org.meveo.api.CountryServiceApi;
import org.meveo.api.dto.CountryDto;
import org.meveo.commons.utils.ParamBean;
import org.meveo.rest.ActionStatus;
import org.meveo.rest.ActionStatusEnum;
import org.meveo.rest.api.response.CountryResponse;
import org.meveo.util.MeveoParamBean;

/**
 * @author Edward P. Legaspi
 * @since Oct 7, 2013
 **/
@Stateless
@Path("/country")
@Consumes({ MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML })
@Produces({ MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML })
public class CountryWS {

	@Inject
	private CountryServiceApi countryServiceApi;

	@Inject
	@MeveoParamBean
	private ParamBean paramBean;

	public CountryWS() {

	}

	@GET
	@Path("/index")
	public ActionStatus index() {
		ActionStatus result = new ActionStatus(ActionStatusEnum.SUCCESS,
				"MEVEO API Rest Web Service");

		return result;
	}

	@POST
	@Path("/")
	public ActionStatus create(CountryDto countryDto) {
		ActionStatus result = new ActionStatus(ActionStatusEnum.SUCCESS, "");

		try {
			countryDto.setCurrentUserId(Long.valueOf(paramBean.getProperty(
					"asp.api.userId", "1")));
			countryDto.setProviderId(Long.valueOf(paramBean.getProperty(
					"asp.api.providerId", "1")));
			
			countryServiceApi.create(countryDto);
		} catch (Exception e) {
			result.setStatus(ActionStatusEnum.FAIL);
			result.setMessage(e.getMessage());
		}

		return result;
	}

	@GET
	@Path("/")
	public CountryResponse find(@QueryParam("countryCode") String countryCode) {
		CountryResponse result = new CountryResponse();
		result.getActionStatus().setStatus(ActionStatusEnum.SUCCESS);

		try {
			result.setCountryDto(countryServiceApi.find(countryCode));
		} catch (Exception e) {
			result.getActionStatus().setStatus(ActionStatusEnum.FAIL);
			result.getActionStatus().setMessage(e.getMessage());
		}

		return result;
	}

	@DELETE
	@Path("/{countryCode}/{currencyCode}")
	public ActionStatus remove(@PathParam("countryCode") String countryCode,
			@PathParam("currencyCode") String currencyCode) {
		ActionStatus result = new ActionStatus(ActionStatusEnum.SUCCESS, "");
		Long providerId = Long.valueOf(paramBean.getProperty(
				"asp.api.providerId", "1"));

		try {
			countryServiceApi.remove(countryCode, currencyCode, providerId);
		} catch (Exception e) {
			result.setStatus(ActionStatusEnum.FAIL);
			result.setMessage(e.getMessage());
		}

		return result;
	}

	@PUT
	@Path("/")
	public ActionStatus update(CountryDto countryDto) {
		ActionStatus result = new ActionStatus(ActionStatusEnum.SUCCESS, "");

		try {
			countryDto.setCurrentUserId(Long.valueOf(paramBean.getProperty(
					"asp.api.userId", "1")));
			countryDto.setProviderId(Long.valueOf(paramBean.getProperty(
					"asp.api.providerId", "1")));
			
			countryServiceApi.update(countryDto);
		} catch (Exception e) {
			result.setStatus(ActionStatusEnum.FAIL);
			result.setMessage(e.getMessage());
		}

		return result;
	}

}
