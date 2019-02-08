package org.meveo.api.generic.wf;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import javax.ejb.Stateless;
import javax.inject.Inject;

import org.apache.commons.collections.CollectionUtils;
import org.meveo.admin.exception.BusinessException;
import org.meveo.api.BaseApi;
import org.meveo.api.dto.generic.wf.GWFTransitionDto;
import org.meveo.api.dto.payment.WFActionDto;
import org.meveo.api.dto.payment.WFDecisionRuleDto;
import org.meveo.api.dto.payment.WFTransitionDto;
import org.meveo.api.exception.BusinessApiException;
import org.meveo.api.exception.EntityAlreadyExistsException;
import org.meveo.api.exception.EntityDoesNotExistsException;
import org.meveo.api.exception.MissingParameterException;
import org.meveo.commons.utils.StringUtils;
import org.meveo.model.generic.wf.GWFTransition;
import org.meveo.model.generic.wf.GenericWorkflow;
import org.meveo.model.scripts.ScriptInstance;
import org.meveo.model.wf.WFAction;
import org.meveo.model.wf.WFDecisionRule;
import org.meveo.model.wf.WFTransition;
import org.meveo.model.wf.Workflow;
import org.meveo.service.generic.wf.GWFTransitionService;
import org.meveo.service.script.ScriptInstanceService;

@Stateless
public class GWFTransitionApi extends BaseApi {

    @Inject
    private GWFTransitionService gwfTransitionService;

    @Inject
    private ScriptInstanceService scriptInstanceService;

    /**
     * Create Workflow
     * 
     * @param genericWorkflow the parent generic workflow
     * @param gwfTransitionDto the transition that will be created and added to the given workflow
     * 
     * @throws MissingParameterException missing parameter
     * @throws EntityDoesNotExistsException lookup entity does not exist
     * @throws EntityAlreadyExistsException entity being created already exists
     * @throws BusinessException generic business exception
     * @return GWFTransition Workflow transition
     */
    public GWFTransition create(GenericWorkflow genericWorkflow, GWFTransitionDto gwfTransitionDto)
            throws MissingParameterException, EntityDoesNotExistsException, EntityAlreadyExistsException, BusinessException {
        validateDto(gwfTransitionDto, false);

        GWFTransition gwfTransition = fromDTO(gwfTransitionDto, null);
        gwfTransition.setGenericWorkflow(genericWorkflow);

        gwfTransitionService.create(gwfTransition);
        return gwfTransition;
    }

