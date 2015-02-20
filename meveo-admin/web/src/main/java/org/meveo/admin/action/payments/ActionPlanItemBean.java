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

import javax.inject.Inject;
import javax.inject.Named;
import javax.persistence.EntityExistsException;

import org.jboss.seam.international.status.builder.BundleKey;
import org.meveo.admin.action.BaseBean;
import org.meveo.admin.exception.BusinessException;
import org.meveo.model.payments.ActionPlanItem;
import org.meveo.model.payments.DunningPlan;
import org.meveo.service.base.PersistenceService;
import org.meveo.service.base.local.IPersistenceService;
import org.meveo.service.payments.impl.ActionPlanItemService;
import org.meveo.service.payments.impl.DunningPlanService;
import org.omnifaces.cdi.ViewScoped;

/**
 * Standard backing bean for {@link ActionPlanItem} (extends {@link BaseBean}
 * that provides almost all common methods to handle entities filtering/sorting
 * in datatable, their create, edit, view, delete operations). It works with
 * Manaty custom JSF components.
 */
@Named
@ViewScoped
public class ActionPlanItemBean extends BaseBean<ActionPlanItem> {

	private static final long serialVersionUID = 1L;

	/**
	 * Injected @{link ActionPlanItem} service. Extends
	 * {@link PersistenceService}.
	 */
	@Inject
	private ActionPlanItemService actionPlanItemService;

	@Inject
	private DunningPlanService dunningPlanService;

	@Inject
	private DunningPlan dunningPlan;

	/**
	 * Constructor. Invokes super constructor and provides class type of this
	 * bean for {@link BaseBean}.
	 */
	public ActionPlanItemBean() {
		super(ActionPlanItem.class);
	}

	/**
	 * Factory method for entity to edit. If objectId param set load that entity
	 * from database, otherwise create new.
	 * 
	 * @throws BusinessException
	 * 
	 * @throws IllegalAccessException
	 * @throws InstantiationException
	 */
	public ActionPlanItem initEntity() {
		if (dunningPlan != null && dunningPlan.getId() == null) {
			try {
				dunningPlanService.create(dunningPlan);
			} catch (BusinessException e) {
				messages.info(new BundleKey("messages",
						"message.exception.business"));
			}
		}
		super.initEntity();
		entity.setDunningPlan(dunningPlan);
		return entity;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.meveo.admin.action.BaseBean#saveOrUpdate(boolean)
	 */
	@Override
	public String saveOrUpdate(boolean killConversation)
			throws BusinessException {
		dunningPlan.getActions().add(entity);
		super.saveOrUpdate(killConversation);
		return "/pages/payments/dunning/dunningPlanDetail.xhtml?objectId="
				+ dunningPlan.getId() + "&edit=true";
	}

	/**
	 * @see org.meveo.admin.action.BaseBean#getPersistenceService()
	 */
	@Override
	protected IPersistenceService<ActionPlanItem> getPersistenceService() {
		return actionPlanItemService;
	}

	@Override
	public void delete(Long id) {
		try {
			entity = getPersistenceService().findById(id);
			log.info(String.format("Deleting entity %s with id = %s", entity
					.getClass().getName(), id));
			entity.getDunningPlan().getActions().remove(entity);
			getPersistenceService().remove(id);
			entity = null;
			messages.info(new BundleKey("messages", "delete.successful"));
		} catch (Throwable t) {
			if (t.getCause() instanceof EntityExistsException) {
				log.info(
						"delete was unsuccessful because entity is used in the system",
						t);
				messages.error(new BundleKey("messages",
						"error.delete.entityUsed"));
			} else {
				log.info("unexpected exception when deleting!", t);
				messages.error(new BundleKey("messages",
						"error.delete.unexpected"));
			}
		}
	}
}
