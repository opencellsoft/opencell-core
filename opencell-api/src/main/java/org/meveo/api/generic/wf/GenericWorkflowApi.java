package org.meveo.api.generic.wf;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import javax.ejb.Stateless;
import javax.inject.Inject;

import org.apache.commons.collections.CollectionUtils;
import org.meveo.admin.exception.BusinessException;
import org.meveo.api.BaseCrudApi;
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
import org.meveo.model.generic.wf.GWFTransition;
import org.meveo.model.generic.wf.GenericWorkflow;
import org.meveo.model.generic.wf.WFStatus;
import org.meveo.model.generic.wf.WorkflowInstanceHistory;
import org.meveo.service.generic.wf.GWFTransitionService;
import org.meveo.service.generic.wf.GenericWorkflowService;
import org.meveo.service.generic.wf.WFStatusService;
import org.meveo.service.generic.wf.WorkflowInstanceHistoryService;

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

        genericWorkflow = fromDTO(genericWorkflowDto, null);
        genericWorkflowService.create(genericWorkflow);

        for (WFStatusDto wfStatusDto : genericWorkflowDto.getStatuses()) {
            WFStatus wfStatus = wfStatusDto.toWFStatus();
            wfStatus.setGenericWorkflow(genericWorkflow);
            wfStatusService.create(wfStatus);
        }

        if (genericWorkflowDto.getTransitions() != null && !genericWorkflowDto.getTransitions().isEmpty()) {
            int priority = 1;
            for (GWFTransitionDto wfTransitionDto : genericWorkflowDto.getTransitions()) {
                wfTransitionDto.setPriority(priority);
                gwfTransitionApi.create(genericWorkflow, wfTransitionDto);
                priority++;
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

        if (CollectionUtils.isNotEmpty(genericWorkflowDto.getStatuses())) {
            boolean noneMatch = genericWorkflowDto.getStatuses().stream().noneMatch(s -> s.getCode().equals(genericWorkflowDto.getInitStatus()));
            if (noneMatch) {
                throw new EntityNotAllowedException(WFStatus.class, GenericWorkflow.class, genericWorkflowDto.getInitStatus());
            }
        }

        genericWorkflow = fromDTO(genericWorkflowDto, genericWorkflow);
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
            int priority = 1;
            for (GWFTransitionDto gwfTransitionDto : genericWorkflowDto.getTransitions()) {
                gwfTransitionDto.setPriority(priority);
                gwfTransitionApi.createOrUpdate(genericWorkflow, gwfTransitionDto);
                priority++;

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

    protected GenericWorkflow fromDTO(GenericWorkflowDto dto, GenericWorkflow workflowToUpdate) {
        GenericWorkflow genericWorkflow = workflowToUpdate;
        if (workflowToUpdate == null) {
            genericWorkflow = new GenericWorkflow();
            genericWorkflow.setTargetEntityClass(dto.getTargetEntityClass());
            if (dto.isDisabled() != null) {
                genericWorkflow.setDisabled(dto.isDisabled());
            }
        }

        genericWorkflow.setCode(dto.getCode());
        genericWorkflow.setDescription(dto.getDescription());
        genericWorkflow.setEnableHistory(dto.getEnableHistory());
        genericWorkflow.setInitStatus(dto.getInitStatus());

        return genericWorkflow;
    }

    /**
     * Validate Workflow Dto
     * 
     * @param genericWorkflowDto Workflow Dto
     * @param isUpdate Indicates that Dto is for update
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
}
