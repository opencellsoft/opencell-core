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
import org.meveo.model.catalog.ServiceUsageChargeTemplate;
import org.meveo.service.base.local.IPersistenceService;
import org.meveo.service.catalog.impl.ServiceUsageChargeTemplateService;

/** 
 * MBAREK
 */
@Named
@ConversationScoped
public class ServiceUsageChargeTemplateBean extends BaseBean<ServiceUsageChargeTemplate> {

    private static final long serialVersionUID = 1L;

   
    @Inject
    private ServiceUsageChargeTemplateService serviceUsageChargeTemplateService;

    /**
     * Constructor. Invokes super constructor and provides class type of this bean for {@link BaseBean}.
     */
    public ServiceUsageChargeTemplateBean() {
        super(ServiceUsageChargeTemplate.class);
    }

    /**
     * Factory method for entity to edit. If objectId param set load that entity from database, otherwise create new.
     * 
     * @throws IllegalAccessException
     * @throws InstantiationException
     */
   
    public ServiceUsageChargeTemplate initEntity() {
        return super.initEntity();
    }

    /**
     * Override default list view name. (By default its class name starting lower case + 's').
     * 
     * @see org.meveo.admin.action.BaseBean#getDefaultViewName()
     */
    protected String getDefaultViewName() {
        return "serviceUsageChargeTemplates";
    }

    /**
     * @see org.meveo.admin.action.BaseBean#getPersistenceService()
     */
    @Override
    protected IPersistenceService<ServiceUsageChargeTemplate> getPersistenceService() {
        return serviceUsageChargeTemplateService;
    }
    
    @Override
    protected String getListViewName() {
    	 return "serviceUsageChargeTemplates";
    }

}