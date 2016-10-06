package org.meveo.api;

import java.util.Date;

import javax.ejb.Stateless;
import javax.inject.Inject;

import org.meveo.admin.exception.BusinessException;
import org.meveo.api.dto.LanguageDto;
import org.meveo.api.exception.EntityAlreadyExistsException;
import org.meveo.api.exception.EntityDoesNotExistsException;
import org.meveo.api.exception.MeveoApiException;
import org.meveo.api.exception.MissingParameterException;
import org.meveo.commons.utils.StringUtils;
import org.meveo.model.Auditable;
import org.meveo.model.admin.User;
import org.meveo.model.billing.Language;
import org.meveo.model.billing.TradingLanguage;
import org.meveo.model.crm.Provider;
import org.meveo.service.admin.impl.LanguageService;
import org.meveo.service.billing.impl.TradingLanguageService;

/**
 * @author Edward P. Legaspi
 * 
 * @deprecated will be renammed to TradingLanguageApi
 **/
@Stateless
public class LanguageApi extends BaseApi {

    @Inject
    private LanguageService languageService;

    @Inject
    private TradingLanguageService tradingLanguageService;

    public void create(LanguageDto postData, User currentUser) throws MissingParameterException, EntityAlreadyExistsException, EntityDoesNotExistsException, BusinessException {

        if (StringUtils.isBlank(postData.getCode())) {
            missingParameters.add("code");
            handleMissingParameters();
        }

        Provider provider = currentUser.getProvider();

        if (tradingLanguageService.findByTradingLanguageCode(postData.getCode(), provider) != null) {
            throw new EntityAlreadyExistsException(TradingLanguage.class, postData.getCode());
        }

        Language language = languageService.findByCode(postData.getCode());

        Auditable auditable = new Auditable();
        auditable.setCreated(new Date());
        auditable.setCreator(currentUser);

        if (language == null) {
            // create
            language = new Language();
            language.setLanguageCode(postData.getCode());
            language.setDescriptionEn(postData.getDescription());
            language.setAuditable(auditable);
            languageService.create(language, currentUser);
        }

        TradingLanguage tradingLanguage = new TradingLanguage();
        tradingLanguage.setAuditable(auditable);
        tradingLanguage.setLanguage(language);
        tradingLanguage.setLanguageCode(postData.getCode());
        tradingLanguage.setPrDescription(postData.getDescription());
        tradingLanguage.setProvider(provider);
        tradingLanguage.setActive(true);
        tradingLanguageService.create(tradingLanguage, currentUser);
    }

    public void remove(String code, User currentUser) throws MissingParameterException, EntityDoesNotExistsException, BusinessException {

        if (StringUtils.isBlank(code)) {
            missingParameters.add("code");
            handleMissingParameters();
        }

        TradingLanguage tradingLanguage = tradingLanguageService.findByTradingLanguageCode(code, currentUser.getProvider());
        if (tradingLanguage == null) {
            throw new EntityDoesNotExistsException(TradingLanguage.class, code);
        } else {
            tradingLanguageService.remove(tradingLanguage, currentUser);
        }
    }

    public void update(LanguageDto postData, User currentUser) throws MissingParameterException, EntityDoesNotExistsException, EntityAlreadyExistsException {

        if (StringUtils.isBlank(postData.getCode())) {
            missingParameters.add("code");
            handleMissingParameters();
        }

        Provider provider = currentUser.getProvider();

        TradingLanguage tradingLanguage = tradingLanguageService.findByTradingLanguageCode(postData.getCode(), provider);
        if (tradingLanguage == null) {
            throw new EntityDoesNotExistsException(TradingLanguage.class, postData.getCode());
        }

        Language language = languageService.findByCode(postData.getCode());

        if (language == null) {
            throw new EntityDoesNotExistsException(Language.class, postData.getCode());
        }
        Auditable auditable = new Auditable();
        auditable.setUpdated(new Date());
        auditable.setUpdater(currentUser);

        language.setDescriptionEn(postData.getDescription());
        language.setAuditable(auditable);

        tradingLanguage.setAuditable(auditable);
        tradingLanguage.setLanguage(language);
        tradingLanguage.setLanguageCode(postData.getCode());
        tradingLanguage.setPrDescription(postData.getDescription());
    }

    public LanguageDto find(String code, Provider provider) throws MeveoApiException {

        if (StringUtils.isBlank(code)) {
            missingParameters.add("code");
            handleMissingParameters();
        }

        TradingLanguage tradingLanguage = tradingLanguageService.findByTradingLanguageCode(code, provider);

        if (tradingLanguage != null) {
            return new LanguageDto(tradingLanguage);
        }

        throw new EntityDoesNotExistsException(TradingLanguage.class, code);
    }

    /**
     * Create or update Language based on the trading language code.
     * 
     * @param postData
     * @param currentUser
     * @throws MeveoApiException
     * @throws BusinessException 
     */
    public void createOrUpdate(LanguageDto postData, User currentUser) throws MeveoApiException, BusinessException {
        Provider provider = currentUser.getProvider();
        TradingLanguage tradingLanguage = tradingLanguageService.findByTradingLanguageCode(postData.getCode(), provider);

        if (tradingLanguage == null) {
            create(postData, currentUser);
        } else {
            update(postData, currentUser);
        }
    }
    public void findOrCreate(String languageCode, User currentUser) throws EntityDoesNotExistsException, BusinessException {
        if (StringUtils.isBlank(languageCode)){
            return;
        }
		TradingLanguage tradingLanguage = tradingLanguageService.findByTradingLanguageCode(languageCode, currentUser.getProvider());
		if (tradingLanguage==null) {
			Language language = languageService.findByCode(languageCode);
			if (language==null) {
				throw new EntityDoesNotExistsException(Language.class, languageCode);
			}
			tradingLanguage = new TradingLanguage();
			tradingLanguage.setLanguage(language);
			tradingLanguage.setPrDescription(language.getDescriptionEn());
			tradingLanguageService.create(tradingLanguage, currentUser);
		}
	}
}
