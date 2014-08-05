package org.meveo.api.rest;

import javax.inject.Inject;
import javax.ws.rs.GET;
import javax.ws.rs.Path;

import org.meveo.api.ActionStatus;
import org.meveo.api.ActionStatusEnum;
import org.meveo.api.rest.security.WSUser;
import org.meveo.commons.utils.ParamBean;
import org.meveo.model.admin.User;

/**
 * @author Edward P. Legaspi
 **/
public abstract class BaseWS {

	protected ParamBean paramBean = ParamBean.getInstance();

	protected final String RESPONSE_DELIMITER = " - ";

	
	@Inject
	@WSUser
	protected User currentUser;
	
	@GET
	@Path("/version")
	public ActionStatus index() {
		ActionStatus result = new ActionStatus(ActionStatusEnum.SUCCESS,
				"MEVEO API Rest Web Service V1.0");

		return result;
	}

}
