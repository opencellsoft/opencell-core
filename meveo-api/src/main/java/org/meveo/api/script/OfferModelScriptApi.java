package org.meveo.api.script;

import javax.ejb.Stateless;
import javax.inject.Inject;

import org.meveo.admin.exception.BusinessException;
import org.meveo.api.BaseApi;
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

    public void create(OfferModelScriptDto postData, User currentUser) throws MeveoApiException, BusinessException {
        if (StringUtils.isBlank(postData.getCode())) {
            missingParameters.add("code");
        }
        if (StringUtils.isBlank(postData.getScript())) {
            missingParameters.add("script");
        }

        handleMissingParameters();

        String derivedCode = offerModelScriptService.getDerivedCode(postData.getScript());

        if (offerModelScriptService.findByCode(derivedCode, currentUser.getProvider()) != null) {
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
        if (StringUtils.isBlank(postData.getCode())) {
            missingParameters.add("code");
        }
        if (StringUtils.isBlank(postData.getScript())) {
            missingParameters.add("script");
        }

        handleMissingParameters();

        String derivedCode = offerModelScriptService.getDerivedCode(postData.getScript());

        OfferModelScript offerModelScript = offerModelScriptService.findByCode(derivedCode, currentUser.getProvider());
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
        String derivedCode = offerModelScriptService.getDerivedCode(postData.getScript());
        OfferModelScript offerModelScript = offerModelScriptService.findByCode(derivedCode, currentUser.getProvider());
        if (offerModelScript == null) {
            // create
            create(postData, currentUser);
        } else {
            // update
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

}
