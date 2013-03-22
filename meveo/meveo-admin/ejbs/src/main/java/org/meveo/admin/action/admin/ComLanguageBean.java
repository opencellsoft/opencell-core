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
import org.meveo.admin.action.BaseBean;
import org.meveo.admin.util.pagination.PaginationDataModel;
import org.meveo.model.billing.Language; 
import org.meveo.model.billing.TradingLanguage;
import org.meveo.model.catalog.PriceCode;
import org.meveo.service.admin.local.ComLanguageServiceLocal;
import org.meveo.service.base.local.IPersistenceService;

/** 
 * @author MBAREK
 */
@Name("comLanguageBean")
@Scope(ScopeType.CONVERSATION)
public class ComLanguageBean extends BaseBean<TradingLanguage> {

    private static final long serialVersionUID = 1L;
  
    @In
    private ComLanguageServiceLocal comLanguageService;
 
    public ComLanguageBean() {
        super(TradingLanguage.class); 
    }
 

    @Begin(nested = true)
    @Factory("comLanguage")
    public TradingLanguage init() {
    	return initEntity();    
    }
 

    /**
     * Data model of entities for data table in GUI.
     * 
     * @return filtered entities.
     */
    @Out(value = "comLanguages", required = false)
    protected PaginationDataModel<TradingLanguage> getDataModel() {
        return entities;
    }
 
    
    @Factory("comLanguages")
    @Begin(join = true)
    public void list() {
        super.list();
    }

   
    @End(beforeRedirect = true, root=false)
    public String saveOrUpdate() {
        return saveOrUpdate(entity);
        
    }

    /**
     * Override default list view name. (By default its class name starting
     * lower case + 's').
     * 
     * @see org.meveo.admin.action.BaseBean#getDefaultViewName()
     */
    protected String getDefaultViewName() {
        return "comLanguages";
    }

    
	public void populateLanguages(Language language){
	      log.info("populatLanguages language", language!=null?language.getLanguageCode():null);
		  if(language!=null){
		      entity.setLanguage(language);
		      entity.setPrDescription(language.getDescriptionEn());
	     }
	}
    
    
    /**
     * @see org.meveo.admin.action.BaseBean#getPersistenceService()
     */
    @Override
    protected IPersistenceService<TradingLanguage> getPersistenceService() {
        return comLanguageService;
    }

    public void test() throws BatchUpdateException {
        throw new BatchUpdateException();
    }
 

    

    
    
    
}