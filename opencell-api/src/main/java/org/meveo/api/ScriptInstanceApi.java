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

    public List<ScriptInstanceErrorDto> create(ScriptInstanceDto scriptInstanceDto)
            throws MissingParameterException, EntityAlreadyExistsException, MeveoApiException, BusinessException {
        List<ScriptInstanceErrorDto> result = new ArrayList<ScriptInstanceErrorDto>();
        checkDtoAndUpdateCode(scriptInstanceDto);

        if (scriptInstanceService.findByCode(scriptInstanceDto.getCode()) != null) {
            throw new EntityAlreadyExistsException(ScriptInstance.class, scriptInstanceDto.getCode());
        }

        ScriptInstance scriptInstance = scriptInstanceFromDTO(scriptInstanceDto, null);

        scriptInstanceService.create(scriptInstance);

        if (scriptInstance != null && scriptInstance.isError() != null && scriptInstance.isError().booleanValue()) {
            for (ScriptInstanceError error : scriptInstance.getScriptErrors()) {
                ScriptInstanceErrorDto errorDto = new ScriptInstanceErrorDto(error);
                result.add(errorDto);
            }
        }
        return result;
    }

    public List<ScriptInstanceErrorDto> update(ScriptInstanceDto scriptInstanceDto)
            throws MissingParameterException, EntityDoesNotExistsException, MeveoApiException, BusinessException {

        List<ScriptInstanceErrorDto> result = new ArrayList<ScriptInstanceErrorDto>();
        checkDtoAndUpdateCode(scriptInstanceDto);

        ScriptInstance scriptInstance = scriptInstanceService.findByCode(scriptInstanceDto.getCode());

        if (scriptInstance == null) {
            throw new EntityDoesNotExistsException(ScriptInstance.class, scriptInstanceDto.getCode());
        } else if (!scriptInstanceService.isUserHasSourcingRole(scriptInstance)) {
            throw new MeveoApiException("User does not have a permission to update a given script");
        }

        scriptInstance = scriptInstanceFromDTO(scriptInstanceDto, scriptInstance);

        scriptInstance = scriptInstanceService.update(scriptInstance);

        if (scriptInstance.isError().booleanValue()) {
            for (ScriptInstanceError error : scriptInstance.getScriptErrors()) {
                ScriptInstanceErrorDto errorDto = new ScriptInstanceErrorDto(error);
                result.add(errorDto);
            }
        }
        return result;
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.meveo.api.ApiService#find(java.lang.String)
     */
    @Override
    public ScriptInstanceDto find(String scriptInstanceCode) throws EntityDoesNotExistsException, MissingParameterException, InvalidParameterException, MeveoApiException {
        ScriptInstanceDto scriptInstanceDtoResult = null;
        if (StringUtils.isBlank(scriptInstanceCode)) {
            missingParameters.add("scriptInstanceCode");
            handleMissingParameters();
        }
        ScriptInstance scriptInstance = scriptInstanceService.findByCode(scriptInstanceCode);
        if (scriptInstance == null) {
            throw new EntityDoesNotExistsException(ScriptInstance.class, scriptInstanceCode);
        }
        scriptInstanceDtoResult = new ScriptInstanceDto(scriptInstance);
        if (!scriptInstanceService.isUserHasSourcingRole(scriptInstance)) {
            scriptInstanceDtoResult.setScript("InvalidPermission");
        }
        return scriptInstanceDtoResult;
    }

    public void removeScriptInstance(String scriptInstanceCode) throws EntityDoesNotExistsException, MissingParameterException, BusinessException {
        if (StringUtils.isBlank(scriptInstanceCode)) {
            missingParameters.add("scriptInstanceCode");
            handleMissingParameters();
        }
        ScriptInstance scriptInstance = scriptInstanceService.findByCode(scriptInstanceCode);
        if (scriptInstance == null) {
            throw new EntityDoesNotExistsException(ScriptInstance.class, scriptInstanceCode);
        }
        scriptInstanceService.remove(scriptInstance);
    }

    @Override
    public ScriptInstance createOrUpdate(ScriptInstanceDto postData) throws MeveoApiException, BusinessException {
        createOrUpdateWithCompile(postData);

        ScriptInstance scriptInstance = scriptInstanceService.findByCode(postData.getCode());
        return scriptInstance;
    }

    public List<ScriptInstanceErrorDto> createOrUpdateWithCompile(ScriptInstanceDto postData) throws MeveoApiException, BusinessException {

        List<ScriptInstanceErrorDto> result = new ArrayList<ScriptInstanceErrorDto>();
        checkDtoAndUpdateCode(postData);

        ScriptInstance scriptInstance = scriptInstanceService.findByCode(postData.getCode());

        if (scriptInstance == null) {
            result = create(postData);
        } else {
            result = update(postData);
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
     * @throws EntityDoesNotExistsException entity does not exist exception.
     */
    public ScriptInstance scriptInstanceFromDTO(ScriptInstanceDto dto, ScriptInstance scriptInstanceToUpdate) throws EntityDoesNotExistsException {

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
            Role role = roleService.findByName(roleDto.getName());
            if (role == null) {
                throw new EntityDoesNotExistsException(Role.class, roleDto.getName(), "name");
            }
            scriptInstance.getExecutionRoles().add(role);
        }
        for (RoleDto roleDto : dto.getSourcingRoles()) {
            Role role = roleService.findByName(roleDto.getName());
            if (role == null) {
                throw new EntityDoesNotExistsException(Role.class, roleDto.getName(), "name");
            }
            scriptInstance.getSourcingRoles().add(role);
        }

        return scriptInstance;
    }
}