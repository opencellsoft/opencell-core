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

import javax.enterprise.context.ConversationScoped;
import javax.enterprise.inject.Produces;
import javax.inject.Inject;
import javax.inject.Named;

import org.meveo.admin.action.BaseBean;
import org.meveo.model.admin.BayadDunningInputHistory;
import org.meveo.service.admin.impl.BayadDunningInputHistoryService;
import org.meveo.service.base.PersistenceService;
import org.meveo.service.base.local.IPersistenceService;

/**
 * Standard backing bean for {@link BayadDunningInputHistory} (extends {@link BaseBean} that provides almost all common methods to handle entities filtering/sorting in datatable,
 * their create, edit, view, delete operations). It works with Manaty custom JSF components.
 * 
 * @author Ignas
 * @created Apr 13, 2011
 */
@Named
@ConversationScoped
public class BayadDunningInputHistoryBean extends BaseBean<BayadDunningInputHistory> {

    private static final long serialVersionUID = 1L;

    /**
     * Injected @{link BayadDunningInputHistory} service. Extends {@link PersistenceService}.
     */
    @Inject
    private BayadDunningInputHistoryService bayadDunningInputHistoryService;

    /**
     * Constructor. Invokes super constructor and provides class type of this bean for {@link BaseBean}.
     */
    public BayadDunningInputHistoryBean() {
        super(BayadDunningInputHistory.class);
    }

    /**
     * Factory method for entity to edit. If objectId param set load that entity from database, otherwise create new.
     * 
     * @throws IllegalAccessException
     * @throws InstantiationException
     */
    @Produces
    @Named("bayadDunningInputHistory")
    public BayadDunningInputHistory init() {
        return super.initEntity();
    }

    /**
     * Factory method, that is invoked if data model is empty. Invokes BaseBean.list() method that handles all data model loading. Overriding is needed only to put factory name on
     * it.
     * 
     * @see org.meveo.admin.action.BaseBean#list()
     */
    @Produces
    @Named("bayadDunningInputs")
    @ConversationScoped
    public void list() {
        super.list();
    }

    /**
     * Override default list view name. (By default view name is class name starting lower case + ending 's').
     * 
     * @see org.meveo.admin.action.BaseBean#getDefaultViewName()
     */
    protected String getDefaultViewName() {
        return "bayadDunningInputs";
    }

    /**
     * @see org.meveo.admin.action.BaseBean#getPersistenceService()
     */
    @Override
    protected IPersistenceService<BayadDunningInputHistory> getPersistenceService() {
        return bayadDunningInputHistoryService;
    }
}