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

import javax.faces.bean.ViewScoped;
import javax.inject.Inject;
import javax.inject.Named;

import org.meveo.admin.action.BaseBean;
import org.meveo.model.billing.BillingAccount;
import org.meveo.model.billing.Invoice;
import org.meveo.service.base.PersistenceService;
import org.meveo.service.base.local.IPersistenceService;
import org.meveo.service.billing.impl.BillingAccountService;
import org.meveo.service.billing.impl.InvoiceService;
import org.primefaces.model.LazyDataModel;

/**
 * Standard backing bean for {@link Invoice} (extends {@link BaseBean} that provides almost all common methods to handle entities filtering/sorting in datatable, their create,
 * edit, view, delete operations). It works with Manaty custom JSF components.
 * 
 * @author Ignas Lelys
 * @created Dec 7, 2010
 * 
 */
@Named
@ViewScoped
public class InvoiceBean extends BaseBean<Invoice> {

    private static final long serialVersionUID = 1L;

    /**
     * Injected
     * 
     * @{link Invoice} service. Extends {@link PersistenceService}.
     */
    @Inject
    private InvoiceService invoiceService;

    @Inject
    BillingAccountService billingAccountService;

    /**
     * Constructor. Invokes super constructor and provides class type of this bean for {@link BaseBean}.
     */
    public InvoiceBean() {
        super(Invoice.class);
    }

    /**
     * Method, that is invoked in billing account screen. This method returns invoices associated with current Billing Account.
     * 
     */
    public LazyDataModel<Invoice> getBillingAccountInvoices(BillingAccount ba) {
        getFilters();
        if (ba.getCode() == null) {
            log.warn("No billingAccount code");
        } else {
            filters.put("billingAccount", ba);
        }

        return getLazyDataModel();
    }

    /**
     * @see org.meveo.admin.action.BaseBean#getPersistenceService()
     */
    @Override
    protected IPersistenceService<Invoice> getPersistenceService() {
        return invoiceService;
    }

}
