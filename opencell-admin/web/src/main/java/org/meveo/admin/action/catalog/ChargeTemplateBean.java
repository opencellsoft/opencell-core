/*
 * (C) Copyright 2015-2016 Opencell SAS (http://opencellsoft.com/) and contributors.
 * (C) Copyright 2009-2014 Manaty SARL (http://manaty.net/) and contributors.
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Affero General Public License as
 * published by the Free Software Foundation, either version 3 of the
 * License, or (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  
 * This program is not suitable for any direct or indirect application in MILITARY industry
 * See the GNU Affero General Public License for more details.
 *
 * You should have received a copy of the GNU Affero General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package org.meveo.admin.action.catalog;

import java.util.ArrayList;
import java.util.List;

import javax.enterprise.inject.Produces;
import javax.faces.view.ViewScoped;
import javax.inject.Inject;
import javax.inject.Named;

import org.meveo.admin.action.BaseBean;
import org.meveo.model.billing.ChargeInstance;
import org.meveo.model.catalog.ChargeTemplate;
import org.meveo.model.catalog.OneShotChargeTemplate;
import org.meveo.model.catalog.ProductChargeTemplate;
import org.meveo.model.catalog.RecurringChargeTemplate;
import org.meveo.model.catalog.UsageChargeTemplate;
import org.meveo.service.base.PersistenceService;
import org.meveo.service.base.local.IPersistenceService;
import org.meveo.service.catalog.impl.ChargeTemplateServiceAll;
import org.primefaces.model.LazyDataModel;

/**
 * Standard backing bean for {@link ChargeInstance} (extends {@link BaseBean}
 * that provides almost all common methods to handle entities filtering/sorting
 * in datatable, their create, edit, view, delete operations). It works with
 * Manaty custom JSF components.
 */
@Named
@ViewScoped
public class ChargeTemplateBean extends BaseBean<ChargeTemplate> {
	private static final long serialVersionUID = 1L;

	/**
	 * Injected @{link OneShotChargeTemplate} service. Extends
	 * {@link PersistenceService}.
	 */
	@Inject
	private ChargeTemplateServiceAll chargeTemplateService;
	
	/**
	 * Constructor. Invokes super constructor and provides class type of this
	 * bean for {@link BaseBean}.
	 */
	public ChargeTemplateBean() {
		super(ChargeTemplate.class);
	}

	/**
	 * Factory method for entity to edit. If objectId param set load that entity
	 * from database, otherwise create new.
	 * @return charge template
	 */
	@Produces
	@Named("chargeTemplate")
	public ChargeTemplate init() {
		return initEntity();
	}

	/**
	 * @see org.meveo.admin.action.BaseBean#getPersistenceService()
	 */
	@Override
	protected IPersistenceService<ChargeTemplate> getPersistenceService() {
		return chargeTemplateService;
	}
	

	public LazyDataModel<ChargeTemplate> getChargeTemplates() {
		StringBuilder chargeList = new StringBuilder();
		List<Long> chargeTemplatesIds=new ArrayList<Long>();
		String sep=""; 
		for(ChargeTemplate charge :chargeTemplateService.list()){
			if(!(charge instanceof ProductChargeTemplate)){
				chargeTemplatesIds.add(charge.getId());
			}
		}
		if(chargeTemplatesIds.size()>0){
			for(Long ids:chargeTemplatesIds){
				chargeList.append(sep);
				chargeList.append(ids.toString());
				sep=",";
			} 
			filters.put("inList id", chargeList);
		}
		return getLazyDataModel();
	}
		
	public String getChargeTemplateDetail(ChargeTemplate chargeTemplate) {
		if(chargeTemplate!=null){
		if(chargeTemplate instanceof RecurringChargeTemplate){
			return "/pages/catalog/recurringChargeTemplates/recurringChargeTemplateDetail.jsf?objectId="+chargeTemplate.getId()+"&cid="
					+ conversation.getId() + "&edit=true&faces-redirect=true&includeViewParams=true";
		}else if(chargeTemplate instanceof UsageChargeTemplate){
			return "/pages/catalog/usageChargeTemplates/usageChargeTemplateDetail.jsf?objectId="+chargeTemplate.getId()+ "&cid="
					+ conversation.getId() + "&edit=true&faces-redirect=true&includeViewParams=true";
		}else if (chargeTemplate instanceof OneShotChargeTemplate){
			return "/pages/catalog/oneShotChargeTemplates/oneShotChargeTemplateDetail.jsf?objectId="+chargeTemplate.getId()+ "&cid="
					+ conversation.getId() + "&edit=true&faces-redirect=true&includeViewParams=true";
		} 
		}
		return null;
	} 
	
	

	
}
