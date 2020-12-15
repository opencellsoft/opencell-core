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

package org.meveo.api.generic.wf;

import static java.util.Optional.ofNullable;
import static org.meveo.api.dto.ActionStatusEnum.SUCCESS;
import static org.meveo.api.MeveoApiErrorCodeEnum.CONDITION_FALSE;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import javax.ejb.Stateless;
import javax.inject.Inject;

import org.apache.commons.collections.CollectionUtils;
import org.hibernate.Hibernate;
import org.meveo.admin.exception.BusinessException;
import org.meveo.api.BaseCrudApi;

import org.meveo.api.dto.ActionStatus;
import org.meveo.api.dto.FilterDto;
import org.meveo.api.dto.generic.wf.GWFTransitionDto;
import org.meveo.api.dto.generic.wf.GenericWorkflowDto;
import org.meveo.api.dto.generic.wf.WFStatusDto;
import org.meveo.api.dto.generic.wf.WorkflowInstanceHistoryDto;
import org.meveo.api.exception.BusinessApiException;
import org.meveo.api.exception.EntityAlreadyExistsException;
import org.meveo.api.exception.EntityDoesNotExistsException;
import org.meveo.api.exception.EntityNotAllowedException;
import org.meveo.api.exception.InvalidParameterException;
import org.meveo.api.exception.MeveoApiException;
import org.meveo.api.exception.MissingParameterException;
import org.meveo.commons.utils.StringUtils;
import org.meveo.model.BaseEntity;
import org.meveo.model.BusinessEntity;
import org.meveo.model.filter.Filter;
import org.meveo.model.generic.wf.GWFTransition;
import org.meveo.model.generic.wf.GenericWorkflow;
import org.meveo.model.generic.wf.WFStatus;
import org.meveo.model.generic.wf.WorkflowInstance;
import org.meveo.model.generic.wf.WorkflowInstanceHistory;
import org.meveo.service.filter.FilterService;
import org.meveo.service.generic.wf.GWFTransitionService;
import org.meveo.service.generic.wf.GenericWorkflowService;
import org.meveo.service.generic.wf.WFStatusService;
import org.meveo.service.generic.wf.WorkflowInstanceHistoryService;

/**
 * The Class GenericWorkflowApi
 *
 * @author Amine Ben Aicha
 * @author Mounir Bahije
 * @lastModifiedVersion 7.0
 */
@Stateless
public class GenericWorkflowApi extends BaseCrudApi<GenericWorkflow, GenericWorkflowDto> {

    @Inject
    private GenericWorkflowService genericWorkflowService;

    @Inject
    private WorkflowInstanceHistoryService workflowInstanceHistoryService;

    @Inject
    private GWFTransitionApi gwfTransitionApi;

    @Inject
    private GWFTransitionService gwfTransitionService;

    @Inject
    private WFStatusService wfStatusService;

    @Inject
    private FilterService filterService;

    @Override
    public GenericWorkflow create(GenericWorkflowDto genericWorkflowDto) throws MeveoApiException, BusinessException {

        validateDto(genericWorkflowDto, false);

        GenericWorkflow genericWorkflow = genericWorkflowService.findByCode(genericWorkflowDto.getCode());
        if (genericWorkflow != null) {
            throw new EntityAlreadyExistsException(GenericWorkflow.class, genericWorkflowDto.getCode());
        }

        boolean noneMatch = genericWorkflowDto.getStatuses().stream().noneMatch(s -> s.getCode().equals(genericWorkflowDto.getInitStatus()));
        if (noneMatch) {
            throw new EntityNotAllowedException(WFStatus.class, GenericWorkflow.class, genericWorkflowDto.getInitStatus());
        }
        genericWorkflow = new GenericWorkflow();
        genericWorkflow = fromDTO(genericWorkflowDto, genericWorkflow);
        
        FilterDto filterDto = genericWorkflowDto.getFilter();
        if (filterDto != null && filterDto.getCode() != null) {
            Filter filter = (Filter) Hibernate.unproxy(filterService.findByCode(filterDto.getCode()));
            if (filter == null) {
                filter = filterFromDto(genericWorkflowDto.getFilter());
                filterService.create(filter);
                genericWorkflow.setFilter(filter);
            } else {
                filter = filterFromDto(genericWorkflowDto.getFilter(), filter);
                genericWorkflow.setFilter(filterService.update(filter));
            }
        }
        
        genericWorkflowService.create(genericWorkflow);

        for (WFStatusDto wfStatusDto : genericWorkflowDto.getStatuses()) {
            WFStatus wfStatus = wfStatusDto.toWFStatus();
            wfStatus.setGenericWorkflow(genericWorkflow);
            wfStatusService.create(wfStatus);
        }

        if (genericWorkflowDto.getTransitions() != null && !genericWorkflowDto.getTransitions().isEmpty()) {
           
            for (GWFTransitionDto wfTransitionDto : genericWorkflowDto.getTransitions()) {              
                gwfTransitionApi.create(genericWorkflow, wfTransitionDto);
            }
        }

        return genericWorkflow;
    }

