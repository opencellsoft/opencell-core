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
package org.meveo.admin.action.crm;

import java.util.Arrays;
import java.util.List;

import javax.enterprise.context.ConversationScoped;
import javax.inject.Inject;
import javax.inject.Named;

import org.meveo.admin.action.BaseBean;
import org.meveo.model.crm.Provider;
import org.meveo.service.base.PersistenceService;
import org.meveo.service.base.local.IPersistenceService;
import org.meveo.service.crm.impl.ProviderService;
import org.primefaces.component.datatable.DataTable;

/**
 * Standard backing bean for {@link Provider} (extends {@link BaseBean} that provides almost all common methods to handle entities filtering/sorting in datatable, their create,
 * edit, view, delete operations). It works with Manaty custom JSF components.
 * 
 * @author Gediminas Ubartas
 * @created 2011-02-28
 * 
 */
@Named
@ConversationScoped
public class ProviderBean extends BaseBean<Provider> {

    private static final long serialVersionUID = 1L;

    /**
     * Injected @{link Provider} service. Extends {@link PersistenceService}.
     */
    @Inject
    private ProviderService providerService;

    /**
     * Constructor. Invokes super constructor and provides class type of this bean for {@link BaseBean}.
     */
    public ProviderBean() {
        super(Provider.class);
    }

    @Override
    public DataTable search() {
        getFilters();
        filters.put("list-users", getCurrentUser());
        return super.search();
    }

    /**
     * @see org.meveo.admin.action.BaseBean#getPersistenceService()
     */
    @Override
    protected IPersistenceService<Provider> getPersistenceService() {
        return providerService;
    }

    /**
     * @see org.meveo.admin.action.BaseBean#getFormFieldsToFetch()
     */
    protected List<String> getFormFieldsToFetch() {
        return Arrays.asList("users");
    }

    /**
     * @see org.meveo.admin.action.BaseBean#getListFieldsToFetch()
     */
    protected List<String> getListFieldsToFetch() {
        return Arrays.asList("users");
    }

    /**
     * if current provider is not null continue action, else require to select provider in login screen
     */
    public String checkCurrentProvider() {
        if (getCurrentUser() == null || getCurrentProvider() == null) {
            return "/home.xhtml";
        }
        return "";
    }
}
