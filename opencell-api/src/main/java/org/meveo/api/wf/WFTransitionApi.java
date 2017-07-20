package org.meveo.api.wf;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import javax.ejb.Stateless;
import javax.inject.Inject;

import org.apache.commons.collections.CollectionUtils;
import org.meveo.admin.exception.BusinessException;
import org.meveo.api.BaseApi;
import org.meveo.api.dto.payment.WFActionDto;
import org.meveo.api.dto.payment.WFDecisionRuleDto;
import org.meveo.api.dto.payment.WFTransitionDto;
import org.meveo.api.exception.BusinessApiException;
import org.meveo.api.exception.EntityAlreadyExistsException;
import org.meveo.api.exception.EntityDoesNotExistsException;
import org.meveo.api.exception.MissingParameterException;
import org.meveo.commons.utils.StringUtils;
import org.meveo.model.wf.WFAction;
import org.meveo.model.wf.WFDecisionRule;
import org.meveo.model.wf.WFTransition;
import org.meveo.model.wf.Workflow;
import org.meveo.service.wf.WFActionService;
import org.meveo.service.wf.WFDecisionRuleService;
import org.meveo.service.wf.WFTransitionService;

@Stateless
public class WFTransitionApi extends BaseApi {

    @Inject
    private WFActionApi wfActionApi;

    @Inject
    private WFTransitionService wfTransitionService;

    @Inject
    private WFDecisionRuleService wfDecisionRuleService;

    @Inject
    private WFActionService wfActionService;

    /**
     *
     * @param workflow
     * @param wfTransitionDto
     * 
     * @throws MissingParameterException
     * @throws EntityDoesNotExistsException
     * @throws EntityAlreadyExistsException
     * @throws BusinessException
     */
    public void create(Workflow workflow, WFTransitionDto wfTransitionDto)
            throws MissingParameterException, EntityDoesNotExistsException, EntityAlreadyExistsException, BusinessException {
        validateDto(wfTransitionDto, false);

        Set<WFDecisionRule> wfDecisionRuleList = new HashSet<>();
        if (CollectionUtils.isNotEmpty(wfTransitionDto.getListWFDecisionRuleDto())) {
            for (WFDecisionRuleDto wfDecisionRuleDto : wfTransitionDto.getListWFDecisionRuleDto()) {
                WFDecisionRule wfDecisionRule = wfDecisionRuleService.getWFDecisionRuleByNameValue(wfDecisionRuleDto.getName(), wfDecisionRuleDto.getValue());
                if (wfDecisionRule == null) {
                    wfDecisionRuleList.add(createNewWFDecisionRuleByName(wfDecisionRuleDto.getName(), wfDecisionRuleDto.getValue()));
                } else {
                    wfDecisionRuleList.add(wfDecisionRule);
                }
            }
        }
        WFTransition wfTransition;
        wfTransition = fromDTO(wfTransitionDto, null);
        wfTransition.setWorkflow(workflow);
        wfTransition.setWfDecisionRules(wfDecisionRuleList);
        wfTransitionService.create(wfTransition);
        if (CollectionUtils.isNotEmpty(wfTransitionDto.getListWFActionDto())) {
            int priority = 1;
            for (WFActionDto wfActionDto : wfTransitionDto.getListWFActionDto()) {
                wfActionDto.setPriority(priority);
                wfActionApi.create(wfTransition, wfActionDto);
                priority++;
            }
        }
    }

    /**
     * 
     * @param wfTransitionDto
     * 
     * @throws MissingParameterException
     * @throws EntityDoesNotExistsException
     * @throws EntityAlreadyExistsException
     * @throws BusinessException
     * @throws BusinessApiException
     */
    public void update(Workflow workflow, WFTransitionDto wfTransitionDto)
            throws MissingParameterException, EntityDoesNotExistsException, EntityAlreadyExistsException, BusinessException, BusinessApiException {
        validateDto(wfTransitionDto, true);

        WFTransition wfTransition = wfTransitionService.findWFTransitionByUUID(wfTransitionDto.getUuid());
        if (wfTransition == null) {
            throw new EntityDoesNotExistsException(WFTransition.class.getName() + "with uuid=" + wfTransitionDto.getUuid());
        }

        if (workflow.equals(wfTransition.getWorkflow())) {
            throw new BusinessApiException("Workflow does not match");
        }

        Set<WFDecisionRule> wfDecisionRuleList = new HashSet<>();
        if (CollectionUtils.isNotEmpty(wfTransitionDto.getListWFDecisionRuleDto())) {
            for (WFDecisionRuleDto wfDecisionRuleDto : wfTransitionDto.getListWFDecisionRuleDto()) {
                WFDecisionRule wfDecisionRule = wfDecisionRuleService.getWFDecisionRuleByNameValue(wfDecisionRuleDto.getName(), wfDecisionRuleDto.getValue());
                if (wfDecisionRule == null) {
                    wfDecisionRuleList.add(createNewWFDecisionRuleByName(wfDecisionRuleDto.getName(), wfDecisionRuleDto.getValue()));
                } else {
                    wfDecisionRuleList.add(wfDecisionRule);
                }
            }
        }

        wfTransition = fromDTO(wfTransitionDto, wfTransition);
        List<WFAction> wfActionList = wfTransition.getWfActions();
        wfTransition.setWorkflow(workflow);
        wfTransition.setWfDecisionRules(wfDecisionRuleList);
        wfTransitionService.update(wfTransition);
        List<WFAction> updatedActions = new ArrayList<>();
        if (CollectionUtils.isNotEmpty(wfTransitionDto.getListWFActionDto())) {
            for (WFActionDto wfActionDto : wfTransitionDto.getListWFActionDto()) {
                if (wfActionDto.getUuid() != null) {
                    WFAction wfAction = wfActionService.findWFActionByUUID(wfActionDto.getUuid());
                    updatedActions.add(wfAction);
                }
            }
        }

        if (CollectionUtils.isNotEmpty(wfActionList)) {
            wfActionList.removeAll(updatedActions);
            if (CollectionUtils.isNotEmpty(wfActionList)) {
                for (WFAction wfAction : wfActionList) {
                    wfActionService.remove(wfAction);
                }
            }
        }

        if (wfTransitionDto.getListWFActionDto() != null && !wfTransitionDto.getListWFActionDto().isEmpty()) {
            int priority = 1;
            for (WFActionDto wfActionDto : wfTransitionDto.getListWFActionDto()) {
                wfActionDto.setPriority(priority);
                wfActionApi.createOrUpdate(wfTransition, wfActionDto);
                priority++;
            }
        }
    }

