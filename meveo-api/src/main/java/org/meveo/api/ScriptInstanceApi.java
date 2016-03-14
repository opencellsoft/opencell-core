package org.meveo.api;

import java.util.ArrayList;
import java.util.List;

import javax.ejb.Stateless;
import javax.inject.Inject;

import org.meveo.api.dto.EntityActionScriptDto;
import org.meveo.api.dto.RoleDto;
import org.meveo.api.dto.ScriptInstanceDto;
import org.meveo.api.dto.ScriptInstanceErrorDto;
import org.meveo.api.exception.EntityAlreadyExistsException;
import org.meveo.api.exception.EntityDoesNotExistsException;
import org.meveo.api.exception.MeveoApiException;
import org.meveo.api.exception.MissingParameterException;
import org.meveo.commons.utils.StringUtils;
import org.meveo.model.admin.User;
import org.meveo.model.scripts.EntityActionScript;
import org.meveo.model.scripts.ScriptInstance;
import org.meveo.model.scripts.ScriptInstanceError;
import org.meveo.model.scripts.ScriptSourceTypeEnum;
import org.meveo.model.security.Role;
import org.meveo.service.admin.impl.RoleService;
import org.meveo.service.script.EntityActionScriptService;
import org.meveo.service.script.ScriptInstanceService;

/**
 * @author Edward P. Legaspi
 **/

@Stateless
public class ScriptInstanceApi extends BaseApi {

    @Inject
    private ScriptInstanceService scriptInstanceService;

