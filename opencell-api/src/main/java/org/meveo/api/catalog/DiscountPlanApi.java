/*
 * (C) Copyright 2015-2020 Opencell SAS (https://opencellsoft.com/) and contributors.
 *
 * This program is free software: you can redistribute it and/or modify it under the terms of the GNU Affero General
 * Public License as published by the Free Software Foundation, either version 3 of the License, or (at your option)
 * any later version.
 *
 * THERE IS NO WARRANTY FOR THE PROGRAM, TO THE EXTENT PERMITTED BY APPLICABLE LAW. EXCEPT WHEN
 * OTHERWISE STATED IN WRITING THE COPYRIGHT HOLDERS AND/OR OTHER PARTIES PROVIDE THE PROGRAM "AS
 * IS" WITHOUT WARRANTY OF ANY KIND, EITHER EXPRESSED OR IMPLIED, INCLUDING, BUT NOT LIMITED TO, THE
 * IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE. THE ENTIRE RISK AS TO
 * THE QUALITY AND PERFORMANCE OF THE PROGRAM IS WITH YOU. SHOULD THE PROGRAM PROVE DEFECTIVE,
 * YOU ASSUME THE COST OF ALL NECESSARY SERVICING, REPAIR OR CORRECTION.
 *
 * For more information on the GNU Affero General Public License, please consult
 * <https://www.gnu.org/licenses/agpl-3.0.en.html>.
 */

package org.meveo.api.catalog;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.ejb.Stateless;
import javax.inject.Inject;

import org.meveo.admin.exception.BusinessException;
import org.meveo.api.BaseCrudApi;
import org.meveo.api.MeveoApiErrorCodeEnum;
import org.meveo.api.dto.ApplicableEntityDto;
import org.meveo.api.dto.catalog.DiscountPlanDto;
import org.meveo.api.dto.catalog.DiscountPlanItemDto;
import org.meveo.api.dto.catalog.DiscountPlansDto;
import org.meveo.api.dto.response.PagingAndFiltering;
import org.meveo.api.dto.response.catalog.GetDiscountPlansResponseDto;
import org.meveo.api.exception.DeleteReferencedEntityException;
import org.meveo.api.exception.EntityAlreadyExistsException;
import org.meveo.api.exception.EntityDoesNotExistsException;
import org.meveo.api.exception.InvalidParameterException;
import org.meveo.api.exception.MeveoApiException;
import org.meveo.api.exception.MissingParameterException;
import org.meveo.api.restful.util.GenericPagingAndFilteringUtils;
import org.meveo.commons.utils.StringUtils;
import org.meveo.model.catalog.ApplicableEntity;
import org.meveo.model.catalog.DiscountPlan;
import org.meveo.model.catalog.DiscountPlan.DurationPeriodUnitEnum;
import org.meveo.model.catalog.DiscountPlanItem;
import org.meveo.model.catalog.DiscountPlanStatusEnum;
import org.meveo.model.crm.custom.CustomFieldInheritanceEnum;
import org.meveo.service.billing.impl.InvoiceLineService;
import org.meveo.service.catalog.impl.DiscountPlanService;

/**
 * @author Said Ramli
 * @lastModifiedVersion 5.3.2
 */
@Stateless
public class DiscountPlanApi extends BaseCrudApi<DiscountPlan, DiscountPlanDto> {

    @Inject
    private DiscountPlanService discountPlanService;

