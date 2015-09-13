package org.meveo.api;

import java.util.ArrayList;
import java.util.List;

import javax.ejb.Stateless;
import javax.inject.Inject;

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
import org.meveo.model.scripts.ScriptTypeEnum;
import org.meveo.service.script.ScriptInstanceService;

/**
 * @author Edward P. Legaspi
 **/

@Stateless
public class ScriptInstanceApi extends BaseApi {

	@Inject
	private ScriptInstanceService scriptInstanceService;


	public List<ScriptInstanceErrorDto> create(ScriptInstanceDto scriptInstanceDto, User currentUser) throws MissingParameterException, EntityAlreadyExistsException, InvalidEnumValue,MeveoApiException {
		List<ScriptInstanceErrorDto> result = new ArrayList<ScriptInstanceErrorDto>();
		checkDto(scriptInstanceDto);
		if (scriptInstanceService.findByCode(scriptInstanceDto.getCode(), currentUser.getProvider()) != null) {
			throw new EntityAlreadyExistsException(ScriptInstance.class, scriptInstanceDto.getCode());
		}
		ScriptInstance scriptInstance = new ScriptInstance();
		scriptInstance.setCode(scriptInstanceDto.getCode());
		scriptInstance.setDescription(scriptInstanceDto.getDescription());
		scriptInstance.setScript(scriptInstanceDto.getScript());

		if (!StringUtils.isBlank(scriptInstanceDto.getType())) {
			try {
				scriptInstance.setScriptTypeEnum(ScriptTypeEnum.valueOf(scriptInstanceDto.getType()));
			} catch (IllegalArgumentException e) {
				throw new InvalidEnumValue(ScriptTypeEnum.class.getName(), scriptInstanceDto.getType());
			}
		} else {
			scriptInstance.setScriptTypeEnum(ScriptTypeEnum.JAVA);
		}
		try {
			scriptInstance = scriptInstanceService.saveOrUpdate(scriptInstance, currentUser, currentUser.getProvider());
		} catch (Exception e) {
              throw new MeveoApiException(e.getMessage());
		}
		
		if (scriptInstance != null && scriptInstance.getError()!=null && scriptInstance.getError().booleanValue()) {
			for (ScriptInstanceError error : scriptInstance.getScriptInstanceErrors()) {
				ScriptInstanceErrorDto errorDto = new ScriptInstanceErrorDto(error);
				result.add(errorDto);
			}
		}
		return result;
	}

	public List<ScriptInstanceErrorDto> update(ScriptInstanceDto scriptInstanceDto, User currentUser) throws MissingParameterException, EntityDoesNotExistsException, InvalidEnumValue,MeveoApiException {
		List<ScriptInstanceErrorDto> result = new ArrayList<ScriptInstanceErrorDto>();
		checkDto(scriptInstanceDto);
		ScriptInstance scriptInstance = scriptInstanceService.findByCode(scriptInstanceDto.getCode(), currentUser.getProvider());
		if (scriptInstance == null) {
			throw new EntityDoesNotExistsException(ScriptInstance.class, scriptInstanceDto.getCode());
		}		
		if (!StringUtils.isBlank(scriptInstanceDto.getType())) {
			try {
				scriptInstance.setScriptTypeEnum(ScriptTypeEnum.valueOf(scriptInstanceDto.getType()));
			} catch (IllegalArgumentException e) {
				throw new InvalidEnumValue(ScriptTypeEnum.class.getName(), scriptInstanceDto.getType());
			}
		}		
		scriptInstance.setDescription(scriptInstanceDto.getDescription());
		scriptInstance.setScript(scriptInstanceDto.getScript());
		try {
			scriptInstance = scriptInstanceService.saveOrUpdate(scriptInstance, currentUser, currentUser.getProvider());
		} catch (Exception e) {
              throw new MeveoApiException(e.getMessage());
		}
		scriptInstance = scriptInstanceService.findByCode(scriptInstanceDto.getCode(), currentUser.getProvider());
		if (scriptInstance.getError().booleanValue()) {
			for (ScriptInstanceError error : scriptInstance.getScriptInstanceErrors()) {
				ScriptInstanceErrorDto errorDto = new ScriptInstanceErrorDto(error);
				result.add(errorDto);
			}
		}
		return result;
	}

	public ScriptInstanceDto find(String scriptInstanceCode, Provider provider) throws EntityDoesNotExistsException, MissingParameterException {
		if (StringUtils.isBlank(scriptInstanceCode)) {
			missingParameters.add("scriptInstanceCode");
			throw new MissingParameterException(getMissingParametersExceptionMessage());
		}
		ScriptInstance scriptInstance = scriptInstanceService.findByCode(scriptInstanceCode, provider);
		if (scriptInstance == null) {
			throw new EntityDoesNotExistsException(ScriptInstance.class, scriptInstanceCode);
		}
		return new ScriptInstanceDto(scriptInstance);
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
		ScriptInstance scriptInstance = scriptInstanceService.findByCode(postData.getCode(), currentUser.getProvider());
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
		if (StringUtils.isBlank(scriptInstanceDto.getCode()) ) {
			missingParameters.add("code");
		}
		if (StringUtils.isBlank(scriptInstanceDto.getScript())) {
			missingParameters.add("script");
		}
		if (!missingParameters.isEmpty()) {
			throw new MissingParameterException(getMissingParametersExceptionMessage());
		}

	}
}
