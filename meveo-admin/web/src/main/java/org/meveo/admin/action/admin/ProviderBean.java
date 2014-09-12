/*
 * (C) Copyright 2009-2014 Manaty SARL (http://manaty.net/) and contributors.
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

import javax.enterprise.context.ConversationScoped;
import javax.inject.Inject;
import javax.inject.Named;

import org.meveo.admin.action.BaseBean;
import org.meveo.model.billing.Language;
import org.meveo.model.crm.Provider;
import org.meveo.service.base.local.IPersistenceService;
import org.meveo.service.crm.impl.ProviderService;
import org.primefaces.event.SelectEvent;

@Named
@ConversationScoped
public class ProviderBean extends BaseBean<Provider> {

	private static final long serialVersionUID = 1L;
	
	@Inject
	private ProviderService providerService;


	public ProviderBean() {
		super(Provider.class);
	}

	/**
     * Factory method for entity to edit. If objectId param set load that entity from database, otherwise create new.
     * 
     * @throws IllegalAccessException
     * @throws InstantiationException
     */
   
    public Provider initEntity() {
        return super.initEntity();
    }

    /**
     * Override default list view name. (By default its class name starting lower case + 's').
     * 
     * @see org.meveo.admin.action.BaseBean#getDefaultViewName()
     */
    protected String getDefaultViewName() {
        return "sellers";
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
			log.info("populatLanguages language", language != null ? language.getLanguageCode()
					: null);
			if (language != null) {
				entity.setLanguage(language);
			}
		}

	}

}