    @Override
    public DiscountPlan create(DiscountPlanDto postData) throws MeveoApiException, BusinessException {

    	if(postData.getDiscountPlanType() == null)
			missingParameters.add("discountPlanType");
    	handleMissingParameters();
        if (StringUtils.isBlank(postData.getCode())) {
            addGenericCodeIfAssociated(DiscountPlan.class.getName(), postData);
        }
        if (discountPlanService.findByCode(postData.getCode()) != null) {
            throw new EntityAlreadyExistsException(DiscountPlan.class, postData.getCode());
        }

        DiscountPlan discountPlan = new DiscountPlan();
        discountPlan.setCode(postData.getCode());
        discountPlan.setDescription(postData.getDescription());
        if (postData.isDisabled() != null) {
            discountPlan.setDisabled(postData.isDisabled());
        }
        discountPlan.setStartDate(postData.getStartDate());
        discountPlan.setEndDate(postData.getEndDate());
        discountPlan.setExpressionEl(postData.getExpressionEl());
        discountPlan.setDefaultDuration(postData.getDefaultDuration());
        if (postData.getDurationUnit() != null) {
            discountPlan.setDurationUnit(postData.getDurationUnit());
        }
        discountPlan.setDiscountPlanType(postData.getDiscountPlanType());
        discountPlan.setStatus(postData.getStatus());
        discountPlan.setInitialQuantity(postData.getInitialQuantity());
        discountPlan.setApplicationLimit(postData.getApplicationLimit());
        discountPlan.setApplicationFilterEL(postData.getApplicationFilterEL());
        discountPlan.setIncompatibleDiscountPlans(getIncompatibleDiscountPlans(postData.getIncompatibleDiscountPlans()));
        discountPlan.setDiscountPlanaApplicableEntities(getApplicableEntities(postData.getApplicableEntities()));
        discountPlan.setUsedQuantity(postData.getUsedQuantity());
        discountPlan.setSequence(postData.getSequence());
        discountPlan.setApplicableOnOverriddenPrice(postData.isApplicableOnOverriddenPrice());
        discountPlan.setApplicableOnDiscountedPrice(postData.isApplicableOnDiscountedPrice());
        if(postData.getApplicableOnContractPrice()!=null) {
        	discountPlan.setApplicableOnContractPrice(postData.getApplicableOnContractPrice());
        }
        // populate customFields
        try {
            populateCustomFields(postData.getCustomFields(), discountPlan, true);
        } catch (MissingParameterException | InvalidParameterException e) {
            log.error("Failed to associate custom field instance to an entity: {}", e.getMessage());
            throw e;
        } catch (Exception e) {
            log.error("Failed to associate custom field instance to an entity", e);
            throw e;
        }

        discountPlanService.create(discountPlan);
        return discountPlan;
    }

    private List<DiscountPlan> getIncompatibleDiscountPlans(List<DiscountPlanDto> incompatibleDiscountPlansDto) {
        if (incompatibleDiscountPlansDto == null) {
            return null;
        }
        List<DiscountPlan> incompatibleDiscountPlans = new ArrayList<>();
        for (DiscountPlanDto discountPlanDto : incompatibleDiscountPlansDto) {
            DiscountPlan discountPlan = discountPlanService.findByCode(discountPlanDto.getCode());
            if (discountPlan == null) {
                throw new BusinessException("The discout plan with code " + discountPlanDto.getCode() + " not found");
            }
            incompatibleDiscountPlans.add(discountPlan);
        }
        return incompatibleDiscountPlans;
    }

