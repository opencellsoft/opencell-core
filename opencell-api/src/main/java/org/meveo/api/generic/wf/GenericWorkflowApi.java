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
import org.meveo.api.dto.payment.WFTransitionDto;
import org.meveo.api.dto.payment.WorkflowDto;
import org.meveo.api.dto.payment.WorkflowHistoryDto;
import org.meveo.api.exception.BusinessApiException;
import org.meveo.api.exception.EntityAlreadyExistsException;
import org.meveo.api.exception.EntityDoesNotExistsException;
import org.meveo.api.exception.InvalidParameterException;
import org.meveo.api.exception.MeveoApiException;
import org.meveo.api.exception.MissingParameterException;
import org.meveo.api.wf.WFTransitionApi;
import org.meveo.commons.utils.StringUtils;
import org.meveo.model.BaseEntity;
import org.meveo.model.BusinessEntity;
import org.meveo.model.generic.wf.GenericWorkflow;
import org.meveo.model.wf.WFTransition;
import org.meveo.model.wf.Workflow;
import org.meveo.model.wf.WorkflowHistory;
import org.meveo.service.generic.wf.GenericWorkflowService;
import org.meveo.service.wf.WFTransitionService;
import org.meveo.service.wf.WorkflowHistoryService;

@Stateless
public class GenericWorkflowApi extends BaseCrudApi<GenericWorkflow, GenericWorkflowDto> {

    @Inject
    private GenericWorkflowService genericWorkflowService;

    @Inject
    private WorkflowHistoryService workflowHistoryService;

    @Inject
    private WFTransitionApi wfTransitionApi;

    @Inject
    private WFTransitionService wfTransitionService;

    @Override
    public GenericWorkflow create(GenericWorkflowDto genericWorkflowDto) throws MeveoApiException, BusinessException {

        validateDto(genericWorkflowDto, false);

        GenericWorkflow genericWorkflow = genericWorkflowService.findByCode(genericWorkflowDto.getCode());
        if (genericWorkflow != null) {
            throw new EntityAlreadyExistsException(GenericWorkflow.class, genericWorkflowDto.getCode());
        }

        genericWorkflow = fromDTO(genericWorkflowDto, null);
        genericWorkflowService.create(genericWorkflow);

        if (genericWorkflowDto.getListGWFTransitionDto() != null && !genericWorkflowDto.getListGWFTransitionDto().isEmpty()) {
            int priority = 1;
            for (GWFTransitionDto wfTransitionDto : genericWorkflowDto.getListGWFTransitionDto()) {
                wfTransitionDto.setPriority(priority);
                wfTransitionApi.create(genericWorkflow, wfTransitionDto);
                priority++;
            }
        }

        return genericWorkflow;
    }

    @Override
    public GenericWorkflow update(WorkflowDto workflowDto) throws MeveoApiException, BusinessException {

        validateDto(workflowDto, true);

        GenericWorkflow workflow = genericWorkflowService.findByCode(workflowDto.getCode(), Arrays.asList("transitions"));
        if (workflow == null) {
            throw new EntityDoesNotExistsException(GenericWorkflow.class, workflowDto.getCode());
        }

        if (workflowDto.getWfType() != null && !workflow.getWfType().equals(workflowDto.getWfType())) {
            throw new BusinessApiException("Workflow type does not match");
        }

        workflow = fromDTO(workflowDto, workflow);
        workflow = genericWorkflowService.update(workflow);

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
        GenericWorkflow workflow = genericWorkflowService.findByCode(workflowCode);
        if (workflow == null) {
            throw new EntityDoesNotExistsException(GenericWorkflow.class, workflowCode);
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
        List<GenericWorkflow> workflows = genericWorkflowService.list();
        if (workflows != null) {
            for (GenericWorkflow workflow : workflows) {
                result.add(new WorkflowDto(workflow));
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
        List<GenericWorkflow> listWF = genericWorkflowService.findByEntity(clazz);
        for (GenericWorkflow wf : listWF) {
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

        genericWorkflowService.executeWorkflow(businessEntity, workflowCode);
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
