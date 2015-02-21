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

import java.sql.BatchUpdateException;

import javax.inject.Inject;
import javax.inject.Named;

import org.meveo.admin.action.BaseBean;
import org.meveo.model.billing.Language;
import org.meveo.service.admin.impl.LanguageService;
import org.meveo.service.base.local.IPersistenceService;
import org.omnifaces.cdi.ViewScoped;

@Named
@ViewScoped
public class LanguageBean extends BaseBean<Language> {

	private static final long serialVersionUID = 1L;

	@Inject
	private LanguageService languageService;

	public LanguageBean() {
		super(Language.class);
	}

	/**
	 * @see org.meveo.admin.action.BaseBean#getPersistenceService()
	 */
	@Override
	protected IPersistenceService<Language> getPersistenceService() {
		return languageService;
	}

	public void test() throws BatchUpdateException {
		throw new BatchUpdateException();
	}

	@Override
	protected String getDefaultSort() {
		return "languageCode";
	}

}