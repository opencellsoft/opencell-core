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