/**
 *
 */
package org.meveo.apiv2.catalog.service;

import org.meveo.admin.exception.BusinessException;
import org.meveo.admin.util.pagination.PaginationConfiguration;
import org.meveo.apiv2.generic.core.GenericHelper;
import org.meveo.apiv2.generic.core.filter.FilterMapper;
import org.meveo.apiv2.generic.core.mapper.JsonGenericMapper;
import org.meveo.apiv2.generic.services.GenericApiAlteringService;
import org.meveo.apiv2.generic.services.GenericApiLoadService;
import org.meveo.apiv2.generic.services.GenericApiPersistenceDelegate;
import org.meveo.apiv2.generic.services.PersistenceServiceHelper;
import org.meveo.jpa.EntityManagerWrapper;
import org.meveo.jpa.MeveoJpa;
import org.meveo.model.IEntity;
import org.meveo.model.catalog.DiscountPlan;
import org.meveo.model.catalog.DiscountPlanItem;
import org.meveo.model.catalog.DiscountPlanStatusEnum;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.annotation.PostConstruct;
import javax.ejb.Stateless;
import javax.inject.Inject;
import javax.ws.rs.NotFoundException;
import java.util.Collections;
import java.util.Date;
import java.util.Map;
import java.util.Optional;
import java.util.Set;

import static org.meveo.apiv2.generic.ValidationUtils.checkDto;
import static org.meveo.apiv2.generic.ValidationUtils.checkId;

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

	@PostConstruct
	public void configure() {
		genericApiAlteringService.addForbiddenFieldsToUpdate(Collections.singletonList("usedQuantity"));
	}

	public String findPaginatedRecords(PaginationConfiguration searchConfig, Set<String> genericFields, Set<String> nestedEntities) {
		return loadService.findPaginatedRecords(true, DiscountPlan.class, searchConfig, genericFields, nestedEntities, 1L);
	}

	public String findPaginatedDiscountPlanItems(PaginationConfiguration searchConfig, Set<String> genericFields, Set<String> nestedEntities) {
		return loadService.findPaginatedRecords(true, DiscountPlanItem.class, searchConfig, genericFields, nestedEntities, 1L);
	}

	public Optional<String> findById(Long id, PaginationConfiguration searchConfig, Set<String> genericFields, Set<String> nestedEntities) {
		return loadService.findByClassNameAndId(true, DiscountPlan.class, id, searchConfig, genericFields, nestedEntities, 1L);
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
		return JsonGenericMapper.Builder.getBuilder().withNestedEntities(null).build().toJson(null, DiscountPlan.class, entity);
	}

	public Optional<String> findDiscountPlanItemById(Long id, PaginationConfiguration searchConfig, Set<String> genericFields, Set<String> nestedEntities) {
		return loadService.findByClassNameAndId(true, DiscountPlanItem.class, id, searchConfig, genericFields, nestedEntities, 1L);
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
		return JsonGenericMapper.Builder.getBuilder().withNestedEntities(null).build().toJson(null, DiscountPlanItem.class, entity);

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
}