    /**
     *
     * @param workflow
     * @param wfTransitionDto
     * 
     * @throws MissingParameterException
     * @throws EntityDoesNotExistsException
     * @throws EntityAlreadyExistsException
     * @throws BusinessException
     * @throws BusinessApiException
     */
    public void createOrUpdate(Workflow workflow, WFTransitionDto wfTransitionDto)
            throws MissingParameterException, EntityDoesNotExistsException, EntityAlreadyExistsException, BusinessException, BusinessApiException {
        WFTransition wfTransition = null;
        if (wfTransitionDto.getUuid() != null) {
            wfTransition = wfTransitionService.findWFTransitionByUUID(wfTransitionDto.getUuid());
        }
        if (wfTransition == null) {
            create(workflow, wfTransitionDto);
        } else {
            update(workflow, wfTransitionDto);
        }
    }

    /**
     * 
     * @param wfTransitionDto
     * @throws MissingParameterException
     */
    public void validateDto(WFTransitionDto wfTransitionDto, boolean isUpdate) throws MissingParameterException {
        if (wfTransitionDto == null) {
            missingParameters.add("WFTransitionDto");
            handleMissingParameters();
        }
        if (isUpdate && StringUtils.isBlank(wfTransitionDto.getUuid())) {
            missingParameters.add("uuid");
        }
        if (StringUtils.isBlank(wfTransitionDto.getFromStatus())) {
            missingParameters.add("FromStatus");
        }
        if (StringUtils.isBlank(wfTransitionDto.getToStatus())) {
            missingParameters.add("ToStatus");
        }
        if (StringUtils.isBlank(wfTransitionDto.getDescription())) {
            missingParameters.add("Description");
        }

        if (CollectionUtils.isNotEmpty(wfTransitionDto.getListWFDecisionRuleDto())) {
            for (WFDecisionRuleDto wfDecisionRuleDto : wfTransitionDto.getListWFDecisionRuleDto()) {
                if (StringUtils.isBlank(wfDecisionRuleDto.getName())) {
                    missingParameters.add("DecisionRuleName");
                }
                if (StringUtils.isBlank(wfDecisionRuleDto.getValue())) {
                    missingParameters.add("DecisionRuleValue");
                }
            }
        }

        handleMissingParameters();
    }

    public WFTransition findTransitionByUUID(String uuid) {
        return wfTransitionService.findWFTransitionByUUID(uuid);
    }

    protected WFTransition fromDTO(WFTransitionDto dto, WFTransition wfTransitionToUpdate) {
        WFTransition wfTransition = new WFTransition();
        if (wfTransitionToUpdate != null) {
            wfTransition = wfTransitionToUpdate;
        }

        wfTransition.setFromStatus(dto.getFromStatus());
        wfTransition.setToStatus(dto.getToStatus());
        wfTransition.setConditionEl(dto.getConditionEl());
        wfTransition.setPriority(dto.getPriority());
        wfTransition.setDescription(dto.getDescription());

        return wfTransition;
    }

    protected WFDecisionRule createNewWFDecisionRuleByName(String name, String value) throws EntityDoesNotExistsException, BusinessException {
        WFDecisionRule wfDecisionRule = wfDecisionRuleService.getWFDecisionRuleByName(name);
        if (wfDecisionRule == null) {
            throw new EntityDoesNotExistsException(WFDecisionRule.class, name);
        }
        WFDecisionRule newWFDecisionRule = new WFDecisionRule();
        newWFDecisionRule.setModel(Boolean.FALSE);
        newWFDecisionRule.setConditionEl(wfDecisionRule.getConditionEl());
        newWFDecisionRule.setName(wfDecisionRule.getName());
        newWFDecisionRule.setType(wfDecisionRule.getType());
        newWFDecisionRule.setDisabled(Boolean.FALSE);
        newWFDecisionRule.setValue(value);
        wfDecisionRuleService.create(newWFDecisionRule);
        return newWFDecisionRule;
    }
}
