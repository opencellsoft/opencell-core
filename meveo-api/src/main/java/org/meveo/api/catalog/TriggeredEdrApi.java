package org.meveo.api.catalog;

import javax.ejb.Stateless;
import javax.inject.Inject;

import org.meveo.admin.exception.BusinessException;
import org.meveo.api.BaseApi;
import org.meveo.api.dto.catalog.TriggeredEdrTemplateDto;
import org.meveo.api.exception.EntityAlreadyExistsException;
import org.meveo.api.exception.EntityDoesNotExistsException;
import org.meveo.api.exception.MeveoApiException;
import org.meveo.api.exception.MissingParameterException;
import org.meveo.commons.utils.StringUtils;
import org.meveo.model.admin.User;
import org.meveo.model.catalog.TriggeredEDRTemplate;
import org.meveo.model.communication.MeveoInstance;
import org.meveo.model.crm.Provider;
import org.meveo.service.catalog.impl.TriggeredEDRTemplateService;
import org.meveo.service.communication.impl.MeveoInstanceService;

/**
 * @author Edward P. Legaspi
 **/
@Stateless
public class TriggeredEdrApi extends BaseApi {

    @Inject
    private TriggeredEDRTemplateService triggeredEDRTemplateService;
    
    @Inject 
    private MeveoInstanceService meveoInstanceService;

    public void create(TriggeredEdrTemplateDto postData, User currentUser) throws MeveoApiException, BusinessException {
        if (!StringUtils.isBlank(postData.getCode()) && !StringUtils.isBlank(postData.getQuantityEl())) {
            if (triggeredEDRTemplateService.findByCode(postData.getCode(), currentUser.getProvider()) != null) {
                throw new EntityAlreadyExistsException(TriggeredEDRTemplate.class, postData.getCode());
            }

            TriggeredEDRTemplate edrTemplate = new TriggeredEDRTemplate();
            edrTemplate.setCode(postData.getCode());
            edrTemplate.setDescription(postData.getDescription());
            edrTemplate.setSubscriptionEl(postData.getSubscriptionEl());
            if(postData.getMeveoInstanceCode()!=null){
            	MeveoInstance meveoInstance=meveoInstanceService.findByCode(postData.getMeveoInstanceCode());
            	if (meveoInstance == null) {
                    throw new EntityDoesNotExistsException(MeveoInstance.class, postData.getMeveoInstanceCode());
                }
            	edrTemplate.setMeveoInstance(meveoInstance);
            }
            edrTemplate.setConditionEl(postData.getConditionEl());
            edrTemplate.setQuantityEl(postData.getQuantityEl());
            edrTemplate.setParam1El(postData.getParam1El());
            edrTemplate.setParam2El(postData.getParam2El());
            edrTemplate.setParam3El(postData.getParam3El());
            edrTemplate.setParam4El(postData.getParam4El());

            triggeredEDRTemplateService.create(edrTemplate, currentUser);
        } else {
            if (StringUtils.isBlank(postData.getCode())) {
                missingParameters.add("code");
            }
            if (StringUtils.isBlank(postData.getQuantityEl())) {
                missingParameters.add("quantityEl");
            }

            handleMissingParameters();
        }
    }

    public void update(TriggeredEdrTemplateDto postData, User currentUser) throws MeveoApiException, BusinessException {
        if (!StringUtils.isBlank(postData.getCode()) && !StringUtils.isBlank(postData.getQuantityEl())) {
            TriggeredEDRTemplate edrTemplate = triggeredEDRTemplateService.findByCode(postData.getCode(), currentUser.getProvider());
            if (edrTemplate == null) {
                throw new EntityDoesNotExistsException(TriggeredEDRTemplate.class, postData.getCode());
            }

            edrTemplate.setDescription(postData.getDescription());
            edrTemplate.setSubscriptionEl(postData.getSubscriptionEl());
            if(postData.getMeveoInstanceCode()!=null){
            	MeveoInstance meveoInstance=meveoInstanceService.findByCode(postData.getMeveoInstanceCode());
            	if (meveoInstance == null) {
                    throw new EntityDoesNotExistsException(MeveoInstance.class, postData.getMeveoInstanceCode());
                }
            	edrTemplate.setMeveoInstance(meveoInstance);
            }
            edrTemplate.setConditionEl(postData.getConditionEl());
            edrTemplate.setQuantityEl(postData.getQuantityEl());
            edrTemplate.setParam1El(postData.getParam1El());
            edrTemplate.setParam2El(postData.getParam2El());
            edrTemplate.setParam3El(postData.getParam3El());
            edrTemplate.setParam4El(postData.getParam4El());

            triggeredEDRTemplateService.update(edrTemplate, currentUser);
        } else {
            if (StringUtils.isBlank(postData.getCode())) {
                missingParameters.add("code");
            }
            if (StringUtils.isBlank(postData.getQuantityEl())) {
                missingParameters.add("quantityEl");
            }

            handleMissingParameters();
        }
    }

    public void remove(String triggeredEdrCode, User currentUser) throws MissingParameterException, EntityDoesNotExistsException, BusinessException {
        if (!StringUtils.isBlank(triggeredEdrCode)) {
            TriggeredEDRTemplate edrTemplate = triggeredEDRTemplateService.findByCode(triggeredEdrCode, currentUser.getProvider());
            if (edrTemplate == null) {
                throw new EntityDoesNotExistsException(TriggeredEDRTemplate.class, triggeredEdrCode);
            }

            triggeredEDRTemplateService.remove(edrTemplate, currentUser);
        } else {
            missingParameters.add("code");

            handleMissingParameters();
        }
    }

    public TriggeredEdrTemplateDto find(String triggeredEdrCode, Provider provider) throws MeveoApiException {
        if (StringUtils.isBlank(triggeredEdrCode)) {
            missingParameters.add("code");
        }
        handleMissingParameters();

        TriggeredEDRTemplate edrTemplate = triggeredEDRTemplateService.findByCode(triggeredEdrCode, provider);
        if (edrTemplate == null) {
            throw new EntityDoesNotExistsException(TriggeredEDRTemplate.class, triggeredEdrCode);
        }

        TriggeredEdrTemplateDto edrTemplateDto = new TriggeredEdrTemplateDto(edrTemplate);
        return edrTemplateDto;
    }

    public void createOrUpdate(TriggeredEdrTemplateDto postData, User currentUser) throws MeveoApiException, BusinessException {
        if (triggeredEDRTemplateService.findByCode(postData.getCode(), currentUser.getProvider()) == null) {
            create(postData, currentUser);
        } else {
            update(postData, currentUser);
        }
    }
}
