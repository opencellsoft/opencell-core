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
import java.util.List;

import jakarta.faces.view.ViewScoped;
import jakarta.inject.Inject;
import jakarta.inject.Named;

import org.jboss.seam.international.status.builder.BundleKey;
import org.meveo.admin.action.BaseBean;
import org.meveo.admin.exception.BusinessEntityException;
import org.meveo.admin.exception.BusinessException;
import org.meveo.admin.web.interceptor.ActionMethod;
import org.meveo.model.billing.Country;
import org.meveo.model.billing.TradingCountry;
import org.meveo.service.base.PersistenceService;
import org.meveo.service.base.local.IPersistenceService;
import org.meveo.service.billing.impl.TradingCountryService;
import org.primefaces.event.SelectEvent;

/**
 * Standard backing bean for {@link TradingCountry} (extends {@link BaseBean}
 * that provides almost all common methods to handle entities filtering/sorting
 * in datatable, their create, edit, view, delete operations). It works with
 * Manaty custom JSF components.
 * @author Abdellatif BARI
 * @lastModifiedVersion 8.0.0
 */
@Named
@ViewScoped
public class TradingCountryBean extends BaseBean<TradingCountry> {

	private static final long serialVersionUID = 1L;

	/**
	 * Injected @{link TradingCountry} service. Extends
	 * {@link PersistenceService} .
	 */
	@Inject
	private TradingCountryService tradingCountryService;

	/**
	 * Constructor. Invokes super constructor and provides class type of this
	 * bean for {@link BaseBean}.
	 */
	public TradingCountryBean() {
		super(TradingCountry.class);
	}

	public void onCountrySelect(SelectEvent event) {
		if (event.getObject() instanceof Country) {
			Country country = (Country) event.getObject();
			log.info("populatCountries country",
					country != null ? country.getCountryCode() : null);
			if (country != null) {
				entity.setCountry(country);
				entity.setDescription(country.getDescription());
			}
		}

	}

	/**
	 * Factory method, that is invoked if data model is empty. Invokes
	 * BaseBean.list() method that handles all data model loading. Overriding is
	 * needed only to put factory name on it.
	 * 
	 * @return list of trading countries
	 * 
	 */
	@Override
	public List<TradingCountry> listAll() {
		getFilters();
		if (filters.containsKey("countryCode")) {
			filters.put("country.countryCode", filters.get("countryCode"));
			filters.remove("countryCode");
		} else if (filters.containsKey("country.countryCode")) {
			filters.remove("country.countryCode");
		}
		return super.listAll();
	}

	@Override
    @ActionMethod
	public String saveOrUpdate(boolean killConversation) throws BusinessException{
		String back = null;
		try {
			for (TradingCountry tr : tradingCountryService.list()) {
				if (tr.getCountry().getCountryCode()
						.equalsIgnoreCase(entity.getCountry().getCountryCode())
						&& !tr.getId().equals(entity.getId())) {
					throw new BusinessEntityException();
				}
			}
			back = super.saveOrUpdate(killConversation);

		} catch (BusinessEntityException e) {
			messages.error(new BundleKey("messages",
					"tradingCountry.uniqueField"));
		} catch (Exception e) {
			log.error("failed to save or update trading country",e);
			messages.error(new BundleKey("messages",
					"tradingCountry.uniqueField"));
		}
		return back;
	}

	/**
	 * @see org.meveo.admin.action.BaseBean#getPersistenceService()
	 */
	@Override
	protected IPersistenceService<TradingCountry> getPersistenceService() {
		return tradingCountryService;
	}

	@Override
	protected List<String> getFormFieldsToFetch() {
		return Arrays.asList("country");
	}

	@Override
	protected List<String> getListFieldsToFetch() {
		return Arrays.asList("country");
	}

	@Override
	protected String getDefaultSort() {
		return "country.countryCode";
	}

}
