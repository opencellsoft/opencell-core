package org.meveo.api.rest.category;

import java.util.List;

import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;

import org.meveo.api.rest.security.RSSecured;
import org.tmf.dsmapi.catalog.resource.category.Category;

//@Path("/categoryManagement/category")
//@Consumes({ MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML })
//@Produces({ MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML })
//@RSSecured
//public interface CategoryRs {
//
//	@GET
//	@Path("/")
//	List<Category> findAll();
//	
//	@GET
//	@Path("{id}")
//	Category find(@PathParam("id")String id);
//
//}
