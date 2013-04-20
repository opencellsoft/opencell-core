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
import org.meveo.model.billing.Country;
import org.meveo.model.catalog.ChargeTemplate;
import org.meveo.model.catalog.PricePlanMatrix;
import org.meveo.service.base.PersistenceService;
import org.meveo.service.base.local.IPersistenceService;
import org.meveo.service.catalog.impl.PricePlanMatrixService;
import org.primefaces.event.SelectEvent;

/**
 * Standard backing bean for {@link PricePlanMatrix} (extends {@link BaseBean} that provides almost all common methods to handle entities filtering/sorting in datatable, their
 * create, edit, view, delete operations). It works with Manaty custom JSF components.
 * 
 * @author Ignas Lelys
 * @created Nov 29, 2010
 * 
 */
@Named
@ConversationScoped
public class PricePlanMatrixBean extends BaseBean<PricePlanMatrix> {

    private static final long serialVersionUID = 1L;

    /**
     * Injected @{link PricePlanMatrix} service. Extends {@link PersistenceService}.
     */
    @Inject
    private PricePlanMatrixService pricePlanMatrixService;

    /**
     * Constructor. Invokes super constructor and provides class type of this bean for {@link BaseBean}.
     */
    public PricePlanMatrixBean() {
        super(PricePlanMatrix.class);
    }

    /**
     * Factory method for entity to edit. If objectId param set load that entity from database, otherwise create new.
     * 
     * @throws IllegalAccessException
     * @throws InstantiationException
     */
   
    public PricePlanMatrix initEntity() {
        return super.initEntity();
    }

    /**
     * Override default list view name. (By default its class name starting lower case + 's').
     * 
     * @see org.meveo.admin.action.BaseBean#getDefaultViewName()
     */
    protected String getDefaultViewName() {
        return "pricePlanMatrixes";
    }

    /**
     * @see org.meveo.admin.action.BaseBean#getPersistenceService()
     */
    @Override
    protected IPersistenceService<PricePlanMatrix> getPersistenceService() {
        return pricePlanMatrixService;
    }
    
    @Override
    protected String getListViewName() {
    	 return "pricePlanMatrixes";
    }
    
    public void onRowSelect(SelectEvent event){  
    	System.out.println("onRowSelect : event.getObject()");
    	if(event.getObject() instanceof ChargeTemplate){
    		ChargeTemplate chargeTemplate = (ChargeTemplate)event.getObject();  
    		System.out.println("onRowSelect chargeTemplate:"+ chargeTemplate.getCode());
    		if (chargeTemplate != null) {
    			entity.setEventCode(chargeTemplate.getCode());
    		}
    	}
    	
    } 

}