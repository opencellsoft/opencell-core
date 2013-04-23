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
package org.meveo.admin.action.billing;

import java.util.Arrays;
import java.util.List;

import javax.enterprise.context.ConversationScoped;
import javax.inject.Inject;
import javax.inject.Named;

import org.jboss.seam.international.status.builder.BundleKey;
import org.meveo.admin.action.BaseBean;
import org.meveo.model.billing.Country;
import org.meveo.model.billing.Language;
import org.meveo.model.billing.TradingCountry;
import org.meveo.model.billing.TradingLanguage;
import org.meveo.model.crm.Provider;
import org.meveo.service.base.PersistenceService;
import org.meveo.service.base.local.IPersistenceService;
import org.meveo.service.billing.impl.TradingCountryService;
import org.meveo.service.crm.impl.ProviderService;
import org.primefaces.event.SelectEvent;

/**
 * Standard backing bean for {@link TradingCountry} (extends {@link BaseBean}
 * that provides almost all common methods to handle entities filtering/sorting
 * in datatable, their create, edit, view, delete operations). It works with
 * Manaty custom JSF components.
 * 
 * @author Marouane ALAMI
 * @created 25-03-2013
 * 
 */
@Named
@ConversationScoped
public class TradingCountryBean extends BaseBean<TradingCountry> {

	private static final long serialVersionUID = 1L;

	/**
	 * Injected @{link TradingCountry} service. Extends
	 * {@link PersistenceService} .
	 */
	@Inject
	private TradingCountryService tradingCountryService;
	
	
	 @Inject
	    private ProviderService providerService;

	/**
	 * Constructor. Invokes super constructor and provides class type of this
	 * bean for {@link BaseBean}.
	 */
	public TradingCountryBean() {
		super(TradingCountry.class);
	}

	/**
	 * Factory method for entity to edit. If objectId param set load that entity
	 * from database, otherwise create new.
	 * 
	 * @throws IllegalAccessException
	 * @throws InstantiationException
	 */
	public TradingCountry initEntity() {
		return super.initEntity();
	}


    public void onRowSelect(SelectEvent event){  
    	if(event.getObject() instanceof Country){
    		Country country = (Country)event.getObject();  
            log.info("populatCountries country", country != null ? country.getCountryCode() : null);
    		if (country != null) {
    			entity.setCountry(country);
    			entity.setPrDescription(country.getDescriptionEn());
    		}
    	}
    	
    } 

	/**
	 * Factory method, that is invoked if data model is empty. Invokes
	 * BaseBean.list() method that handles all data model loading. Overriding is
	 * needed only to put factory name on it.
	 * 
	 * @return
	 * 
	 * @see org.meveo.admin.action.BaseBean#list()
	 */
	@Override
	public List<TradingCountry> listAll() {
		getFilters();
		if (filters.containsKey("countryCode")) {
			filters.put("country.countryCode", filters.get("countryCode"));
			filters.remove("countryCode");
		} else if (filters.containsKey("country.countryCode")) {
			filters.remove("country.countryCode");
		}
		return super.listAll();
	}

	
	 @Override
	    public String saveOrUpdate(boolean killConversation) {
	        String back = null;
	        try {
	        	providerService.refresh(currentProvider);
	            for (TradingCountry tr : currentProvider.getTradingCountries()) {
	                if (tr.getCountry().getCountryCode().equalsIgnoreCase(entity.getCountry().getCountryCode()) && !tr.getId().equals(entity.getId())) {
	                    throw new Exception();
	                }
	            }
	            back = super.saveOrUpdate(killConversation);

	        } catch (Exception e) {
	            messages.error(new BundleKey("messages", "tradingCountry.uniqueField"));
	        }

	        return back;
	
	 }
	
	/**
	 * @see org.meveo.admin.action.BaseBean#getPersistenceService()
	 */
	@Override
	protected IPersistenceService<TradingCountry> getPersistenceService() {
		return tradingCountryService;
	}

	@Override
	protected List<String> getFormFieldsToFetch() {
		return Arrays.asList("country");
	}

	@Override
	protected List<String> getListFieldsToFetch() {
		return Arrays.asList("country");
	}
	
	@Override
	protected String getListViewName() {
		return "tradingCountries";
	}

	@Override
	public String getNewViewName() {
		return "tradingCountryDetail";
	}
}
