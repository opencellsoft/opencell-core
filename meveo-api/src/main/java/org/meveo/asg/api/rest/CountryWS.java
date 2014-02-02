package org.meveo.asg.api.rest;

import javax.enterprise.context.RequestScoped;
import javax.inject.Inject;
import javax.persistence.EntityManager;
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

import org.meveo.api.ActionStatus;
import org.meveo.api.ActionStatusEnum;
import org.meveo.api.dto.CountryDto;
import org.meveo.api.rest.response.CountryResponse;
import org.meveo.asg.api.model.EntityCodeEnum;
import org.meveo.asg.api.service.AsgIdMappingService;
import org.meveo.util.MeveoJpaForJobs;

/**
 * @author Edward P. Legaspi
 * @since Oct 7, 2013
 **/
@Path("/asg/country")
@RequestScoped
@Consumes({ MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML })
@Produces({ MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML })
public class CountryWS extends org.meveo.api.rest.CountryWS {

	@Inject
	private AsgIdMappingService asgIdMappingService;

	@Inject
	@MeveoJpaForJobs
	private EntityManager em;

	@GET
	@Path("/index")
	@Override
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
			String asgCountryCode = countryDto.getCountryCode();
			countryDto.setCountryCode(asgIdMappingService.getNewCode(em,
					asgCountryCode, EntityCodeEnum.C));
			result = super.create(countryDto);

			if (result.getStatus() == ActionStatusEnum.FAIL) {
				asgIdMappingService.removeByCodeAndType(em, asgCountryCode,
						EntityCodeEnum.C);
			}
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
			countryCode = asgIdMappingService.getMeveoCode(em, countryCode,
					EntityCodeEnum.C);
			result = super.find(countryCode);
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

		try {
			String asgCountryCode = countryCode;
			countryCode = asgIdMappingService.getMeveoCode(em, countryCode,
					EntityCodeEnum.C);
			asgIdMappingService.removeByCodeAndType(em, asgCountryCode,
					EntityCodeEnum.C);
			result = super.remove(countryCode, currencyCode);
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
			countryDto.setCountryCode(asgIdMappingService.getMeveoCode(em,
					countryDto.getCountryCode(), EntityCodeEnum.C));
			result = super.update(countryDto);
		} catch (Exception e) {
			result.setStatus(ActionStatusEnum.FAIL);
			result.setMessage(e.getMessage());
		}

		return result;
	}

}
