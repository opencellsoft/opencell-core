package org.meveo.api;

import java.util.ArrayList;
import java.util.List;

import javax.ejb.Stateless;
import javax.inject.Inject;

import org.meveo.admin.exception.BusinessException;
import org.meveo.api.dto.RoleDto;
import org.meveo.api.dto.ScriptInstanceDto;
import org.meveo.api.dto.ScriptInstanceErrorDto;
import org.meveo.api.dto.script.CustomScriptDto;
import org.meveo.api.exception.BusinessApiException;
import org.meveo.api.exception.EntityAlreadyExistsException;
import org.meveo.api.exception.EntityDoesNotExistsException;
import org.meveo.api.exception.InvalidParameterException;
import org.meveo.api.exception.MeveoApiException;
import org.meveo.api.exception.MissingParameterException;
import org.meveo.commons.utils.StringUtils;
import org.meveo.model.admin.User;
import org.meveo.model.scripts.ScriptInstance;
import org.meveo.model.scripts.ScriptInstanceError;
import org.meveo.model.scripts.ScriptSourceTypeEnum;
import org.meveo.model.security.Role;
import org.meveo.service.admin.impl.RoleService;
import org.meveo.service.script.CustomScriptService;
import org.meveo.service.script.ScriptInstanceService;

/**
 * @author Edward P. Legaspi
 **/

@Stateless
public class ScriptInstanceApi extends BaseCrudApi<ScriptInstance, ScriptInstanceDto> {

    @Inject
    private ScriptInstanceService scriptInstanceService;

    @Inject
    private RoleService roleService;

    public List<ScriptInstanceErrorDto> create(ScriptInstanceDto scriptInstanceDto, User currentUser) throws MissingParameterException, EntityAlreadyExistsException,
            MeveoApiException {
        List<ScriptInstanceErrorDto> result = new ArrayList<ScriptInstanceErrorDto>();
        checkDtoAndUpdateCode(scriptInstanceDto);

        if (scriptInstanceService.findByCode(scriptInstanceDto.getCode(), currentUser.getProvider()) != null) {
            throw new EntityAlreadyExistsException(ScriptInstance.class, scriptInstanceDto.getCode());
        }

        ScriptInstance scriptInstance = scriptInstanceFromDTO(scriptInstanceDto, null, currentUser);

        try {
            scriptInstanceService.create(scriptInstance, currentUser);
        } catch (BusinessException e) {
            throw new BusinessApiException(e.getMessage());
        }

        if (scriptInstance != null && scriptInstance.isError() != null && scriptInstance.isError().booleanValue()) {
            for (ScriptInstanceError error : scriptInstance.getScriptErrors()) {
                ScriptInstanceErrorDto errorDto = new ScriptInstanceErrorDto(error);
                result.add(errorDto);
            }
        }
        return result;
    }

    public List<ScriptInstanceErrorDto> update(ScriptInstanceDto scriptInstanceDto, User currentUser) throws MissingParameterException, EntityDoesNotExistsException,
            MeveoApiException {

        List<ScriptInstanceErrorDto> result = new ArrayList<ScriptInstanceErrorDto>();
        checkDtoAndUpdateCode(scriptInstanceDto);

        ScriptInstance scriptInstance = scriptInstanceService.findByCode(scriptInstanceDto.getCode(), currentUser.getProvider());

        if (scriptInstance == null) {
            throw new EntityDoesNotExistsException(ScriptInstance.class, scriptInstanceDto.getCode());
        } else if (!scriptInstanceService.isUserHasSourcingRole(scriptInstance, currentUser)) {
            throw new MeveoApiException("Invalid Sourcing Permission");
        }

        scriptInstance = scriptInstanceFromDTO(scriptInstanceDto, scriptInstance, currentUser);

        try {
            scriptInstance = scriptInstanceService.update(scriptInstance, currentUser);
        } catch (Exception e) {
            throw new MeveoApiException(e.getMessage());
        }
        if (scriptInstance.isError().booleanValue()) {
            for (ScriptInstanceError error : scriptInstance.getScriptErrors()) {
                ScriptInstanceErrorDto errorDto = new ScriptInstanceErrorDto(error);
                result.add(errorDto);
            }
        }
        return result;
    }

