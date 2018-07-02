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
import org.meveo.model.catalog.TriggeredEDRTemplate;
import org.meveo.model.communication.MeveoInstance;
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

    public void create(TriggeredEdrTemplateDto postData) throws MeveoApiException, BusinessException {
        if (!StringUtils.isBlank(postData.getCode()) && !StringUtils.isBlank(postData.getQuantityEl())) {
            if (triggeredEDRTemplateService.findByCode(postData.getCode()) != null) {
                throw new EntityAlreadyExistsException(TriggeredEDRTemplate.class, postData.getCode());
            }

            TriggeredEDRTemplate edrTemplate = new TriggeredEDRTemplate();
            edrTemplate.setCode(postData.getCode());
            edrTemplate.setDescription(postData.getDescription());
            edrTemplate.setSubscriptionEl(postData.getSubscriptionEl());
            edrTemplate.setSubscriptionElSpark(postData.getSubscriptionElSpark());
            if (postData.getMeveoInstanceCode() != null) {
                MeveoInstance meveoInstance = meveoInstanceService.findByCode(postData.getMeveoInstanceCode());
                if (meveoInstance == null) {
                    throw new EntityDoesNotExistsException(MeveoInstance.class, postData.getMeveoInstanceCode());
                }
                edrTemplate.setMeveoInstance(meveoInstance);
            }
            edrTemplate.setConditionEl(postData.getConditionEl());
            edrTemplate.setConditionElSpark(postData.getConditionElSpark());
            edrTemplate.setQuantityEl(postData.getQuantityEl());
            edrTemplate.setQuantityElSpark(postData.getQuantityElSpark());
            edrTemplate.setParam1El(postData.getParam1El());
            edrTemplate.setParam1ElSpark(postData.getParam1ElSpark());
            edrTemplate.setParam2El(postData.getParam2El());
            edrTemplate.setParam2ElSpark(postData.getParam2ElSpark());
            edrTemplate.setParam3El(postData.getParam3El());
            edrTemplate.setParam3ElSpark(postData.getParam3ElSpark());
            edrTemplate.setParam4El(postData.getParam4El());
            edrTemplate.setParam4ElSpark(postData.getParam4ElSpark());

            triggeredEDRTemplateService.create(edrTemplate);
        } else {
            if (StringUtils.isBlank(postData.getCode())) {
                missingParameters.add("code");
            }
            if (StringUtils.isBlank(postData.getQuantityEl())) {
                missingParameters.add("quantityEl");
            }

            handleMissingParametersAndValidate(postData);
        }
    }

    public void update(TriggeredEdrTemplateDto postData) throws MeveoApiException, BusinessException {
        if (!StringUtils.isBlank(postData.getCode()) && !StringUtils.isBlank(postData.getQuantityEl())) {
            TriggeredEDRTemplate edrTemplate = triggeredEDRTemplateService.findByCode(postData.getCode());
            if (edrTemplate == null) {
                throw new EntityDoesNotExistsException(TriggeredEDRTemplate.class, postData.getCode());
            }

            edrTemplate.setDescription(postData.getDescription());
            edrTemplate.setSubscriptionEl(postData.getSubscriptionEl());
            edrTemplate.setSubscriptionElSpark(postData.getSubscriptionElSpark());
            if (postData.getMeveoInstanceCode() != null) {
                MeveoInstance meveoInstance = meveoInstanceService.findByCode(postData.getMeveoInstanceCode());
                if (meveoInstance == null) {
                    throw new EntityDoesNotExistsException(MeveoInstance.class, postData.getMeveoInstanceCode());
                }
                edrTemplate.setMeveoInstance(meveoInstance);
            }
            edrTemplate.setCode(StringUtils.isBlank(postData.getUpdatedCode()) ? postData.getCode() : postData.getUpdatedCode());
            edrTemplate.setConditionEl(postData.getConditionEl());
            edrTemplate.setConditionElSpark(postData.getConditionElSpark());
            edrTemplate.setQuantityEl(postData.getQuantityEl());
            edrTemplate.setQuantityElSpark(postData.getQuantityElSpark());
            edrTemplate.setParam1El(postData.getParam1El());
            edrTemplate.setParam1ElSpark(postData.getParam1ElSpark());
            edrTemplate.setParam2El(postData.getParam2El());
            edrTemplate.setParam2ElSpark(postData.getParam2ElSpark());
            edrTemplate.setParam3El(postData.getParam3El());
            edrTemplate.setParam3ElSpark(postData.getParam3ElSpark());
            edrTemplate.setParam4El(postData.getParam4El());
            edrTemplate.setParam4ElSpark(postData.getParam4ElSpark());

            triggeredEDRTemplateService.update(edrTemplate);
        } else {
            if (StringUtils.isBlank(postData.getCode())) {
                missingParameters.add("code");
            }
            if (StringUtils.isBlank(postData.getQuantityEl())) {
                missingParameters.add("quantityEl");
            }

            handleMissingParametersAndValidate(postData);
        }
    }

    public void remove(String triggeredEdrCode) throws MissingParameterException, EntityDoesNotExistsException, BusinessException {
        if (!StringUtils.isBlank(triggeredEdrCode)) {
            TriggeredEDRTemplate edrTemplate = triggeredEDRTemplateService.findByCode(triggeredEdrCode);
            if (edrTemplate == null) {
                throw new EntityDoesNotExistsException(TriggeredEDRTemplate.class, triggeredEdrCode);
            }

            triggeredEDRTemplateService.remove(edrTemplate);
        } else {
            missingParameters.add("code");

            handleMissingParameters();
        }
    }

    public TriggeredEdrTemplateDto find(String triggeredEdrCode) throws MeveoApiException {
        if (StringUtils.isBlank(triggeredEdrCode)) {
            missingParameters.add("code");
        }
        handleMissingParameters();

        TriggeredEDRTemplate edrTemplate = triggeredEDRTemplateService.findByCode(triggeredEdrCode);
        if (edrTemplate == null) {
            throw new EntityDoesNotExistsException(TriggeredEDRTemplate.class, triggeredEdrCode);
        }

        TriggeredEdrTemplateDto edrTemplateDto = new TriggeredEdrTemplateDto(edrTemplate);
        return edrTemplateDto;
    }

    public void createOrUpdate(TriggeredEdrTemplateDto postData) throws MeveoApiException, BusinessException {
        if (triggeredEDRTemplateService.findByCode(postData.getCode()) == null) {
            create(postData);
        } else {
            update(postData);
        }
    }
}
