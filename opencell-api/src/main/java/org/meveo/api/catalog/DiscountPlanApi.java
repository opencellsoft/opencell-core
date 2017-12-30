package org.meveo.api.catalog;

import java.util.ArrayList;
import java.util.List;

import javax.ejb.Stateless;
import javax.inject.Inject;

import org.meveo.admin.exception.BusinessException;
import org.meveo.api.BaseApi;
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
public class DiscountPlanApi extends BaseApi {

    @Inject
    private DiscountPlanService discountPlanService;

    /**
     * creates a discount plan
     * 
     * @param postData posted data to API

     * @throws MeveoApiException meveo api exception
     * @throws BusinessException  business exception
     */
    public void create(DiscountPlanDto postData) throws MeveoApiException, BusinessException {

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

        discountPlanService.create(discountPlan);
    }

    /**
     * updates the description of an existing discount plan.
     * 
     * @param postData posted data to API

     * @throws MeveoApiException meveo api exception
     * @throws BusinessException  business exception
     */
    public void update(DiscountPlanDto postData) throws MeveoApiException, BusinessException {

        if (StringUtils.isBlank(postData.getCode())) {
            missingParameters.add("code");            
        }
        handleMissingParametersAndValidate(postData);
        
        DiscountPlan discountPlan = discountPlanService.findByCode(postData.getCode());

        if (discountPlan == null) {
            throw new EntityDoesNotExistsException(DiscountPlan.class, postData.getCode());
        }
        discountPlan.setDescription(postData.getDescription());
        discountPlan.setCode(StringUtils.isBlank(postData.getUpdatedCode())?postData.getCode():postData.getUpdatedCode());

        discountPlanService.update(discountPlan);
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

        DiscountPlanDto discountPlanDto = new DiscountPlanDto();

        DiscountPlan discountPlan = discountPlanService.findByCode(discountPlanCode);
        if (discountPlan == null) {
            throw new EntityDoesNotExistsException(DiscountPlan.class, discountPlanCode);
        }

        discountPlanDto.setCode(discountPlan.getCode());
        discountPlanDto.setDescription(discountPlan.getDescription());

        return discountPlanDto;
    }

    /**
     * deletes a discount plan based on code.
     * 
     * @param discountPlanCode discount plan code
     * @throws MeveoApiException meveo api exception
     * @throws BusinessException  business exception 
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

     * @throws MeveoApiException meveo api exception
     * @throws BusinessException  business exception
     */
    public void createOrUpdate(DiscountPlanDto postData) throws MeveoApiException, BusinessException {

        if (StringUtils.isBlank(postData.getCode())) {
            missingParameters.add("code");
            handleMissingParameters();
        }
        if (discountPlanService.findByCode(postData.getCode()) == null) {
            create(postData);
        } else {
            update(postData);
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
                DiscountPlanDto dpd = new DiscountPlanDto();
                dpd.setCode(dp.getCode());
                dpd.setDescription(dp.getDescription());
                discountPlanDtos.add(dpd);
            }
            discountPlansDto.setDiscountPlan(discountPlanDtos);
        }

        return discountPlansDto;
    }
}
