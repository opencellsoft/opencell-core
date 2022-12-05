/*
 * (C) Copyright 2015-2020 Opencell SAS (https://opencellsoft.com/) and contributors.
 *
 * This program is free software: you can redistribute it and/or modify it under the terms of the GNU Affero General
 * Public License as published by the Free Software Foundation, either version 3 of the License, or (at your option)
 * any later version.
 *
 * THERE IS NO WARRANTY FOR THE PROGRAM, TO THE EXTENT PERMITTED BY APPLICABLE LAW. EXCEPT WHEN
 * OTHERWISE STATED IN WRITING THE COPYRIGHT HOLDERS AND/OR OTHER PARTIES PROVIDE THE PROGRAM "AS
 * IS" WITHOUT WARRANTY OF ANY KIND, EITHER EXPRESSED OR IMPLIED, INCLUDING, BUT NOT LIMITED TO, THE
 * IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE. THE ENTIRE RISK AS TO
 * THE QUALITY AND PERFORMANCE OF THE PROGRAM IS WITH YOU. SHOULD THE PROGRAM PROVE DEFECTIVE,
 * YOU ASSUME THE COST OF ALL NECESSARY SERVICING, REPAIR OR CORRECTION.
 *
 * For more information on the GNU Affero General Public License, please consult
 * <https://www.gnu.org/licenses/agpl-3.0.en.html>.
 */
package org.meveo.admin.action.billing;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import jakarta.faces.view.ViewScoped;
import jakarta.inject.Inject;
import jakarta.inject.Named;

import org.jboss.seam.international.status.Messages;
import org.jboss.seam.international.status.builder.BundleKey;
import org.meveo.admin.action.BaseBean;
import org.meveo.admin.exception.BusinessEntityException;
import org.meveo.admin.exception.BusinessException;
import org.meveo.admin.web.interceptor.ActionMethod;
import org.meveo.model.billing.Language;
import org.meveo.model.billing.TradingLanguage;
import org.meveo.service.base.PersistenceService;
import org.meveo.service.base.local.IPersistenceService;
import org.meveo.service.billing.impl.TradingLanguageService;
import org.primefaces.event.SelectEvent;

/**
 * Standard backing bean for {@link TradingLanguage} (extends {@link BaseBean}
 * that provides almost all common methods to handle entities filtering/sorting
 * in datatable, their create, edit, view, delete operations). It works with
 * Manaty custom JSF components.
 */
@Named
@ViewScoped
public class TradingLanguageBean extends BaseBean<TradingLanguage> {

	private static final long serialVersionUID = 1L;

	/**
	 * Injected @{link TradingLanguage} service. Extends
	 * {@link PersistenceService} .
	 */
	@Inject
	private TradingLanguageService tradingLanguageService;

	/**
	 * Constructor. Invokes super constructor and provides class type of this
	 * bean for {@link BaseBean}.
	 */
	public TradingLanguageBean() {
		super(TradingLanguage.class);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.meveo.admin.action.BaseBean#saveOrUpdate(boolean)
	 */
	@Override
    @ActionMethod
	public String saveOrUpdate(boolean killConversation) throws BusinessException{
		String back = null;
		try {
			for (TradingLanguage tr : tradingLanguageService.list()) {
				if (tr.getLanguage()
						.getLanguageCode()
						.equalsIgnoreCase(
								entity.getLanguage().getLanguageCode())
						&& !tr.getId().equals(entity.getId())) {
					throw new BusinessEntityException();
				}
			}
			back = super.saveOrUpdate(killConversation);
		} catch (BusinessEntityException e) {
			messages.error(new BundleKey("messages",
					"tradingLanguage.uniqueField"));
		} catch (Exception e) {
			log.error("failed to save or update trading language ",e);

			messages.error(new BundleKey("messages",
					"tradingLanguage.uniqueField"));
		}

		return back;
	}

	public void onLanguageSelect(SelectEvent event) {
		if (event.getObject() instanceof Language) {
			Language language = (Language) event.getObject();
			log.info("populatLanguages language",
					language != null ? language.getLanguageCode() : null);
			if (language != null) {
				entity.setLanguage(language);
				entity.setPrDescription(language.getDescriptionEn());
			}
		}

	}

	/**
	 * @see org.meveo.admin.action.BaseBean#getPersistenceService()
	 */
	@Override
	protected IPersistenceService<TradingLanguage> getPersistenceService() {
		return tradingLanguageService;
	}

	@Override
	public String getNewViewName() {
		return "tradingLanguagesDetail";
	}

	@Override
	public String getEditViewName() {
		return "tradingLanguagesDetail";
	}

	@Override
	protected List<String> getFormFieldsToFetch() {
		return Arrays.asList("language");
	}

	@Override
	protected List<String> getListFieldsToFetch() {
		return Arrays.asList("language");
	}

	@Override
	protected String getDefaultSort() {
		return "language.languageCode";
	}
	
	public Map<String,String> getLanguageCodes(){
		Map<String,String> result=new HashMap<String,String>();
		List<TradingLanguage> langs=tradingLanguageService.list();
		for(TradingLanguage lang:langs){
			result.put(lang.getLanguageCode(), lang.getLanguageCode());
		}
		return result;
	}

}
