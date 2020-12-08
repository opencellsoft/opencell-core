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

import static java.util.UUID.randomUUID;
import static java.util.Optional.ofNullable;

import javax.ejb.Stateless;
import javax.inject.Inject;

import org.meveo.admin.exception.BusinessException;
import org.meveo.api.BaseApi;
import org.meveo.api.dto.generic.wf.GWFActionDto;
import org.meveo.api.dto.generic.wf.GWFTransitionDto;
import org.meveo.api.exception.BusinessApiException;
import org.meveo.api.exception.EntityAlreadyExistsException;
import org.meveo.api.exception.EntityDoesNotExistsException;
import org.meveo.api.exception.MissingParameterException;
import org.meveo.commons.utils.StringUtils;
import org.meveo.model.generic.wf.Action;
import org.meveo.model.generic.wf.GWFTransition;
import org.meveo.model.generic.wf.GenericWorkflow;
import org.meveo.model.notification.Notification;
import org.meveo.model.scripts.ScriptInstance;
import org.meveo.model.wf.WFTransition;
import org.meveo.service.generic.wf.GWFTransitionService;
import org.meveo.service.notification.NotificationService;
import org.meveo.service.script.ScriptInstanceService;

@Stateless
public class GWFTransitionApi extends BaseApi {

    @Inject
    private GWFTransitionService gwfTransitionService;

    @Inject
    private ScriptInstanceService scriptInstanceService;

    @Inject
    private NotificationService notificationService;

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

        gwfTransition = fromDTO(gwfTransitionDto, gwfTransition);
        gwfTransition.setGenericWorkflow(genericWorkflow);
        gwfTransition = gwfTransitionService.update(gwfTransition);

        return gwfTransition;
    }

    /**
     * Create or update Workflow
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
    public GWFTransition createOrUpdate(GenericWorkflow genericWorkflow, GWFTransitionDto gwfTransitionDto)
            throws MissingParameterException, EntityDoesNotExistsException, EntityAlreadyExistsException, BusinessException, BusinessApiException {

        GWFTransition gwfTransition = null;
        if (gwfTransitionDto.getUuid() != null) {
            gwfTransition = gwfTransitionService.findWFTransitionByUUID(gwfTransitionDto.getUuid());
        }
        if (gwfTransition == null) {
            return create(genericWorkflow, gwfTransitionDto);
        } else {
            return update(genericWorkflow, gwfTransitionDto);
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
    public GWFTransition findTransitionByUUID(String uuid) {
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
        for (GWFActionDto action : dto.getActions()) {
            gwfTransition.getActions().add(from(action, gwfTransition));
        }
        return gwfTransition;
    }

    public Action from(GWFActionDto actionDto, GWFTransition gwfTransition) {
        Action action = new Action();
        if(actionDto.getUuid() == null) {
            action.setUuid(randomUUID().toString());
        } else {
            action.setUuid(actionDto.getUuid());
        }
        action.setTransition(gwfTransition);
        action.setType(actionDto.getType().name());
        action.setConditionEl(actionDto.getConditionEl());
        ofNullable(actionDto.getField()).ifPresent(field -> action.setFieldToUpdate(field));
        action.setValueEL(actionDto.getValueEl());
        action.setDescription(actionDto.getDescription());
        ofNullable(actionDto.getLogLevel())
                .ifPresent(log -> action.setLogLevel(log.toString()));
        action.setAsynchronous(actionDto.isAsynchronous());
        action.setPriority(actionDto.getPriority());
        ofNullable(actionDto.getActionScriptCode())
                .ifPresent(code -> action.setActionScript(scriptInstanceService.findByCode(code)));
        if (actionDto.getNotificationCode() != null) {
            Notification notification = ofNullable(notificationService.findByCode(actionDto.getNotificationCode()))
                    .orElseThrow(() -> new BusinessException("Notification does not exits code : " + actionDto.getNotificationCode()));
            ofNullable(actionDto.getParameters())
                    .ifPresent(params -> notification.setParams(params));
            action.setNotification(notification);

        }
        return action;
    }
}