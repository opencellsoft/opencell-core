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

import javax.faces.context.FacesContext;

import org.jboss.seam.ScopeType;
import org.jboss.seam.annotations.Begin;
import org.jboss.seam.annotations.End;
import org.jboss.seam.annotations.Factory;
import org.jboss.seam.annotations.In;
import org.jboss.seam.annotations.Name;
import org.jboss.seam.annotations.Out;
import org.jboss.seam.annotations.Scope;
import org.jboss.seam.faces.Redirect;
import org.meveo.admin.action.BaseBean;
import org.meveo.admin.util.pagination.PaginationDataModel;
import org.meveo.model.crm.Customer;
import org.meveo.service.base.PersistenceService;
import org.meveo.service.base.local.IPersistenceService;
import org.meveo.service.crm.local.CustomerServiceLocal;

/**
 * Standard backing bean for {@link Customer} (extends {@link BaseBean} that
 * provides almost all common methods to handle entities filtering/sorting in
 * datatable, their create, edit, view, delete operations). It works with Manaty
 * custom JSF components.
 * 
 * @author Gediminas Ubartas
 * @created 2010.11.15
 */
@Name("customerBean")
@Scope(ScopeType.CONVERSATION)
public class CustomerBean extends BaseBean<Customer> {

    private static final long serialVersionUID = 1L;

    /** Injected @{link Customer} service. Extends {@link PersistenceService}. */
    @In
    private CustomerServiceLocal customerService;
    
    /**
     * Constructor. Invokes super constructor and provides class type of this
     * bean for {@link BaseBean}.
     */
    public CustomerBean() {
        super(Customer.class);
    }

    /**
     * Factory method for entity to edit. If objectId param set load that entity
     * from database, otherwise create new.
     * 
     * @throws IllegalAccessException
     * @throws InstantiationException
     */
    @Factory("customer")
    @Begin(nested=true)
    public Customer init() {
        return initEntity();
    }

    /**
     * Data model of entities for data table in GUI.
     * 
     * @return filtered entities.
     */
    @Out(value = "customers", required = false)
    protected PaginationDataModel<Customer> getDataModel() {
        return entities;
    }

    /**
     * Invokes BaseBean.list() method that handles all data model loading.
     * 
     * @see org.meveo.admin.action.BaseBean#list()
     */
    @Begin(join = true)
    @Factory("customerList")
    public void list() {
        super.list();
    }

    /**
     * Factory method, that is invoked if data model is empty. In this window we
     * don't need to display data table when page is loaded. Only when Search
     * button is pressed we should show the list. In this method we check if
     * list isn't called for sorting and pagination.
     * 
     * @see org.meveo.admin.action.BaseBean#list()
     */
    @Begin(join = true)
    @Factory("customers")
    public void noList() {
        final FacesContext context = FacesContext.getCurrentInstance();
        final String sortField = context.getExternalContext().getRequestParameterMap().get("sortField");
        final String resultsForm = context.getExternalContext().getRequestParameterMap().get("results_form");

        if ((sortField != null) || (resultsForm != null)) {
            this.list();
        }
    }

    /**
     * Conversation is ended and user is redirected from edit to his previous
     * window.
     * 
     * @see org.meveo.admin.action.BaseBean#saveOrUpdate(org.meveo.model.IEntity)
     */
    @End(beforeRedirect = true, root=false)
    public String saveOrUpdate() {
        saveOrUpdate(entity);
        Redirect.instance().setParameter("edit", "false");
        Redirect.instance().setParameter("objectId", entity.getId());
        Redirect.instance().setViewId("/pages/crm/customers/customerDetail.xhtml");
        Redirect.instance().execute();
        return null;

    }

    /**
     * @see org.meveo.admin.action.BaseBean#getPersistenceService()
     */
    @Override
    protected IPersistenceService<Customer> getPersistenceService() {
        return customerService;
    }
}