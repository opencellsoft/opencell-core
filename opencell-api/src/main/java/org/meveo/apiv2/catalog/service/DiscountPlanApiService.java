/**
 *
 */
package org.meveo.apiv2.catalog.service;

import static org.meveo.apiv2.generic.ValidationUtils.checkDto;
import static org.meveo.apiv2.generic.ValidationUtils.checkId;

import java.security.InvalidParameterException;
import java.util.Collections;
import java.util.Date;
import java.util.Map;
import java.util.Optional;
import java.util.Set;

import javax.annotation.PostConstruct;
import javax.ejb.Stateless;
import javax.inject.Inject;
import javax.ws.rs.NotFoundException;

import org.meveo.admin.exception.BusinessException;
import org.meveo.admin.util.pagination.PaginationConfiguration;
import org.meveo.apiv2.billing.DiscountPlanInstanciateDto;
import org.meveo.apiv2.generic.core.mapper.JsonGenericMapper;
import org.meveo.apiv2.generic.services.GenericApiAlteringService;
import org.meveo.apiv2.generic.services.GenericApiLoadService;
import org.meveo.apiv2.generic.services.GenericApiPersistenceDelegate;
import org.meveo.apiv2.models.Resource;
import org.meveo.jpa.EntityManagerWrapper;
import org.meveo.jpa.MeveoJpa;
import org.meveo.model.IEntity;
import org.meveo.model.billing.BillingAccount;
import org.meveo.model.billing.ServiceInstance;
import org.meveo.model.billing.Subscription;
import org.meveo.model.catalog.DiscountPlan;
import org.meveo.model.catalog.DiscountPlanItem;
import org.meveo.model.catalog.DiscountPlanStatusEnum;
import org.meveo.service.billing.impl.BillingAccountService;
import org.meveo.service.billing.impl.ServiceInstanceService;
import org.meveo.service.billing.impl.SubscriptionService;
import org.meveo.service.catalog.impl.DiscountPlanService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import liquibase.repackaged.org.apache.commons.lang3.StringUtils;

@Stateless
public class DiscountPlanApiService {

	private static final Logger log = LoggerFactory.getLogger(DiscountPlanApiService.class);

	@Inject
	private GenericApiAlteringService genericApiAlteringService;

	@Inject
	private GenericApiLoadService loadService;

	@Inject
	private GenericApiPersistenceDelegate persistenceDelegate;

	@Inject
	@MeveoJpa
	private EntityManagerWrapper entityManagerWrapper;
    @Inject
    private DiscountPlanService discountPlanService;
    @Inject
    private ServiceInstanceService serviceInstanceService;
    @Inject
    private SubscriptionService subscriptionService;
    @Inject
    private BillingAccountService billingAccountService;

	@PostConstruct
	public void configure() {
		genericApiAlteringService.addForbiddenFieldsToUpdate(Collections.singletonList("usedQuantity"));
	}

	public String findPaginatedRecords(PaginationConfiguration searchConfig, Set<String> genericFields, Set<String> nestedEntities) {
		return loadService.findPaginatedRecords(true, DiscountPlan.class, searchConfig, genericFields, nestedEntities, 1L, null, null);
	}

	public String findPaginatedDiscountPlanItems(PaginationConfiguration searchConfig, Set<String> genericFields, Set<String> nestedEntities) {
		return loadService.findPaginatedRecords(true, DiscountPlanItem.class, searchConfig, genericFields, nestedEntities, 1L, null, null);
	}

	public Optional<String> findById(Long id, PaginationConfiguration searchConfig, Set<String> genericFields, Set<String> nestedEntities) {
		return loadService.findByClassNameAndId(true, DiscountPlan.class, id, searchConfig, genericFields, nestedEntities, 1L, null);
	}

	public Optional<Long> create(String dto) {
		checkDto(dto);
		IEntity entityToCreate = JsonGenericMapper.Builder.getBuilder().build().parseFromJson(dto, DiscountPlan.class);
		genericApiAlteringService.refreshEntityWithDotFields(JsonGenericMapper.Builder.getBuilder().build().readValue(dto, Map.class), entityToCreate, entityToCreate);
		persistenceDelegate.create(DiscountPlan.class, entityToCreate);
		return Optional.ofNullable((Long) entityToCreate.getId());
	}

