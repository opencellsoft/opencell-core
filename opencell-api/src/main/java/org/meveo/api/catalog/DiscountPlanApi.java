package org.meveo.api.catalog;

import java.util.ArrayList;
import java.util.List;

import javax.ejb.Stateless;
import javax.inject.Inject;

import org.meveo.admin.exception.BusinessException;
import org.meveo.api.BaseCrudApi;
import org.meveo.api.dto.catalog.DiscountPlanDto;
import org.meveo.api.dto.catalog.DiscountPlansDto;
import org.meveo.api.exception.EntityAlreadyExistsException;
import org.meveo.api.exception.EntityDoesNotExistsException;
import org.meveo.api.exception.MeveoApiException;
import org.meveo.commons.utils.StringUtils;
import org.meveo.model.catalog.DiscountPlan;
import org.meveo.service.catalog.impl.DiscountPlanService;

/**
 * 
 * 
 *
 */
@Stateless
public class DiscountPlanApi extends BaseCrudApi<DiscountPlan, DiscountPlanDto> {

    @Inject
    private DiscountPlanService discountPlanService;

    /**
     * creates a discount plan
     * 
     * @param postData posted data to API
     * 
     * @throws MeveoApiException meveo api exception
     * @throws BusinessException business exception
     */
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

        discountPlanService.create(discountPlan);
        return discountPlan;
    }

    /**
     * updates the description of an existing discount plan.
     * 
     * @param postData posted data to API
     * 
     * @throws MeveoApiException meveo api exception
     * @throws BusinessException business exception
     */
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

        discountPlan = discountPlanService.update(discountPlan);
        return discountPlan;
    }

    /**
     * retrieves a discount plan based on code
     * 
     * @param discountPlanCode discount plan code
     * @return discount plan
     * @throws MeveoApiException meveo api exception
     */
    public DiscountPlanDto find(String discountPlanCode) throws MeveoApiException {

        if (StringUtils.isBlank(discountPlanCode)) {
            missingParameters.add("code");
            handleMissingParameters();
        }

        DiscountPlan discountPlan = discountPlanService.findByCode(discountPlanCode);
        if (discountPlan == null) {
            throw new EntityDoesNotExistsException(DiscountPlan.class, discountPlanCode);
        }

        DiscountPlanDto discountPlanDto = new DiscountPlanDto(discountPlan);

        return discountPlanDto;
    }

    /**
     * deletes a discount plan based on code.
     * 
     * @param discountPlanCode discount plan code
     * @throws MeveoApiException meveo api exception
     * @throws BusinessException business exception
     */
    public void remove(String discountPlanCode) throws MeveoApiException, BusinessException {

        if (StringUtils.isBlank(discountPlanCode)) {
            missingParameters.add("code");
            handleMissingParameters();
        }

        DiscountPlan discountPlan = discountPlanService.findByCode(discountPlanCode);
        if (discountPlan == null) {
            throw new EntityDoesNotExistsException(DiscountPlan.class, discountPlanCode);
        }

        discountPlanService.remove(discountPlan);
    }

    /**
     * creates if the the discount plan code is not existing, updates if exists.
     * 
     * @param postData posted data to API
     * 
     * @throws MeveoApiException meveo api exception
     * @throws BusinessException business exception
     */
    public DiscountPlan createOrUpdate(DiscountPlanDto postData) throws MeveoApiException, BusinessException {

        if (StringUtils.isBlank(postData.getCode())) {
            missingParameters.add("code");
            handleMissingParameters();
        }
        if (discountPlanService.findByCode(postData.getCode()) == null) {
            return create(postData);
        } else {
            return update(postData);
        }
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
            List<DiscountPlanDto> discountPlanDtos = new ArrayList<DiscountPlanDto>();
            for (DiscountPlan dp : discountPlans) {
                discountPlanDtos.add(new DiscountPlanDto(dp));
            }
            discountPlansDto.setDiscountPlan(discountPlanDtos);
        }

        return discountPlansDto;
    }
}