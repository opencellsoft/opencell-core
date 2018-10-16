/*
 * (C) Copyright 2015-2016 Opencell SAS (http://opencellsoft.com/) and contributors.
 * (C) Copyright 2009-2014 Manaty SARL (http://manaty.net/) and contributors.
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Affero General Public License as
 * published by the Free Software Foundation, either version 3 of the
 * License, or (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  
 * This program is not suitable for any direct or indirect application in MILITARY industry
 * See the GNU Affero General Public License for more details.
 *
 * You should have received a copy of the GNU Affero General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
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
import org.meveo.model.crm.Customer;
import org.meveo.model.shared.Address;
import org.meveo.model.shared.ContactInformation;
import org.meveo.model.shared.Name;
import org.meveo.service.admin.impl.SellerService;
import org.meveo.service.base.PersistenceService;
import org.meveo.service.base.local.IPersistenceService;
import org.meveo.service.crm.impl.CustomerService;
import org.meveo.service.dwh.GdprService;

/**
 * Standard backing bean for {@link Customer} (extends {@link BaseBean} that provides almost all common methods to handle entities filtering/sorting in datatable, their create,
 * edit, view, delete operations). It works with Manaty custom JSF components.
 * @author Edward P. Legaspi
 * @lastModifiedVersion 5.2
 */
@Named
@ViewScoped
public class CustomerBean extends AccountBean<Customer> {

    private static final long serialVersionUID = 1L;

    /** Injected @{link Customer} service. Extends {@link PersistenceService}. */
    @Inject
    private CustomerService customerService;

    @Inject
    private SellerService sellerService;
    
    @Inject
    private CustomerApi customerApi;
    
    @Inject
    private GdprService gpdrService;

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
        return entity;
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.meveo.admin.action.BaseBean#saveOrUpdate(boolean)
     */
    @Override
    @ActionMethod
    public String saveOrUpdate(boolean killConversation) throws BusinessException {
        if(entity.getSeller() != null) {
            entity.setSeller(sellerService.retrieveIfNotManaged(entity.getSeller()));
        }
        String outcome = super.saveOrUpdate(killConversation);

        if (outcome != null) {
            return getEditViewName(); // "/pages/crm/customers/customerDetail.xhtml?edit=true&customerId=" + entity.getId() + "&faces-redirect=true";
        }
        return null;
    }

    /**
     * @see org.meveo.admin.action.BaseBean#getPersistenceService()
     */
    @Override
    protected IPersistenceService<Customer> getPersistenceService() {
        return customerService;
    }

    @Override
    protected String getDefaultSort() {
        return "code";
    }
    
    /**
	 * Exports an account hierarchy given a specific customer selected in the GUI.
	 * It includes Subscription, AccountOperation and Invoice details. It packaged the json output
	 * as a zipped file along with the pdf invoices.
	 * 
	 * @return null for JSF navigation
	 * @throws Exception when zipping fail
	 */
    @ActionMethod
	public String exportCustomerHierarchy() throws Exception {
		javax.faces.context.FacesContext context = javax.faces.context.FacesContext.getCurrentInstance();
        HttpServletResponse response = (HttpServletResponse) context.getExternalContext().getResponse();
		customerApi.exportCustomerHierarchy(entity.getCode(), response);
        context.responseComplete();
        
        return null;
	}
    
    /**
    * Deletes customer data. 
    * In such case, mandatory information (accounting, invoicing, payments) are preserved but the data tables including the
    * customer's data must are anonymized (firstname/name/emails/phones/addresses..). 
    * So if a person register back it will be treated as a new customer without history.
    * 
    * @return null for JSF navigation
    */
    @ActionMethod
    public String anonymizeGpdr() {
    	try {
    		entity = customerService.refreshOrRetrieve(entity);
	    	gpdrService.anonymize(entity);
	    	messages.info(new BundleKey("messages", "gdpr.delete.ok"));
    	
    	} catch(Exception e) {
    		log.error("Failed anonymizing account hierarchy={}", e.getMessage());
    		messages.info(new BundleKey("messages", "gdpr.delete.ko"));
    	}
    	
    	return null;
    }
}