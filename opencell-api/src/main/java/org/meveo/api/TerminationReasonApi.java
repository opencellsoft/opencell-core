package org.meveo.api;

import java.util.ArrayList;
import java.util.List;

import javax.ejb.Stateless;
import javax.inject.Inject;

import org.meveo.admin.exception.BusinessException;
import org.meveo.api.dto.TerminationReasonDto;
import org.meveo.api.exception.EntityAlreadyExistsException;
import org.meveo.api.exception.EntityDoesNotExistsException;
import org.meveo.api.exception.MeveoApiException;
import org.meveo.commons.utils.StringUtils;
import org.meveo.model.billing.SubscriptionTerminationReason;
import org.meveo.service.billing.impl.TerminationReasonService;

@Stateless
public class TerminationReasonApi extends BaseApi {

    @Inject
    private TerminationReasonService terminationReasonService;

    /**
     * creates a SubscriptionTerminationReason based on code and description.
     * 
     * @param postData posted data to API.

     * @throws MeveoApiException meveo api exception.
     * @throws BusinessException  business exception.
     */
    public void create(TerminationReasonDto postData) throws MeveoApiException, BusinessException {

        if (StringUtils.isBlank(postData.getCode())) {
            missingParameters.add("code");            
        }

        handleMissingParametersAndValidate(postData);

        if (terminationReasonService.findByCode(postData.getCode()) != null) {
            throw new EntityAlreadyExistsException(SubscriptionTerminationReason.class, postData.getCode());
        }

        SubscriptionTerminationReason subscriptionTerminationReason = new SubscriptionTerminationReason();

        subscriptionTerminationReason.setCode(postData.getCode());
        subscriptionTerminationReason.setDescription(postData.getDescription());
        subscriptionTerminationReason.setApplyAgreement(postData.isApplyAgreement());
        subscriptionTerminationReason.setApplyReimbursment(postData.isApplyReimbursment());
        subscriptionTerminationReason.setApplyTerminationCharges(postData.isApplyTerminationCharges());

        terminationReasonService.create(subscriptionTerminationReason);
    }

    /**
     * updates a SubscriptionTerminationReason based on code.
     * 
     * @param postData

     * @throws MeveoApiException meveo api exception
     * @throws BusinessException  business exception.
     */
    public void update(TerminationReasonDto postData) throws MeveoApiException, BusinessException {

        if (StringUtils.isBlank(postData.getCode())) {
            missingParameters.add("code");            
        }
        
        handleMissingParametersAndValidate(postData);
        
        SubscriptionTerminationReason subscriptionTerminationReason = terminationReasonService.findByCode(postData.getCode());

        if (subscriptionTerminationReason == null) {
            throw new EntityDoesNotExistsException(SubscriptionTerminationReason.class, postData.getCode());
        }
        subscriptionTerminationReason.setCode(StringUtils.isBlank(postData.getUpdatedCode()) ? postData.getCode() : postData.getUpdatedCode());
        subscriptionTerminationReason.setDescription(postData.getDescription());
        subscriptionTerminationReason.setApplyAgreement(postData.isApplyAgreement());
        subscriptionTerminationReason.setApplyReimbursment(postData.isApplyReimbursment());
        subscriptionTerminationReason.setApplyTerminationCharges(postData.isApplyTerminationCharges());

        terminationReasonService.update(subscriptionTerminationReason);
    }

    /**
     * removes a SubscriptionTerminationReason from db based on code.
     * 
     * @param code code of termination reason.

     * @throws MeveoApiException meveo api exception
     * @throws BusinessException  business exception.
     */
    public void remove(String code) throws MeveoApiException, BusinessException {

        if (StringUtils.isBlank(code)) {
            missingParameters.add("code");
            handleMissingParameters();
        }

        SubscriptionTerminationReason subscriptionTerminationReason = terminationReasonService.findByCode(code);

        if (subscriptionTerminationReason == null) {
            throw new EntityDoesNotExistsException(SubscriptionTerminationReason.class, code);
        }

        terminationReasonService.remove(subscriptionTerminationReason);
    }

    /**
     * create or updates a SubscriptionTerminationReason.
     * 
     * @param postData posted data

     * @throws MeveoApiException meveo api exception
     * @throws BusinessException  business exception.
     */
    public void createOrUpdate(TerminationReasonDto postData) throws MeveoApiException, BusinessException {

        if (StringUtils.isBlank(postData.getCode())) {
            missingParameters.add("code");
            handleMissingParameters();
        }

        SubscriptionTerminationReason subscriptionTerminationReason = terminationReasonService.findByCode(postData.getCode());

        if (subscriptionTerminationReason == null) {
            create(postData);
        } else {
            update(postData);
        }
    }

    /**
     * Retrieves a SubscriptionTerminationReason based on code.
     * 
     * @param code termination code

     * @return termiation reason.
     * @throws MeveoApiException meveo api exception.
     */
    public TerminationReasonDto find(String code) throws MeveoApiException {
        TerminationReasonDto terminationReasonDto = null;

        if (StringUtils.isBlank(code)) {
            missingParameters.add("terminationReasonCode");
            handleMissingParameters();
        }
        SubscriptionTerminationReason subscriptionTerminationReason = terminationReasonService.findByCode(code);

        if (subscriptionTerminationReason == null) {
            throw new EntityDoesNotExistsException(SubscriptionTerminationReason.class, code);
        }

        terminationReasonDto = new TerminationReasonDto(subscriptionTerminationReason);

        return terminationReasonDto;
    }

    /**
     * Retrieves all SubscriptionTerminationReason attached to the given provider.
     * 
     * @return list of termination reason
     * @throws MeveoApiException meveo apoi exception.
     */
    public List<TerminationReasonDto> list() throws MeveoApiException {
        List<TerminationReasonDto> terminationReasonDtos = new ArrayList<TerminationReasonDto>();

        List<SubscriptionTerminationReason> subscriptionTerminationReasons = terminationReasonService.list();

        if (subscriptionTerminationReasons != null && !subscriptionTerminationReasons.isEmpty()) {
            for (SubscriptionTerminationReason subscriptionTerminationReason : subscriptionTerminationReasons) {
                TerminationReasonDto terminationReasonDto = new TerminationReasonDto(subscriptionTerminationReason);
                terminationReasonDtos.add(terminationReasonDto);
            }
        }

        return terminationReasonDtos;
    }

}
