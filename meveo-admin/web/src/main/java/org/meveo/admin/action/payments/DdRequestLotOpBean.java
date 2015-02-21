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
package org.meveo.admin.action.payments;

import java.util.Arrays;
import java.util.List;

import javax.inject.Inject;
import javax.inject.Named;

import org.jboss.seam.international.status.builder.BundleKey;
import org.meveo.admin.action.BaseBean;
import org.meveo.admin.exception.BusinessException;
import org.meveo.model.payments.ActionPlanItem;
import org.meveo.model.payments.DDRequestLotOp;
import org.meveo.model.payments.DDRequestOpEnum;
import org.meveo.model.payments.DDRequestOpStatusEnum;
import org.meveo.service.base.local.IPersistenceService;
import org.meveo.service.payments.impl.DDRequestLotOpService;
import org.omnifaces.cdi.ViewScoped;

/**
 * Standard backing bean for {@link ActionPlanItem} (extends {@link BaseBean}
 * that provides almost all common methods to handle entities filtering/sorting
 * in datatable, their create, edit, view, delete operations). It works with
 * Manaty custom JSF components.
 */
@Named
@ViewScoped
public class DdRequestLotOpBean extends BaseBean<DDRequestLotOp> {

	private static final long serialVersionUID = 1L;

	@Inject
	private DDRequestLotOpService ddRequestLotOpService;

	/**
	 * Constructor. Invokes super constructor and provides class type of this
	 * bean for {@link BaseBean}.
	 */
	public DdRequestLotOpBean() {
		super(DDRequestLotOp.class);
	}

	@Override
	public String saveOrUpdate(DDRequestLotOp entity) throws BusinessException {
		if (entity.isTransient()) {
			entity.setDdrequestOp(DDRequestOpEnum.CREATE);
			entity.setStatus(DDRequestOpStatusEnum.WAIT);
			getPersistenceService().create(entity);
			messages.info(new BundleKey("messages", "save.successful"));
		} else {
			getPersistenceService().update(entity);
			messages.info(new BundleKey("messages", "update.successful"));
		}

		return back();
	}

	@Override
	protected String getListViewName() {
		return "ddrequestLotOps";
	}

	@Override
	public String getEditViewName() {
		return "ddrequestLotOpDetail";
	}

	/**
	 * @see org.meveo.admin.action.BaseBean#getPersistenceService()
	 */
	@Override
	protected IPersistenceService<DDRequestLotOp> getPersistenceService() {
		return ddRequestLotOpService;
	}

	@Override
	protected List<String> getFormFieldsToFetch() {
		return Arrays.asList("provider");
	}

	@Override
	protected List<String> getListFieldsToFetch() {
		return Arrays.asList("provider");
	}

}
