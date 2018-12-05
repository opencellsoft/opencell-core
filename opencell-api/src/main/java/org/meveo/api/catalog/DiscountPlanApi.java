package org.meveo.api.catalog;

import java.util.ArrayList;
import java.util.List;

import javax.ejb.Stateless;
import javax.inject.Inject;

import org.meveo.admin.exception.BusinessException;
import org.meveo.api.BaseCrudApi;
import org.meveo.api.dto.catalog.DiscountPlanDto;
import org.meveo.api.dto.catalog.DiscountPlanItemDto;
import org.meveo.api.dto.catalog.DiscountPlansDto;
import org.meveo.api.exception.EntityAlreadyExistsException;
import org.meveo.api.exception.EntityDoesNotExistsException;
import org.meveo.api.exception.MeveoApiException;
import org.meveo.commons.utils.StringUtils;
import org.meveo.model.catalog.DiscountPlan;
import org.meveo.model.catalog.DiscountPlan.DurationPeriodUnitEnum;
import org.meveo.model.catalog.DiscountPlanItem;
import org.meveo.model.crm.custom.CustomFieldInheritanceEnum;
import org.meveo.service.catalog.impl.DiscountPlanService;

@Stateless
public class DiscountPlanApi extends BaseCrudApi<DiscountPlan, DiscountPlanDto> {

    @Inject
    private DiscountPlanService discountPlanService;

    @Override
    public DiscountPlan create(DiscountPlanDto postData) throws MeveoApiException, BusinessException {

        if (StringUtils.isBlank(postData.getCode())) {
            missingParameters.add("code");
        }
        handleMissingParametersAndValidate(postData);
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
		discountPlan.setDefaultDuration(postData.getDefaultDuration());
		if (postData.getDurationUnit() != null) {
			discountPlan.setDurationUnit(postData.getDurationUnit());
		}

        discountPlanService.create(discountPlan);
        return discountPlan;
    }

    @Override
    public DiscountPlan update(DiscountPlanDto postData) throws MeveoApiException, BusinessException {

        if (StringUtils.isBlank(postData.getCode())) {
            missingParameters.add("code");
        }
        handleMissingParametersAndValidate(postData);

        DiscountPlan discountPlan = discountPlanService.findByCode(postData.getCode());

        if (discountPlan == null) {
            throw new EntityDoesNotExistsException(DiscountPlan.class, postData.getCode());
        }
        discountPlan.setDescription(postData.getDescription());
        discountPlan.setCode(StringUtils.isBlank(postData.getUpdatedCode()) ? postData.getCode() : postData.getUpdatedCode());
        if (postData.getStartDate() != null) {
			discountPlan.setStartDate(postData.getStartDate());
		} else {
			discountPlan.setStartDate(null);
		}
		if (postData.getEndDate() != null) {
			discountPlan.setEndDate(postData.getEndDate());
		} else {
			discountPlan.setEndDate(null);
		}
		if (postData.getDefaultDuration() != null) {
			discountPlan.setDefaultDuration(postData.getDefaultDuration());
		} else {
			if (StringUtils.isBlank(postData.getDefaultDuration())) {
				discountPlan.setDefaultDuration(null);
			}
		}
		if (postData.getDurationUnit() != null) {
			discountPlan.setDurationUnit(postData.getDurationUnit());
		} else {
			if (StringUtils.isBlank(postData.getDurationUnit())) {
				discountPlan.setDurationUnit(DurationPeriodUnitEnum.DAY);
			}
		}
		
        discountPlan = discountPlanService.update(discountPlan);
        return discountPlan;
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
				discountPlanItemsDto.add(new DiscountPlanItemDto(dpi,
						entityToDtoConverter.getCustomFieldsDTO(dpi, CustomFieldInheritanceEnum.INHERIT_NO_MERGE)));
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
				DiscountPlanDto dpDto = new DiscountPlanDto(discountPlan, entityToDtoConverter
						.getCustomFieldsDTO(discountPlan, CustomFieldInheritanceEnum.INHERIT_NO_MERGE));

				if (discountPlan.getDiscountPlanItems() != null && !discountPlan.getDiscountPlanItems().isEmpty()) {
					List<DiscountPlanItemDto> discountPlanItemsDto = new ArrayList<>();
					for (DiscountPlanItem dpi : discountPlan.getDiscountPlanItems()) {
						discountPlanItemsDto.add(new DiscountPlanItemDto(dpi, entityToDtoConverter.getCustomFieldsDTO(dpi,
								CustomFieldInheritanceEnum.INHERIT_NO_MERGE)));
					}
					dpDto.setDiscountPlanItems(discountPlanItemsDto);
				}

				discountPlanDtos.add(dpDto);
			}
			discountPlansDto.setDiscountPlan(discountPlanDtos);
		}

        return discountPlansDto;
    }
}