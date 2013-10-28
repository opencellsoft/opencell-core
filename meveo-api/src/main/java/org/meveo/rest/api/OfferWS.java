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

import org.meveo.api.OfferServiceApi;
import org.meveo.api.dto.OfferDto;
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
@Path("/offer")
@Consumes({ MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML })
@Produces({ MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML })
public class OfferWS {

	@Inject
	@MeveoParamBean
	private ParamBean paramBean;

	@Inject
	private OfferServiceApi offerServiceApi;

	@POST
	@Path("/")
	public ActionStatus create(OfferDto offerDto) {
		ActionStatus result = new ActionStatus(ActionStatusEnum.SUCCESS, "");

		try {
			offerDto.setCurrentUserId(Long.valueOf(paramBean.getProperty(
					"asp.api.userId", "1")));
			offerDto.setProviderId(Long.valueOf(paramBean.getProperty(
					"asp.api.providerId", "1")));

			offerServiceApi.create(offerDto);
		} catch (MeveoApiException e) {
			result.setStatus(ActionStatusEnum.FAIL);
			result.setMessage(e.getMessage());
		}

		return result;
	}

	@PUT
	@Path("/")
	public ActionStatus update(OfferDto offerDto) {
		ActionStatus result = new ActionStatus(ActionStatusEnum.SUCCESS, "");

		try {
			offerDto.setCurrentUserId(Long.valueOf(paramBean.getProperty(
					"asp.api.userId", "1")));
			offerDto.setProviderId(Long.valueOf(paramBean.getProperty(
					"asp.api.providerId", "1")));

			offerServiceApi.update(offerDto);
		} catch (Exception e) {
			result.setStatus(ActionStatusEnum.FAIL);
			result.setMessage(e.getMessage());
		}

		return result;
	}
	
	@DELETE
	@Path("/{offerId}")
	public ActionStatus remove(@PathParam("offerId") String offerId) {
		ActionStatus result = new ActionStatus(ActionStatusEnum.SUCCESS, "");

		try {
			offerServiceApi.remove(Long.valueOf(paramBean.getProperty(
					"asp.api.providerId", "1")), offerId);
		} catch (Exception e) {
			result.setStatus(ActionStatusEnum.FAIL);
			result.setMessage(e.getMessage());
		}

		return result;
	}

}
