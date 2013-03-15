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

import org.jboss.seam.ScopeType;
import org.jboss.seam.annotations.Begin;
import org.jboss.seam.annotations.End;
import org.jboss.seam.annotations.Factory;
import org.jboss.seam.annotations.In;
import org.jboss.seam.annotations.Name;
import org.jboss.seam.annotations.Out;
import org.jboss.seam.annotations.Scope;
import org.meveo.admin.action.BaseBean;
import org.meveo.admin.util.pagination.PaginationDataModel;
import org.meveo.model.billing.CatMessages;
import org.meveo.model.billing.InvoiceCategory;
import org.meveo.model.billing.LanguageEnum;
import org.meveo.model.catalog.ChargeTemplate;
import org.meveo.model.catalog.OneShotChargeTemplate;
import org.meveo.model.catalog.OneShotChargeTemplateTypeEnum;
import org.meveo.service.base.PersistenceService;
import org.meveo.service.base.local.IPersistenceService;
import org.meveo.service.catalog.local.CatMessagesServiceLocal;
import org.meveo.service.catalog.local.OneShotChargeTemplateServiceLocal;

/**
 * Standard backing bean for {@link OneShotChargeTemplate} (extends
 * {@link BaseBean} that provides almost all common methods to handle entities
 * filtering/sorting in datatable, their create, edit, view, delete operations).
 * It works with Manaty custom JSF components.
 * 
 * @author Ignas Lelys
 * @created Nov 18, 2010
 * 
 */
@Name("oneShotChargeTemplateBean")
@Scope(ScopeType.CONVERSATION)
public class OneShotChargeTemplateBean extends BaseBean<OneShotChargeTemplate> {
    private static final long serialVersionUID = 1L;

    /**
     * Injected @{link OneShotChargeTemplate} service. Extends
     * {@link PersistenceService}.
     */
    @In
    private OneShotChargeTemplateServiceLocal oneShotChargeTemplateService;
    
    @In
    private CatMessagesServiceLocal catMessagesService;
     
     private String descriptionFr;

    /**
     * Constructor. Invokes super constructor and provides class type of this
     * bean for {@link BaseBean}.
     */
    public OneShotChargeTemplateBean() {
        super(OneShotChargeTemplate.class);
    }

    /**
     * Factory method for entity to edit. If objectId param set load that entity
     * from database, otherwise create new.
     * 
     * @throws IllegalAccessException
     * @throws InstantiationException
     */
    @Begin(nested = true)
    @Factory("oneShotChargeTemplate")
    public OneShotChargeTemplate init() {
        OneShotChargeTemplate oneShotChargeTemplate= initEntity();
        descriptionFr=catMessagesService.getMessageDescription(ChargeTemplate.class.getSimpleName()+"_"+oneShotChargeTemplate.getId(),LanguageEnum.FR.toString());
        return oneShotChargeTemplate; 
    }

    /**
     * Data model of entities for data table in GUI.
     * 
     * @return filtered entities.
     */
    @Out(value = "oneShotChargeTemplates", required = false)
    protected PaginationDataModel<OneShotChargeTemplate> getDataModel() {
        return entities;
    }

    /**
     * Factory method, that is invoked if data model is empty. Invokes
     * BaseBean.list() method that handles all data model loading. Overriding is
     * needed only to put factory name on it.
     * 
     * @see org.meveo.admin.action.BaseBean#list()
     */
    @Factory("oneShotChargeTemplates")
    @Override
    @Begin(join = true)
    public void list() {
        getFilters();
        if (!filters.containsKey("disabled")) {
            filters.put("disabled", false);
        }
        super.list();
    }

    
    /**
     * Data model of entities for data table in GUI. Filters charges of Usage type.
     * 
     * @return filtered entities.
     */
    @Out(value = "oneShotChargeTemplatesForUsageType", required = false)
    protected PaginationDataModel<OneShotChargeTemplate> getDataModelForUsageType() {
        return entities;
    }

    /**
     * Factory method, that is invoked if data model is empty. Invokes
     * BaseBean.list() method that handles all data model loading. Overriding is
     * needed only to put factory name on it. Filters charges of Usage type.
     * 
     * @see org.meveo.admin.action.BaseBean#list()
     */
    @Factory("oneShotChargeTemplatesForUsageType")
    public void listForUsageType() {
        getFilters();
        if (!filters.containsKey("disabled")) {
            filters.put("disabled", false);
        }
        filters.put("oneShotChargeTemplateType", OneShotChargeTemplateTypeEnum.USAGE);
        super.list();
    }
    
    /**
     * Conversation is ended and user is redirected from edit to his previous
     * window.
     * 
     * @see org.meveo.admin.action.BaseBean#saveOrUpdate(org.meveo.model.IEntity)
     */
    @End(beforeRedirect = true, root=false)
    public String saveOrUpdate() {
    	String back;
    	if(entity.getId()!=null){
    		CatMessages oneShorMsfr=catMessagesService.getCatMessages(ChargeTemplate.class.getSimpleName()+"_"+entity.getId(),LanguageEnum.FR.toString()); 
    		oneShorMsfr.setDescription(descriptionFr);
    	    catMessagesService.update(oneShorMsfr); 
    	    back=saveOrUpdate(entity);
    	}else{
    		back=saveOrUpdate(entity); 
    	   	CatMessages catMessagesFr=new CatMessages(ChargeTemplate.class.getSimpleName()+"_"+entity.getId(),LanguageEnum.FR.toString(),descriptionFr); 
    	   	catMessagesService.create(catMessagesFr);
    	}
    	
	   	return back;
    }
    
 

    /**
     * @see org.meveo.admin.action.BaseBean#getPersistenceService()
     */
    @Override
    protected IPersistenceService<OneShotChargeTemplate> getPersistenceService() {
        return oneShotChargeTemplateService;
    }

	public String getDescriptionFr() {
		return descriptionFr;
	}

	public void setDescriptionFr(String descriptionFr) {
		this.descriptionFr = descriptionFr;
	}
    
    

}
