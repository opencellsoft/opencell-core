/*
 * (C) Copyright 2009-2013 Manaty SARL (http://manaty.net/) and contributors.
 *
 * Licensed under the GNU Public Licence, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.gnu.org/licenses/gpl-2.0.txt
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.meveo.admin.action.admin;

import java.util.Date;

import javax.enterprise.context.ConversationScoped;
import javax.inject.Inject;
import javax.inject.Named;

import org.meveo.admin.action.BaseBean;
import org.meveo.model.Auditable;
import org.meveo.model.admin.Currency;
import org.meveo.service.admin.impl.CurrencyService;
import org.meveo.service.base.local.IPersistenceService;

/**
 * Standard backing bean for {@link Currency} (extends {@link BaseBean} that
 * provides almost all common methods to handle entities filtering/sorting in
 * datatable, their create, edit, view, delete operations). It works with Manaty
 * custom JSF components.
 * 
 * @author Ignas
 * @created 2009.10.13
 */
@Named
@ConversationScoped
public class CurrencyBean extends BaseBean<Currency> {

	private static final long serialVersionUID = 1L;

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
}