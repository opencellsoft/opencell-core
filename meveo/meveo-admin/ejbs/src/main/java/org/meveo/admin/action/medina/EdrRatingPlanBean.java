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
package org.meveo.admin.action.medina;

import javax.enterprise.context.ConversationScoped;
import javax.enterprise.inject.Produces;
import javax.inject.Inject;
import javax.inject.Named;

import org.meveo.admin.action.BaseBean;
import org.meveo.admin.util.pagination.PaginationDataModel;
import org.meveo.model.rating.EDRRatingPlan;
import org.meveo.service.base.PersistenceService;
import org.meveo.service.base.local.IPersistenceService;
import org.meveo.service.medina.impl.EdrRatingPlanService;

/**
 * @author MBAREK
 * 
 */
@Named
@ConversationScoped
public class EdrRatingPlanBean extends BaseBean<EDRRatingPlan> {

	private static final long serialVersionUID = 1L;

	/**
	 * Injected @{link PriceCode} service. Extends {@link PersistenceService}.
	 */
	@Inject
	private EdrRatingPlanService edrRatingPlanService;

	/**
	 * Constructor. Invokes super constructor and provides class type of this
	 * bean for {@link BaseBean}.
	 */
	public EdrRatingPlanBean() {
		super(EDRRatingPlan.class);
	}

	/**
	 * Factory method for entity to edit. If objectId param set load that entity
	 * from database, otherwise create new.
	 * 
	 * @throws IllegalAccessException
	 * @throws InstantiationException
	 */
	@Produces
	@Named("edrRatingPlan")
	public EDRRatingPlan init() {
		return initEntity();
	}

	/**
	 * Factory method, that is invoked if data model is empty. Invokes
	 * BaseBean.list() method that handles all data model loading. Overriding is
	 * needed only to put factory name on it.
	 * @return 
	 * 
	 * @see org.meveo.admin.action.BaseBean#list()
	 */
	@Produces
	@Named("edrRatingPlans")
	@ConversationScoped
	public PaginationDataModel<EDRRatingPlan> list() {
		return super.list();
	}

	/**
	 * @see org.meveo.admin.action.BaseBean#getPersistenceService()
	 */
	@Override
	protected IPersistenceService<EDRRatingPlan> getPersistenceService() {
		return edrRatingPlanService;
	}

}