    @Override
    public GenericWorkflow update(GenericWorkflowDto genericWorkflowDto) throws MeveoApiException, BusinessException {

        validateDto(genericWorkflowDto, true);

        GenericWorkflow genericWorkflow = genericWorkflowService.findByCode(genericWorkflowDto.getCode(), Arrays.asList("transitions"));
        if (genericWorkflow == null) {
            throw new EntityDoesNotExistsException(GenericWorkflow.class, genericWorkflowDto.getCode());
        }

        if (!StringUtils.isBlank(genericWorkflowDto.getTargetEntityClass()) && !genericWorkflow.getTargetEntityClass().equals(genericWorkflowDto.getTargetEntityClass())) {
            throw new BusinessApiException("Workflow target class does not match");
        }

        if (!StringUtils.isBlank(genericWorkflowDto.getTargetCetCode()) && !genericWorkflow.getTargetCetCode().equals(genericWorkflowDto.getTargetCetCode())) {
            throw new BusinessApiException("Workflow target CET code does not match");
        }

        if (CollectionUtils.isNotEmpty(genericWorkflowDto.getStatuses())) {
            boolean noneMatch = genericWorkflowDto.getStatuses().stream().noneMatch(s -> s.getCode().equals(genericWorkflowDto.getInitStatus()));
            if (noneMatch) {
                throw new EntityNotAllowedException(WFStatus.class, GenericWorkflow.class, genericWorkflowDto.getInitStatus());
            }
        }

        genericWorkflow = fromDTO(genericWorkflowDto, genericWorkflow);
        
        FilterDto filterDto = genericWorkflowDto.getFilter();
        if (filterDto != null && filterDto.getCode() != null) {
            Filter filter = (Filter) Hibernate.unproxy(filterService.findByCode(filterDto.getCode()));
            if (filter == null) {
                filter = filterFromDto(genericWorkflowDto.getFilter());
                filterService.create(filter);
                genericWorkflow.setFilter(filter);
            } else {
                filter = filterFromDto(genericWorkflowDto.getFilter(), filter);
                genericWorkflow.setFilter(filterService.update(filter));
            }
        }
        
        genericWorkflow = genericWorkflowService.update(genericWorkflow);
        
        // Update Transitions
        List<GWFTransition> listUpdate = new ArrayList<>();
        if (CollectionUtils.isNotEmpty(genericWorkflowDto.getTransitions())) {
            for (GWFTransitionDto gwfTransitionDto : genericWorkflowDto.getTransitions()) {
                if (gwfTransitionDto.getUuid() != null) {
                    GWFTransition gwfTransition = gwfTransitionApi.findTransitionByUUID(gwfTransitionDto.getUuid());
                    if (gwfTransition != null) {
                        listUpdate.add(gwfTransition);
                    }
                }
            }
        }

        List<GWFTransition> currentGWfTransitions = genericWorkflow.getTransitions();
        List<GWFTransition> gwfTransitionsToRemove = new ArrayList<>();
        if (CollectionUtils.isNotEmpty(currentGWfTransitions)) {
            currentGWfTransitions.removeAll(listUpdate);
            if (CollectionUtils.isNotEmpty(currentGWfTransitions)) {
                for (GWFTransition gwfTransition : currentGWfTransitions) {
                    gwfTransitionsToRemove.add(gwfTransition);
                    gwfTransitionService.remove(gwfTransition);
                }
            }
        }

        genericWorkflow.getTransitions().removeAll(gwfTransitionsToRemove);

        if (genericWorkflowDto.getTransitions() != null && !genericWorkflowDto.getTransitions().isEmpty()) {           
            for (GWFTransitionDto gwfTransitionDto : genericWorkflowDto.getTransitions()) {                
                gwfTransitionApi.createOrUpdate(genericWorkflow, gwfTransitionDto);               
            }
        }

        return genericWorkflow;
    }

