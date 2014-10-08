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
package org.meveo.admin.action.catalog;

import javax.enterprise.context.ConversationScoped;
import javax.inject.Inject;
import javax.inject.Named;

import org.meveo.admin.action.BaseBean;
import org.meveo.model.catalog.ChargeTemplate;
import org.meveo.model.catalog.DiscountPlanMatrix;
import org.meveo.service.base.PersistenceService;
import org.meveo.service.base.local.IPersistenceService;
import org.meveo.service.catalog.impl.DiscountPlanMatrixService;
import org.primefaces.event.SelectEvent;

/**
 * Standard backing bean for {@link DiscountPlanMatrix} (extends {@link BaseBean} that provides almost all common methods to handle entities filtering/sorting in datatable, their
 * create, edit, view, delete operations). It works with Manaty custom JSF components.
 * 
 */
@Named
@ConversationScoped
public class DiscountPlanMatrixBean extends BaseBean<DiscountPlanMatrix> {

    private static final long serialVersionUID = 1L;

    /**
     * Injected @{link DiscountPlanMatrix} service. Extends {@link PersistenceService}.
     */
    @Inject
    private DiscountPlanMatrixService discountPlanMatrixService;

    /**
     * Constructor. Invokes super constructor and provides class type of this bean for {@link BaseBean}.
     */
    public DiscountPlanMatrixBean() {
        super(DiscountPlanMatrix.class);
    }
	/**
	 * Factory method for entity to edit. If objectId param set load that entity
	 * from database, otherwise create new.
	 * 
	 * @throws IllegalAccessException
	 * @throws InstantiationException
	 */
	public DiscountPlanMatrix initEntity() {
		DiscountPlanMatrix obj = super.initEntity();
		obj.setNbPeriod(0);
		return obj;
	}
	
    /**
     * Override default list view name. (By default view name is class name starting lower case + ending 's').
     * 
     * @see org.meveo.admin.action.BaseBean#getDefaultViewName()
     */
    protected String getDefaultViewName() {
        return "discountPlanMatrixes";
    }

    /**
     * @see org.meveo.admin.action.BaseBean#getPersistenceService()
     */
    @Override
    protected String getListViewName() {
    	 return "discountPlanMatrixes";
    }
    
    @Override
    protected IPersistenceService<DiscountPlanMatrix> getPersistenceService() {
        return discountPlanMatrixService;
    }

	public void onRowSelect(SelectEvent event) {
		if (event.getObject() instanceof ChargeTemplate) {
			ChargeTemplate chargeTemplate = (ChargeTemplate) event.getObject();
			if (chargeTemplate != null) {
				entity.setEventCode(chargeTemplate.getCode());
			}
		}

	}

	@Override
	protected String getDefaultSort() {
		return "eventCode";
	}
}