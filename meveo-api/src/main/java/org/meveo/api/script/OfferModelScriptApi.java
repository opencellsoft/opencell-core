package org.meveo.api.script;

import java.util.ArrayList;
import java.util.List;

import javax.ejb.Stateless;
import javax.inject.Inject;

import org.meveo.admin.exception.BusinessException;
import org.meveo.api.BaseApi;
import org.meveo.api.ScriptInstanceApi;
import org.meveo.api.dto.script.OfferModelScriptDto;
import org.meveo.api.exception.EntityAlreadyExistsException;
import org.meveo.api.exception.EntityDoesNotExistsException;
import org.meveo.api.exception.MeveoApiException;
import org.meveo.commons.utils.StringUtils;
import org.meveo.model.admin.User;
import org.meveo.model.crm.Provider;
import org.meveo.model.scripts.OfferModelScript;
import org.meveo.model.scripts.ScriptSourceTypeEnum;
import org.meveo.service.script.OfferModelScriptService;

/**
 * @author Edward P. Legaspi
 **/
@Stateless
public class OfferModelScriptApi extends BaseApi {

	@Inject
	private OfferModelScriptService offerModelScriptService;

    @Inject
    private ScriptInstanceApi scriptInstanceApi;

    public void create(OfferModelScriptDto postData, User currentUser) throws MeveoApiException, BusinessException {
        scriptInstanceApi.checkDtoAndUpdateCode(postData);

        if (offerModelScriptService.findByCode(postData.getCode(), currentUser.getProvider()) != null) {
            throw new EntityAlreadyExistsException(OfferModelScript.class, postData.getCode());
        }

		OfferModelScript offerModelScript = new OfferModelScript();
		offerModelScript.setCode(postData.getCode());
		offerModelScript.setDescription(postData.getDescription());

		if (postData.getType() != null) {
			offerModelScript.setSourceTypeEnum(postData.getType());
		} else {
			offerModelScript.setSourceTypeEnum(ScriptSourceTypeEnum.JAVA);
		}
		offerModelScript.setScript(postData.getScript());

		offerModelScriptService.create(offerModelScript, currentUser);
	}

    public void update(OfferModelScriptDto postData, User currentUser) throws MeveoApiException, BusinessException {
        scriptInstanceApi.checkDtoAndUpdateCode(postData);

        OfferModelScript offerModelScript = offerModelScriptService.findByCode(postData.getCode(), currentUser.getProvider());
        if (offerModelScript == null) {
            throw new EntityDoesNotExistsException(OfferModelScript.class, postData.getCode());
        }

		offerModelScript.setDescription(postData.getDescription());

		if (postData.getType() != null) {
			offerModelScript.setSourceTypeEnum(postData.getType());
		} else {
			offerModelScript.setSourceTypeEnum(ScriptSourceTypeEnum.JAVA);
		}
		offerModelScript.setScript(postData.getScript());

		offerModelScriptService.update(offerModelScript, currentUser);
	}

    public void createOrUpdate(OfferModelScriptDto postData, User currentUser) throws MeveoApiException, BusinessException {
       
    	scriptInstanceApi.checkDtoAndUpdateCode(postData);

        OfferModelScript offerModelScript = offerModelScriptService.findByCode(postData.getCode(), currentUser.getProvider());
        if (offerModelScript == null) {
            create(postData, currentUser);
        } else {
            update(postData, currentUser);
        }
    }

	public void delete(String code, User currentUser) throws EntityDoesNotExistsException {
		OfferModelScript offerModelScript = offerModelScriptService.findByCode(code, currentUser.getProvider());
		if (offerModelScript == null) {
			throw new EntityDoesNotExistsException(OfferModelScript.class, code);
		}

		offerModelScriptService.remove(offerModelScript);
	}

	public OfferModelScriptDto get(String code, Provider provider) throws MeveoApiException {
		if (StringUtils.isBlank(code)) {
			missingParameters.add("code");
		}

		handleMissingParameters();

		OfferModelScript offerModelScript = offerModelScriptService.findByCode(code, provider);
		if (offerModelScript == null) {
			throw new EntityDoesNotExistsException(OfferModelScript.class, code);
		}

		return new OfferModelScriptDto(offerModelScript);
	}

	public List<OfferModelScriptDto> list(User currentUser) {
		List<OfferModelScriptDto> result = new ArrayList<>();

		List<OfferModelScript> offerModelScripts = offerModelScriptService.list(currentUser.getProvider());
		if (offerModelScripts != null) {
			for (OfferModelScript e : offerModelScripts) {
				result.add(new OfferModelScriptDto(e));
			}
		}

		return result;
	}

}
