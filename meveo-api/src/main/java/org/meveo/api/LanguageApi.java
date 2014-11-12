package org.meveo.api;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

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
public class LanguageApi {

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
			tradingLanguageService.create(tradingLanguage, currentUser);
		} else {
			StringBuilder sb = new StringBuilder(
					"The following parameters are required ");
			List<String> missingFields = new ArrayList<String>();

			if (StringUtils.isBlank(postData.getCode())) {
				missingFields.add("code");
			}
			if (missingFields.size() > 1) {
				sb.append(org.apache.commons.lang.StringUtils.join(
						missingFields.toArray(), ", "));
			} else {
				sb.append(missingFields.get(0));
			}
			sb.append(".");

			throw new MissingParameterException(sb.toString());
		}
	}

	public void remove(String code, User currentUser)
			throws MissingParameterException, EntityDoesNotExistsException {
		if (!StringUtils.isBlank(code)) {
			TradingLanguage tradingLanguage = tradingLanguageService
					.findByTradingLanguageCode(code, currentUser.getProvider());
			if (tradingLanguage == null) {
				throw new EntityDoesNotExistsException(TradingLanguage.class,
						code);
			} else {
				tradingLanguageService.remove(tradingLanguage);
			}
		} else {
			StringBuilder sb = new StringBuilder(
					"The following parameters are required ");
			List<String> missingFields = new ArrayList<String>();

			if (StringUtils.isBlank(code)) {
				missingFields.add("code");
			}
			if (missingFields.size() > 1) {
				sb.append(org.apache.commons.lang.StringUtils.join(
						missingFields.toArray(), ", "));
			} else {
				sb.append(missingFields.get(0));
			}
			sb.append(".");

			throw new MissingParameterException(sb.toString());
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
				create(postData, currentUser);
			}
		} else {
			StringBuilder sb = new StringBuilder(
					"The following parameters are required ");
			List<String> missingFields = new ArrayList<String>();

			if (StringUtils.isBlank(postData.getCode())) {
				missingFields.add("code");
			}
			if (missingFields.size() > 1) {
				sb.append(org.apache.commons.lang.StringUtils.join(
						missingFields.toArray(), ", "));
			} else {
				sb.append(missingFields.get(0));
			}
			sb.append(".");

			throw new MissingParameterException(sb.toString());
		}
	}

	public LanguageDto find(String code, User currentUser)
			throws MeveoApiException {
		if (!StringUtils.isBlank(code)) {
			TradingLanguage tradingLanguage = tradingLanguageService
					.findByTradingLanguageCode(code, currentUser.getProvider());

			if (tradingLanguage != null) {
				return new LanguageDto(tradingLanguage);
			}

			throw new EntityDoesNotExistsException(TradingLanguage.class, code);
		} else {
			StringBuilder sb = new StringBuilder(
					"The following parameters are required ");
			List<String> missingFields = new ArrayList<String>();

			if (StringUtils.isBlank(code)) {
				missingFields.add("code");
			}
			if (missingFields.size() > 1) {
				sb.append(org.apache.commons.lang.StringUtils.join(
						missingFields.toArray(), ", "));
			} else {
				sb.append(missingFields.get(0));
			}
			sb.append(".");

			throw new MissingParameterException(sb.toString());
		}
	}

}
