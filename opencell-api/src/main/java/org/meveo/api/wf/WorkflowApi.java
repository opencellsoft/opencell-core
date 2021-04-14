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
import java.util.Arrays;
import java.util.List;

import javax.ejb.Stateless;
import javax.inject.Inject;

import org.apache.commons.collections.CollectionUtils;
import org.meveo.admin.exception.BusinessException;
import org.meveo.api.BaseCrudApi;
import org.meveo.api.dto.payment.WFTransitionDto;
import org.meveo.api.dto.payment.WorkflowDto;
import org.meveo.api.dto.payment.WorkflowHistoryDto;
import org.meveo.api.exception.BusinessApiException;
import org.meveo.api.exception.EntityAlreadyExistsException;
import org.meveo.api.exception.EntityDoesNotExistsException;
import org.meveo.api.exception.InvalidParameterException;
import org.meveo.api.exception.MeveoApiException;
import org.meveo.api.exception.MissingParameterException;
import org.meveo.commons.utils.StringUtils;
import org.meveo.model.BaseEntity;
import org.meveo.model.BusinessEntity;
import org.meveo.model.wf.WFTransition;
import org.meveo.model.wf.Workflow;
import org.meveo.model.wf.WorkflowHistory;
import org.meveo.service.wf.WFTransitionService;
import org.meveo.service.wf.WorkflowHistoryService;
import org.meveo.service.wf.WorkflowService;

@Stateless
public class WorkflowApi extends BaseCrudApi<Workflow, WorkflowDto> {

    @Inject
    private WorkflowService workflowService;

    @Inject
    private WorkflowHistoryService workflowHistoryService;

    @Inject
    private WFTransitionApi wfTransitionApi;

    @Inject
    private WFTransitionService wfTransitionService;

    @Override
    public Workflow create(WorkflowDto workflowDto) throws MeveoApiException, BusinessException {

        validateDto(workflowDto, false);

        Workflow workflow = workflowService.findByCode(workflowDto.getCode());
        if (workflow != null) {
            throw new EntityAlreadyExistsException(Workflow.class, workflowDto.getCode());
        }

        workflow = fromDTO(workflowDto, null);
        workflowService.create(workflow);

        if (workflowDto.getListWFTransitionDto() != null && !workflowDto.getListWFTransitionDto().isEmpty()) {
            int priority = 1;
            for (WFTransitionDto wfTransitionDto : workflowDto.getListWFTransitionDto()) {
                wfTransitionDto.setPriority(priority);
                wfTransitionApi.create(workflow, wfTransitionDto);
                priority++;
            }
        }

        return workflow;
    }

