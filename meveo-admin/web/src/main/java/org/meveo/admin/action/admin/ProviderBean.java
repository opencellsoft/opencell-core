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
package org.meveo.admin.action.admin;

import javax.inject.Inject;
import javax.inject.Named;

import org.meveo.admin.action.BaseBean;
import org.meveo.admin.exception.BusinessException;
import org.meveo.model.billing.BillingRun;
import org.meveo.model.billing.InvoiceConfiguration;
import org.meveo.model.billing.Language;
import org.meveo.model.crm.Provider;
import org.meveo.service.base.local.IPersistenceService;
import org.meveo.service.billing.impl.InvoiceConfigurationService;
import org.meveo.service.crm.impl.ProviderService;
import org.omnifaces.cdi.ViewScoped;
import org.primefaces.event.SelectEvent;

@Named
@ViewScoped
public class ProviderBean extends BaseBean<Provider> {

	private static final long serialVersionUID = 1L;

	@Inject
	private ProviderService providerService;
	
	 @Inject
	 private InvoiceConfigurationService invoiceConfigurationService;

	public ProviderBean() {
		super(Provider.class);
	}

	/**
	 * @see org.meveo.admin.action.BaseBean#getPersistenceService()
	 */
	@Override
	protected IPersistenceService<Provider> getPersistenceService() {
		return providerService;
	}

	@Override
	protected String getListViewName() {
		return "providers";
	}

	@Override
	protected String getDefaultSort() {
		return "code";
	}

	public void onRowSelect(SelectEvent event) {
		if (event.getObject() instanceof Language) {
			Language language = (Language) event.getObject();
			log.info("populatLanguages language",
					language != null ? language.getLanguageCode() : null);
			if (language != null) {
				entity.setLanguage(language);
			}
		}

	}
	
    @Override
	public Provider initEntity() {
		 super.initEntity();
	   try{
		if(entity.getId()!=null && entity.getInvoiceConfiguration()==null){ 
		   InvoiceConfiguration invoiceConfiguration =new InvoiceConfiguration();
			invoiceConfiguration.setCode(entity.getCode()); 
			invoiceConfiguration.setDescription(entity.getDescription());   
	   		invoiceConfiguration.setProvider(entity);
	   		invoiceConfigurationService.create(invoiceConfiguration);
	   	    entity.setInvoiceConfiguration(invoiceConfiguration);
	   	    log.info("created invoiceConfiguration id={} for provider {}", invoiceConfiguration.getId(), entity.getCode());
			}
		}catch(BusinessException e){
		  log.error("error while saving invoiceConfiguration "+e);	
		 }
		return entity;
	}
	
	@Override
	public String saveOrUpdate(boolean killConversation)
			throws BusinessException { 
		if (entity.isTransient()) {  
			InvoiceConfiguration invoiceConfiguration =new InvoiceConfiguration();
			invoiceConfiguration.setCode(entity.getCode()); 
			invoiceConfiguration.setDescription(entity.getDescription());   
	   		invoiceConfiguration.setProvider(entity);
	   		invoiceConfiguration.setDisplayOffers(entity.getInvoiceConfiguration().getDisplayOffers());
	   		invoiceConfiguration.setDisplayServices(entity.getInvoiceConfiguration().getDisplayServices());
	   		invoiceConfiguration.setDisplaySubscriptions(entity.getInvoiceConfiguration().getDisplaySubscriptions());
	   		invoiceConfigurationService.create(invoiceConfiguration);
			entity.setInvoiceConfiguration(invoiceConfiguration);
		   	log.info("created invoiceConfiguration id={} for provider {}", invoiceConfiguration.getId(), entity.getCode());
		  } 
		return super.saveOrUpdate(killConversation);  
	}

}