    /**
     * (non-Javadoc)
     * 
     * @see org.meveo.api.ApiService#find(java.lang.String)
     */
    @Override
    public GenericWorkflowDto find(String workflowCode) throws EntityDoesNotExistsException, MissingParameterException, InvalidParameterException, MeveoApiException {

        if (StringUtils.isBlank(workflowCode)) {
            missingParameters.add("workflowCode");
            handleMissingParameters();
        }
        GenericWorkflow genericWorkflow = genericWorkflowService.findByCode(workflowCode);
        if (genericWorkflow == null) {
            throw new EntityDoesNotExistsException(GenericWorkflow.class, workflowCode);
        }

        return new GenericWorkflowDto(genericWorkflow);
    }

    /**
     * Return list of workflow dto
     * 
     * @return list of workflow dto
     */
    public List<GenericWorkflowDto> list() {
        List<GenericWorkflowDto> result = new ArrayList<>();
        List<GenericWorkflow> workflows = genericWorkflowService.list();
        if (workflows != null) {
            for (GenericWorkflow workflow : workflows) {
                result.add(new GenericWorkflowDto(workflow));
            }
        }
        return result;
    }

    protected GenericWorkflow fromDTO(GenericWorkflowDto dto, GenericWorkflow genericWorkflow) {
        genericWorkflow.setTargetEntityClass(dto.getTargetEntityClass());
        genericWorkflow.setTargetCetCode(dto.getTargetCetCode());
        if (dto.isDisabled() != null) {
            genericWorkflow.setDisabled(dto.isDisabled());
        }
        if (dto.getFilter() != null && dto.getFilter().getCode() == null) {
            genericWorkflow.setFilter(null);
        }

        genericWorkflow.setCode(dto.getCode());
        genericWorkflow.setDescription(dto.getDescription());
        genericWorkflow.setEnableHistory(dto.getEnableHistory());
        genericWorkflow.setInitStatus(dto.getInitStatus());

        return genericWorkflow;
    }

    private Filter filterFromDto(FilterDto filterDto) {
        if (filterDto == null) {
            return null;
        }
        Filter filter = new Filter();
        filter.clearUuid();
        filter.setCode(filterDto.getCode());
        filter.setDescription(filterDto.getDescription());
        filter.setInputXml(filterDto.getInputXml());
        if(filterDto.getShared() != null) {
            filter.setShared(filterDto.getShared());
        }
        filter.setPollingQuery(filterDto.getPollingQuery());
        return filter;
    }
    
    private Filter filterFromDto(FilterDto filterDto, Filter filter) {
        if (filterDto == null) {
            return filter;
        }
        filter.setDescription(filterDto.getDescription());
        filter.setInputXml(filterDto.getInputXml());
        if(filterDto.getShared() != null) {
            filter.setShared(filterDto.getShared());
        }
        filter.setPollingQuery(filterDto.getPollingQuery());
        return filter;
    }