    @Override
    public Workflow update(WorkflowDto workflowDto) throws MeveoApiException, BusinessException {

        validateDto(workflowDto, true);

        Workflow workflow = workflowService.findByCode(workflowDto.getCode(), Arrays.asList("transitions"));
        if (workflow == null) {
            throw new EntityDoesNotExistsException(Workflow.class, workflowDto.getCode());
        }

        if (workflowDto.getWfType() != null && !workflow.getWfType().equals(workflowDto.getWfType())) {
            throw new BusinessApiException("Workflow type does not match");
        }

        workflow = fromDTO(workflowDto, workflow);
        workflow = workflowService.update(workflow);

        List<WFTransition> listUpdate = new ArrayList<>();
        if (CollectionUtils.isNotEmpty(workflowDto.getListWFTransitionDto())) {
            for (WFTransitionDto wfTransitionDto : workflowDto.getListWFTransitionDto()) {
                if (wfTransitionDto.getUuid() != null) {
                    WFTransition wfTransition = wfTransitionApi.findTransitionByUUID(wfTransitionDto.getUuid());
                    if (wfTransition != null) {
                        listUpdate.add(wfTransition);
                    }
                }
            }
        }

        List<WFTransition> currentWfTransitions = workflow.getTransitions();
        List<WFTransition> wfTransitionsToRemove = new ArrayList<>();
        if (CollectionUtils.isNotEmpty(currentWfTransitions)) {
            currentWfTransitions.removeAll(listUpdate);
            if (CollectionUtils.isNotEmpty(currentWfTransitions)) {
                for (WFTransition wfTransition : currentWfTransitions) {
                    wfTransitionsToRemove.add(wfTransition);
                    wfTransitionService.remove(wfTransition);
                }
            }
        }

        workflow.getTransitions().removeAll(wfTransitionsToRemove);

        if (workflowDto.getListWFTransitionDto() != null && !workflowDto.getListWFTransitionDto().isEmpty()) {
            int priority = 1;
            for (WFTransitionDto wfTransitionDto : workflowDto.getListWFTransitionDto()) {
                wfTransitionDto.setPriority(priority);
                wfTransitionApi.createOrUpdate(workflow, wfTransitionDto);
                priority++;

            }
        }

        return workflow;
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.meveo.api.ApiService#find(java.lang.String)
     */
    @Override
    public WorkflowDto find(String workflowCode) throws EntityDoesNotExistsException, MissingParameterException, InvalidParameterException, MeveoApiException {

        if (StringUtils.isBlank(workflowCode)) {
            missingParameters.add("workflowCode");
            handleMissingParameters();
        }
        Workflow workflow = workflowService.findByCode(workflowCode);
        if (workflow == null) {
            throw new EntityDoesNotExistsException(Workflow.class, workflowCode);
        }

        return new WorkflowDto(workflow);
    }

    /**
     * Return list of workflow dto
     * 
     * @return list of workflow dto
     */
    public List<WorkflowDto> list() {
        List<WorkflowDto> result = new ArrayList<>();
        List<Workflow> workflows = workflowService.list();
        if (workflows != null) {
            for (Workflow workflow : workflows) {
                result.add(new WorkflowDto(workflow));
            }
        }
        return result;
    }

    protected Workflow fromDTO(WorkflowDto dto, Workflow workflowToUpdate) {
        Workflow workflow = workflowToUpdate;
        if (workflowToUpdate == null) {
            workflow = new Workflow();
            workflow.setWfType(dto.getWfType());
            if (dto.isDisabled() != null) {
                workflow.setDisabled(dto.isDisabled());
            }
        }

        workflow.setCode(dto.getCode());
        workflow.setDescription(dto.getDescription());
        workflow.setEnableHistory(dto.getEnableHistory());

        return workflow;
    }

    /**
     * Validate Workflow Dto
     * 
     * @param workflowDto Workflow Dto
     * @param isUpdate Indicates that Dto is for update
     * @throws MissingParameterException Missing one or more parameters
     */
    public void validateDto(WorkflowDto workflowDto, boolean isUpdate) throws MissingParameterException {
        if (StringUtils.isBlank(workflowDto.getCode())) {
            addGenericCodeIfAssociated(Workflow.class.getName(), workflowDto);
        }
        if (!isUpdate && StringUtils.isBlank(workflowDto.getWfType())) {
            missingParameters.add("WFType");
        }
        handleMissingParameters();
    }

    /**
     * Find a Workflow by an Entity
     * 
     * @param baseEntityName Base entity name
     * @return list of Workflow Dto
     * @throws MeveoApiException Meveo api exception
     */
    @SuppressWarnings("unchecked")
    public List<WorkflowDto> findByEntity(String baseEntityName) throws MeveoApiException {
        if (StringUtils.isBlank(baseEntityName)) {
            missingParameters.add("baseEntityName");
            handleMissingParameters();
        }
        Class<? extends BusinessEntity> clazz = null;
        try {
            clazz = (Class<? extends BusinessEntity>) Class.forName(baseEntityName);
        } catch (Exception e) {
            throw new MeveoApiException("Cant find class for baseEntityName");
        }
        List<WorkflowDto> listWfDto = new ArrayList<WorkflowDto>();
        List<Workflow> listWF = workflowService.findByEntity(clazz);
        for (Workflow wf : listWF) {
            listWfDto.add(new WorkflowDto(wf));
        }
        return listWfDto;
    }

    /**
     * 
     * @param baseEntityName Base entity name
     * @param baseEntityInstanceId Base entity instance Id
     * @param workflowCode Workflow code
     * @throws BusinessException General business exception
     * @throws MeveoApiException Meveo api exception
     */
    @SuppressWarnings("unchecked")
    public void execute(String baseEntityName, String baseEntityInstanceId, String workflowCode) throws BusinessException, MeveoApiException {
        if (StringUtils.isBlank(baseEntityName)) {
            missingParameters.add("baseEntityName");
            handleMissingParameters();
        }

        if (StringUtils.isBlank(baseEntityInstanceId)) {
            missingParameters.add("baseEntityInstanceId");
            handleMissingParameters();
        }

        Class<BusinessEntity> clazz = null;
        try {
            clazz = (Class<BusinessEntity>) Class.forName(baseEntityName);
        } catch (Exception e) {
            throw new MeveoApiException("Cant find class for baseEntityName");
        }
        businessEntityService.setEntityClass(clazz);

        BusinessEntity businessEntity = businessEntityService.findByCode(baseEntityInstanceId);
        if (businessEntity == null) {
            throw new EntityDoesNotExistsException(BaseEntity.class, baseEntityInstanceId);
        }
        log.debug("businessEntity.getCode() : " + businessEntity.getCode());

        workflowService.executeWorkflow(businessEntity, workflowCode);
    }

    public List<WorkflowHistoryDto> findHistory(String entityInstanceCode, String workflowCode, String fromStatus, String toStatus) {

        List<WorkflowHistory> wfsHistory = workflowHistoryService.find(entityInstanceCode, workflowCode, fromStatus, toStatus);
        List<WorkflowHistoryDto> result = new ArrayList<WorkflowHistoryDto>();
        if (wfsHistory != null) {
            for (WorkflowHistory wfHistory : wfsHistory) {
                WorkflowHistoryDto wfHistoryDto = new WorkflowHistoryDto(wfHistory);
                result.add(wfHistoryDto);
            }
        }
        return result;
    }
}
