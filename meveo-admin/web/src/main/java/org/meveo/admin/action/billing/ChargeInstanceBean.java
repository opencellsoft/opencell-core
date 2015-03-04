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

import javax.inject.Inject;
import javax.inject.Named;

import org.meveo.admin.action.BaseBean;
import org.meveo.model.billing.ChargeInstance;
import org.meveo.model.billing.WalletInstance;
import org.meveo.service.base.PersistenceService;
import org.meveo.service.base.local.IPersistenceService;
import org.meveo.service.billing.impl.ChargeInstanceService;
import org.omnifaces.cdi.ViewScoped;

/**
 * Standard backing bean for {@link WalletInstance} (extends {@link BaseBean}
 * that provides almost all common methods to handle entities filtering/sorting
 * in datatable, their create, edit, view, delete operations). It works with
 * Manaty custom JSF components.
 */
@Named
@ViewScoped
public class ChargeInstanceBean extends BaseBean<ChargeInstance> {

	private static final long serialVersionUID = 1L;

	/**
	 * Injected @{link WalletInstance} service. Extends
	 * {@link PersistenceService}.
	 */
	@SuppressWarnings("rawtypes")
	@Inject
	private ChargeInstanceService chargeInstanceService;

	/**
	 * Customer account Id passed as a parameter. Used when creating new
	 * WalletInstance from customer account window, so default customer account
	 * will be set on newly created wallet.
	 */

	/**
	 * Constructor. Invokes super constructor and provides class type of this
	 * bean for {@link BaseBean}.
	 */
	public ChargeInstanceBean() {
		super(ChargeInstance.class);
	}

	/**
	 * Factory method for entity to edit. If objectId param set load that entity
	 * from database, otherwise create new.
	 * 
	 * @throws IllegalAccessException
	 * @throws InstantiationException
	 */
	public ChargeInstance initEntity() {
		super.initEntity();
		return entity;
	}

	/**
	 * @see org.meveo.admin.action.BaseBean#getPersistenceService()
	 */
	@SuppressWarnings("unchecked")
	@Override
	protected IPersistenceService<ChargeInstance> getPersistenceService() {
		return chargeInstanceService;
	}

}
