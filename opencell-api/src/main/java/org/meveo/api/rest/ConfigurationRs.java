package org.meveo.api.rest;

import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;

import org.meveo.api.dto.ActionStatus;
import org.meveo.api.dto.response.GetConfigurationResponse;

/**
 * Manages system configuration.
 * 
 * @author Edward P. Legaspi
 * @author Khalid HORRI
 * @lastModifiedVersion 7.1
 */
@Path("/configurations")
@Consumes({ MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML })
@Produces({ MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML })
public interface ConfigurationRs extends IBaseRs {

	/**
	 * Converts system properties into json string.
	 * @return system properties
	 */
	@GET
	GetConfigurationResponse systemProperties();

}
