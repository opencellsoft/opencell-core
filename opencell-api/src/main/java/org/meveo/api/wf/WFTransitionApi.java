/*
 * (C) Copyright 2015-2020 Opencell SAS (https://opencellsoft.com/) and contributors.
 *
 * This program is free software: you can redistribute it and/or modify it under the terms of the GNU Affero General
 * Public License as published by the Free Software Foundation, either version 3 of the License, or (at your option)
 * any later version.
 *
 * THERE IS NO WARRANTY FOR THE PROGRAM, TO THE EXTENT PERMITTED BY APPLICABLE LAW. EXCEPT WHEN
 * OTHERWISE STATED IN WRITING THE COPYRIGHT HOLDERS AND/OR OTHER PARTIES PROVIDE THE PROGRAM "AS
 * IS" WITHOUT WARRANTY OF ANY KIND, EITHER EXPRESSED OR IMPLIED, INCLUDING, BUT NOT LIMITED TO, THE
 * IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE. THE ENTIRE RISK AS TO
 * THE QUALITY AND PERFORMANCE OF THE PROGRAM IS WITH YOU. SHOULD THE PROGRAM PROVE DEFECTIVE,
 * YOU ASSUME THE COST OF ALL NECESSARY SERVICING, REPAIR OR CORRECTION.
 *
 * For more information on the GNU Affero General Public License, please consult
 * <https://www.gnu.org/licenses/agpl-3.0.en.html>.
 */

package org.meveo.api.wf;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import jakarta.ejb.Stateless;
import jakarta.inject.Inject;

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
     * Create Workflow
     *  
     * @param workflow the parent workflow
     * @param wfTransitionDto the transition that will be created and added to the given workflow
     * 
     * @throws MissingParameterException missing parameter
     * @throws EntityDoesNotExistsException lookup entity does not exist
     * @throws EntityAlreadyExistsException entity being created already exists
     * @throws BusinessException generic business exception
     * @return WFTransition Workflow transition
     */
    public WFTransition create(Workflow workflow, WFTransitionDto wfTransitionDto)
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
        WFTransition wfTransition = fromDTO(wfTransitionDto, null);
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
        return wfTransition;
    }

    /**
     * Update Workflow
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
    public WFTransition update(Workflow workflow, WFTransitionDto wfTransitionDto)
            throws MissingParameterException, EntityDoesNotExistsException, EntityAlreadyExistsException, BusinessException, BusinessApiException {
        validateDto(wfTransitionDto, true);

        WFTransition wfTransition = wfTransitionService.findWFTransitionByUUID(wfTransitionDto.getUuid());
        if (wfTransition == null) {
            throw new EntityDoesNotExistsException(WFTransition.class.getName() + "with uuid=" + wfTransitionDto.getUuid());
        }

        if (!workflow.equals(wfTransition.getWorkflow())) {
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
        wfTransition = wfTransitionService.update(wfTransition);
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
        return wfTransition;
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
            wfTransition = wfTransitionService.findWFTransitionByUUID(wfTransitionDto.getUuid());
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
     * @param wfTransitionDto Workflow transition Dto
     * @param isUpdate indicates that Dto is for update
     * @throws MissingParameterException Missing one or more parameters
     */
    public void validateDto(WFTransitionDto wfTransitionDto, boolean isUpdate) throws MissingParameterException {
        if (wfTransitionDto == null) {
            missingParameters.add("WFTransitionDto");
            handleMissingParameters();
        }
        if (wfTransitionDto != null) {
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
        return wfTransitionService.findWFTransitionByUUID(uuid);
    }

    /**
     * Transform Workflow transition Dto to Workflow transition entity
     * 
     * @param dto Workflow transition Dto
     * @param wfTransitionToUpdate Workflow transition to update
     * @return Workflow transition entity
     */
    protected WFTransition fromDTO(WFTransitionDto dto, WFTransition wfTransitionToUpdate) {
        WFTransition wfTransition = wfTransitionToUpdate;
        if (wfTransitionToUpdate == null) {
            wfTransition = new WFTransition();
            if (dto.getUuid() != null) {
                wfTransition.setUuid(dto.getUuid());
            }
        }

        wfTransition.setFromStatus(dto.getFromStatus());
        wfTransition.setToStatus(dto.getToStatus());
        wfTransition.setConditionEl(dto.getConditionEl());
        wfTransition.setPriority(dto.getPriority());
        wfTransition.setDescription(dto.getDescription());

        return wfTransition;
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