    @Override
    public ScriptInstanceDto find(String scriptInstanceCode, User currentUser) throws EntityDoesNotExistsException, MissingParameterException {
        ScriptInstanceDto scriptInstanceDtoResult = null;
        if (StringUtils.isBlank(scriptInstanceCode)) {
            missingParameters.add("scriptInstanceCode");
            handleMissingParameters();
        }
        ScriptInstance scriptInstance = scriptInstanceService.findByCode(scriptInstanceCode, currentUser.getProvider());
        if (scriptInstance == null) {
            throw new EntityDoesNotExistsException(ScriptInstance.class, scriptInstanceCode);
        }
        scriptInstanceDtoResult = new ScriptInstanceDto(scriptInstance);
        if (!scriptInstanceService.isUserHasSourcingRole(scriptInstance, currentUser)) {
            scriptInstanceDtoResult.setScript("InvalidPermission");
        }
        return scriptInstanceDtoResult;
    }

    public void removeScriptInstance(String scriptInstanceCode, User currentUser) throws EntityDoesNotExistsException, MissingParameterException, BusinessException {
        if (StringUtils.isBlank(scriptInstanceCode)) {
            missingParameters.add("scriptInstanceCode");
            handleMissingParameters();
        }
        ScriptInstance scriptInstance = scriptInstanceService.findByCode(scriptInstanceCode, currentUser.getProvider());
        if (scriptInstance == null) {
            throw new EntityDoesNotExistsException(ScriptInstance.class, scriptInstanceCode);
        }
        scriptInstanceService.remove(scriptInstance, currentUser);
    }

    @Override
    public ScriptInstance createOrUpdate(ScriptInstanceDto postData, User currentUser) throws MeveoApiException {
        createOrUpdateWithCompile(postData, currentUser);

        ScriptInstance scriptInstance = scriptInstanceService.findByCode(postData.getCode(), currentUser.getProvider());
        return scriptInstance;
    }

    public List<ScriptInstanceErrorDto> createOrUpdateWithCompile(ScriptInstanceDto postData, User currentUser) throws MeveoApiException {

        List<ScriptInstanceErrorDto> result = new ArrayList<ScriptInstanceErrorDto>();
        checkDtoAndUpdateCode(postData);

        ScriptInstance scriptInstance = scriptInstanceService.findByCode(postData.getCode(), currentUser.getProvider());

        if (scriptInstance == null) {
            result = create(postData, currentUser);
        } else {
            result = update(postData, currentUser);
        }
        return result;
    }

    public void checkDtoAndUpdateCode(CustomScriptDto dto) throws BusinessApiException, MissingParameterException, InvalidParameterException {

        if (StringUtils.isBlank(dto.getScript())) {
            missingParameters.add("script");
        }

        handleMissingParameters();

        String scriptCode = ScriptInstanceService.getFullClassname(dto.getScript());
        if (!StringUtils.isBlank(dto.getCode()) && !dto.getCode().equals(scriptCode)) {
            throw new BusinessApiException("The code and the canonical script class name must be identical");
        }

        // check script existed full class name in class path
        if (CustomScriptService.isOverwritesJavaClass(scriptCode)) {
            throw new InvalidParameterException("The class with such name already exists");
        }

        dto.setCode(scriptCode);
    }

    /**
     * Convert ScriptInstanceDto to a ScriptInstance instance.
     * 
     * @param dto ScriptInstanceDto object to convert
     * @param scriptInstanceToUpdate ScriptInstance to update with values from dto, or if null create a new one
     * @return A new or updated ScriptInstance object
     * @throws EntityDoesNotExistsException
     */
    public ScriptInstance scriptInstanceFromDTO(ScriptInstanceDto dto, ScriptInstance scriptInstanceToUpdate, User currentUser) throws EntityDoesNotExistsException {

        ScriptInstance scriptInstance = new ScriptInstance();
        if (scriptInstanceToUpdate != null) {
            scriptInstance = scriptInstanceToUpdate;
        }
        scriptInstance.setCode(dto.getCode());
        scriptInstance.setDescription(dto.getDescription());
        scriptInstance.setScript(dto.getScript());

        if (dto.getType() != null) {
            scriptInstance.setSourceTypeEnum(dto.getType());
        } else {
            scriptInstance.setSourceTypeEnum(ScriptSourceTypeEnum.JAVA);
        }

        for (RoleDto roleDto : dto.getExecutionRoles()) {
            Role role = roleService.findByName(roleDto.getName(), currentUser.getProvider());
            if (role == null) {
                throw new EntityDoesNotExistsException(Role.class, roleDto.getName(), "name");
            }
            scriptInstance.getExecutionRoles().add(role);
        }
        for (RoleDto roleDto : dto.getSourcingRoles()) {
            Role role = roleService.findByName(roleDto.getName(), currentUser.getProvider());
            if (role == null) {
                throw new EntityDoesNotExistsException(Role.class, roleDto.getName(), "name");
            }
            scriptInstance.getSourcingRoles().add(role);
        }

        return scriptInstance;
    }
}