	public Optional<Long> update(Long id, String dto) {
		checkId(id).checkDto(dto);
		DiscountPlan entity = entityManagerWrapper.getEntityManager().find(DiscountPlan.class, id);
		//PersistenceServiceHelper.getPersistenceService(DiscountPlan.class).findById(id);

		if (entity == null) {
			throw new NotFoundException("entity discount plan with id " + id + " not found.");
		}
		if (entity != null && !entity.getStatus().equals(DiscountPlanStatusEnum.DRAFT)) {
			log.error("Only status DRAFT is allowed to update a discount plan: {}", entity.getCode());
			throw new BusinessException("Only status DRAFT is allowed to update a discount plan: " + entity.getCode());
		}
		JsonGenericMapper jsonGenericMapper = JsonGenericMapper.Builder.getBuilder().build();
		genericApiAlteringService.refreshEntityWithDotFields(jsonGenericMapper.readValue(dto, Map.class), entity, jsonGenericMapper.parseFromJson(dto, entity.getClass()));
		if (!(entity.getStatus().equals(DiscountPlanStatusEnum.ACTIVE) || entity.getStatus().equals(DiscountPlanStatusEnum.DRAFT))) {
			throw new BusinessException("only ACTIVE status can be accepted");
		}
		IEntity updatedEntity = persistenceDelegate.update(DiscountPlan.class, entity);
		return Optional.ofNullable((Long) updatedEntity.getId());
	}

	public String delete(Long id) {
		checkId(id);
		DiscountPlan entity = entityManagerWrapper.getEntityManager().find(DiscountPlan.class, id);
		if (entity == null) {
			throw new NotFoundException("entity DiscountPlan with id " + id + " not found.");
		}
		persistenceDelegate.remove(DiscountPlan.class, entity);
		return JsonGenericMapper.Builder.getBuilder().withNestedEntities(null).build().toJson(null, DiscountPlan.class, entity, null);
	}

	public Optional<String> findDiscountPlanItemById(Long id, PaginationConfiguration searchConfig, Set<String> genericFields, Set<String> nestedEntities) {
		return loadService.findByClassNameAndId(true, DiscountPlanItem.class, id, searchConfig, genericFields, nestedEntities, 1L, null);
	}

	public Optional<Long> createItem(String dto) {
		checkDto(dto);
		IEntity entityToCreate = JsonGenericMapper.Builder.getBuilder().build().parseFromJson(dto, DiscountPlanItem.class);
		genericApiAlteringService.refreshEntityWithDotFields(JsonGenericMapper.Builder.getBuilder().build().readValue(dto, Map.class), entityToCreate, entityToCreate);
		persistenceDelegate.create(DiscountPlanItem.class, entityToCreate);
		return Optional.ofNullable((Long) entityToCreate.getId());
	}

	public Optional<Long> updateItem(Long id, String dto) {
		checkId(id).checkDto(dto);
		IEntity entity = entityManagerWrapper.getEntityManager().find(DiscountPlanItem.class, id);

		if (entity == null) {
			throw new NotFoundException("entity discount plan with id " + id + " not found.");
		}
		JsonGenericMapper jsonGenericMapper = JsonGenericMapper.Builder.getBuilder().build();
		genericApiAlteringService.refreshEntityWithDotFields(JsonGenericMapper.Builder.getBuilder().build().readValue(dto, Map.class), entity,
				jsonGenericMapper.parseFromJson(dto, entity.getClass()));
		IEntity updatedEntity = persistenceDelegate.update(DiscountPlanItem.class, entity);
		return Optional.ofNullable((Long) updatedEntity.getId());
	}

	public String deleteItem(Long id) {
		checkId(id);
		DiscountPlanItem entity = entityManagerWrapper.getEntityManager().find(DiscountPlanItem.class, id);
		if (entity == null) {
			throw new NotFoundException("entity DiscountPlan with id " + id + " not found.");
		}
		persistenceDelegate.remove(DiscountPlanItem.class, entity);
		return JsonGenericMapper.Builder.getBuilder().withNestedEntities(null).build().toJson(null, DiscountPlanItem.class, entity, null);

	}

