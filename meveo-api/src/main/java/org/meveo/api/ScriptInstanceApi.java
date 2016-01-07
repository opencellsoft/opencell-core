package org.meveo.api;

import java.util.ArrayList;
import java.util.List;

import javax.ejb.Stateless;
import javax.inject.Inject;

import org.meveo.api.dto.RoleDto;
import org.meveo.api.dto.ScriptInstanceDto;
import org.meveo.api.dto.ScriptInstanceErrorDto;
import org.meveo.api.exception.EntityAlreadyExistsException;
import org.meveo.api.exception.EntityDoesNotExistsException;
import org.meveo.api.exception.InvalidEnumValue;
import org.meveo.api.exception.MeveoApiException;
import org.meveo.api.exception.MissingParameterException;
import org.meveo.commons.utils.StringUtils;
import org.meveo.model.admin.User;
import org.meveo.model.crm.Provider;
import org.meveo.model.scripts.ScriptInstance;
import org.meveo.model.scripts.ScriptInstanceError;
import org.meveo.model.scripts.ScriptSourceTypeEnum;
import org.meveo.model.security.Role;
import org.meveo.service.admin.impl.RoleService;
import org.meveo.service.script.ScriptInstanceService;

/**
 * @author Edward P. Legaspi
 **/

@Stateless
public class ScriptInstanceApi extends BaseApi {

	@Inject
	private ScriptInstanceService scriptInstanceService;
	
	@Inject
	private RoleService roleService;


	public List<ScriptInstanceErrorDto> create(ScriptInstanceDto scriptInstanceDto, User currentUser) throws MissingParameterException, EntityAlreadyExistsException, InvalidEnumValue,MeveoApiException {
		List<ScriptInstanceErrorDto> result = new ArrayList<ScriptInstanceErrorDto>();
		checkDto(scriptInstanceDto);
		
		String packageName = scriptInstanceService.getPackageName(scriptInstanceDto.getScript());
		String className = scriptInstanceService.getClassName(scriptInstanceDto.getScript());
		String scriptCode=packageName + "." + className;
		if(!StringUtils.isBlank(scriptInstanceDto.getCode()) && !scriptInstanceDto.getCode().equals(scriptCode)){ 
			throw new MeveoApiException("The code and the canonical script class name must be identical"); 
		}
		
		if (scriptInstanceService.findByCode(StringUtils.isBlank(scriptInstanceDto.getCode())?scriptCode:scriptInstanceDto.getCode(), currentUser.getProvider()) != null) {
			throw new EntityAlreadyExistsException(ScriptInstance.class, scriptInstanceDto.getCode());
		}
		ScriptInstance scriptInstance = new ScriptInstance();
		scriptInstance.setDescription(scriptInstanceDto.getDescription());
		scriptInstance.setScript(scriptInstanceDto.getScript());
		
		for(RoleDto roleDto : scriptInstanceDto.getExecutionRoles()){
			Role role = roleService.findByName(roleDto.getName(), currentUser.getProvider());
            if (role == null) {
                throw new EntityDoesNotExistsException(Role.class, roleDto.getName(), "name");
            }
            scriptInstance.getExecutionRoles().add(role);
		}
		for(RoleDto roleDto : scriptInstanceDto.getSourcingRoles()){
			Role role = roleService.findByName(roleDto.getName(), currentUser.getProvider());
            if (role == null) {
                throw new EntityDoesNotExistsException(Role.class, roleDto.getName(), "name");
            }
            scriptInstance.getSourcingRoles().add(role);
		}		

		if (!StringUtils.isBlank(scriptInstanceDto.getType())) {
			try {
				scriptInstance.setSourceTypeEnum(ScriptSourceTypeEnum.valueOf(scriptInstanceDto.getType()));
			} catch (IllegalArgumentException e) {
				throw new InvalidEnumValue(ScriptSourceTypeEnum.class.getName(), scriptInstanceDto.getType());
			}
		} else {
			scriptInstance.setSourceTypeEnum(ScriptSourceTypeEnum.JAVA);
		}
		try {
			scriptInstanceService.create(scriptInstance, currentUser, currentUser.getProvider());
		} catch (Exception e) {
              throw new MeveoApiException(e.getMessage());
		}
		
		if (scriptInstance != null && scriptInstance.isError()!=null && scriptInstance.isError().booleanValue()) {
			for (ScriptInstanceError error : scriptInstance.getScriptErrors()) {
				ScriptInstanceErrorDto errorDto = new ScriptInstanceErrorDto(error);
				result.add(errorDto);
			}
		}
		return result;
	}

