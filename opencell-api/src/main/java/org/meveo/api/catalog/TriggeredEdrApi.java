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
import org.meveo.model.scripts.ScriptInstance;
import org.meveo.service.catalog.impl.TriggeredEDRTemplateService;
import org.meveo.service.communication.impl.MeveoInstanceService;
import org.meveo.service.script.ScriptInstanceService;

/**
 * @author Edward P. Legaspi
 * @lastModifiedVersion 6.0
 **/
@Stateless
public class TriggeredEdrApi extends BaseApi {

    @Inject
    private TriggeredEDRTemplateService triggeredEDRTemplateService;

    @Inject
    private MeveoInstanceService meveoInstanceService;
    
    @Inject
    private ScriptInstanceService scriptInstanceService;

    public void create(TriggeredEdrTemplateDto postData) throws MeveoApiException, BusinessException {

        if (StringUtils.isBlank(postData.getCode())) {
            missingParameters.add("code");
        }
        if (StringUtils.isBlank(postData.getQuantityEl())) {
            missingParameters.add("quantityEl");
        }

        handleMissingParametersAndValidate(postData);

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
        edrTemplate.setOpencellInstanceEL(postData.getOpencellInstanceEL());
        if (!StringUtils.isBlank(postData.getTriggeredEdrScript())) {
            ScriptInstance si = scriptInstanceService.findByCode(postData.getTriggeredEdrScript());
            if (si == null) {
                throw new EntityDoesNotExistsException(ScriptInstance.class, postData.getTriggeredEdrScript());
            }
            edrTemplate.setTriggeredEdrScript(si);
        }

        triggeredEDRTemplateService.create(edrTemplate);
    }

    public void update(TriggeredEdrTemplateDto postData) throws MeveoApiException, BusinessException {

        if (StringUtils.isBlank(postData.getCode())) {
            missingParameters.add("code");
        }
        if (StringUtils.isBlank(postData.getQuantityEl())) {
            missingParameters.add("quantityEl");
        }

        handleMissingParametersAndValidate(postData);

        TriggeredEDRTemplate edrTemplate = triggeredEDRTemplateService.findByCode(postData.getCode());
        if (edrTemplate == null) {
            throw new EntityDoesNotExistsException(TriggeredEDRTemplate.class, postData.getCode());
        }

        if (postData.getDescription() != null) {
            edrTemplate.setDescription(postData.getDescription());
        }
        if (postData.getSubscriptionEl() != null) {
            edrTemplate.setSubscriptionEl(postData.getSubscriptionEl());
        }
        if (postData.getSubscriptionElSpark() != null) {
            edrTemplate.setSubscriptionElSpark(postData.getSubscriptionElSpark());
        }
        if (postData.getMeveoInstanceCode() != null) {
            MeveoInstance meveoInstance = meveoInstanceService.findByCode(postData.getMeveoInstanceCode());
            if (meveoInstance == null) {
                throw new EntityDoesNotExistsException(MeveoInstance.class, postData.getMeveoInstanceCode());
            }
            edrTemplate.setMeveoInstance(meveoInstance);
        }
        edrTemplate.setCode(StringUtils.isBlank(postData.getUpdatedCode()) ? postData.getCode() : postData.getUpdatedCode());
        if (postData.getConditionEl() != null) {
            edrTemplate.setConditionEl(postData.getConditionEl());
        }
        if (postData.getConditionElSpark() != null) {
            edrTemplate.setConditionElSpark(postData.getConditionElSpark());
        }
        if (postData.getQuantityEl() != null) {
            edrTemplate.setQuantityEl(postData.getQuantityEl());
        }
        if (postData.getQuantityElSpark() != null) {
            edrTemplate.setQuantityElSpark(postData.getQuantityElSpark());
        }
        if (postData.getParam1El() != null) {
            edrTemplate.setParam1El(postData.getParam1El());
        }
        if (postData.getParam1ElSpark() != null) {
            edrTemplate.setParam1ElSpark(postData.getParam1ElSpark());
        }
        if (postData.getParam2El() != null) {
            edrTemplate.setParam2El(postData.getParam2El());
        }
        if (postData.getParam2ElSpark() != null) {
            edrTemplate.setParam2ElSpark(postData.getParam2ElSpark());
        }
        if (postData.getParam3El() != null) {
            edrTemplate.setParam3El(postData.getParam3El());
        }
        if (postData.getParam3ElSpark() != null) {
            edrTemplate.setParam3ElSpark(postData.getParam3ElSpark());
        }
        if (postData.getParam4El() != null) {
            edrTemplate.setParam4El(postData.getParam4El());
        }
        if (postData.getParam4ElSpark() != null) {
            edrTemplate.setParam4ElSpark(postData.getParam4ElSpark());
        }
        if(postData.getOpencellInstanceEL() != null) {
            edrTemplate.setOpencellInstanceEL(postData.getOpencellInstanceEL());
        }
        if (postData.getTriggeredEdrScript() != null) {
            if (!StringUtils.isBlank(postData.getTriggeredEdrScript())) {
                ScriptInstance si = scriptInstanceService.findByCode(postData.getTriggeredEdrScript());
                if (si == null) {
                    throw new EntityDoesNotExistsException(ScriptInstance.class, postData.getTriggeredEdrScript());
                }
                edrTemplate.setTriggeredEdrScript(si);
            } else {
                edrTemplate.setTriggeredEdrScript(null);
            }
        }

        triggeredEDRTemplateService.update(edrTemplate);
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

        return new TriggeredEdrTemplateDto(edrTemplate);
    }

    public void createOrUpdate(TriggeredEdrTemplateDto postData) throws MeveoApiException, BusinessException {
        if (triggeredEDRTemplateService.findByCode(postData.getCode()) == null) {
            create(postData);
        } else {
            update(postData);
        }
    }
}
