package org.meveo.api;

import java.util.ArrayList;
import java.util.List;

import javax.ejb.Stateless;
import javax.inject.Inject;

import org.meveo.admin.exception.BusinessException;
import org.meveo.api.dto.EntityCustomActionDto;
import org.meveo.api.dto.ScriptInstanceErrorDto;
import org.meveo.api.exception.BusinessApiException;
import org.meveo.api.exception.EntityAlreadyExistsException;
import org.meveo.api.exception.EntityDoesNotExistsException;
import org.meveo.api.exception.MeveoApiException;
import org.meveo.api.exception.MissingParameterException;
import org.meveo.commons.utils.StringUtils;
import org.meveo.model.admin.User;
import org.meveo.model.crm.custom.EntityCustomAction;
import org.meveo.model.scripts.ScriptInstance;
import org.meveo.model.scripts.ScriptInstanceError;
import org.meveo.service.custom.EntityCustomActionService;
import org.meveo.service.script.ScriptInstanceService;

/**
 * @author Edward P. Legaspi
 **/

@Stateless
public class EntityCustomActionApi extends BaseApi {

    @Inject
    private ScriptInstanceService scriptInstanceService;

    @Inject
    private ScriptInstanceApi scriptInstanceApi;

    @Inject
    private EntityCustomActionService entityCustomActionService;

    public List<ScriptInstanceErrorDto> create(EntityCustomActionDto actionDto, String appliesTo, User currentUser) throws MissingParameterException, EntityAlreadyExistsException,
            MeveoApiException {

        checkDtoAndSetAppliesTo(actionDto, appliesTo);

        if (entityCustomActionService.findByCodeAndAppliesTo(actionDto.getCode(), actionDto.getAppliesTo(), currentUser.getProvider()) != null) {
            throw new EntityAlreadyExistsException(EntityCustomAction.class, actionDto.getCode() + "/" + actionDto.getAppliesTo());
        }

        EntityCustomAction action = new EntityCustomAction();
        entityCustomActionFromDTO(actionDto, action, currentUser);

        try {
            entityCustomActionService.create(action, currentUser);
        } catch (BusinessException e) {
            throw new BusinessApiException(e.getMessage());
        }

        List<ScriptInstanceErrorDto> result = new ArrayList<ScriptInstanceErrorDto>();
        ScriptInstance scriptInstance = action.getScript();
        if (scriptInstance.isError() != null && scriptInstance.isError().booleanValue()) {
            for (ScriptInstanceError error : scriptInstance.getScriptErrors()) {
                ScriptInstanceErrorDto errorDto = new ScriptInstanceErrorDto(error);
                result.add(errorDto);
            }
        }
        return result;
    }

    public List<ScriptInstanceErrorDto> update(EntityCustomActionDto actionDto, String appliesTo, User currentUser) throws MissingParameterException, EntityDoesNotExistsException,
            MeveoApiException {

        checkDtoAndSetAppliesTo(actionDto, appliesTo);

        EntityCustomAction action = entityCustomActionService.findByCodeAndAppliesTo(actionDto.getCode(), actionDto.getAppliesTo(), currentUser.getProvider());
        if (action == null) {
            throw new EntityDoesNotExistsException(EntityCustomAction.class, actionDto.getCode() + "/" + actionDto.getAppliesTo());
        }

        entityCustomActionFromDTO(actionDto, action, currentUser);

        try {
            action = entityCustomActionService.update(action, currentUser);
        } catch (Exception e) {
            throw new MeveoApiException(e.getMessage());
        }

        List<ScriptInstanceErrorDto> result = new ArrayList<ScriptInstanceErrorDto>();
        ScriptInstance scriptInstance = action.getScript();
        if (scriptInstance.isError() != null && scriptInstance.isError().booleanValue()) {
            for (ScriptInstanceError error : scriptInstance.getScriptErrors()) {
                ScriptInstanceErrorDto errorDto = new ScriptInstanceErrorDto(error);
                result.add(errorDto);
            }
        }
        return result;
    }

    public EntityCustomActionDto find(String actionCode, String appliesTo, User currentUser) throws EntityDoesNotExistsException, MissingParameterException {

        if (StringUtils.isBlank(actionCode)) {
            missingParameters.add("actionCode");
        }
        if (StringUtils.isBlank(appliesTo)) {
            missingParameters.add("appliesTo");
        }
        handleMissingParameters();

        EntityCustomAction action = entityCustomActionService.findByCodeAndAppliesTo(actionCode, appliesTo, currentUser.getProvider());
        if (action == null) {
            throw new EntityDoesNotExistsException(EntityCustomAction.class, actionCode + "/" + appliesTo);
        }
        EntityCustomActionDto actionDto = new EntityCustomActionDto(action);

        return actionDto;
    }

