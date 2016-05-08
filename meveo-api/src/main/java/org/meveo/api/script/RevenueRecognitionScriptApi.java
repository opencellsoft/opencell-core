package org.meveo.api.script;

import java.util.ArrayList;
import java.util.List;

import javax.ejb.Stateless;
import javax.inject.Inject;

import org.meveo.admin.exception.BusinessException;
import org.meveo.api.BaseApi;
import org.meveo.api.dto.script.RevenueRecognitionScriptDto;
import org.meveo.api.exception.EntityAlreadyExistsException;
import org.meveo.api.exception.EntityDoesNotExistsException;
import org.meveo.api.exception.MeveoApiException;
import org.meveo.commons.utils.StringUtils;
import org.meveo.model.admin.User;
import org.meveo.model.crm.Provider;
import org.meveo.model.scripts.RevenueRecognitionScript;
import org.meveo.model.scripts.ScriptSourceTypeEnum;
import org.meveo.service.script.revenue.RevenueRecognitionScriptService;

@Stateless
public class RevenueRecognitionScriptApi extends BaseApi {

	@Inject
	private RevenueRecognitionScriptService revenueRecognitionScriptService;

	public void create(RevenueRecognitionScriptDto postData, User currentUser) throws MeveoApiException, BusinessException {
		if (StringUtils.isBlank(postData.getCode())) {
			missingParameters.add("code");
		}
		if (StringUtils.isBlank(postData.getScript())) {
			missingParameters.add("script");
		}

		handleMissingParameters();

		String derivedCode = revenueRecognitionScriptService.getFullClassname(postData.getScript());

		if (revenueRecognitionScriptService.findByCode(derivedCode, currentUser.getProvider()) != null) {
			throw new EntityAlreadyExistsException(RevenueRecognitionScript.class, postData.getCode());
		}

		RevenueRecognitionScript RevenueRecognitionScript = new RevenueRecognitionScript();
		RevenueRecognitionScript.setCode(postData.getCode());
		RevenueRecognitionScript.setDescription(postData.getDescription());

		if (postData.getType() != null) {
			RevenueRecognitionScript.setSourceTypeEnum(postData.getType());
		} else {
			RevenueRecognitionScript.setSourceTypeEnum(ScriptSourceTypeEnum.JAVA);
		}
		RevenueRecognitionScript.setScript(postData.getScript());

		revenueRecognitionScriptService.create(RevenueRecognitionScript, currentUser);
	}

	public void update(RevenueRecognitionScriptDto postData, User currentUser) throws MeveoApiException, BusinessException {
		if (StringUtils.isBlank(postData.getCode())) {
			missingParameters.add("code");
		}
		if (StringUtils.isBlank(postData.getScript())) {
			missingParameters.add("script");
		}

		handleMissingParameters();

		String derivedCode = revenueRecognitionScriptService.getFullClassname(postData.getScript());

		RevenueRecognitionScript RevenueRecognitionScript = revenueRecognitionScriptService.findByCode(derivedCode, currentUser.getProvider());
		if (RevenueRecognitionScript == null) {
			throw new EntityDoesNotExistsException(RevenueRecognitionScript.class, postData.getCode());
		}

		RevenueRecognitionScript.setDescription(postData.getDescription());

		if (postData.getType() != null) {
			RevenueRecognitionScript.setSourceTypeEnum(postData.getType());
		} else {
			RevenueRecognitionScript.setSourceTypeEnum(ScriptSourceTypeEnum.JAVA);
		}
		RevenueRecognitionScript.setScript(postData.getScript());

		revenueRecognitionScriptService.update(RevenueRecognitionScript, currentUser);
	}

	public void createOrUpdate(RevenueRecognitionScriptDto postData, User currentUser) throws MeveoApiException, BusinessException {
		String derivedCode = revenueRecognitionScriptService.getFullClassname(postData.getScript());
		RevenueRecognitionScript RevenueRecognitionScript = revenueRecognitionScriptService.findByCode(derivedCode, currentUser.getProvider());
		if (RevenueRecognitionScript == null) {
			// create
			create(postData, currentUser);
		} else {
			// update
			update(postData, currentUser);
		}
	}

	public void delete(String code, User currentUser) throws EntityDoesNotExistsException {
		RevenueRecognitionScript RevenueRecognitionScript = revenueRecognitionScriptService.findByCode(code, currentUser.getProvider());
		if (RevenueRecognitionScript == null) {
			throw new EntityDoesNotExistsException(RevenueRecognitionScript.class, code);
		}

		revenueRecognitionScriptService.remove(RevenueRecognitionScript);
	}

	public RevenueRecognitionScriptDto get(String code, Provider provider) throws MeveoApiException {
		if (StringUtils.isBlank(code)) {
			missingParameters.add("code");
		}

		handleMissingParameters();

		RevenueRecognitionScript RevenueRecognitionScript = revenueRecognitionScriptService.findByCode(code, provider);
		if (RevenueRecognitionScript == null) {
			throw new EntityDoesNotExistsException(RevenueRecognitionScript.class, code);
		}

		return new RevenueRecognitionScriptDto(RevenueRecognitionScript);
	}

	public List<RevenueRecognitionScriptDto> list(User currentUser) {
		List<RevenueRecognitionScriptDto> result = new ArrayList<>();

		List<RevenueRecognitionScript> RevenueRecognitionScripts = revenueRecognitionScriptService.list(currentUser.getProvider());
		if (RevenueRecognitionScripts != null) {
			for (RevenueRecognitionScript e : RevenueRecognitionScripts) {
				result.add(new RevenueRecognitionScriptDto(e));
			}
		}

		return result;
	}

}
