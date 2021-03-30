/**
 *
 */
package org.meveo.apiv2.billing.service;

import org.meveo.admin.exception.BusinessException;
import org.meveo.admin.util.pagination.PaginationConfiguration;
import org.meveo.apiv2.generic.core.mapper.JsonGenericMapper;
import org.meveo.apiv2.generic.services.GenericApiAlteringService;
import org.meveo.apiv2.generic.services.GenericApiLoadService;
import org.meveo.apiv2.generic.services.GenericApiPersistenceDelegate;
import org.meveo.apiv2.generic.services.PersistenceServiceHelper;
import org.meveo.model.IDiscountable;
import org.meveo.model.IEntity;
import org.meveo.model.billing.BillingAccount;
import org.meveo.model.billing.DiscountPlanInstance;
import org.meveo.model.billing.DiscountPlanInstanceStatusEnum;
import org.meveo.model.billing.Subscription;
import org.meveo.model.catalog.DiscountPlan;
import org.meveo.model.catalog.DiscountPlanItem;
import org.meveo.model.catalog.DiscountPlanStatusEnum;
import org.meveo.service.billing.impl.BillingAccountService;
import org.meveo.service.billing.impl.SubscriptionService;
import org.meveo.service.catalog.impl.DiscountPlanService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.annotation.PostConstruct;
import javax.ejb.Stateless;
import javax.inject.Inject;
import javax.ws.rs.NotFoundException;
import java.util.Collections;
import java.util.Map;
import java.util.Optional;
import java.util.Set;

import static org.meveo.apiv2.generic.ValidationUtils.checkDto;
import static org.meveo.apiv2.generic.ValidationUtils.checkId;

@Stateless
public class DiscountPlanInstanceApiService {

	private static final Logger log = LoggerFactory.getLogger(DiscountPlanInstanceApiService.class);

	@Inject
	private GenericApiAlteringService genericApiAlteringService;

	@Inject
	private GenericApiLoadService loadService;

	@Inject
	private GenericApiPersistenceDelegate persistenceDelegate;

	@Inject
	private BillingAccountService billingAccountService;

	@Inject
	private SubscriptionService subscriptionService;

	@Inject
	private DiscountPlanService discountPlanService;

	public String findPaginatedRecords(PaginationConfiguration searchConfig, Set<String> genericFields, Set<String> nestedEntities) {
		return loadService.findPaginatedRecords(true, DiscountPlanInstance.class, searchConfig, genericFields, nestedEntities, 1L);
	}

	public String findPaginatedDiscountPlanItems(PaginationConfiguration searchConfig, Set<String> genericFields, Set<String> nestedEntities) {
		return loadService.findPaginatedRecords(true, DiscountPlanItem.class, searchConfig, genericFields, nestedEntities, 1L);
	}

	public Optional<String> findById(Long id, PaginationConfiguration searchConfig, Set<String> genericFields, Set<String> nestedEntities) {
		return loadService.findByClassNameAndId(true, DiscountPlanInstance.class, id, searchConfig, genericFields, nestedEntities, 1L);
	}

	public Optional<Long> create(IDiscountable discountable, String dto) {
		checkDto(dto);
		DiscountPlanInstance entityToCreate = (DiscountPlanInstance) JsonGenericMapper.Builder.getBuilder().build().parseFromJson(dto, DiscountPlanInstance.class);
		genericApiAlteringService.refreshEntityWithDotFields(JsonGenericMapper.Builder.getBuilder().build().readValue(dto, Map.class), entityToCreate, entityToCreate);
		DiscountPlan dp = discountPlanService.findById(entityToCreate.getDiscountPlan().getId());
		IEntity entity = null;
		if (discountable instanceof BillingAccount) {
			entity = billingAccountService.instantiateDiscountPlan((BillingAccount) discountable, dp);
		} else {
			entity = subscriptionService.instantiateDiscountPlan((Subscription) discountable, dp);
		}
		return Optional.ofNullable((Long) entity.getId());
	}

