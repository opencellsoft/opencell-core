package org.meveo.rest.environment;

import javax.ejb.Stateless;
import javax.ws.rs.Consumes;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;

/**
 * @author Edward P. Legaspi
 * @since Oct 11, 2013
 **/
@Stateless
@Path("/offer")
@Consumes({ MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML })
@Produces({ MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML })
public class OfferWS {

}
