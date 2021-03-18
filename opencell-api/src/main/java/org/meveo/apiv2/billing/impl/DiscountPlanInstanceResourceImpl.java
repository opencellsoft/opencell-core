package org.meveo.apiv2.billing.impl;

import org.meveo.admin.util.pagination.PaginationConfiguration;
import org.meveo.apiv2.billing.resource.DiscountPlanInstanceResource;
import org.meveo.apiv2.billing.service.DiscountPlanInstanceApiService;
import org.meveo.apiv2.catalog.resource.DiscountPlanResource;
import org.meveo.apiv2.catalog.service.DiscountPlanApiService;
import org.meveo.apiv2.generic.GenericPagingAndFiltering;
import org.meveo.apiv2.generic.common.LinkGenerator;
import org.meveo.apiv2.generic.core.GenericRequestMapper;
import org.meveo.apiv2.generic.services.PersistenceServiceHelper;
import org.meveo.model.billing.BillingAccount;
import org.meveo.model.billing.Subscription;
import org.meveo.model.catalog.DiscountPlan;

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
public class DiscountPlanInstanceResourceImpl implements DiscountPlanInstanceResource {

	@Inject
	private DiscountPlanInstanceApiService discountPlanInstanceApiService;

	private static final String ENTITY_NAME = "DiscountPlanInstance";

	private Link buildPaginatedResourceLink(String entityName) {
		return new LinkGenerator.SelfLinkGenerator(DiscountPlanResource.class).withGetAction().withPostAction().withDeleteAction().build(entityName);
	}

	private Link buildSingleResourceLink(String entityName, Long entityId) {
		return new LinkGenerator.SelfLinkGenerator(DiscountPlanResource.class).withGetAction().withPostAction().withId(entityId).withDeleteAction().build(entityName);
	}

	@Override
	public Response getDiscountPlanInstances(Long id, GenericPagingAndFiltering searchConfig) {
		Set<String> genericFields = null;
		Set<String> nestedEntities = new HashSet<>();
		if (searchConfig != null) {
			genericFields = searchConfig.getGenericFields();
			nestedEntities = searchConfig.getNestedEntities();
		}
		GenericRequestMapper genericRequestMapper = new GenericRequestMapper(DiscountPlan.class, PersistenceServiceHelper.getPersistenceService());
		PaginationConfiguration paginationConfiguration = genericRequestMapper.mapTo(searchConfig);
		paginationConfiguration.getFilters().put("billingAccount.id", id);
		String jsonEntity = discountPlanInstanceApiService.findPaginatedRecords(paginationConfiguration, genericFields, nestedEntities);
		return Response.ok().entity(jsonEntity).links(buildPaginatedResourceLink(ENTITY_NAME)).build();
	}

	@Override
	public Response getDiscountPlanInstance(Long id, Long idInsance) {
		Set<String> genericFields = null;
		Set<String> nestedEntities = new HashSet<>();
		PaginationConfiguration paginationConfiguration = new PaginationConfiguration(null, null);
		return discountPlanInstanceApiService.findById(idInsance, paginationConfiguration, genericFields, nestedEntities)
				.map(fetchedEntity -> Response.ok().entity(fetchedEntity).links(buildSingleResourceLink(ENTITY_NAME, id)).build())
				.orElseThrow(() -> new NotFoundException("entity discount plan with id " + id + " not found."));
	}

	@Override
	public Response create(Long id, String dto) {
		BillingAccount billingAccount = (BillingAccount) PersistenceServiceHelper.getPersistenceService(BillingAccount.class).findById(id);
		return discountPlanInstanceApiService.create(billingAccount, dto)
				.map(entityId -> Response.ok().entity(Collections.singletonMap("id", entityId)).links(buildSingleResourceLink(ENTITY_NAME, entityId)).build()).get();
	}

	@Override
	public Response update(Long billingAccountId, Long id, String dto) {
		BillingAccount billingAccount = (BillingAccount) PersistenceServiceHelper.getPersistenceService(BillingAccount.class).findById(id);
		return discountPlanInstanceApiService.update(billingAccount, id, dto)
				.map(entityId -> Response.ok().entity(Collections.singletonMap("id", entityId)).links(buildSingleResourceLink(ENTITY_NAME, entityId)).build()).get();
	}

