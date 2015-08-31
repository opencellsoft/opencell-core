package org.meveo.api;

import java.util.Date;

import javax.ejb.Stateless;
import javax.inject.Inject;

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
 **/
@Stateless
public class LanguageApi extends BaseApi {

	@Inject
	private LanguageService languageService;

	@Inject
	private TradingLanguageService tradingLanguageService;

	public void create(LanguageDto postData, User currentUser)
			throws MissingParameterException, EntityAlreadyExistsException,
			EntityDoesNotExistsException {
		if (!StringUtils.isBlank(postData.getCode())) {
			Provider provider = currentUser.getProvider();

			if (tradingLanguageService.findByTradingLanguageCode(
					postData.getCode(), provider) != null) {
				throw new EntityAlreadyExistsException(TradingLanguage.class,
						postData.getCode());
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
			tradingLanguageService.create(tradingLanguage, currentUser, provider);
		} else {
			if (StringUtils.isBlank(postData.getCode())) {
				missingParameters.add("code");
			}

			throw new MissingParameterException(
					getMissingParametersExceptionMessage());
		}
	}

	public void remove(String code, Provider provider)
			throws MissingParameterException, EntityDoesNotExistsException {
		if (!StringUtils.isBlank(code)) {
			TradingLanguage tradingLanguage = tradingLanguageService
					.findByTradingLanguageCode(code, provider);
			if (tradingLanguage == null) {
				throw new EntityDoesNotExistsException(TradingLanguage.class,
						code);
			} else {
				tradingLanguageService.remove(tradingLanguage);
			}
		} else {
			if (StringUtils.isBlank(code)) {
				missingParameters.add("code");
			}

			throw new MissingParameterException(
					getMissingParametersExceptionMessage());
		}
	}

	public void update(LanguageDto postData, User currentUser)
			throws MissingParameterException, EntityDoesNotExistsException,
			EntityAlreadyExistsException {
		if (!StringUtils.isBlank(postData.getCode())) {
			Provider provider = currentUser.getProvider();

			TradingLanguage tradingLanguage = tradingLanguageService
					.findByTradingLanguageCode(postData.getCode(), provider);
			if (tradingLanguage == null) {
				throw new EntityDoesNotExistsException(TradingLanguage.class,
						postData.getCode());
			}

			Language language = languageService.findByCode(postData.getCode());

			if (language != null) {
				Auditable auditable = new Auditable();
				auditable.setUpdated(new Date());
				auditable.setUpdater(currentUser);

				language.setDescriptionEn(postData.getDescription());
				language.setAuditable(auditable);

				tradingLanguage.setAuditable(auditable);
				tradingLanguage.setLanguage(language);
				tradingLanguage.setLanguageCode(postData.getCode());
				tradingLanguage.setPrDescription(postData.getDescription());
			} else {
				throw new EntityDoesNotExistsException(Language.class,
						postData.getCode());
			}
		} else {
			if (StringUtils.isBlank(postData.getCode())) {
				missingParameters.add("code");
			}

			throw new MissingParameterException(
					getMissingParametersExceptionMessage());
		}
	}

	public LanguageDto find(String code, Provider provider)
			throws MeveoApiException {
		if (!StringUtils.isBlank(code)) {
			TradingLanguage tradingLanguage = tradingLanguageService
					.findByTradingLanguageCode(code, provider);

			if (tradingLanguage != null) {
				return new LanguageDto(tradingLanguage);
			}

			throw new EntityDoesNotExistsException(TradingLanguage.class, code);
		} else {
			if (StringUtils.isBlank(code)) {
				missingParameters.add("code");
			}

			throw new MissingParameterException(
					getMissingParametersExceptionMessage());
		}
	}

}
