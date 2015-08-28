package org.meveo.api;

import javax.ejb.Stateless;
import javax.inject.Inject;

import org.meveo.api.dto.ScriptInstanceDto;
import org.meveo.api.exception.EntityAlreadyExistsException;
import org.meveo.api.exception.EntityDoesNotExistsException;
import org.meveo.api.exception.InvalidEnumValue;
import org.meveo.api.exception.MeveoApiException;
import org.meveo.api.exception.MissingParameterException;
import org.meveo.commons.utils.StringUtils;
import org.meveo.model.admin.User;
import org.meveo.model.crm.Provider;
import org.meveo.model.jobs.ScriptInstance;
import org.meveo.model.jobs.ScriptTypeEnum;
import org.meveo.service.script.JavaCompilerManager;
import org.meveo.service.script.ScriptInstanceService;

/**
 * @author Edward P. Legaspi
 **/
@Stateless
public class ScriptInstanceApi extends BaseApi {

	@Inject
	private ScriptInstanceService scriptInstanceService;

	@Inject
	private JavaCompilerManager javaCompilerManager;

	public void create(ScriptInstanceDto postData, User currentUser) throws MeveoApiException {
		if (!StringUtils.isBlank(postData.getCode())) {
			if (scriptInstanceService.findByCode(postData.getCode(), currentUser.getProvider()) != null) {
				throw new EntityAlreadyExistsException(ScriptInstance.class, postData.getCode());
			}

			ScriptInstance scriptInstance = new ScriptInstance();
			scriptInstance.setCode(postData.getCode());
			scriptInstance.setDescription(postData.getDescription());
			scriptInstance.setScript(postData.getScript());

			if (!StringUtils.isBlank(postData.getType())) {
				try {
					scriptInstance.setScriptTypeEnum(ScriptTypeEnum.valueOf(postData.getType()));
				} catch (IllegalArgumentException e) {
					throw new InvalidEnumValue(ScriptTypeEnum.class.getName(), postData.getType());
				}
			} else {
				scriptInstance.setScriptTypeEnum(ScriptTypeEnum.JAVA);
			}

			scriptInstanceService.create(scriptInstance, currentUser, currentUser.getProvider());
			javaCompilerManager.compileScript(scriptInstance);
		} else {
			if (StringUtils.isBlank(postData.getCode())) {
				missingParameters.add("code");
			}

			throw new MissingParameterException(getMissingParametersExceptionMessage());
		}
	}

	public void update(ScriptInstanceDto postData, User currentUser) throws MeveoApiException {
		if (!StringUtils.isBlank(postData.getCode())) {
			ScriptInstance scriptInstance = scriptInstanceService.findByCode(postData.getCode(),
					currentUser.getProvider());

			if (scriptInstance == null) {
				throw new EntityDoesNotExistsException(ScriptInstance.class, postData.getCode());
			}

			scriptInstance.setDescription(postData.getDescription());
			scriptInstance.setScript(postData.getScript());

			if (!StringUtils.isBlank(postData.getType())) {
				try {
					scriptInstance.setScriptTypeEnum(ScriptTypeEnum.valueOf(postData.getType()));
				} catch (IllegalArgumentException e) {
					throw new InvalidEnumValue(ScriptTypeEnum.class.getName(), postData.getType());
				}
			} else {
				scriptInstance.setScriptTypeEnum(ScriptTypeEnum.JAVA);
			}

			scriptInstanceService.update(scriptInstance, currentUser);
			javaCompilerManager.compileScript(scriptInstance);
		} else {
			if (StringUtils.isBlank(postData.getCode())) {
				missingParameters.add("code");
			}

			throw new MissingParameterException(getMissingParametersExceptionMessage());
		}
	}

	public ScriptInstanceDto find(String scriptInstanceCode, Provider provider) throws MeveoApiException {
		ScriptInstance scriptInstance = scriptInstanceService.findByCode(scriptInstanceCode, provider);
		if (scriptInstance == null) {
			throw new EntityDoesNotExistsException(ScriptInstance.class, scriptInstanceCode);
		}

		return new ScriptInstanceDto(scriptInstance);
	}

	public void remove(String scriptInstanceCode, Provider provider) throws MeveoApiException {
		ScriptInstance scriptInstance = scriptInstanceService.findByCode(scriptInstanceCode, provider);
		if (scriptInstance == null) {
			throw new EntityDoesNotExistsException(ScriptInstance.class, scriptInstanceCode);
		}

		scriptInstanceService.remove(scriptInstance);
	}

}
