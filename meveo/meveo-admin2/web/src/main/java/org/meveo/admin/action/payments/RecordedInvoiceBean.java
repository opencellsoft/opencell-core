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
package org.meveo.admin.action.payments;

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
import org.meveo.model.admin.User;
import org.meveo.model.payments.RecordedInvoice;
import org.meveo.service.base.PersistenceService;
import org.meveo.service.base.local.IPersistenceService;
import org.meveo.service.payments.local.RecordedInvoiceServiceLocal;

/**
 * Standard backing bean for {@link RecordedInvoice} (extends {@link BaseBean}
 * that provides almost all common methods to handle entities filtering/sorting
 * in datatable, their create, edit, view, delete operations). It works with
 * Manaty custom JSF components.
 * 
 * @author Ignas
 * @created 2009.10.13
 */
@Name("recordedInvoiceBean")
@Scope(ScopeType.CONVERSATION)
public class RecordedInvoiceBean extends BaseBean<RecordedInvoice> {

    private static final long serialVersionUID = 1L;

    /**
     * Injected @{link RecordedInvoice} service. Extends
     * {@link PersistenceService}.
     */
    @In
    private RecordedInvoiceServiceLocal recordedInvoiceService;

    @In
    private User currentUser;

    /**
     * Constructor. Invokes super constructor and provides class type of this
     * bean for {@link BaseBean}.
     */
    public RecordedInvoiceBean() {
        super(RecordedInvoice.class);
    }

    /**
     * Factory method for entity to edit. If objectId param set load that entity
     * from database, otherwise create new.
     * 
     * @throws IllegalAccessException
     * @throws InstantiationException
     */
    @Factory("recordedInvoice")
    @Begin(nested = true)
    public RecordedInvoice init() {
        return initEntity();
    }

    /**
     * Data model of entities for data table in GUI.
     * 
     * @return filtered entities.
     */
    @Out(value = "recordedInvoices", required = false)
    protected PaginationDataModel<RecordedInvoice> getDataModel() {
        return entities;
    }

    /**
     * Factory method, that is invoked if data model is empty. Invokes
     * BaseBean.list() method that handles all data model loading. Overriding is
     * needed only to put factory name on it.
     * 
     * @see org.meveo.admin.action.BaseBean#list()
     */
    @Override
    @Begin(join = true)
    @Factory("recordedInvoices")
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
        entity.getCustomerAccount().getAccountOperations().add(entity);
        return saveOrUpdate(entity);
    }

    /**
     * @see org.meveo.admin.action.BaseBean#getPersistenceService()
     */
    @Override
    protected IPersistenceService<RecordedInvoice> getPersistenceService() {
        return recordedInvoiceService;
    }

    public String addLitigation() {
        try {
            recordedInvoiceService.addLitigation(entity, currentUser);
            statusMessages.addFromResourceBundle("customerAccount.addLitigationSuccessful");
        } catch (Exception e) {
            e.printStackTrace();
            statusMessages.add(e.getMessage());
        }
        return null;
    }

    public String cancelLitigation() {

        try {
            recordedInvoiceService.cancelLitigation(entity, currentUser);
            statusMessages.addFromResourceBundle("customerAccount.cancelLitigationSuccessful");
        } catch (Exception e) {
            e.printStackTrace();
            statusMessages.add(e.getMessage());
        }
        return null;
    }

}