	public Optional<Long> update(IEntity discountable, Long id, String dto) {
		checkId(id).checkDto(dto);
		DiscountPlanInstance entity = (DiscountPlanInstance) PersistenceServiceHelper.getPersistenceService(DiscountPlanInstance.class).findById(id);

		if (entity == null) {
			throw new NotFoundException("entity discount plan instance with id " + id + " not found.");
		}
		if (discountable instanceof BillingAccount && !discountable.getId().equals(entity.getBillingAccount().getId())) {
			log.error("The billing account with the Id: {} is invalid for discount plan instance with id: {}", discountable, id);
			throw new BusinessException("The billing account with the Id: " + discountable.getId() + " is invalid for discount plan instance with id: " + id);
		}
		if (discountable instanceof Subscription && !discountable.getId().equals(entity.getSubscription().getId())) {
			log.error("The Subscription with the Id: {} is invalid for discount plan instance with id: {}", discountable, id);
			throw new BusinessException("The Subscription with the Id: " + discountable.getId() + " is invalid for discount plan instance with id: " + id);
		}
		if (entity.getStatus() != null && !entity.getStatus().equals(DiscountPlanInstanceStatusEnum.APPLIED)) {
			log.error("Only status APPLIED is allowed to update a discount plan instance: {}", entity.getId());
			throw new BusinessException("Only status APPLIED is allowed to update a discount plan instance: " + entity.getId());
		}
		JsonGenericMapper jsonGenericMapper = JsonGenericMapper.Builder.getBuilder().build();
		genericApiAlteringService.refreshEntityWithDotFields(jsonGenericMapper.readValue(dto, Map.class), entity, jsonGenericMapper.parseFromJson(dto, entity.getClass()));
		IEntity updatedEntity = persistenceDelegate.update(DiscountPlanInstance.class, entity);
		return Optional.ofNullable((Long) updatedEntity.getId());
	}

	public String delete(IEntity discountable, Long id) {
		checkId(id);
		DiscountPlanInstance entity = (DiscountPlanInstance) PersistenceServiceHelper.getPersistenceService(DiscountPlanInstance.class).findById(id);
		if (entity == null) {
			throw new NotFoundException("entity DiscountPlanInstance with id " + id + " not found.");
		}
		if (discountable instanceof BillingAccount && !discountable.getId().equals(entity.getBillingAccount().getId())) {
			log.error("The billing account with the Id: {} is invalid for discount plan instance with id: {}", discountable, id);
			throw new BusinessException("The billing account with the Id: " + discountable.getId() + " is invalid for discount plan instance with id: " + id);
		}
		if (discountable instanceof Subscription && !discountable.getId().equals(entity.getSubscription().getId())) {
			log.error("The Subscription with the Id: {} is invalid for discount plan instance with id: {}", discountable, id);
			throw new BusinessException("The Subscription with the Id: " + discountable.getId() + " is invalid for discount plan instance with id: " + id);
		}
		persistenceDelegate.remove(DiscountPlanInstance.class, entity);
		return JsonGenericMapper.Builder.getBuilder().withNestedEntities(null).build().toJson(null, DiscountPlanInstance.class, entity);
	}

	public Optional<Long> expire(IEntity discountable, Long id) {
		DiscountPlanInstance entity = (DiscountPlanInstance) PersistenceServiceHelper.getPersistenceService(DiscountPlanInstance.class).findById(id);
		if (entity == null) {
			throw new NotFoundException("entity discount plan instance with id " + id + " not found.");
		}
		if (discountable instanceof BillingAccount && !discountable.getId().equals(entity.getBillingAccount().getId())) {
			log.error("The billing account with the Id: {} is invalid for discount plan instance with id: {}", discountable, id);
			throw new BusinessException("The billing account with the Id: " + discountable.getId() + " is invalid for discount plan instance with id: " + id);
		}
		if (discountable instanceof Subscription && !discountable.getId().equals(entity.getSubscription().getId())) {
			log.error("The Subscription with the Id: {} is invalid for discount plan instance with id: {}", discountable, id);
			throw new BusinessException("The Subscription with the Id: " + discountable.getId() + " is invalid for discount plan instance with id: " + id);
		}
		entity.setStatus(DiscountPlanInstanceStatusEnum.EXPIRED);
		IEntity updatedEntity = persistenceDelegate.update(DiscountPlanInstance.class, entity);
		return Optional.ofNullable((Long) updatedEntity.getId());
	}
}
