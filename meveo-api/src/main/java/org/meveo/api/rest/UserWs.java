package org.meveo.api.rest;

import javax.enterprise.context.RequestScoped;
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

import org.meveo.api.MeveoApiErrorCode;
import org.meveo.api.UserApi;
import org.meveo.api.dto.ActionStatus;
import org.meveo.api.dto.ActionStatusEnum;
import org.meveo.api.dto.UserDto;
import org.meveo.api.dto.response.GetUserResponse;
import org.meveo.api.exception.EntityAlreadyExistsException;
import org.meveo.api.exception.EntityDoesNotExistsException;
import org.meveo.api.exception.MissingParameterException;

/**
 * @author Edward P. Legaspi
 **/
@Path("/user")
@RequestScoped
@Consumes({ MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML })
@Produces({ MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML })
public class UserWs extends BaseWs {

	@Inject
	private UserApi userApi;

	@POST
	@Path("/")
	public ActionStatus create(UserDto postData) {
		ActionStatus result = new ActionStatus();

		try {
			userApi.create(postData, getCurrentUser());
		} catch (MissingParameterException e) {
			result.setErrorCode(MeveoApiErrorCode.MISSING_PARAMETER);
			result.setStatus(ActionStatusEnum.FAIL);
			result.setMessage(e.getMessage());
		} catch (EntityDoesNotExistsException e) {
			result.setErrorCode(MeveoApiErrorCode.ENTITY_DOES_NOT_EXISTS_EXCEPTION);
			result.setStatus(ActionStatusEnum.FAIL);
			result.setMessage(e.getMessage());
		} catch (EntityAlreadyExistsException e) {
			result.setErrorCode(MeveoApiErrorCode.ENTITY_ALREADY_EXISTS_EXCEPTION);
			result.setStatus(ActionStatusEnum.FAIL);
			result.setMessage(e.getMessage());
		} catch (Exception e) {
			result.setStatus(ActionStatusEnum.FAIL);
			result.setMessage(e.getMessage());
		}

		return result;
	}

	@PUT
	@Path("/")
	public ActionStatus update(UserDto postData) {
		ActionStatus result = new ActionStatus();

		try {
			userApi.update(postData, getCurrentUser());
		} catch (MissingParameterException e) {
			result.setErrorCode(MeveoApiErrorCode.MISSING_PARAMETER);
			result.setStatus(ActionStatusEnum.FAIL);
			result.setMessage(e.getMessage());
		} catch (EntityDoesNotExistsException e) {
			result.setErrorCode(MeveoApiErrorCode.ENTITY_DOES_NOT_EXISTS_EXCEPTION);
			result.setStatus(ActionStatusEnum.FAIL);
			result.setMessage(e.getMessage());
		} catch (Exception e) {
			result.setStatus(ActionStatusEnum.FAIL);
			result.setMessage(e.getMessage());
		}

		return result;
	}

	@DELETE
	@Path("/{username}")
	public ActionStatus remove(@PathParam("username") String username) {
		ActionStatus result = new ActionStatus();

		try {
			userApi.remove(username);
		} catch (EntityDoesNotExistsException e) {
			result.setErrorCode(MeveoApiErrorCode.ENTITY_DOES_NOT_EXISTS_EXCEPTION);
			result.setStatus(ActionStatusEnum.FAIL);
			result.setMessage(e.getMessage());
		} catch (Exception e) {
			result.setStatus(ActionStatusEnum.FAIL);
			result.setMessage(e.getMessage());
		}

		return result;
	}

	@GET
	@Path("/")
	public GetUserResponse find(@QueryParam("username") String username) {
		GetUserResponse result = new GetUserResponse();

		try {
			result.setUser(userApi.find(username));
		} catch (EntityDoesNotExistsException e) {
			result.getActionStatus().setErrorCode(
					MeveoApiErrorCode.ENTITY_DOES_NOT_EXISTS_EXCEPTION);
			result.getActionStatus().setStatus(ActionStatusEnum.FAIL);
			result.getActionStatus().setMessage(e.getMessage());
		} catch (Exception e) {
			result.getActionStatus().setStatus(ActionStatusEnum.FAIL);
			result.getActionStatus().setMessage(e.getMessage());
		}

		return result;
	}

}
