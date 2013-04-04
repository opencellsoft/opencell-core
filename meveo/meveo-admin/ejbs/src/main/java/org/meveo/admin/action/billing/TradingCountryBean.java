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
import org.meveo.model.billing.Country;
import org.meveo.model.billing.Language;
import org.meveo.model.billing.TradingCountry;
import org.meveo.service.base.PersistenceService;
import org.meveo.service.base.local.IPersistenceService;
import org.meveo.service.billing.local.TradingCountryServiceLocal;

/**
 * Standard backing bean for {@link TradingCountry} (extends {@link BaseBean} that
 * provides almost all common methods to handle entities filtering/sorting in
 * datatable, their create, edit, view, delete operations). It works with Manaty
 * custom JSF components.
 * 
 * @author Marouane ALAMI
 * @created 25-03-2013
 * 
 */
@Name("tradingCountryBean")
@Scope(ScopeType.CONVERSATION)
public class TradingCountryBean extends BaseBean<TradingCountry> {

    private static final long serialVersionUID = 1L;

    /**
     * Injected @{link TradingCountry} service. Extends {@link PersistenceService}
     * .
     */
    @In
    private TradingCountryServiceLocal tradingCountryService;

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
    @Begin(nested = true)
    @Factory("tradingCountry")
    public TradingCountry init() {
        return initEntity();
    }

    /**
     * Data model of entities for data table in GUI.
     * 
     * @return filtered entities.
     */
    @Out(value = "tradingCountries", required = false)
    protected PaginationDataModel<TradingCountry> getDataModel() {
        return entities;
    }
    
    public void populateCountries(Country country){
	      log.info("populatCountries country", country!=null?country.getCountryCode():null);
		  if(country!=null){
		      entity.setCountry(country);
		      entity.setPrDescription(country.getDescriptionEn());
	     }
	}

    /**
     * Factory method, that is invoked if data model is empty. Invokes
     * BaseBean.list() method that handles all data model loading. Overriding is
     * needed only to put factory name on it.
     * 
     * @see org.meveo.admin.action.BaseBean#list()
     */
    @Begin(join = true)
    @Factory("tradingCountries")
    public void list() {
    	if(filters.containsKey("countryCode")){
    		filters.put("country.countryCode", filters.get("countryCode"));
    		filters.remove("countryCode");
    	}else if (filters.containsKey("country.countryCode")){
    		filters.remove("country.countryCode");
    	}
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
        return saveOrUpdate(entity);
    }

    /**
     * @see org.meveo.admin.action.BaseBean#getPersistenceService()
     */
    @Override
    protected IPersistenceService<TradingCountry> getPersistenceService() {
        return tradingCountryService;
    }
}
