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

package org.meveo.api;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import javax.ejb.Stateless;
import javax.inject.Inject;

import org.meveo.admin.exception.BusinessException;
import org.meveo.admin.util.ResourceBundle;
import org.meveo.api.dto.LanguageDescriptionDto;
import org.meveo.api.dto.ScriptInstanceDto;
import org.meveo.api.dto.ScriptInstanceErrorDto;
import org.meveo.api.dto.ScriptParameterDto;
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
import org.meveo.model.scripts.ScriptParameter;
import org.meveo.model.scripts.ScriptSourceTypeEnum;
import org.meveo.service.script.ScriptInstanceCategoryService;
import org.meveo.service.script.ScriptInstanceService;
import org.meveo.service.script.ScriptUtils;

/**
 * @author Edward P. Legaspi
 * @author Mounir Bahije
 * @author melyoussoufi
 * @lastModifiedVersion 7.2.0
 *
 **/

@Stateless
public class ScriptInstanceApi extends BaseCrudApi<ScriptInstance, ScriptInstanceDto> {

    @Inject
    private ScriptInstanceService scriptInstanceService;

    @Inject
    private ScriptInstanceCategoryService scriptInstanceCategoryService;

    @Inject
    private ResourceBundle resourceMessages;

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

        if (scriptInstance != null && scriptInstance.isError()) {
            for (ScriptInstanceError error : scriptInstance.getScriptErrors()) {
                ScriptInstanceErrorDto errorDto = new ScriptInstanceErrorDto(error);
                result.add(errorDto);
            }
        }
        return result;
    }

    @Override
    public ScriptInstance create(ScriptInstanceDto scriptInstanceDto) throws MeveoApiException, BusinessException {
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

        if (scriptInstance.isError()) {
            for (ScriptInstanceError error : scriptInstance.getScriptErrors()) {
                ScriptInstanceErrorDto errorDto = new ScriptInstanceErrorDto(error);
                result.add(errorDto);
            }
        }
        return result;
    }

    @Override
    public ScriptInstance update(ScriptInstanceDto scriptInstanceDto) throws MeveoApiException, BusinessException {

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

        if (scriptInstance.isError()) {
            for (ScriptInstanceError error : scriptInstance.getScriptErrors()) {
                ScriptInstanceErrorDto errorDto = new ScriptInstanceErrorDto(error);
                result.add(errorDto);
            }
        }
        return result;
    }

    public void checkDtoAndUpdateCode(CustomScriptDto dto) throws BusinessApiException, MissingParameterException, InvalidParameterException {

        if (dto.getType() == ScriptSourceTypeEnum.JAVA_CLASS) {
            if (StringUtils.isBlank(dto.getCode())) {
                addGenericCodeIfAssociated(ScriptInstance.class.getName(), dto);
            }

            handleMissingParameters();

            if (!ScriptUtils.isOverwritesJavaClass(dto.getCode())) {
                throw new BusinessException(resourceMessages.getString("message.scriptInstance.classDoesNotExist", dto.getCode()));
            } else if (!ScriptUtils.isScriptInterfaceClass(dto.getCode())) {
                throw new BusinessException(resourceMessages.getString("message.scriptInstance.classNotScriptInstance", dto.getCode()));
            }
            
        } else {

            if (StringUtils.isBlank(dto.getScript())) {
                missingParameters.add("script");
            }

            handleMissingParameters();

            String scriptCode = ScriptUtils.getFullClassname(dto.getScript());
            if (!StringUtils.isBlank(dto.getCode()) && !dto.getCode().equals(scriptCode)) {
                throw new BusinessApiException("The code and the canonical script class name must be identical");
            }

            // check script existed full class name in class path
            if (ScriptUtils.isOverwritesJavaClass(scriptCode)) {
                throw new InvalidParameterException("The class with such name already exists");
            }

            dto.setCode(scriptCode);
        }
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

        if (dto.getReuse() != null) {
            scriptInstance.setReuse(dto.getReuse());
        }

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

        scriptInstance.getExecutionRoles().addAll(dto.getExecutionRoles());
        scriptInstance.getSourcingRoles().addAll(dto.getExecutionRoles());
        
        if(dto.getLanguageDescriptions() != null && !dto.getLanguageDescriptions().isEmpty()) {
            scriptInstance.setDescriptionI18n(dto.getLanguageDescriptions().stream().collect(Collectors.toMap(LanguageDescriptionDto::getLanguageCode, LanguageDescriptionDto::getDescription)));
        }
        
        List<ScriptParameter> existingScriptParamters = scriptInstance.getScriptParameters();
        if (existingScriptParamters != null && !existingScriptParamters.isEmpty()) {
        	scriptInstance.getScriptParameters().removeAll(existingScriptParamters);
        }
        
        if(dto.getScriptParameters() != null && !dto.getScriptParameters().isEmpty()) {
            scriptInstance.getScriptParameters().addAll(dto.getScriptParameters().stream().map(ScriptParameterDto::mapToEntity).collect(Collectors.toList()));
            for (ScriptParameter sp : scriptInstance.getScriptParameters()) sp.setScriptInstance(scriptInstance);
        }
        
        return scriptInstance;
    }
}