    /**
     * Validate Workflow Dto
     *
     * @param genericWorkflowDto Workflow Dto
     * @param isUpdate           Indicates that Dto is for update
     * @throws MissingParameterException Missing one or more parameters
     */
    public void validateDto(GenericWorkflowDto genericWorkflowDto, boolean isUpdate) throws MissingParameterException {
        if (StringUtils.isBlank(genericWorkflowDto.getCode())) {
            missingParameters.add("code");
        }
        if (!isUpdate && StringUtils.isBlank(genericWorkflowDto.getTargetEntityClass())) {
            missingParameters.add("targetEntityClass");
        }
        if (!isUpdate && genericWorkflowDto.getStatuses().isEmpty()) {
            missingParameters.add("statuses");
        }
        if (!isUpdate && StringUtils.isBlank(genericWorkflowDto.getInitStatus())) {
            missingParameters.add("initStatus");
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
    public List<GenericWorkflowDto> findByEntity(String baseEntityName) throws MeveoApiException {
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
        List<GenericWorkflowDto> listWfDto = new ArrayList<>();
        List<GenericWorkflow> listWF = genericWorkflowService.findByTargetEntityClass(clazz.getName());
        for (GenericWorkflow wf : listWF) {
            listWfDto.add(new GenericWorkflowDto(wf));
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

        GenericWorkflow genericWorkflow = genericWorkflowService.findByCode(workflowCode);
        if (genericWorkflow == null) {
            throw new EntityDoesNotExistsException(GenericWorkflow.class, workflowCode);
        }
        log.debug("genericWorkflow.getCode() : " + genericWorkflow.getCode());

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

        genericWorkflowService.executeWorkflow(businessEntity, genericWorkflow);
    }

    /**
     * 
     * @param entityInstanceCode
     * @param workflowCode
     * @param fromStatus
     * @param toStatus
     * @return list of Workflow instance history Dto
     */
    public List<WorkflowInstanceHistoryDto> findHistory(String entityInstanceCode, String workflowCode, String fromStatus, String toStatus) {

        List<WorkflowInstanceHistory> wfInsHistory = workflowInstanceHistoryService.find(entityInstanceCode, workflowCode, fromStatus, toStatus);
        List<WorkflowInstanceHistoryDto> result = new ArrayList<>();
        if (wfInsHistory != null) {
            for (WorkflowInstanceHistory wfHistory : wfInsHistory) {
                WorkflowInstanceHistoryDto wfHistoryDto = new WorkflowInstanceHistoryDto(wfHistory);
                result.add(wfHistoryDto);
            }
        }
        return result;
    }

    public ActionStatus executeTransition(String baseEntityName, String entityInstanceCode, String workflowCode, String transitionUUID,
                                          boolean ignoreConditionEL) throws BusinessException, MeveoApiException {
        if (StringUtils.isBlank(baseEntityName)) {
            missingParameters.add("baseEntityName");
            handleMissingParameters();
        }
        if (StringUtils.isBlank(entityInstanceCode)) {
            missingParameters.add("entityInstanceCode");
            handleMissingParameters();
        }
        GenericWorkflow genericWorkflow = ofNullable(genericWorkflowService.findByCode(workflowCode))
                .orElseThrow(() -> new EntityDoesNotExistsException(GenericWorkflow.class, workflowCode));
        BusinessEntity businessEntity = ofNullable(businessEntityFrom(baseEntityName, entityInstanceCode ))
                .orElseThrow(() -> new EntityDoesNotExistsException(BaseEntity.class, entityInstanceCode ));
        GWFTransition transition =  ofNullable(gwfTransitionService.findWFTransitionByUUID(transitionUUID))
                .orElseThrow(() -> new EntityDoesNotExistsException(GWFTransition.class, transitionUUID));
        WorkflowInstance result = genericWorkflowService.executeTransition(transition, businessEntity, genericWorkflow, ignoreConditionEL);
        if (result == null) {
            throw new MeveoApiException(CONDITION_FALSE, "Transition not executed: condition is false");
        }
        return new ActionStatus(SUCCESS, "Transition executed successfully");
    }

    private BusinessEntity businessEntityFrom(String baseEntityName, String entityInstanceCode) {
        Class<BusinessEntity> clazz;
        try {
            clazz = (Class<BusinessEntity>) Class.forName(baseEntityName);
        } catch (Exception e) {
            throw new MeveoApiException("Can not find class for baseEntityName");
        }
        businessEntityService.setEntityClass(clazz);
        return businessEntityService.findByCode(entityInstanceCode);
    }
}