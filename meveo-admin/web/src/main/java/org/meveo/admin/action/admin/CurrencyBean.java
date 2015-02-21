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
package org.meveo.admin.action.admin;

import java.util.Date;

import javax.inject.Inject;
import javax.inject.Named;

import org.meveo.admin.action.BaseBean;
import org.meveo.model.Auditable;
import org.meveo.model.admin.Currency;
import org.meveo.service.admin.impl.CurrencyService;
import org.meveo.service.base.local.IPersistenceService;
import org.omnifaces.cdi.ViewScoped;

/**
 * Standard backing bean for {@link Currency} (extends {@link BaseBean} that
 * provides almost all common methods to handle entities filtering/sorting in
 * datatable, their create, edit, view, delete operations). It works with Manaty
 * custom JSF components.
 */
@Named
@ViewScoped
public class CurrencyBean extends BaseBean<Currency> {

    private static final long serialVersionUID = 1895532923500996522L;

    @Inject
	private CurrencyService currencyService;

	/**
	 * Constructor. Invokes super constructor and provides class type of this
	 * bean for {@link BaseBean}.
	 */
	public CurrencyBean() {
		super(Currency.class);
	}

	/**
	 * Factory method for entity to edit. If objectId param set load that entity
	 * from database, otherwise create new.
	 * 
	 * @throws IllegalAccessException
	 * @throws InstantiationException
	 */
	@Override
	public Currency initEntity() {
		Currency currency = super.initEntity();

		Auditable auditable = new Auditable();
		auditable.setCreated(new Date());
		auditable.setCreator(getCurrentUser());
		currency.setAuditable(auditable);

		return currency;
	}

	@Override
	protected String getListViewName() {
		return "currencies";
	}

	/**
	 * @see org.meveo.admin.action.BaseBean#getPersistenceService()
	 */
	@Override
	protected IPersistenceService<Currency> getPersistenceService() {
		return currencyService;
	}

	@Override
	protected String getDefaultSort() {
		return "currencyCode";
	}
}