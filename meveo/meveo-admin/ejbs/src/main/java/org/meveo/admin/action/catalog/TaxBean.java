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
import org.meveo.commons.utils.ParamBean;
import org.meveo.model.billing.CatMessages;
import org.meveo.model.billing.InvoiceCategory;
import org.meveo.model.billing.LanguageEnum;
import org.meveo.model.billing.Tax;
import org.meveo.service.base.PersistenceService;
import org.meveo.service.base.local.IPersistenceService;
import org.meveo.service.catalog.local.CatMessagesServiceLocal;
import org.meveo.service.catalog.local.TaxServiceLocal;

/**
 * Standard backing bean for {@link Tax} (extends {@link BaseBean} that provides
 * almost all common methods to handle entities filtering/sorting in datatable,
 * their create, edit, view, delete operations). It works with Manaty custom JSF
 * components.
 * 
 * @author Seb
 * @created Dec 20, 2010
 */
@Name("taxBean")
@Scope(ScopeType.CONVERSATION)
public class TaxBean extends BaseBean<Tax> {

    private static final long serialVersionUID = 1L;
    /**
     * Injected @{link Tax} service. Extends {@link PersistenceService}.
     */
    
    @In
    private CatMessagesServiceLocal catMessagesService;
     
     private String descriptionFr;
    
    
    @In
    private TaxServiceLocal taxService;

    private String[] accountingCodeFields = new String[7];
    private String separator;

    
    /**
     * Constructor. Invokes super constructor and provides class type of this
     * bean for {@link BaseBean}.
     */
    public TaxBean() {
        super(Tax.class);
        ParamBean param = ParamBean.getInstance("meveo-admin.properties");
        separator = param.getProperty("reporting.accountingCode.separator",",");
    }

    /**
     * Factory method for entity to edit. If objectId param set load that entity
     * from database, otherwise create new.
     * 
     * @throws IllegalAccessException
     * @throws InstantiationException
     */
    @Factory("tax")
    @Begin(nested = true)
    public Tax init() {
        Tax tax=initEntity();
        languageMessagesMap.clear();
        if(tax.getId()!=null){
        	for(CatMessages msg:catMessagesService.getCatMessagesList(Tax.class.getSimpleName()+"_"+tax.getId())){
            	languageMessagesMap.put(msg.getLanguageCode(), msg.getDescription());
            }
        }
        parseAccountingCode();
        return tax;    
    }
   
    

    /**
     * Data model of entities for data table in GUI.
     * 
     * @return filtered entities.
     */
    @Out(value = "taxes", required = false)
    protected PaginationDataModel<Tax> getDataModel() {
        return entities;
    }

    /**
     * Factory method, that is invoked if data model is empty. Invokes
     * BaseBean.list() method that handles all data model loading. Overriding is
     * needed only to put factory name on it.
     * 
     * @see org.meveo.admin.action.BaseBean#list()
     */
    @Factory("taxes")
    @Begin(join = true)
    public void list() {
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
    	String back=null;
    	if(entity.getId()!=null){	
    		for(String msgKey:languageMessagesMap.keySet()){
				String description=languageMessagesMap.get(msgKey);
				CatMessages catMsg=catMessagesService.getCatMessages(entity.getClass().getSimpleName()+"_"+entity.getId(),msgKey); 
				if(catMsg!=null){
					catMsg.setDescription(description);
            	    catMessagesService.update(catMsg);
				}else{
					CatMessages catMessages=new CatMessages(entity.getClass().getSimpleName()+"_"+entity.getId(),msgKey,description);  
                	catMessagesService.create(catMessages);	
				}
    		}
    	    back=saveOrUpdate(entity);
    	}else{
    		back=saveOrUpdate(entity);
    		for(String msgKey:languageMessagesMap.keySet()){
    			String description=languageMessagesMap.get(msgKey);
    			CatMessages catMessages=new CatMessages(entity.getClass().getSimpleName()+"_"+entity.getId(),msgKey,description);  
            	catMessagesService.create(catMessages);	
    		}	
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
        return "taxes";
    }

    /**
     * @see org.meveo.admin.action.BaseBean#getPersistenceService()
     */
    @Override
    protected IPersistenceService<Tax> getPersistenceService() {
        return taxService;
    }

    public void test() throws BatchUpdateException {
        throw new BatchUpdateException();
    }


    /**
     * Constructs cost accounting code
     */
    public String generateAccountingCode() {
        return accountingCodeFields[0] + separator + accountingCodeFields[1] + separator + accountingCodeFields[2]
           + separator + accountingCodeFields[3] + separator + accountingCodeFields[4]
           + separator + accountingCodeFields[5] + separator + accountingCodeFields[6];
    }

    /**
     * Parses cost accounting code
     * 
     */
    public void parseAccountingCode() {
        if (entity.getAccountingCode() != null) {
            String[] accountingCodeValues = entity.getAccountingCode().split(separator);
            if(accountingCodeValues !=null){
            	for(int i=0;i<accountingCodeFields.length;i++){
            	  if(i<accountingCodeValues.length){
            		  accountingCodeFields[i] = accountingCodeValues[i];
            	  }
            	}
            }
       }
    }

    

    public String getAccountingCodeField1() {
        return accountingCodeFields[0];
    }

    public void setAccountingCodeField1(String accountingCodeField1) {
        this.accountingCodeFields[0] = accountingCodeField1;
    }

    public String getAccountingCodeField2() {
        return accountingCodeFields[1];
    }

    public void setAccountingCodeField2(String accountingCodeField2) {
        this.accountingCodeFields[1] = accountingCodeField2;
    }

    public String getAccountingCodeField3() {
        return accountingCodeFields[2];
    }

    public void setAccountingCodeField3(String accountingCodeField3) {
        this.accountingCodeFields[2] = accountingCodeField3;
    }

    public String getAccountingCodeField4() {
        return accountingCodeFields[3];
    }

    public void setAccountingCodeField4(String accountingCodeField4) {
        this.accountingCodeFields[3] = accountingCodeField4;
    }

    public String getAccountingCodeField5() {
        return accountingCodeFields[4];
    }

    public void setAccountingCodeField5(String accountingCodeField5) {
        this.accountingCodeFields[4] = accountingCodeField5;
    }

    public String getAccountingCodeField6() {
        return accountingCodeFields[5];
    }

    public void setAccountingCodeField6(String accountingCodeField6) {
        this.accountingCodeFields[5] = accountingCodeField6;
    }

    public String getAccountingCodeField7() {
        return accountingCodeFields[6];
    }

    public void setAccountingCodeField7(String accountingCodeField7) {
        this.accountingCodeFields[6] = accountingCodeField7;
    }

	public String getDescriptionFr() {
		return descriptionFr;
	}

	public void setDescriptionFr(String descriptionFr) {
		this.descriptionFr = descriptionFr;
	}
    
    
}