	public List<ScriptInstanceErrorDto> update(ScriptInstanceDto scriptInstanceDto, User currentUser) throws MissingParameterException, EntityDoesNotExistsException, InvalidEnumValue,MeveoApiException {
		List<ScriptInstanceErrorDto> result = new ArrayList<ScriptInstanceErrorDto>();
		checkDto(scriptInstanceDto);
		String packageName = scriptInstanceService.getPackageName(scriptInstanceDto.getScript());
		String className = scriptInstanceService.getClassName(scriptInstanceDto.getScript()); 
		String scriptCode=packageName + "." + className;
		if(!StringUtils.isBlank(scriptInstanceDto.getCode()) && !scriptInstanceDto.getCode().equals(scriptCode)){ 
			throw new MeveoApiException("The code and the canonical script class name must be identical"); 
		}
		ScriptInstance scriptInstance = scriptInstanceService.findByCode(StringUtils.isBlank(scriptInstanceDto.getCode())?scriptCode:scriptInstanceDto.getCode(), currentUser.getProvider());
		if (scriptInstance == null) {
			throw new EntityDoesNotExistsException(ScriptInstance.class, scriptInstanceDto.getCode());
		}		
		if (!StringUtils.isBlank(scriptInstanceDto.getType())) {
			try {
				scriptInstance.setSourceTypeEnum(ScriptSourceTypeEnum.valueOf(scriptInstanceDto.getType()));
			} catch (IllegalArgumentException e) {
				throw new InvalidEnumValue(ScriptSourceTypeEnum.class.getName(), scriptInstanceDto.getType());
			}
		}		
		if(!scriptInstanceService.isUserHasSourcingRole(scriptInstance, currentUser)){
			throw new MeveoApiException("Invalid Sourcing Permission");
		}
		for(RoleDto roleDto : scriptInstanceDto.getExecutionRoles()){
			Role role = roleService.findByName(roleDto.getName(), currentUser.getProvider());
            if (role == null) {
                throw new EntityDoesNotExistsException(Role.class, roleDto.getName(), "name");
            }
            scriptInstance.getExecutionRoles().add(role);
		}
		for(RoleDto roleDto : scriptInstanceDto.getSourcingRoles()){
			Role role = roleService.findByName(roleDto.getName(), currentUser.getProvider());
            if (role == null) {
                throw new EntityDoesNotExistsException(Role.class, roleDto.getName(), "name");
            }
            scriptInstance.getSourcingRoles().add(role);
		}		
		scriptInstance.setDescription(scriptInstanceDto.getDescription());
		scriptInstance.setScript(scriptInstanceDto.getScript());
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

	public ScriptInstanceDto find(String scriptInstanceCode, Provider provider,User currentUser) throws EntityDoesNotExistsException, MissingParameterException {
		ScriptInstanceDto scriptInstanceDtoResult = null;
		if (StringUtils.isBlank(scriptInstanceCode)) {
			missingParameters.add("scriptInstanceCode");
			throw new MissingParameterException(getMissingParametersExceptionMessage());
		}
		ScriptInstance scriptInstance = scriptInstanceService.findByCode(scriptInstanceCode, provider);
		if (scriptInstance == null) {
			throw new EntityDoesNotExistsException(ScriptInstance.class, scriptInstanceCode);
		}
		scriptInstanceDtoResult = new ScriptInstanceDto(scriptInstance);
		if(!scriptInstanceService.isUserHasSourcingRole(scriptInstance, currentUser)){
			scriptInstanceDtoResult.setScript("InvalidPermission");
		}
		return scriptInstanceDtoResult;
	}

	public void remove(String scriptInstanceCode, Provider provider) throws EntityDoesNotExistsException, MissingParameterException {
		if (StringUtils.isBlank(scriptInstanceCode)) {
			missingParameters.add("scriptInstanceCode");
			throw new MissingParameterException(getMissingParametersExceptionMessage());
		}
		ScriptInstance scriptInstance = scriptInstanceService.findByCode(scriptInstanceCode, provider);
		if (scriptInstance == null) {
			throw new EntityDoesNotExistsException(ScriptInstance.class, scriptInstanceCode);
		}
		scriptInstanceService.remove(scriptInstance);
	}

	public  List<ScriptInstanceErrorDto> createOrUpdate(ScriptInstanceDto postData, User currentUser) throws MissingParameterException, EntityAlreadyExistsException, InvalidEnumValue, EntityDoesNotExistsException,MeveoApiException{
		List<ScriptInstanceErrorDto> result = new ArrayList<ScriptInstanceErrorDto>();
		checkDto(postData);
		String packageName = scriptInstanceService.getPackageName(postData.getScript());
		String className = scriptInstanceService.getClassName(postData.getScript());
		ScriptInstance scriptInstance = scriptInstanceService.findByCode(StringUtils.isBlank(postData.getCode())?(packageName + "." + className):postData.getCode(), currentUser.getProvider());
		if (scriptInstance == null) {
			result = create(postData, currentUser);
		} else {
			result = update(postData, currentUser);
		}
		return result;
	}

	private void checkDto(ScriptInstanceDto scriptInstanceDto) throws MissingParameterException {
		if (scriptInstanceDto == null) {
			missingParameters.add("scriptInstanceDto");
			throw new MissingParameterException(getMissingParametersExceptionMessage());
		}
		if (StringUtils.isBlank(scriptInstanceDto.getScript())) {
			missingParameters.add("script");
		}
		if (!missingParameters.isEmpty()) {
			throw new MissingParameterException(getMissingParametersExceptionMessage());
		}

	}
}
