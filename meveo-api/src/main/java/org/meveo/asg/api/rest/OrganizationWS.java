package org.meveo.asg.api.rest;

import javax.enterprise.context.RequestScoped;
import javax.inject.Inject;
import javax.ws.rs.Consumes;
import javax.ws.rs.DELETE;
import javax.ws.rs.POST;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;

import org.meveo.admin.exception.AccountAlreadyExistsException;
import org.meveo.api.ActionStatus;
import org.meveo.api.ActionStatusEnum;
import org.meveo.api.MeveoApiErrorCode;
import org.meveo.api.dto.OrganizationDto;
import org.meveo.api.exception.MeveoApiException;
import org.meveo.api.exception.MissingParameterException;
import org.meveo.api.exception.ParentSellerDoesNotExistsException;
import org.meveo.api.exception.SellerAlreadyExistsException;
import org.meveo.api.exception.SellerDoesNotExistsException;
import org.meveo.api.exception.TradingCountryDoesNotExistsException;
import org.meveo.api.exception.TradingCurrencyDoesNotExistsException;
import org.meveo.asg.api.OrganizationServiceApi;
import org.meveo.asg.api.model.EntityCodeEnum;
import org.meveo.commons.utils.ParamBean;
import org.meveo.util.MeveoParamBean;

/**
 * @author Edward P. Legaspi
 * @since Oct 11, 2013
 **/
@Path("/asg/organization")
@RequestScoped
@Consumes({ MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML })
@Produces({ MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML })
public class OrganizationWS {

	@Inject
	@MeveoParamBean
	protected ParamBean paramBean;

	@Inject
	protected OrganizationServiceApi organizationServiceApi;

	@POST
	@Path("/")
	public ActionStatus create(OrganizationDto orgDto) {
		ActionStatus result = new ActionStatus(ActionStatusEnum.SUCCESS, "");

		String organizationId = orgDto.getOrganizationId();
		try {
			orgDto.setCurrentUserId(Long.valueOf(paramBean.getProperty(
					"asp.api.userId", "1")));
			orgDto.setProviderId(Long.valueOf(paramBean.getProperty(
					"asp.api.providerId", "1")));

			organizationServiceApi.create(orgDto);
		} catch (ParentSellerDoesNotExistsException e) {
			result.setErrorCode(MeveoApiErrorCode.PARENT_SELLER_DOES_NOT_EXISTS);
			result.setStatus(ActionStatusEnum.FAIL);
			result.setMessage(e.getMessage());
		} catch (TradingCurrencyDoesNotExistsException e) {
			result.setErrorCode(MeveoApiErrorCode.TRADING_CURRENCY_DOES_NOT_EXISTS);
			result.setStatus(ActionStatusEnum.FAIL);
			result.setMessage(e.getMessage());
		} catch (TradingCountryDoesNotExistsException e) {
			result.setErrorCode(MeveoApiErrorCode.TRADING_COUNTRY_DOES_NOT_EXISTS);
			result.setStatus(ActionStatusEnum.FAIL);
			result.setMessage(e.getMessage());
		} catch (SellerAlreadyExistsException e) {
			result.setErrorCode(MeveoApiErrorCode.SELLER_ALREADY_EXISTS);
			result.setStatus(ActionStatusEnum.FAIL);
			result.setMessage(e.getMessage());
		} catch (MissingParameterException e) {
			result.setErrorCode(MeveoApiErrorCode.MISSING_PARAMETER);
			result.setStatus(ActionStatusEnum.FAIL);
			result.setMessage(e.getMessage());
		} catch (MeveoApiException e) {
			result.setStatus(ActionStatusEnum.FAIL);
			result.setMessage(e.getMessage());
		} catch (AccountAlreadyExistsException e) {
			result.setStatus(ActionStatusEnum.FAIL);
			result.setMessage(e.getMessage());
		}

		if (result.getStatus() == ActionStatusEnum.FAIL) {
			organizationServiceApi.removeAsgMapping(organizationId,
					EntityCodeEnum.ORG);
		}

		return result;
	}

	@PUT
	@Path("/")
	public ActionStatus update(OrganizationDto orgDto) {
		ActionStatus result = new ActionStatus(ActionStatusEnum.SUCCESS, "");

		try {
			orgDto.setCurrentUserId(Long.valueOf(paramBean.getProperty(
					"asp.api.userId", "1")));
			orgDto.setProviderId(Long.valueOf(paramBean.getProperty(
					"asp.api.providerId", "1")));

			organizationServiceApi.update(orgDto);
		} catch (TradingCurrencyDoesNotExistsException e) {
			result.setErrorCode(MeveoApiErrorCode.TRADING_CURRENCY_DOES_NOT_EXISTS);
			result.setStatus(ActionStatusEnum.FAIL);
			result.setMessage(e.getMessage());
		} catch (SellerDoesNotExistsException e) {
			result.setErrorCode(MeveoApiErrorCode.SELLER_DOES_NOT_EXISTS);
			result.setStatus(ActionStatusEnum.FAIL);
			result.setMessage(e.getMessage());
		} catch (MissingParameterException e) {
			result.setErrorCode(MeveoApiErrorCode.MISSING_PARAMETER);
			result.setStatus(ActionStatusEnum.FAIL);
			result.setMessage(e.getMessage());
		} catch (Exception e) {
			result.setStatus(ActionStatusEnum.FAIL);
			result.setMessage(e.getMessage());
		}

		return result;
	}

	@DELETE
	@Path("/{organizationId}")
	public ActionStatus remove(
			@PathParam("organizationId") String organizationId) {
		ActionStatus result = new ActionStatus(ActionStatusEnum.SUCCESS, "");

		try {
			organizationServiceApi.remove(organizationId, Long
					.valueOf(paramBean.getProperty("asp.api.providerId", "1")));
		} catch (SellerDoesNotExistsException e) {
			result.setErrorCode(MeveoApiErrorCode.SELLER_DOES_NOT_EXISTS);
			result.setStatus(ActionStatusEnum.FAIL);
			result.setMessage(e.getMessage());
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
