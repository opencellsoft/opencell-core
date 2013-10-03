package org.meveo.rest;

import java.util.ArrayList;
import java.util.List;

import javax.inject.Inject;
import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.MediaType;

import org.meveo.commons.utils.ParamBean;
import org.meveo.commons.utils.StringUtils;
import org.meveo.service.admin.impl.CountryService;
import org.meveo.service.admin.impl.CurrencyService;
import org.meveo.util.MeveoParamBean;

/**
 * @author Edward P. Legaspi
 * @since Oct 2, 2013
 **/
@Path("/")
@Consumes(MediaType.APPLICATION_JSON)
@Produces(MediaType.APPLICATION_JSON)
public class MeveoAPI {

	@Inject
	private CountryService countryService;

	@Inject
	private CurrencyService currencyService;

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
	public ActionStatus createCountry(
			@QueryParam("countryCode") String countryCode,
			@QueryParam("name") String name,
			@QueryParam("currencyCode") String currencyCode) {

		ActionStatus result = new ActionStatus(ActionStatusEnum.SUCCESS, "");

		try {
			if (!StringUtils.isBlank(countryCode) && !StringUtils.isBlank(name)
					&& !StringUtils.isBlank(currencyCode)) {
				if (countryService.findByCode(countryCode) != null) {
					result.setStatus(ActionStatusEnum.FAIL.getStatus());
					result.setMessage("Country code already exist.");
				} else {
					if (currencyService.findByCode(currencyCode) != null) {
						countryService.create(countryCode, name, currencyCode,
								Long.valueOf(paramBean.getProperty(
										"asp.api.userId", "1")));
					} else {
						result.setStatus(ActionStatusEnum.FAIL.getStatus());
						result.setMessage("Currency code does not exist.");
					}
				}
			} else {
				StringBuilder sb = new StringBuilder(
						"The following parameters are required ");
				List<String> missingFields = new ArrayList<String>();

				if (StringUtils.isBlank(countryCode)) {
					missingFields.add("countryCode");
				}
				if (StringUtils.isBlank(name)) {
					missingFields.add("name");
				}
				if (StringUtils.isBlank(currencyCode)) {
					missingFields.add("currencyCode");
				}
				sb.append(org.apache.commons.lang.StringUtils.join(
						missingFields.toArray(), ", "));
				sb.append(".");

				result.setStatus(ActionStatusEnum.FAIL.getStatus());
				result.setMessage(sb.toString());
			}
		} catch (Exception e) {
			result.setStatus(ActionStatusEnum.FAIL.getStatus());
			result.setMessage(e.getMessage());
		}

		return result;
	}

}
