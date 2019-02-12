package org.meveo.api;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

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
import org.meveo.model.scripts.ScriptInstanceCategory;
import org.meveo.model.scripts.ScriptInstanceError;
import org.meveo.model.scripts.ScriptSourceTypeEnum;
import org.meveo.model.security.Role;
import org.meveo.service.admin.impl.RoleService;
import org.meveo.service.script.CustomScriptService;
import org.meveo.service.script.ScriptInstanceCategoryService;
import org.meveo.service.script.ScriptInstanceService;

/**
 * @author Edward P. Legaspi
 * @author Mounir Bahije
 * @lastModifiedVersion 5.2
 *
 **/

@Stateless
public class ScriptInstanceApi extends BaseCrudApi<ScriptInstance, ScriptInstanceDto> {

    @Inject
    private ScriptInstanceService scriptInstanceService;

    @Inject
    private ScriptInstanceCategoryService scriptInstanceCategoryService;

    @Inject
    private RoleService roleService;

    /**
     * Create ScriptInstance entity. Same as {@link #create(ScriptInstanceDto)}, only returns a list of compilation errors as DTOs
     * 
     * @param scriptInstanceDto Script information
     * @return A list of compilation errors
     * @throws MeveoApiException Meveo api exception
     * @throws BusinessException A general business exception
     */
    public List<ScriptInstanceErrorDto> createWithCompile(ScriptInstanceDto scriptInstanceDto) throws MeveoApiException, BusinessException {

        List<ScriptInstanceErrorDto> result = new ArrayList<ScriptInstanceErrorDto>();

        ScriptInstance scriptInstance = create(scriptInstanceDto);

        scriptInstanceService.create(scriptInstance);

        if (scriptInstance != null && scriptInstance.isError() != null && scriptInstance.isError().booleanValue()) {
            for (ScriptInstanceError error : scriptInstance.getScriptErrors()) {
                ScriptInstanceErrorDto errorDto = new ScriptInstanceErrorDto(error);
                result.add(errorDto);
            }
        }
        return result;
    }

    @Override
    public ScriptInstance create(ScriptInstanceDto scriptInstanceDto) throws MeveoApiException, BusinessException {
        List<ScriptInstanceErrorDto> result = new ArrayList<ScriptInstanceErrorDto>();
        checkDtoAndUpdateCode(scriptInstanceDto);

        if (scriptInstanceService.findByCode(scriptInstanceDto.getCode()) != null) {
            throw new EntityAlreadyExistsException(ScriptInstance.class, scriptInstanceDto.getCode());
        }

        ScriptInstance scriptInstance = scriptInstanceFromDTO(scriptInstanceDto, null);

        scriptInstanceService.create(scriptInstance);

        return scriptInstance;
    }

    /**
     * Update ScriptInstance entity. Same as {@link #update(ScriptInstanceDto)}, only returns a list of compilation errors as DTOs
     * 
     * @param scriptInstanceDto Script information
     * @return A list of compilation errors
     * @throws MeveoApiException Meveo api exception
     * @throws BusinessException A general business exception
     */
    public List<ScriptInstanceErrorDto> updateWithCompile(ScriptInstanceDto scriptInstanceDto) throws MeveoApiException, BusinessException {

        List<ScriptInstanceErrorDto> result = new ArrayList<ScriptInstanceErrorDto>();

        ScriptInstance scriptInstance = update(scriptInstanceDto);

        if (scriptInstance.isError().booleanValue()) {
            for (ScriptInstanceError error : scriptInstance.getScriptErrors()) {
                ScriptInstanceErrorDto errorDto = new ScriptInstanceErrorDto(error);
                result.add(errorDto);
            }
        }
        return result;
    }

    @Override
    public ScriptInstance update(ScriptInstanceDto scriptInstanceDto) throws MeveoApiException, BusinessException {

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

        return scriptInstance;
    }

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

    /**
     * Execute a script instance with a given code and context
     *
     * @param scriptInstanceCode Script instance code
     * @param context Context of values
     * @return A map of values
     * @throws MeveoApiException Api exception
     * @throws BusinessException General business exception
     */
    public Map<String, Object> execute(String scriptInstanceCode, Map<String, Object> context) throws MeveoApiException, BusinessException {

        find(scriptInstanceCode);
        Map<String, Object> result = scriptInstanceService.executeWInitAndFinalize(scriptInstanceCode, context);
        return result;
    }

    @Override
    public ScriptInstance createOrUpdate(ScriptInstanceDto scriptInstanceDto) throws MeveoApiException, BusinessException {

        checkDtoAndUpdateCode(scriptInstanceDto);

        return super.createOrUpdate(scriptInstanceDto);
    }

    /**
     * Create or update existing ScriptInstance entity. Same as {@link #createOrUpdate(ScriptInstanceDto)}, only returns a list of compilation errors as DTOs
     * 
     * @param scriptInstanceDto Script information
     * @return A list of compilation errors
     * @throws MeveoApiException Meveo api exception
     * @throws BusinessException A general business exception
     */
    public List<ScriptInstanceErrorDto> createOrUpdateWithCompile(ScriptInstanceDto scriptInstanceDto) throws MeveoApiException, BusinessException {

        List<ScriptInstanceErrorDto> result = new ArrayList<ScriptInstanceErrorDto>();

        ScriptInstance scriptInstance = createOrUpdate(scriptInstanceDto);

        if (scriptInstance.isError().booleanValue()) {
            for (ScriptInstanceError error : scriptInstance.getScriptErrors()) {
                ScriptInstanceErrorDto errorDto = new ScriptInstanceErrorDto(error);
                result.add(errorDto);
            }
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

        ScriptInstance scriptInstance = scriptInstanceToUpdate;
        if (scriptInstanceToUpdate == null) {
            scriptInstance = new ScriptInstance();
            if (dto.isDisabled() != null) {
                scriptInstance.setDisabled(dto.isDisabled());
            }
        }
        scriptInstance.setCode(dto.getCode());
        scriptInstance.setDescription(dto.getDescription());
        scriptInstance.setScript(dto.getScript());

        if (!StringUtils.isBlank(dto.getScriptInstanceCategoryCode())) {
            ScriptInstanceCategory scriptInstanceCategory = scriptInstanceCategoryService.findByCode(dto.getScriptInstanceCategoryCode());
            if (scriptInstanceCategory != null) {
                scriptInstance.setScriptInstanceCategory(scriptInstanceCategory);
            }
        }

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