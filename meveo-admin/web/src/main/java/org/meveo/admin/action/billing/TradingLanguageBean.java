/*
 * (C) Copyright 2009-2014 Manaty SARL (http://manaty.net/) and contributors.
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Affero General Public License as
 * published by the Free Software Foundation, either version 3 of the
 * License, or (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Affero General Public License for more details.
 *
 * You should have received a copy of the GNU Affero General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package org.meveo.admin.action.billing;

import java.util.Arrays;
import java.util.List;

import javax.inject.Inject;
import javax.inject.Named;

import org.jboss.seam.international.status.Messages;
import org.jboss.seam.international.status.builder.BundleKey;
import org.meveo.admin.action.BaseBean;
import org.meveo.admin.exception.BusinessEntityException;
import org.meveo.model.billing.Language;
import org.meveo.model.billing.TradingLanguage;
import org.meveo.model.crm.Provider;
import org.meveo.service.base.PersistenceService;
import org.meveo.service.base.local.IPersistenceService;
import org.meveo.service.billing.impl.TradingLanguageService;
import org.meveo.service.crm.impl.ProviderService;
import org.omnifaces.cdi.ViewScoped;
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

	@Inject
	private ProviderService providerService;

	@Inject
	private Messages messages;

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
	public String saveOrUpdate(boolean killConversation) {
		String back = null;
		try {
			Provider currentProvider = providerService
					.findById(getCurrentProvider().getId());
			for (TradingLanguage tr : currentProvider.getTradingLanguages()) {
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
			log.error(e.getMessage());

			messages.error(new BundleKey("messages",
					"tradingLanguage.uniqueField"));
		}

		return back;
	}

	public void onRowSelect(SelectEvent event) {
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
	protected String getListViewName() {
		return "tradingLanguages";
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
		return Arrays.asList("language", "provider");
	}

	@Override
	protected List<String> getListFieldsToFetch() {
		return Arrays.asList("language");
	}

	@Override
	protected String getDefaultSort() {
		return "language.languageCode";
	}

}
