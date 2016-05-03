package org.meveo.api;

import javax.ejb.Stateless;
import javax.inject.Inject;

import org.meveo.admin.exception.BusinessException;
import org.meveo.api.dto.LanguageDto;
import org.meveo.api.exception.EntityAlreadyExistsException;
import org.meveo.api.exception.EntityDoesNotExistsException;
import org.meveo.api.exception.MeveoApiException;
import org.meveo.commons.utils.StringUtils;
import org.meveo.model.admin.User;
import org.meveo.model.billing.Language;
import org.meveo.model.crm.Provider;
import org.meveo.service.admin.impl.LanguageService;


/**
 * @author Mounir HAMMAM
 **/
@Stateless
public class LanguageApi extends BaseApi {

    @Inject
    private LanguageService languageService;

    public void create(LanguageDto postData, User currentUser) throws MeveoApiException, BusinessException {

        if (StringUtils.isBlank(postData.getCode())) {
            missingParameters.add("code");
        }
 
        handleMissingParameters();

        if (languageService.findByCode(postData.getCode()) != null) {
            throw new EntityAlreadyExistsException(Language.class, postData.getCode());
        }

        Language language = new Language();
        language.setLanguageCode(postData.getCode());
        language.setDescriptionEn(postData.getDescription());
        languageService.create(language, currentUser);

    }

    public void update(LanguageDto postData, User currentUser) throws MeveoApiException, BusinessException {

        if (StringUtils.isBlank(postData.getCode())) {
            missingParameters.add("code");
        }

        handleMissingParameters();

        Language language = languageService.findByCode(postData.getCode());

        if (language == null) {
            throw new EntityDoesNotExistsException(Language.class, postData.getCode());
        }
        language.setDescriptionEn(postData.getDescription());

        languageService.update(language, currentUser);
    }

    public LanguageDto find(String languageCode) throws MeveoApiException {

        if (StringUtils.isBlank(languageCode)) {
            missingParameters.add("languageCode");
            handleMissingParameters();
        }

        LanguageDto result = new LanguageDto();

        Language language = languageService.findByCode(languageCode);
        if (language == null) {
            throw new EntityDoesNotExistsException(Language.class, languageCode);
        }

        result = new LanguageDto(language);

        return result;
    }

    public void remove(String languageCode) throws MeveoApiException {
        if (StringUtils.isBlank(languageCode)) {
            missingParameters.add("languageCode");
            handleMissingParameters();
        }

        Language language = languageService.findByCode(languageCode);
        if (language == null) {
            throw new EntityDoesNotExistsException(Language.class, languageCode);
        }

        languageService.remove(language);
    }

    public void createOrUpdate(LanguageDto postData, User currentUser) throws MeveoApiException, BusinessException {

        Language language = languageService.findByCode(postData.getCode());
        if (language == null) {
            create(postData, currentUser);
        } else {
            update(postData, currentUser);
        }
    }
}