    /**
     * Update Workflow
     *
     * @param genericWorkflow workflow of the transition that will be updated
     * @param gwfTransitionDto details of the transition that will be updated
     * 
     * @throws MissingParameterException missing parameter
     * @throws EntityDoesNotExistsException lookup entity does not exist
     * @throws EntityAlreadyExistsException entity being created already exists
     * @throws BusinessException generic business exception
     * @throws BusinessApiException equivalent of business exception in api context
     * @return WFTransition Workflow transition
     */
    public GWFTransition update(GenericWorkflow genericWorkflow, GWFTransitionDto gwfTransitionDto)
            throws MissingParameterException, EntityDoesNotExistsException, EntityAlreadyExistsException, BusinessException, BusinessApiException {
        validateDto(gwfTransitionDto, true);

        GWFTransition gwfTransition = gwfTransitionService.findWFTransitionByUUID(gwfTransitionDto.getUuid());
        if (gwfTransition == null) {
            throw new EntityDoesNotExistsException(WFTransition.class.getName() + "with uuid=" + gwfTransitionDto.getUuid());
        }

        if (!genericWorkflow.equals(gwfTransition.getGenericWorkflow())) {
            throw new BusinessApiException("Workflow does not match");
        }

        Set<WFDecisionRule> wfDecisionRuleList = new HashSet<>();
        if (CollectionUtils.isNotEmpty(gwfTransitionDto.getListWFDecisionRuleDto())) {
            for (WFDecisionRuleDto wfDecisionRuleDto : gwfTransitionDto.getListWFDecisionRuleDto()) {
                WFDecisionRule wfDecisionRule = wfDecisionRuleService.getWFDecisionRuleByNameValue(wfDecisionRuleDto.getName(), wfDecisionRuleDto.getValue());
                if (wfDecisionRule == null) {
                    wfDecisionRuleList.add(createNewWFDecisionRuleByName(wfDecisionRuleDto.getName(), wfDecisionRuleDto.getValue()));
                } else {
                    wfDecisionRuleList.add(wfDecisionRule);
                }
            }
        }

        gwfTransition = fromDTO(gwfTransitionDto, gwfTransition);
        List<WFAction> wfActionList = gwfTransition.getWfActions();
        gwfTransition.setWorkflow(genericWorkflow);
        gwfTransition.setWfDecisionRules(wfDecisionRuleList);
        gwfTransition = gwfTransitionService.update(gwfTransition);
        List<WFAction> updatedActions = new ArrayList<>();
        if (CollectionUtils.isNotEmpty(gwfTransitionDto.getListWFActionDto())) {
            for (WFActionDto wfActionDto : gwfTransitionDto.getListWFActionDto()) {
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

        if (gwfTransitionDto.getListWFActionDto() != null && !gwfTransitionDto.getListWFActionDto().isEmpty()) {
            int priority = 1;
            for (WFActionDto wfActionDto : gwfTransitionDto.getListWFActionDto()) {
                wfActionDto.setPriority(priority);
                wfActionApi.createOrUpdate(gwfTransition, wfActionDto);
                priority++;
            }
        }
        return gwfTransition;
    }

    /**
     * Create or update Workflow
     *
     * @param workflow workflow of the transition that will be updated
     * @param wfTransitionDto details of the transition that will be updated
     * 
     * @throws MissingParameterException missing parameter
     * @throws EntityDoesNotExistsException lookup entity does not exist
     * @throws EntityAlreadyExistsException entity being created already exists
     * @throws BusinessException generic business exception
     * @throws BusinessApiException equivalent of business exception in api context
     * @return WFTransition Workflow transition
     */
    public WFTransition createOrUpdate(Workflow workflow, WFTransitionDto wfTransitionDto)
            throws MissingParameterException, EntityDoesNotExistsException, EntityAlreadyExistsException, BusinessException, BusinessApiException {

        WFTransition wfTransition = null;
        if (wfTransitionDto.getUuid() != null) {
            wfTransition = gwfTransitionService.findWFTransitionByUUID(wfTransitionDto.getUuid());
        }
        if (wfTransition == null) {
            return create(workflow, wfTransitionDto);
        } else {
            return update(workflow, wfTransitionDto);
        }
    }

    /**
     * Validate Workflow transition Dto
     *
     * @param gwfTransitionDto Workflow transition Dto
     * @param isUpdate indicates that Dto is for update
     * @throws MissingParameterException Missing one or more parameters
     */
    public void validateDto(GWFTransitionDto gwfTransitionDto, boolean isUpdate) throws MissingParameterException {
        if (gwfTransitionDto == null) {
            missingParameters.add("GWFTransitionDto");
            handleMissingParameters();
        }
        if (gwfTransitionDto != null) {
            if (isUpdate && StringUtils.isBlank(gwfTransitionDto.getUuid())) {
                missingParameters.add("uuid");
            }
            if (StringUtils.isBlank(gwfTransitionDto.getFromStatus())) {
                missingParameters.add("FromStatus");
            }
            if (StringUtils.isBlank(gwfTransitionDto.getToStatus())) {
                missingParameters.add("ToStatus");
            }
            if (StringUtils.isBlank(gwfTransitionDto.getDescription())) {
                missingParameters.add("Description");
            }
        }

        handleMissingParameters();
    }

    /**
     * Find Workflow transition by uuid
     *
     * @param uuid uuid of workflow transition
     * @return Workflow transition
     */
    public WFTransition findTransitionByUUID(String uuid) {
        return gwfTransitionService.findWFTransitionByUUID(uuid);
    }

    /**
     * Transform Workflow transition Dto to Generic Workflow transition entity
     * 
     * @param dto Workflow transition Dto
     * @param gwfTransitionToUpdate Workflow transition to update
     * @return Workflow transition entity
     */
    protected GWFTransition fromDTO(GWFTransitionDto dto, GWFTransition gwfTransitionToUpdate) {
        GWFTransition gwfTransition = gwfTransitionToUpdate;
        if (gwfTransitionToUpdate == null) {
            gwfTransition = new GWFTransition();
            if (dto.getUuid() != null) {
                gwfTransition.setUuid(dto.getUuid());
            }
        }

        gwfTransition.setFromStatus(dto.getFromStatus());
        gwfTransition.setToStatus(dto.getToStatus());
        gwfTransition.setConditionEl(dto.getConditionEl());
        gwfTransition.setPriority(dto.getPriority());
        gwfTransition.setDescription(dto.getDescription());

        if (dto.getActionScriptCode() != null) {
            ScriptInstance actionScript = scriptInstanceService.findByCode(dto.getActionScriptCode());
            gwfTransition.setActionScript(actionScript);
        }

        return gwfTransition;
    }

    /**
     * Create new workflow decision rule by name
     * 
     * @param name Workflow decision rule name
     * @param value Workflow decision rule value
     * @throws EntityDoesNotExistsException lookup entity does not exist
     * @throws BusinessException generic business exception
     * @return Workflow decision rule entity
     */
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
        newWFDecisionRule.setValue(value);
        wfDecisionRuleService.create(newWFDecisionRule);
        return newWFDecisionRule;
    }
}
