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
package org.meveo.admin.action.payments;

import javax.enterprise.context.ConversationScoped;
import javax.enterprise.inject.Produces;
import javax.inject.Inject;
import javax.inject.Named;

import org.jboss.seam.international.status.builder.BundleKey;
import org.meveo.admin.action.BaseBean;
import org.meveo.admin.exception.BusinessEntityException;
import org.meveo.admin.util.pagination.PaginationDataModel;
import org.meveo.model.payments.ActionPlanItem;
import org.meveo.model.payments.DunningPlan;
import org.meveo.model.payments.DunningPlanTransition;
import org.meveo.service.base.PersistenceService;
import org.meveo.service.base.local.IPersistenceService;
import org.meveo.service.payments.impl.ActionPlanItemService;
import org.meveo.service.payments.impl.DunningPlanService;
import org.meveo.service.payments.impl.DunningPlanTransitionService;

/**
 * Standard backing bean for {@link DunningPlan} (extends {@link BaseBean} that
 * provides almost all common methods to handle entities filtering/sorting in
 * datatable, their create, edit, view, delete operations). It works with Manaty
 * custom JSF components.
 * 
 * @author Tyshan(tyshan@manaty.net)
 */
@Named
@ConversationScoped
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
	
	@Produces
	@Named
	private DunningPlanTransition dunningPlanTransition = new DunningPlanTransition();
	
	
	@Produces
	@Named
	private ActionPlanItem actionPlanItem = new ActionPlanItem();

	/**
	 * Constructor. Invokes super constructor and provides class type of this
	 * bean for {@link BaseBean}.
	 */
	public DunningPlanBean() {
		super(DunningPlan.class);
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
			try{
		    	 for (DunningPlanTransition transition : entity.getTransitions()) {
		             
		         	if ((transition.getDunningLevelFrom().equals(dunningPlanTransition.getDunningLevelFrom())) && (transition.getDunningLevelTo().equals(dunningPlanTransition.getDunningLevelTo())))  { 
				            throw new BusinessEntityException();
		         	}      
		         }  
		    	 dunningPlanTransition.setDunningPlan(entity);
					dunningPlanTransitionService.create(dunningPlanTransition);
					entity.getTransitions().add(dunningPlanTransition);
					messages.info(new BundleKey("messages", "save.successful")); 
		    	} catch (BusinessEntityException e) {
		            messages.error(new BundleKey("messages", "dunningPlanTransition.uniqueField"));
		        }catch (Exception e) {
					e.printStackTrace();

		            messages.error(new BundleKey("messages", "dunningPlanTransition.uniqueField"));
				} 
		}

		dunningPlanTransition = new DunningPlanTransition();
}
	
	
	public void saveActionPlanItem() { 
		 
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
	
	
	public void deleteDunningPlanTransition(DunningPlanTransition dunningPlanTransition) {  
		dunningPlanTransitionService.remove(dunningPlanTransition); 
		entity.getTransitions().remove(dunningPlanTransition);
		messages.info(new BundleKey("messages", "delete.successful")); 
	}
	
	
	public void deleteActionPlanItem(ActionPlanItem actionPlanItem) {  
		actionPlanItemService.remove(actionPlanItem); 
		entity.getActions().remove(actionPlanItem);
		messages.info(new BundleKey("messages", "delete.successful")); 
	}
	
	
	
	public void editDunningPlanTransition(DunningPlanTransition dunningPlanTransition) {
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
	public DunningPlan getDunningPlan(){
	    return entity;
	}


 
	
	
}