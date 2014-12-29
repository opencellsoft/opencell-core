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

import javax.enterprise.context.ConversationScoped;
import javax.inject.Inject;
import javax.inject.Named;

import org.meveo.admin.action.StatelessBaseBean;
import org.meveo.model.billing.Language;
import org.meveo.model.crm.Provider;
import org.meveo.service.base.local.IPersistenceService;
import org.meveo.service.crm.impl.ProviderService;
import org.primefaces.event.SelectEvent;

@Named
@ConversationScoped
public class ProviderBean extends StatelessBaseBean<Provider> {

	private static final long serialVersionUID = 1L;

	@Inject
	private ProviderService providerService;

	public ProviderBean() {
		super(Provider.class);
	}

	/**
	 * Factory method for entity to edit. If objectId param set load that entity
	 * from database, otherwise create new.
	 * 
	 * @throws IllegalAccessException
	 * @throws InstantiationException
	 */

	public Provider initEntity() {
		return super.initEntity();
	}

	/**
	 * Override default list view name. (By default its class name starting
	 * lower case + 's').
	 * 
	 * @see org.meveo.admin.action.BaseBean#getDefaultViewName()
	 */
	protected String getDefaultViewName() {
		return "sellers";
	}

	/**
	 * @see org.meveo.admin.action.BaseBean#getPersistenceService()
	 */
	@Override
	protected IPersistenceService<Provider> getPersistenceService() {
		return providerService;
	}

	@Override
	protected String getListViewName() {
		return "providers";
	}

	@Override
	protected String getDefaultSort() {
		return "code";
	}

	public void onRowSelect(SelectEvent event) {
		if (event.getObject() instanceof Language) {
			Language language = (Language) event.getObject();
			log.info("populatLanguages language",
					language != null ? language.getLanguageCode() : null);
			if (language != null) {
				entity.setLanguage(language);
			}
		}

	}

}
