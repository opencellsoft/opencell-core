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
package org.meveo.admin.action.admin;

import java.sql.BatchUpdateException;

import org.jboss.seam.ScopeType;
import org.jboss.seam.annotations.Begin;
import org.jboss.seam.annotations.End;
import org.jboss.seam.annotations.Factory;
import org.jboss.seam.annotations.In;
import org.jboss.seam.annotations.Name;
import org.jboss.seam.annotations.Out;
import org.jboss.seam.annotations.Scope;
import org.jboss.seam.international.StatusMessage.Severity;
import org.meveo.admin.action.BaseBean;
import org.meveo.admin.util.pagination.PaginationDataModel;
import org.meveo.model.admin.Currency;
import org.meveo.model.billing.TradingCurrency;
import org.meveo.model.billing.TradingLanguage;
import org.meveo.service.admin.local.TradingCurrencyServiceLocal;
import org.meveo.service.base.local.IPersistenceService;
import org.meveo.service.crm.local.ProviderServiceLocal;

/** 
 * @author Marouane ALAMI
 */
@Name("tradingCurrencyBean")
@Scope(ScopeType.CONVERSATION)
public class TradingCurrencyBean extends BaseBean<TradingCurrency> {

    private static final long serialVersionUID = 1L;
  
    @In
    private TradingCurrencyServiceLocal tradingCurrencyService;
    
    @In
    ProviderServiceLocal providerService;
 
    public TradingCurrencyBean() {
        super(TradingCurrency.class); 
    }
 

    @Begin(nested = true)
    @Factory("tradingCurrency")
    public TradingCurrency init() {
    	return initEntity();    
    }
 

    /**
     * Data model of entities for data table in GUI.
     * 
     * @return filtered entities.
     */
    @Out(value = "tradingCurrencies", required = false)
    protected PaginationDataModel<TradingCurrency> getDataModel() {
        return entities;
    }
 
    
    @Factory("tradingCurrencies")
    @Begin(join = true)
    public void list() {
    	setSortField("currency.currencyCode");
    	setSortOrder("DESC");
        super.list();
    }

   
    @End(beforeRedirect = true, root=false)
    public String saveOrUpdate() {
    	String back=null;
    	try {
    		currentProvider=providerService.findById(currentProvider.getId());
    		for(TradingLanguage tr : currentProvider.getTradingLanguage()){
        		if(tr.getLanguage().getLanguageCode().equalsIgnoreCase(entity.getCurrency().getCurrencyCode())
        				&& !tr.getId().equals(entity.getId())){
        			throw new Exception("cette devise existe déjà pour ce provider");
        		}
    		}
		    back=saveOrUpdate(entity); 
			
		} catch (Exception e) {
			statusMessages.addFromResourceBundle(Severity.ERROR, e.getMessage());
		}
        return back;
        
    }

    /**
     * Override default list view name. (By default its class name starting
     * lower case + 's').
     * 
     * @see org.meveo.admin.action.BaseBean#getDefaultViewName()
     */
    protected String getDefaultViewName() {
        return "tradingCurrencies";
    }

    
	public void populateCurrencies(Currency currency){
	      log.info("populatCurrencies currency", currency!=null?currency.getCurrencyCode():null);
		  if(currency!=null){
		      entity.setCurrency(currency);
		      entity.setPrDescription(currency.getDescriptionEn());
	     }
	}
    
    
    /**
     * @see org.meveo.admin.action.BaseBean#getPersistenceService()
     */
    @Override
    protected IPersistenceService<TradingCurrency> getPersistenceService() {
        return tradingCurrencyService;
    }

    public void test() throws BatchUpdateException {
        throw new BatchUpdateException();
    }
 

    

    
    
    
}