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
import org.meveo.model.admin.User;
import org.meveo.model.billing.SubscriptionTerminationReason;
import org.meveo.model.crm.Provider;
import org.meveo.service.billing.impl.TerminationReasonService;

@Stateless
public class TerminationReasonApi extends BaseApi {

    @Inject
    private TerminationReasonService terminationReasonService;

    /**
     * creates a SubscriptionTerminationReason based on code and description.
     * 
     * @param postData
     * @param currentUser
     * @throws MeveoApiException
     * @throws BusinessException 
     */
    public void create(TerminationReasonDto postData, User currentUser) throws MeveoApiException, BusinessException {

        if (StringUtils.isBlank(postData.getCode())) {
            missingParameters.add("code");
            handleMissingParameters();
        }

        if (terminationReasonService.findByCode(postData.getCode(), currentUser.getProvider()) != null) {
            throw new EntityAlreadyExistsException(SubscriptionTerminationReason.class, postData.getCode());
        }

        SubscriptionTerminationReason subscriptionTerminationReason = new SubscriptionTerminationReason();

        subscriptionTerminationReason.setCode(postData.getCode());
        subscriptionTerminationReason.setDescription(postData.getDescription());
        subscriptionTerminationReason.setApplyAgreement(postData.isApplyAgreement());
        subscriptionTerminationReason.setApplyReimbursment(postData.isApplyReimbursment());
        subscriptionTerminationReason.setApplyTerminationCharges(postData.isApplyTerminationCharges());

        terminationReasonService.create(subscriptionTerminationReason, currentUser);
    }

    /**
     * updates a SubscriptionTerminationReason based on code.
     * 
     * @param postData
     * @param currentUser
     * @throws MeveoApiException
     * @throws BusinessException 
     */
    public void update(TerminationReasonDto postData, User currentUser) throws MeveoApiException, BusinessException {

        if (StringUtils.isBlank(postData.getCode())) {
            missingParameters.add("code");
            handleMissingParameters();
        }
        SubscriptionTerminationReason subscriptionTerminationReason = terminationReasonService.findByCode(postData.getCode(), currentUser.getProvider());

        if (subscriptionTerminationReason == null) {
            throw new EntityDoesNotExistsException(SubscriptionTerminationReason.class, postData.getCode());
        }
        
        subscriptionTerminationReason.setDescription(postData.getDescription());
        subscriptionTerminationReason.setApplyAgreement(postData.isApplyAgreement());
        subscriptionTerminationReason.setApplyReimbursment(postData.isApplyReimbursment());
        subscriptionTerminationReason.setApplyTerminationCharges(postData.isApplyTerminationCharges());

        terminationReasonService.update(subscriptionTerminationReason, currentUser);
    }

    /**
     * removes a SubscriptionTerminationReason from db based on code.
     * 
     * @param code
     * @param currentUser
     * @throws MeveoApiException
     * @throws BusinessException 
     */
    public void remove(String code, User currentUser) throws MeveoApiException, BusinessException {

        if (StringUtils.isBlank(code)) {
            missingParameters.add("code");
            handleMissingParameters();
        }

        SubscriptionTerminationReason subscriptionTerminationReason = terminationReasonService.findByCode(code, currentUser.getProvider());

        if (subscriptionTerminationReason == null) {
            throw new EntityDoesNotExistsException(SubscriptionTerminationReason.class, code);
        }

        terminationReasonService.remove(subscriptionTerminationReason, currentUser);
    }

    /**
     * create or updates a SubscriptionTerminationReason.
     * 
     * @param postData
     * @param currentUser
     * @throws MeveoApiException
     * @throws BusinessException 
     */
    public void createOrUpdate(TerminationReasonDto postData, User currentUser) throws MeveoApiException, BusinessException {

        if (StringUtils.isBlank(postData.getCode())) {
            missingParameters.add("code");
            handleMissingParameters();
        }

        SubscriptionTerminationReason subscriptionTerminationReason = terminationReasonService.findByCode(postData.getCode(), currentUser.getProvider());

        if (subscriptionTerminationReason == null) {
            create(postData, currentUser);
        } else {
            update(postData, currentUser);
        }
    }

    /**
     * Retrieves a SubscriptionTerminationReason based on code.
     * 
     * @param code
     * @param currentUser
     * @return
     * @throws MeveoApiException
     */
    public TerminationReasonDto find(String code, User currentUser) throws MeveoApiException {
        TerminationReasonDto terminationReasonDto = null;

        if (StringUtils.isBlank(code)) {
            missingParameters.add("terminationReasonCode");
            handleMissingParameters();
        }
        SubscriptionTerminationReason subscriptionTerminationReason = terminationReasonService.findByCode(code, currentUser.getProvider());

        if (subscriptionTerminationReason == null) {
            throw new EntityDoesNotExistsException(SubscriptionTerminationReason.class, code);
        }

        terminationReasonDto = new TerminationReasonDto(subscriptionTerminationReason);

        return terminationReasonDto;
    }

    /**
     * Retrieves all SubscriptionTerminationReason attached to the given provider.
     * 
     * @param provider
     * @return
     * @throws MeveoApiException
     */
    public List<TerminationReasonDto> list(Provider provider) throws MeveoApiException {
        List<TerminationReasonDto> terminationReasonDtos = new ArrayList<TerminationReasonDto>();

        List<SubscriptionTerminationReason> subscriptionTerminationReasons = terminationReasonService.list(provider);

        if (subscriptionTerminationReasons != null && !subscriptionTerminationReasons.isEmpty()) {
            for (SubscriptionTerminationReason subscriptionTerminationReason : subscriptionTerminationReasons) {
                TerminationReasonDto terminationReasonDto = new TerminationReasonDto(subscriptionTerminationReason);
                terminationReasonDtos.add(terminationReasonDto);
            }
        }

        return terminationReasonDtos;
    }

}
