package org.meveo.apiv2.catalog.resource;

import org.meveo.admin.util.pagination.PaginationConfiguration;
import org.meveo.apiv2.catalog.service.DiscountPlanApiService;
import org.meveo.apiv2.generic.common.LinkGenerator;

import javax.ejb.Stateless;
import javax.inject.Inject;
import javax.ws.rs.NotFoundException;
import javax.ws.rs.core.Link;
import javax.ws.rs.core.Response;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

@Stateless
public class DiscountPlanResourceImpl implements DiscountPlanResource {

	@Inject
	private DiscountPlanApiService discountPlanApiService;

	private static final String ENTITY_NAME = "DiscountPlan";

	@Override
	public Response getAll(Long offset, Long limit, String sort, String orderBy, String filter) {
		Set<String> genericFields = null;
		Set<String> nestedEntities = new HashSet<>();
		nestedEntities.add("discountPlanItems");
		PaginationConfiguration paginationConfiguration = new PaginationConfiguration(offset.intValue(), limit.intValue(), null, filter, null, null, null);
		String jsonEntity = discountPlanApiService.findPaginatedRecords(paginationConfiguration, genericFields, nestedEntities);
		return Response.ok().entity(jsonEntity).links(buildPaginatedResourceLink(ENTITY_NAME)).build();
	}

	@Override
	public Response get(Long id) {
		Set<String> genericFields = null;
		Set<String> nestedEntities = new HashSet<>();
		nestedEntities.add("discountPlanItems");
		PaginationConfiguration paginationConfiguration = new PaginationConfiguration(null, null);
		return discountPlanApiService.findById(id, paginationConfiguration, genericFields, nestedEntities)
				.map(fetchedEntity -> Response.ok().entity(fetchedEntity).links(buildSingleResourceLink(ENTITY_NAME, id)).build())
				.orElseThrow(() -> new NotFoundException("entity discount plan with id " + id + " not found."));
	}

	@Override
	public Response create(String dto) {
		return discountPlanApiService.create(dto)
				.map(entityId -> Response.ok().entity(Collections.singletonMap("id", entityId)).links(buildSingleResourceLink(ENTITY_NAME, entityId)).build()).get();
	}

	@Override
	public Response update(Long id, String dto) {
		return discountPlanApiService.update(id, dto)
				.map(entityId -> Response.ok().entity(Collections.singletonMap("id", entityId)).links(buildSingleResourceLink(ENTITY_NAME, entityId)).build()).get();
	}

	@Override
	public Response delete(Long id) {
		return Response.ok().entity(discountPlanApiService.delete(id)).links(buildSingleResourceLink(ENTITY_NAME, id)).build();
	}

	@Override
	public Response getDiscountPlanItems(Long id, Long offset, Long limit, String sort, String orderBy, String filter) {
		Map<String, Object> filters = new HashMap<>();
		filters.put("discountPlan.id", id);
		PaginationConfiguration paginationConfiguration = new PaginationConfiguration(offset.intValue(), limit.intValue(), filters, filter, null, null, null);
		String jsonEntity = discountPlanApiService.findPaginatedDiscountPlanItems(paginationConfiguration, null, null);
		return Response.ok().entity(jsonEntity).links(buildPaginatedResourceLink("DiscountPlanItem")).build();
	}

	@Override
	public Response getDiscountPlanItem(Long id, Long idItem) {
		Map<String, Object> filters = new HashMap<>();
		filters.put("discountPlan.id", id);
		PaginationConfiguration paginationConfiguration = new PaginationConfiguration(0, 50, filters, null, null, null, null);
		return discountPlanApiService.findDiscountPlanItemById(idItem, paginationConfiguration, null, null)
				.map(fetchedEntity -> Response.ok().entity(fetchedEntity).links(buildSingleResourceLink("DiscountPlanItem", idItem)).build())
				.orElseThrow(() -> new NotFoundException("entity discount plan item with id " + idItem + " not found."));
	}

	@Override
	public Response createItem(Long id, String dto) {
		return discountPlanApiService.createItem(dto)
				.map(entityId -> Response.ok().entity(Collections.singletonMap("id", entityId)).links(buildSingleResourceLink(ENTITY_NAME, entityId)).build()).get();

	}

	@Override
	public Response updateItem(Long id, String dto) {
		return discountPlanApiService.updateItem(id, dto)
				.map(entityId -> Response.ok().entity(Collections.singletonMap("id", entityId)).links(buildSingleResourceLink(ENTITY_NAME, entityId)).build()).get();

	}

	@Override
	public Response deleteItem(Long id) {
		return Response.ok().entity(discountPlanApiService.deleteItem(id)).links(buildSingleResourceLink(ENTITY_NAME, id)).build();
	}

	private Link buildPaginatedResourceLink(String entityName) {
		return new org.meveo.apiv2.generic.common.LinkGenerator.SelfLinkGenerator(DiscountPlanResource.class).withGetAction().withPostAction().withDeleteAction().build(entityName);
	}

	private Link buildSingleResourceLink(String entityName, Long entityId) {
		return new LinkGenerator.SelfLinkGenerator(DiscountPlanResource.class).withGetAction().withPostAction().withId(entityId).withDeleteAction().build(entityName);
	}

}
