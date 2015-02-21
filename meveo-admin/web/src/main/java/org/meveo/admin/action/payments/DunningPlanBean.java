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

import javax.enterprise.inject.Produces;
import javax.inject.Inject;
import javax.inject.Named;

import org.jboss.seam.international.status.builder.BundleKey;
import org.meveo.admin.action.BaseBean;
import org.meveo.admin.exception.BusinessEntityException;
import org.meveo.admin.exception.BusinessException;
import org.meveo.model.payments.ActionPlanItem;
import org.meveo.model.payments.DunningPlan;
import org.meveo.model.payments.DunningPlanTransition;
import org.meveo.service.base.PersistenceService;
import org.meveo.service.base.local.IPersistenceService;
import org.meveo.service.payments.impl.ActionPlanItemService;
import org.meveo.service.payments.impl.DunningPlanService;
import org.meveo.service.payments.impl.DunningPlanTransitionService;
import org.omnifaces.cdi.ViewScoped;

/**
 * Standard backing bean for {@link DunningPlan} (extends {@link BaseBean} that
 * provides almost all common methods to handle entities filtering/sorting in
 * datatable, their create, edit, view, delete operations). It works with Manaty
 * custom JSF components.
 */
@Named
@ViewScoped
public class DunningPlanBean extends BaseBean<DunningPlan> {

	private static final long serialVersionUID = 1L;

	/**
	 * Injected @{link DunningPlan} service. Extends {@link PersistenceService}.
	 */
	@Inject
	private DunningPlanService dunningPlanService;

	@Inject
	private DunningPlanTransitionService dunningPlanTransitionService;

	@Inject
	private ActionPlanItemService actionPlanItemService;

	// @Produces
	// @Named
	private transient DunningPlanTransition dunningPlanTransition = new DunningPlanTransition();

	// @Produces
	// @Named
	private transient ActionPlanItem actionPlanItem = new ActionPlanItem();


    /**
	 * Constructor. Invokes super constructor and provides class type of this
	 * bean for {@link BaseBean}.
	 */
	public DunningPlanBean() {
		super(DunningPlan.class);
	}	

    public DunningPlanTransition getDunningPlanTransition() {
        return dunningPlanTransition;
    }

    public void setDunningPlanTransition(DunningPlanTransition dunningPlanTransition) {
        this.dunningPlanTransition = dunningPlanTransition;
    }

    public ActionPlanItem getActionPlanItem() {
        return actionPlanItem;
    }

    public void setActionPlanItem(ActionPlanItem actionPlanItem) {
        this.actionPlanItem = actionPlanItem;
    }

	public void newDunningPlanTransitionInstance() {
		this.dunningPlanTransition = new DunningPlanTransition();
	}

	public void newActionPlanItemInstance() {
		this.actionPlanItem = new ActionPlanItem();
	}

	public void saveDunningPlanTransition() {

		if (dunningPlanTransition.getId() != null) {
			dunningPlanTransitionService.update(dunningPlanTransition);
			messages.info(new BundleKey("messages", "update.successful"));
		} else {
			try {
				for (DunningPlanTransition transition : entity.getTransitions()) {

					if ((transition.getDunningLevelFrom()
							.equals(dunningPlanTransition.getDunningLevelFrom()))
							&& (transition.getDunningLevelTo()
									.equals(dunningPlanTransition
											.getDunningLevelTo()))) {
						throw new BusinessEntityException();
					}
				}
				dunningPlanTransition.setDunningPlan(entity);
				dunningPlanTransitionService.create(dunningPlanTransition);
				entity.getTransitions().add(dunningPlanTransition);
				messages.info(new BundleKey("messages", "save.successful"));
			} catch (BusinessEntityException e) {
				messages.error(new BundleKey("messages",
						"dunningPlanTransition.uniqueField"));
			} catch (Exception e) {
				log.error(e.getMessage());

				messages.error(new BundleKey("messages",
						"dunningPlanTransition.uniqueField"));
			}
		}

		dunningPlanTransition = new DunningPlanTransition();
	}

	public void saveActionPlanItem() throws BusinessException {

		if (actionPlanItem.getId() != null) {
			actionPlanItemService.update(actionPlanItem);
			messages.info(new BundleKey("messages", "update.successful"));
		} else {
			actionPlanItem.setDunningPlan(entity);
			actionPlanItemService.create(actionPlanItem);
			entity.getActions().add(actionPlanItem);
			messages.info(new BundleKey("messages", "save.successful"));

		}
		actionPlanItem = new ActionPlanItem();
	}

	public void deleteDunningPlanTransition(
			DunningPlanTransition dunningPlanTransition) {
		dunningPlanTransitionService.remove(dunningPlanTransition);
		entity.getTransitions().remove(dunningPlanTransition);
		messages.info(new BundleKey("messages", "delete.successful"));
	}

	public void deleteActionPlanItem(ActionPlanItem actionPlanItem) {
		actionPlanItemService.remove(actionPlanItem);
		entity.getActions().remove(actionPlanItem);
		messages.info(new BundleKey("messages", "delete.successful"));
	}

	public void editDunningPlanTransition(
			DunningPlanTransition dunningPlanTransition) {
		this.dunningPlanTransition = dunningPlanTransition;
	}

	public void editActionPlanItem(ActionPlanItem actionPlanItem) {
		this.actionPlanItem = actionPlanItem;
	}

	/**
	 * @see org.meveo.admin.action.BaseBean#getPersistenceService()
	 */
	@Override
	protected IPersistenceService<DunningPlan> getPersistenceService() {
		return dunningPlanService;
	}

	@Produces
	public DunningPlan getDunningPlan() {
		return entity;
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