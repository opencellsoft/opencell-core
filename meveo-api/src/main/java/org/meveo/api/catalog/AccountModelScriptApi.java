package org.meveo.api.catalog;

import java.util.ArrayList;
import java.util.List;

import javax.ejb.Stateless;
import javax.inject.Inject;

import org.meveo.admin.exception.BusinessException;
import org.meveo.api.BaseApi;
import org.meveo.api.dto.script.AccountModelScriptDto;
import org.meveo.api.exception.EntityAlreadyExistsException;
import org.meveo.api.exception.EntityDoesNotExistsException;
import org.meveo.api.exception.MeveoApiException;
import org.meveo.commons.utils.StringUtils;
import org.meveo.model.admin.User;
import org.meveo.model.crm.AccountModelScript;
import org.meveo.model.crm.Provider;
import org.meveo.model.scripts.ScriptSourceTypeEnum;
import org.meveo.service.crm.impl.AccountModelScriptService;

/**
 * @author Edward P. Legaspi
 **/
@Stateless
public class AccountModelScriptApi extends BaseApi {

	@Inject
	private AccountModelScriptService accountModelScriptService;

	public void create(AccountModelScriptDto postData, User currentUser) throws MeveoApiException, BusinessException {
		if (StringUtils.isBlank(postData.getCode())) {
			missingParameters.add("code");
		}
		if (StringUtils.isBlank(postData.getScript())) {
			missingParameters.add("script");
		}

		handleMissingParameters();

		String derivedCode = accountModelScriptService.getFullClassname(postData.getScript());

		if (accountModelScriptService.findByCode(derivedCode, currentUser.getProvider()) != null) {
			throw new EntityAlreadyExistsException(AccountModelScript.class, postData.getCode());
		}

		AccountModelScript accountModelScript = new AccountModelScript();
		accountModelScript.setCode(postData.getCode());
		accountModelScript.setDescription(postData.getDescription());

		if (postData.getType() != null) {
			accountModelScript.setSourceTypeEnum(postData.getType());
		} else {
			accountModelScript.setSourceTypeEnum(ScriptSourceTypeEnum.JAVA);
		}
		accountModelScript.setScript(postData.getScript());

		accountModelScriptService.create(accountModelScript, currentUser);
	}

	public void update(AccountModelScriptDto postData, User currentUser) throws MeveoApiException, BusinessException {
		if (StringUtils.isBlank(postData.getCode())) {
			missingParameters.add("code");
		}
		if (StringUtils.isBlank(postData.getScript())) {
			missingParameters.add("script");
		}

		handleMissingParameters();

		String derivedCode = accountModelScriptService.getFullClassname(postData.getScript());

		AccountModelScript accountModelScript = accountModelScriptService.findByCode(derivedCode, currentUser.getProvider());
		if (accountModelScript == null) {
			throw new EntityDoesNotExistsException(AccountModelScript.class, postData.getCode());
		}

		accountModelScript.setDescription(postData.getDescription());

		if (postData.getType() != null) {
			accountModelScript.setSourceTypeEnum(postData.getType());
		} else {
			accountModelScript.setSourceTypeEnum(ScriptSourceTypeEnum.JAVA);
		}
		accountModelScript.setScript(postData.getScript());

		accountModelScriptService.update(accountModelScript, currentUser);
	}

	public void createOrUpdate(AccountModelScriptDto postData, User currentUser) throws MeveoApiException, BusinessException {
		String derivedCode = accountModelScriptService.getFullClassname(postData.getScript());
		AccountModelScript accountModelScript = accountModelScriptService.findByCode(derivedCode, currentUser.getProvider());
		if (accountModelScript == null) {
			// create
			create(postData, currentUser);
		} else {
			// update
			update(postData, currentUser);
		}
	}

	public void delete(String code, User currentUser) throws EntityDoesNotExistsException {
		AccountModelScript accountModelScript = accountModelScriptService.findByCode(code, currentUser.getProvider());
		if (accountModelScript == null) {
			throw new EntityDoesNotExistsException(AccountModelScript.class, code);
		}

		accountModelScriptService.remove(accountModelScript);
	}

	public AccountModelScriptDto get(String code, Provider provider) throws MeveoApiException {
		if (StringUtils.isBlank(code)) {
			missingParameters.add("code");
		}

		handleMissingParameters();

		AccountModelScript accountModelScript = accountModelScriptService.findByCode(code, provider);
		if (accountModelScript == null) {
			throw new EntityDoesNotExistsException(AccountModelScript.class, code);
		}

		return new AccountModelScriptDto(accountModelScript);
	}

	public List<AccountModelScriptDto> list(Provider provider) {
		List<AccountModelScriptDto> result = new ArrayList<>();

		List<AccountModelScript> accountModelScripts = accountModelScriptService.list(provider);
		if (accountModelScripts != null) {
			for (AccountModelScript e : accountModelScripts) {
				result.add(new AccountModelScriptDto(e));
			}
		}

		return result;
	}

}