	@Override
	public Response delete(Long billingAccountId, Long id) {
		BillingAccount billingAccount = (BillingAccount) PersistenceServiceHelper.getPersistenceService(BillingAccount.class).findById(id);
		return Response.ok().entity(discountPlanInstanceApiService.delete(billingAccount, id)).links(buildSingleResourceLink(ENTITY_NAME, id)).build();
	}

	@Override
	public Response expire(Long billingAccountId, Long id) {
		BillingAccount billingAccount = (BillingAccount) PersistenceServiceHelper.getPersistenceService(BillingAccount.class).findById(id);
		return discountPlanInstanceApiService.expire(billingAccount, id)
				.map(entityId -> Response.ok().entity(Collections.singletonMap("id", entityId)).links(buildSingleResourceLink(ENTITY_NAME, entityId)).build()).get();
	}

	@Override
	public Response getDiscountPlanInstancesBySubscription(Long id, GenericPagingAndFiltering searchConfig) {
		Set<String> genericFields = null;
		Set<String> nestedEntities = null;
		if (searchConfig != null) {
			genericFields = searchConfig.getGenericFields();
			nestedEntities = searchConfig.getNestedEntities();
		}
		GenericRequestMapper genericRequestMapper = new GenericRequestMapper(DiscountPlan.class, PersistenceServiceHelper.getPersistenceService());
		PaginationConfiguration paginationConfiguration = genericRequestMapper.mapTo(searchConfig);
		paginationConfiguration.getFilters().put("subscription.id", id);
		String jsonEntity = discountPlanInstanceApiService.findPaginatedRecords(paginationConfiguration, genericFields, nestedEntities);
		return Response.ok().entity(jsonEntity).links(buildPaginatedResourceLink(ENTITY_NAME)).build();
	}

	@Override
	public Response getDiscountPlanInstanceBySubscription(Long id, Long idInsance) {
		Set<String> genericFields = null;
		Set<String> nestedEntities = new HashSet<>();
		PaginationConfiguration paginationConfiguration = new PaginationConfiguration(null, null);
		return discountPlanInstanceApiService.findById(idInsance, paginationConfiguration, genericFields, nestedEntities)
				.map(fetchedEntity -> Response.ok().entity(fetchedEntity).links(buildSingleResourceLink(ENTITY_NAME, id)).build())
				.orElseThrow(() -> new NotFoundException("entity discount plan with id " + id + " not found."));
	}

	@Override
	public Response createBySubscription(Long id, String dto) {
		Subscription subscription = (Subscription) PersistenceServiceHelper.getPersistenceService(Subscription.class).findById(id);
		return discountPlanInstanceApiService.create(subscription, dto)
				.map(entityId -> Response.ok().entity(Collections.singletonMap("id", entityId)).links(buildSingleResourceLink(ENTITY_NAME, entityId)).build()).get();
	}

	@Override
	public Response updateBySubscription(Long subscriptionId, Long id, String dto) {
		Subscription subscription = (Subscription) PersistenceServiceHelper.getPersistenceService(Subscription.class).findById(id);
		return discountPlanInstanceApiService.update(subscription, id, dto)
				.map(entityId -> Response.ok().entity(Collections.singletonMap("id", entityId)).links(buildSingleResourceLink(ENTITY_NAME, entityId)).build()).get();
	}

	@Override
	public Response deleteBySubscription(Long subscriptionId, Long id) {
		Subscription subscription = (Subscription) PersistenceServiceHelper.getPersistenceService(Subscription.class).findById(id);
		return Response.ok().entity(discountPlanInstanceApiService.delete(subscription, id)).links(buildSingleResourceLink(ENTITY_NAME, id)).build();
	}

	@Override
	public Response expireBySubscription(Long subscriptionId, Long id) {
		Subscription subscription = (Subscription) PersistenceServiceHelper.getPersistenceService(Subscription.class).findById(id);
		return discountPlanInstanceApiService.expire(subscription, id)
				.map(entityId -> Response.ok().entity(Collections.singletonMap("id", entityId)).links(buildSingleResourceLink(ENTITY_NAME, entityId)).build()).get();
	}
}