	public Optional<Long> expire(Long id) {
		checkId(id);
		DiscountPlan entity = entityManagerWrapper.getEntityManager().find(DiscountPlan.class, id);
		if (entity == null) {
			throw new NotFoundException("entity DiscountPlan with id " + id + " not found.");
		}
		entity.setStatus(DiscountPlanStatusEnum.EXPIRED);
		entity.setStatusDate(new Date());
		IEntity updatedEntity = persistenceDelegate.update(DiscountPlan.class, entity);
		return Optional.ofNullable((Long) updatedEntity.getId());
	}
	private DiscountPlan getDiscountPlan(Resource entity) {
		DiscountPlan discountPlan = null;
        if(entity.getId() != null) {
            discountPlan = discountPlanService.findById(entity.getId());
        }
        if(discountPlan == null && StringUtils.isNotEmpty(entity.getCode())) {
            discountPlan = discountPlanService.findByCode(entity.getCode());
        }
        if(discountPlan == null) {
            throw new NotFoundException("entity DiscountPlan with (id :" + entity.getId() + ", code : "+ entity.getCode() +" )  not found.");
        }
        return discountPlan;
	}
	
	private void instianciateSubscription(Resource subscriptionResource, DiscountPlan discountPlan) {
         Subscription subscription = null;
         if(subscriptionResource.getId() != null) {
             subscription = subscriptionService.findById(subscriptionResource.getId());
             if(subscription == null)
                 throw new NotFoundException("entity subscription with id " + subscriptionResource.getId() + " not found.");
         }
         if(subscription == null && StringUtils.isNotEmpty(subscriptionResource.getCode())) {
             subscription = subscriptionService.findByCode(subscriptionResource.getCode());
             if(subscription == null)
                 throw new NotFoundException("entity subscription with code " + subscriptionResource.getCode() + " not found.");
         }
         subscriptionService.instantiateDiscountPlan(subscription, discountPlan);
	}
	
	private void instanciateBillingAccount(Resource billingAccountResource, DiscountPlan discountPlan) {
          BillingAccount billingAccount = null;
          if(billingAccountResource.getId() != null) {
              billingAccount = billingAccountService.findById(billingAccountResource.getId());
              if(billingAccount == null)
                  throw new NotFoundException("entity billingAccount with id " + billingAccountResource.getId() + " not found.");
          }
          if(billingAccount == null && StringUtils.isNotEmpty(billingAccountResource.getCode())) {
              billingAccount = billingAccountService.findByCode(billingAccountResource.getCode());
              if(billingAccount == null)
                  throw new NotFoundException("entity billingAccount with code " + billingAccountResource.getCode() + " not found.");
          }
          billingAccountService.instantiateDiscountPlan(billingAccount, discountPlan);
	}
	
    public void instanciateDP(DiscountPlanInstanciateDto discountPlanInstanciateDto) {
        if(discountPlanInstanciateDto.getDiscountPlan() == null) {
            throw new InvalidParameterException("The discountPlan is required");
        }
        Resource discountPlanResource = discountPlanInstanciateDto.getDiscountPlan();
        if(discountPlanResource.getId() == null && StringUtils.isEmpty(discountPlanResource.getCode())) {
            throw new InvalidParameterException("One of discountPlanResource.id or discountPlanResource.code must be present");
        }
        DiscountPlan discountPlan = getDiscountPlan(discountPlanResource);
        
        if(discountPlanInstanciateDto.getServiceInstance() != null && discountPlanInstanciateDto.getServiceInstance().getId() != null) {
            ServiceInstance serviceInstance = serviceInstanceService.findById(discountPlanInstanciateDto.getServiceInstance().getId());
            if(serviceInstance == null)
                throw new NotFoundException("entity serviceInstance with id " + discountPlanInstanciateDto.getServiceInstance().getId() + " not found.");
            serviceInstanceService.instantiateDiscountPlan(serviceInstance, discountPlan, false);
        }else if(discountPlanInstanciateDto.getSubscription() != null && (discountPlanInstanciateDto.getSubscription().getId() != null || StringUtils.isNotEmpty(discountPlanInstanciateDto.getSubscription().getCode())))  {
        	instianciateSubscription(discountPlanInstanciateDto.getSubscription(), discountPlan);
        }else if(discountPlanInstanciateDto.getBillingAccount() != null && (discountPlanInstanciateDto.getBillingAccount().getId() != null || StringUtils.isNotEmpty(discountPlanInstanciateDto.getBillingAccount().getCode()))) {
        	instanciateBillingAccount(discountPlanInstanciateDto.getBillingAccount(), discountPlan);
        }else {
            throw new InvalidParameterException("One of these property must be present : serviceInstance, subscription, billingAccount");
        }
    }
}