    @Override
    public DiscountPlan update(DiscountPlanDto postData) throws MeveoApiException, BusinessException {

        if (StringUtils.isBlank(postData.getCode())) {
            missingParameters.add("code");
        }
    	if(postData.getDiscountPlanType() == null)
			missingParameters.add("discountPlanType");
    	
    	handleMissingParameters();

        DiscountPlan discountPlan = discountPlanService.findByCode(postData.getCode());
        if (discountPlan == null) {
            throw new EntityDoesNotExistsException(DiscountPlan.class, postData.getCode());
        }


	        final String description = postData.getDescription();
	        if (description != null) {
	            discountPlan.setDescription(description);
	        }
	
	        discountPlan.setCode(StringUtils.isBlank(postData.getUpdatedCode()) ? postData.getCode() : postData.getUpdatedCode());
	
	        if(!StringUtils.isBlank(postData.getExpressionEl())){
	        	discountPlan.setExpressionEl(postData.getExpressionEl());
	        }
	
			if (postData.getStartDate() != null) {
				discountPlan.setStartDate(postData.getStartDate());
			} 
			if (postData.getDefaultDuration() != null) {
				discountPlan.setDefaultDuration(postData.getDefaultDuration());
			} 
			if (postData.getDurationUnit() != null) {
			    if (StringUtils.isBlank(postData.getDurationUnit())) {
			        discountPlan.setDurationUnit(DurationPeriodUnitEnum.DAY);  
	            } else {
	                discountPlan.setDurationUnit(postData.getDurationUnit());
	            }
	        }
	        if (postData.getStatus() != null) {
	            if(postData.getStatus().equals(DiscountPlanStatusEnum.ACTIVE) && discountPlan.getDiscountPlanItems().isEmpty()){
	                throw new BusinessException("User can not be able to activate a DP if no Discount Line exists");
                }
	            discountPlan.setStatus(postData.getStatus());
	            discountPlan.setStatusDate(new Date());
	        }
	        if (postData.getDiscountPlanType() != null) {
	            discountPlan.setDiscountPlanType(postData.getDiscountPlanType());
	        }
	        if (postData.getInitialQuantity() != null) {
	            discountPlan.setInitialQuantity(postData.getInitialQuantity());
	        }
	        List<DiscountPlan> discountPlans = getIncompatibleDiscountPlans(postData.getIncompatibleDiscountPlans());
	        if (discountPlans != null && !discountPlans.isEmpty()) {
	            discountPlan.setIncompatibleDiscountPlans(getIncompatibleDiscountPlans(postData.getIncompatibleDiscountPlans()));
	        }
	        List<ApplicableEntity> applicableEntities = getApplicableEntities(postData.getApplicableEntities());
	        if (applicableEntities != null && !applicableEntities.isEmpty()) {
	            discountPlan.setDiscountPlanaApplicableEntities(applicableEntities);
	        }
	        if (postData.getSequence() != null) {
	        	discountPlan.setSequence(postData.getSequence());
	        }

            discountPlan.setApplicableOnOverriddenPrice(postData.isApplicableOnOverriddenPrice());
            discountPlan.setApplicableOnDiscountedPrice(postData.isApplicableOnDiscountedPrice());
            if(postData.getApplicableOnContractPrice()!=null) {
            	discountPlan.setApplicableOnContractPrice(postData.getApplicableOnContractPrice());
            }

	        // populate customFields
	        try {
	            populateCustomFields(postData.getCustomFields(), discountPlan, false);
	        } catch (MissingParameterException | InvalidParameterException e) {
	            log.error("Failed to associate custom field instance to an entity: {}", e.getMessage());
	            throw e;
	        } catch (Exception e) {
	            log.error("Failed to associate custom field instance to an entity", e);
	            throw e;
	        }
    	if(postData.getEndDate() != null)
    		discountPlan.setEndDate(postData.getEndDate());

        if (postData.getApplicationFilterEL() != null) {
            discountPlan.setApplicationFilterEL(postData.getApplicationFilterEL());
        }

        if (postData.getApplicationLimit() != null) {
            discountPlan.setApplicationLimit(postData.getApplicationLimit());
        }
        
        discountPlan = discountPlanService.update(discountPlan);
        return discountPlan;
    }

    private List<ApplicableEntity> getApplicableEntities(List<ApplicableEntityDto> applicableEntitiesDto) {
        if (applicableEntitiesDto == null) {
            return null;
        }
        List<ApplicableEntity> applicableEntities = new ArrayList<>();
        for (ApplicableEntityDto dto : applicableEntitiesDto) {
            ApplicableEntity applicableEntity = new ApplicableEntity();
            applicableEntity.setCode(dto.getCode());
            applicableEntity.setEntityClass(dto.getEntityClass());
            applicableEntities.add(applicableEntity);
        }
        return applicableEntities;
    }

    @Override
    public DiscountPlanDto find(String discountPlanCode) throws MeveoApiException {

        if (StringUtils.isBlank(discountPlanCode)) {
            missingParameters.add("code");
            handleMissingParameters();
        }

        DiscountPlan discountPlan = discountPlanService.findByCode(discountPlanCode);
        if (discountPlan == null) {
            throw new EntityDoesNotExistsException(DiscountPlan.class, discountPlanCode);
        }

        DiscountPlanDto dpDto = new DiscountPlanDto(discountPlan, entityToDtoConverter.getCustomFieldsDTO(discountPlan, CustomFieldInheritanceEnum.INHERIT_NO_MERGE));

        if (discountPlan.getDiscountPlanItems() != null && !discountPlan.getDiscountPlanItems().isEmpty()) {
            List<DiscountPlanItemDto> discountPlanItemsDto = new ArrayList<>();
            for (DiscountPlanItem dpi : discountPlan.getDiscountPlanItems()) {
                discountPlanItemsDto.add(new DiscountPlanItemDto(dpi, entityToDtoConverter.getCustomFieldsDTO(dpi, CustomFieldInheritanceEnum.INHERIT_NO_MERGE)));
            }
            dpDto.setDiscountPlanItems(discountPlanItemsDto);
        }

        return dpDto;
    }

