/*
 * (C) Copyright 2015-2020 Opencell SAS (https://opencellsoft.com/) and contributors.
 *
 * This program is free software: you can redistribute it and/or modify it under the terms of the GNU Affero General
 * Public License as published by the Free Software Foundation, either version 3 of the License, or (at your option)
 * any later version.
 *
 * THERE IS NO WARRANTY FOR THE PROGRAM, TO THE EXTENT PERMITTED BY APPLICABLE LAW. EXCEPT WHEN
 * OTHERWISE STATED IN WRITING THE COPYRIGHT HOLDERS AND/OR OTHER PARTIES PROVIDE THE PROGRAM "AS
 * IS" WITHOUT WARRANTY OF ANY KIND, EITHER EXPRESSED OR IMPLIED, INCLUDING, BUT NOT LIMITED TO, THE
 * IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE. THE ENTIRE RISK AS TO
 * THE QUALITY AND PERFORMANCE OF THE PROGRAM IS WITH YOU. SHOULD THE PROGRAM PROVE DEFECTIVE,
 * YOU ASSUME THE COST OF ALL NECESSARY SERVICING, REPAIR OR CORRECTION.
 *
 * For more information on the GNU Affero General Public License, please consult
 * <https://www.gnu.org/licenses/agpl-3.0.en.html>.
 */
package org.meveo.admin.action.crm;

import javax.faces.view.ViewScoped;
import javax.inject.Inject;
import javax.inject.Named;
import javax.servlet.http.HttpServletResponse;

import org.jboss.seam.international.status.builder.BundleKey;
import org.meveo.admin.action.AccountBean;
import org.meveo.admin.action.BaseBean;
import org.meveo.admin.exception.BusinessException;
import org.meveo.admin.web.interceptor.ActionMethod;
import org.meveo.api.account.CustomerApi;
import org.meveo.model.billing.CounterInstance;
import org.meveo.model.crm.Customer;
import org.meveo.model.shared.Address;
import org.meveo.model.shared.ContactInformation;
import org.meveo.model.shared.Name;
import org.meveo.service.base.PersistenceService;
import org.meveo.service.base.local.IPersistenceService;
import org.meveo.service.billing.impl.CounterInstanceService;
import org.meveo.service.crm.impl.CustomerService;
import org.meveo.service.dwh.GdprService;

/**
 * Standard backing bean for {@link Customer} (extends {@link BaseBean} that provides almost all common methods to handle entities filtering/sorting in datatable, their create,
 * edit, view, delete operations). It works with Manaty custom JSF components.
 *
 * @author Edward P. Legaspi
 * @lastModifiedVersion 5.2
 */
@Named
@ViewScoped
public class CustomerBean extends AccountBean<Customer> {

    private static final long serialVersionUID = 1L;

    /**
     * Injected @{link Customer} service. Extends {@link PersistenceService}.
     */
    @Inject
    private CustomerService customerService;

    @Inject
    private CustomerApi customerApi;

    @Inject
    private GdprService gpdrService;

    @Inject
    private CounterInstanceService counterInstanceService;

    private CounterInstance selectedCounterInstance;
    
    private Boolean messageDisplayed= false;

    /**
     * Constructor. Invokes super constructor and provides class type of this bean for {@link BaseBean}.
     */
    public CustomerBean() {
        super(Customer.class);
    }

    @Override
    public Customer initEntity() {
        super.initEntity();
        if (entity.getAddress() == null) {
            entity.setAddress(new Address());
        }
        if (entity.getName() == null) {
            entity.setName(new Name());
        }
        if (entity.getContactInformation() == null) {
            entity.setContactInformation(new ContactInformation());
        }
        selectedCounterInstance = entity.getCounters() != null && entity.getCounters().size() > 0 ? entity.getCounters().values().iterator().next() : null;
        return entity;
    }

    @Override
    @ActionMethod
    public String saveOrUpdate(boolean killConversation) throws BusinessException {
        String outcome = super.saveOrUpdate(killConversation);

        if (outcome != null) {
            return getEditViewName(); // "/pages/crm/customers/customerDetail.xhtml?edit=true&customerId=" + entity.getId() + "&faces-redirect=true";
        }
        return null;
    }

    @Override
    protected IPersistenceService<Customer> getPersistenceService() {
        return customerService;
    }

    @Override
    protected String getDefaultSort() {
        return "code";
    }

    /**
     * Exports an account hierarchy given a specific customer selected in the GUI. It includes Subscription, AccountOperation and Invoice details. It packaged the json output as a
     * zipped file along with the pdf invoices.
     * 
     * @return null for JSF navigation
     * @throws Exception when zipping fail
     */
    @ActionMethod
    public String exportCustomerHierarchy() throws Exception {
        HttpServletResponse response = (HttpServletResponse) facesContext.getExternalContext().getResponse();
        customerApi.exportCustomerHierarchy(entity.getCode(), response);
        facesContext.responseComplete();

        return null;
    }

    /**
     * Deletes customer data. In such case, mandatory information (accounting, invoicing, payments) are preserved but the data tables including the customer's data must are
     * anonymized (firstname/name/emails/phones/addresses..). So if a person register back it will be treated as a new customer without history.
     * 
     * @return null for JSF navigation
     */
    @ActionMethod
    public String anonymizeGdpr() {
        try {
            entity = customerService.refreshOrRetrieve(entity);
            gpdrService.anonymize(entity);
            messages.info(new BundleKey("messages", "gdpr.delete.ok"));

        } catch (Exception e) {
            log.error("Failed anonymizing account hierarchy={}", e.getMessage());
            messages.info(new BundleKey("messages", "gdpr.delete.ko"));
        }

        return null;
    }

    /**
     * Gets selected counter instances.
     *
     * @return the selected counter instances
     */
    public CounterInstance getSelectedCounterInstance() {
        if (entity == null) {
            initEntity();
        }
        return selectedCounterInstance;
    }

    /**
     * Select counter instance.
     *
     * @param selectedCounterInstance selected counter instance
     */
    public void setSelectedCounterInstance(CounterInstance selectedCounterInstance) {
        if (selectedCounterInstance != null) {
            this.selectedCounterInstance = counterInstanceService.refreshOrRetrieve(selectedCounterInstance);
        } else {
            this.selectedCounterInstance = null;
        }
    }
    
    /**
   	 * Check if field is still encrypted
   	 * @param field
   	 * @return boolean 
   	 */
    public Boolean isEncrypted(String field) {
       	if(field == null) {
       		return false;
       	}
       	Boolean encrypted = field.contains("####");
       	if(encrypted && !messageDisplayed) {
       		messages.error(new BundleKey("messages", "decrypt.ko"));
       		messageDisplayed = true;
       	}
       	return encrypted;
    }
}