    @Inject
    private EntityActionScriptService entityActionScriptService;

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
            scriptInstanceService.create(scriptInstance, currentUser, currentUser.getProvider());
        } catch (Exception e) {
            throw new MeveoApiException(e.getMessage());
        }

        if (scriptInstance != null && scriptInstance.isError() != null && scriptInstance.isError().booleanValue()) {
            for (ScriptInstanceError error : scriptInstance.getScriptErrors()) {
                ScriptInstanceErrorDto errorDto = new ScriptInstanceErrorDto(error);
                result.add(errorDto);
            }
        }
        return result;
    }

    public List<ScriptInstanceErrorDto> create(EntityActionScriptDto scriptDto, String appliesTo, User currentUser) throws MissingParameterException, EntityAlreadyExistsException,
            MeveoApiException {

        List<ScriptInstanceErrorDto> result = new ArrayList<ScriptInstanceErrorDto>();
        checkDtoAndSetAppliesTo(scriptDto, appliesTo);

        if (entityActionScriptService.findByCode(scriptDto.getFullCode(), currentUser.getProvider()) != null) {
            throw new EntityAlreadyExistsException(ScriptInstance.class, scriptDto.getFullCode());
        }
        EntityActionScript scriptInstance = entityActionScriptFromDTO(scriptDto, null, currentUser);

        try {
            entityActionScriptService.create(scriptInstance, currentUser, currentUser.getProvider());
        } catch (Exception e) {
            throw new MeveoApiException(e.getMessage());
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

    public List<ScriptInstanceErrorDto> update(EntityActionScriptDto scriptDto, String appliesTo, User currentUser) throws MissingParameterException, EntityDoesNotExistsException,
            MeveoApiException {

        List<ScriptInstanceErrorDto> result = new ArrayList<ScriptInstanceErrorDto>();
        checkDtoAndSetAppliesTo(scriptDto, appliesTo);

        EntityActionScript scriptInstance = entityActionScriptService.findByCode(scriptDto.getFullCode(), currentUser.getProvider());

        if (scriptInstance == null) {
            throw new EntityDoesNotExistsException(ScriptInstance.class, scriptDto.getCode());
        }

        scriptInstance = entityActionScriptFromDTO(scriptDto, scriptInstance, currentUser);

        try {
            scriptInstance = entityActionScriptService.update(scriptInstance, currentUser);
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

    public ScriptInstanceDto findScriptInstance(String scriptInstanceCode, User currentUser) throws EntityDoesNotExistsException, MissingParameterException {
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

    public EntityActionScriptDto findEntityAction(String scriptCode, String appliesTo, User currentUser) throws EntityDoesNotExistsException, MissingParameterException {
        EntityActionScriptDto scriptInstanceDtoResult = null;
        if (StringUtils.isBlank(scriptCode)) {
            missingParameters.add("scriptCode");
            handleMissingParameters();

        } else if (StringUtils.isBlank(appliesTo)) {
            missingParameters.add("appliesTo");
            handleMissingParameters();
        }

        EntityActionScript scriptInstance = entityActionScriptService.findByCodeAndAppliesTo(EntityActionScript.composeCode(scriptCode, appliesTo), appliesTo,
            currentUser.getProvider());
        if (scriptInstance == null) {
            throw new EntityDoesNotExistsException(EntityActionScript.class, EntityActionScript.composeCode(scriptCode, appliesTo));
        }
        scriptInstanceDtoResult = new EntityActionScriptDto(scriptInstance);

        return scriptInstanceDtoResult;
    }

    public void removeScriptInstance(String scriptInstanceCode, User currentUser) throws EntityDoesNotExistsException, MissingParameterException {
        if (StringUtils.isBlank(scriptInstanceCode)) {
            missingParameters.add("scriptInstanceCode");
            handleMissingParameters();
        }
        ScriptInstance scriptInstance = scriptInstanceService.findByCode(scriptInstanceCode, currentUser.getProvider());
        if (scriptInstance == null) {
            throw new EntityDoesNotExistsException(ScriptInstance.class, scriptInstanceCode);
        }
        scriptInstanceService.remove(scriptInstance);
    }

    public void removeEntityAction(String scriptCode, String appliesTo, User currentUser) throws EntityDoesNotExistsException, MissingParameterException {

        if (StringUtils.isBlank(scriptCode)) {
            missingParameters.add("scriptInstanceCode");
            handleMissingParameters();
        } else if (StringUtils.isBlank(appliesTo)) {
            missingParameters.add("appliesTo");
            handleMissingParameters();
        }

        EntityActionScript scriptInstance = entityActionScriptService.findByCodeAndAppliesTo(EntityActionScript.composeCode(scriptCode, appliesTo), appliesTo,
            currentUser.getProvider());
        if (scriptInstance == null) {
            throw new EntityDoesNotExistsException(EntityActionScript.class, scriptCode);
        }
        entityActionScriptService.remove(scriptInstance);
    }

    public List<ScriptInstanceErrorDto> createOrUpdate(ScriptInstanceDto postData, User currentUser) throws MissingParameterException, EntityAlreadyExistsException,
            EntityDoesNotExistsException, MeveoApiException {

        List<ScriptInstanceErrorDto> result = new ArrayList<ScriptInstanceErrorDto>();
        checkDtoAndUpdateCode(postData);

        String packageName = scriptInstanceService.getPackageName(postData.getScript());
        String className = scriptInstanceService.getClassName(postData.getScript());
        ScriptInstance scriptInstance = scriptInstanceService.findByCode(StringUtils.isBlank(postData.getCode()) ? (packageName + "." + className) : postData.getCode(),
            currentUser.getProvider());

        if (scriptInstance == null) {
            result = create(postData, currentUser);
        } else {
            result = update(postData, currentUser);
        }
        return result;
    }

    public List<ScriptInstanceErrorDto> createOrUpdate(EntityActionScriptDto postData, String appliesTo, User currentUser) throws MissingParameterException,
            EntityAlreadyExistsException, EntityDoesNotExistsException, MeveoApiException {

        List<ScriptInstanceErrorDto> result = new ArrayList<ScriptInstanceErrorDto>();
        checkDtoAndSetAppliesTo(postData, appliesTo);

        EntityActionScript scriptInstance = entityActionScriptService.findByCode(postData.getFullCode(), currentUser.getProvider());

        if (scriptInstance == null) {
            result = create(postData, appliesTo, currentUser);
        } else {
            result = update(postData, appliesTo, currentUser);
        }
        return result;
    }

    private void checkDtoAndUpdateCode(ScriptInstanceDto dto) throws MeveoApiException {
        if (dto == null) {
            missingParameters.add("scriptInstanceDto");
            handleMissingParameters();
        }
        if (StringUtils.isBlank(dto.getScript())) {
            missingParameters.add("script");
        }

        handleMissingParameters();

        String packageName = scriptInstanceService.getPackageName(dto.getScript());
        String className = scriptInstanceService.getClassName(dto.getScript());
        String scriptCode = packageName + "." + className;
        if (!StringUtils.isBlank(dto.getCode()) && !dto.getCode().equals(scriptCode)) {
            throw new MeveoApiException("The code and the canonical script class name must be identical");
        }
        dto.setCode(scriptCode);
    }

    private void checkDtoAndSetAppliesTo(EntityActionScriptDto dto, String appliesTo) throws MeveoApiException {
        if (dto == null) {
            missingParameters.add("entityActionScriptDto");
            handleMissingParameters();
        }
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
        }

        handleMissingParameters();

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

    /**
     * Convert EntityActionScriptDto to a EntityActionScript instance.
     * 
     * @param dto EntityActionScriptDto object to convert
     * @param scriptToUpdate EntityActionScript to update with values from dto, or if null create a new one
     * @return A new or updated EntityActionScript object
     */
    public EntityActionScript entityActionScriptFromDTO(EntityActionScriptDto dto, EntityActionScript scriptToUpdate, User currentUser) {

        EntityActionScript scriptInstance = new EntityActionScript();
        if (scriptToUpdate != null) {
            scriptInstance = scriptToUpdate;
        }
        scriptInstance.setCode(dto.getCode(), dto.getAppliesTo());
        scriptInstance.setDescription(dto.getDescription());
        scriptInstance.setScript(dto.getScript());
        scriptInstance.setApplicableOnEl(dto.getApplicableOnEl());
        scriptInstance.setAppliesTo(dto.getAppliesTo());
        scriptInstance.setLabel(dto.getLabel());

        if (dto.getType() != null) {
            scriptInstance.setSourceTypeEnum(dto.getType());
        } else {
            scriptInstance.setSourceTypeEnum(ScriptSourceTypeEnum.JAVA);
        }

        return scriptInstance;
    }
}