    /**
     * retrieves all discount plan of the user.
     * 
     * @return list of discount plan
     * @throws MeveoApiException meveo api exception
     */
    public DiscountPlansDto list() throws MeveoApiException {

        DiscountPlansDto discountPlansDto = null;
        List<DiscountPlan> discountPlans = discountPlanService.list();

		if (discountPlans != null && !discountPlans.isEmpty()) {
			discountPlansDto = new DiscountPlansDto();
			List<DiscountPlanDto> discountPlanDtos = new ArrayList<>();
			for (DiscountPlan discountPlan : discountPlans) {
                DiscountPlanDto dpDto = new DiscountPlanDto(discountPlan, entityToDtoConverter.getCustomFieldsDTO(discountPlan, CustomFieldInheritanceEnum.INHERIT_NO_MERGE));

                if (discountPlan.getDiscountPlanItems() != null && !discountPlan.getDiscountPlanItems().isEmpty()) {
                    List<DiscountPlanItemDto> discountPlanItemsDto = new ArrayList<>();
                    for (DiscountPlanItem dpi : discountPlan.getDiscountPlanItems()) {
                        discountPlanItemsDto.add(new DiscountPlanItemDto(dpi, entityToDtoConverter.getCustomFieldsDTO(dpi, CustomFieldInheritanceEnum.INHERIT_NO_MERGE)));
                    }
                    dpDto.setDiscountPlanItems(discountPlanItemsDto);
                }

                discountPlanDtos.add(dpDto);
            }
            discountPlansDto.setDiscountPlan(discountPlanDtos);
        }

        return discountPlansDto;
    }

    public void remove(String code) throws MissingParameterException, EntityDoesNotExistsException, BusinessException {

        if (org.apache.commons.lang3.StringUtils.isBlank(code)) {
            missingParameters.add("code");
        }

        handleMissingParameters();

        DiscountPlan entity = (DiscountPlan) discountPlanService.findByCode(code);

        if (entity == null) {
            throw new EntityDoesNotExistsException(DiscountPlan.class, code);
        }
        if (entity.getStatus().equals(DiscountPlanStatusEnum.DRAFT) || entity.getStatus().equals(DiscountPlanStatusEnum.ACTIVE)) {
        	try {
            discountPlanService.remove(entity);
            discountPlanService.commit();
        	} catch (Exception e) {
                if (e.getMessage().indexOf("ConstraintViolationException") > -1) {
                	throw new DeleteReferencedEntityException(DiscountPlan.class, code);
                }
                throw new MeveoApiException(MeveoApiErrorCodeEnum.BUSINESS_API_EXCEPTION, "Cannot delete entity");
            }
        } else {
            throw new BusinessException("only DRAFT and ACTIVE discount plans can be removed");
        }
    }

    public GetDiscountPlansResponseDto list(PagingAndFiltering pagingAndFiltering) {
        GetDiscountPlansResponseDto result = new GetDiscountPlansResponseDto();
        result.setPaging( pagingAndFiltering );

        List<DiscountPlan> discountPlans = discountPlanService.list( GenericPagingAndFilteringUtils.getInstance().getPaginationConfiguration() );
        if (discountPlans != null) {
            for (DiscountPlan discountPlan : discountPlans) {
                result.getDiscountPlan().getDiscountPlan().add(new DiscountPlanDto(discountPlan,
                        entityToDtoConverter.getCustomFieldsDTO(discountPlan, CustomFieldInheritanceEnum.INHERIT_NO_MERGE)));
            }
        }

        return result;
    }
}