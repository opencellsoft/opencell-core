package org.meveo.api.tmforum.rest;

import java.util.List;

import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import org.meveo.api.rest.security.RSSecured;
import org.meveo.model.catalog.OfferTemplate;
import org.tmf.dsmapi.catalog.resource.category.Category;

@Path("/catalogManagement")
@Consumes({ MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML })
@Produces({ MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML })
@RSSecured
public interface CatalogRs {

	@GET
	@Path("/category")
	List<Category> findCategories();

	@GET
	@Path("/category/{id}")
	Response findCategoryById(@PathParam("id") String id);

	@POST
	@Path("/productOffering")
	Response createProductOffering(OfferTemplate offer);

	@GET
	@Path("/productOffering/{id}")
	Response findProductOfferingById(@PathParam("id") String id);

	@POST
	@Path("/productSpecification")
	Response createProductSpecification(OfferTemplate offer);

	@GET
	@Path("/productSpecification/{id}")
	Response findProductSpecificationById(@PathParam("id") String id);
}
