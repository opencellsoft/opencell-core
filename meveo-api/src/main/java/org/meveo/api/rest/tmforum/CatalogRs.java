package org.meveo.api.rest.tmforum;

import java.util.List;

import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import org.meveo.api.rest.security.RSSecured;
import org.tmf.dsmapi.catalog.resource.category.Category;
import org.tmf.dsmapi.catalog.resource.product.ProductOffering;
import org.tmf.dsmapi.catalog.resource.product.ProductSpecification;

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

	@GET
	@Path("/productOffering")
	List<ProductOffering> findProductOfferings();
	
	@GET
	@Path("/productOffering/{id}")
	Response findProductOfferingById(@PathParam("id") String id);

	@GET
	@Path("/productSpecification")
	List<ProductSpecification> findProductSpecifications();

	@GET
	@Path("/productSpecification/{id}")
	Response findProductSpecificationById(@PathParam("id") String id);
}