    public void remove(String actionCode, String appliesTo, User currentUser) throws EntityDoesNotExistsException, MissingParameterException, BusinessException  {

        if (StringUtils.isBlank(actionCode)) {
            missingParameters.add("actionCode");
        }
        if (StringUtils.isBlank(appliesTo)) {
            missingParameters.add("appliesTo");
        }

        handleMissingParameters();

        EntityCustomAction scriptInstance = entityCustomActionService.findByCodeAndAppliesTo(actionCode, appliesTo, currentUser.getProvider());
        if (scriptInstance == null) {
            throw new EntityDoesNotExistsException(EntityCustomAction.class, actionCode);
        }
        entityCustomActionService.remove(scriptInstance, currentUser);
    }

    public List<ScriptInstanceErrorDto> createOrUpdate(EntityCustomActionDto postData, String appliesTo, User currentUser) throws MissingParameterException,
            EntityAlreadyExistsException, EntityDoesNotExistsException, MeveoApiException {

        List<ScriptInstanceErrorDto> result = new ArrayList<ScriptInstanceErrorDto>();
        checkDtoAndSetAppliesTo(postData, appliesTo);

        EntityCustomAction scriptInstance = entityCustomActionService.findByCodeAndAppliesTo(postData.getCode(), postData.getAppliesTo(), currentUser.getProvider());

        if (scriptInstance == null) {
            result = create(postData, appliesTo, currentUser);
        } else {
            result = update(postData, appliesTo, currentUser);
        }
        return result;
    }

    private void checkDtoAndSetAppliesTo(EntityCustomActionDto dto, String appliesTo) throws MissingParameterException, BusinessApiException {

        if (StringUtils.isBlank(dto.getCode())) {
            missingParameters.add("code");
        }

        if (appliesTo != null) {
            dto.setAppliesTo(appliesTo);
        }

        if (StringUtils.isBlank(dto.getAppliesTo())) {
            missingParameters.add("appliesTo");
        }
        if (StringUtils.isBlank(dto.getScript())) {
            missingParameters.add("script");

        } else {
            // If script was passed, code is needed if script source was not passed.
            if (StringUtils.isBlank(dto.getScript().getCode()) && StringUtils.isBlank(dto.getScript().getScript())) {
                missingParameters.add("script.code");

                // Otherwise code is calculated from script source by combining package and classname
            } else if (!StringUtils.isBlank(dto.getScript().getScript())) {
                String fullClassname = ScriptInstanceService.getFullClassname(dto.getScript().getScript());
                if (!StringUtils.isBlank(dto.getScript().getCode()) && !dto.getScript().getCode().equals(fullClassname)) {
                    throw new BusinessApiException("The code and the canonical script class name must be identical");
                }
                dto.getScript().setCode(fullClassname);
            }
        }

        handleMissingParameters();

    }

    /**
     * Convert EntityCustomActionDto to a EntityCustomAction instance.
     * 
     * @param dto EntityCustomActionDto object to convert
     * @param action EntityCustomAction to update with values from dto
     * @return A new or updated EntityCustomAction object
     * @throws MeveoApiException
     */
    private void entityCustomActionFromDTO(EntityCustomActionDto dto, EntityCustomAction action, User currentUser) throws MeveoApiException {

        action.setCode(dto.getCode());
        action.setDescription(dto.getDescription());
        action.setApplicableOnEl(dto.getApplicableOnEl());
        action.setAppliesTo(dto.getAppliesTo());
        action.setLabel(dto.getLabel());

        // Extract script associated with an action
        ScriptInstance scriptInstance = null;

        // Should create it or update script only if it has full information only
        if (!dto.getScript().isCodeOnly()) {
            scriptInstanceApi.createOrUpdate(dto.getScript(), currentUser);
        }

        scriptInstance = scriptInstanceService.findByCode(dto.getScript().getCode(), currentUser.getProvider());
        if (scriptInstance == null) {
            throw new EntityDoesNotExistsException(ScriptInstance.class, dto.getScript().getCode());
        }
        action.setScript(scriptInstance);

    }
}