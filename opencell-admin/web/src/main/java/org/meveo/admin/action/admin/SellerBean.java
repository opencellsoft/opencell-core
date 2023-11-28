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
package org.meveo.admin.action.admin;

import javax.faces.view.ViewScoped;
import javax.inject.Inject;
import javax.inject.Named;

import org.jboss.seam.international.status.builder.BundleKey;
import org.meveo.admin.action.BaseBean;
import org.meveo.admin.action.CustomFieldBean;
import org.meveo.admin.exception.BusinessException;
import org.meveo.admin.web.interceptor.ActionMethod;
import org.meveo.model.admin.Seller;
import org.meveo.model.billing.BankCoordinates;
import org.meveo.model.billing.InvoiceSequence;
import org.meveo.model.billing.InvoiceType;
import org.meveo.model.billing.InvoiceTypeSellerSequence;
import org.meveo.model.billing.IsoIcd;
import org.meveo.model.crm.CustomerSequence;
import org.meveo.model.shared.Address;
import org.meveo.model.shared.ContactInformation;
import org.meveo.service.admin.impl.SellerService;
import org.meveo.service.base.PersistenceService;
import org.meveo.service.base.local.IPersistenceService;
import org.meveo.service.catalog.impl.CustomerSequenceService;

/**
 * @author Edward P. Legaspi
 * @author akadid abdelmounaim
 * @lastModifiedVersion 5.2
 **/
@Named
@ViewScoped
public class SellerBean extends CustomFieldBean<Seller> {

    private static final long serialVersionUID = 1L;

    /**
     * Injected @{link PricePlanMatrix} service. Extends {@link PersistenceService}.
     */
    @Inject
    private SellerService sellerService;
    
    @Inject
    private CustomerSequenceService customerSequenceService;
    
    private InvoiceTypeSellerSequence selectedInvoiceTypeSellerSequence;
    private CustomerSequence selectedCustomerSequence;
    private String prefixEl;
    private InvoiceType invoiceType;
    private InvoiceSequence invoiceSequence;
    private IsoIcd icdId;
    private boolean editSellerSequence = false;
    
    

    /**
     * Constructor. Invokes super constructor and provides class type of this bean for {@link BaseBean}.
     */
    public SellerBean() {
        super(Seller.class);
    }

    /**
     * Initialize bean's entity
     * 
     * @return bean's entity
     * @author akadid abdelmounaim
     * @lastModifiedVersion 5.2
     */
    @Override
    public Seller initEntity() {
        super.initEntity();
        this.setIcdId(entity.getIcdId());
        if (entity.getAddress() == null) {
            entity.setAddress(new Address());
        }
        if (entity.getContactInformation() == null) {
            entity.setContactInformation(new ContactInformation());
        }
        if (entity.getBankCoordinates() == null) {
            entity.setBankCoordinates(new BankCoordinates());
        }
        return entity;
    }

    /**
     * @see org.meveo.admin.action.BaseBean#getPersistenceService()
     */
    @Override
    protected IPersistenceService<Seller> getPersistenceService() {
        return sellerService;
    }

    @Override
    protected String getDefaultSort() {
        return "code";
    }

    @Override
    @ActionMethod
    public String saveOrUpdate(boolean killConversation) throws BusinessException {
        if(entity.getRegistrationNo() != null && this.getIcdId() == null) {
            messages.error(new BundleKey("messages", "seller.icd.error"));
            facesContext.validationFailed();
            return "";
        }
        entity.setIcdId(this.getIcdId());
        return super.saveOrUpdate(killConversation);
    }

    public void saveOrUpdateSequence() throws BusinessException {
        if (!editSellerSequence) {
            if (entity.isContainsInvoiceTypeSequence(invoiceType)) {
                messages.error(new BundleKey("messages", "seller.sellerSequence.unique"));
                facesContext.validationFailed();
                return;
            } else {
                entity.getInvoiceTypeSequence().add(new InvoiceTypeSellerSequence(invoiceType, entity, invoiceSequence, prefixEl));
                messages.info(new BundleKey("messages", "seller.sequence.saved"));
            }
        } else {
            selectedInvoiceTypeSellerSequence.setPrefixEL(prefixEl);
            selectedInvoiceTypeSellerSequence.setInvoiceType(invoiceType);
            selectedInvoiceTypeSellerSequence.setInvoiceSequence(invoiceSequence);
            messages.info(new BundleKey("messages", "seller.sequence.saved"));
        }
        resetSequenceField();
    }

    public void deleteSellerSequence(InvoiceType invoiceType) {

        for (int i = 0; i < entity.getInvoiceTypeSequence().size(); i++) {
            if (entity.getInvoiceTypeSequence().get(i).getInvoiceType().equals(invoiceType)) {
                entity.getInvoiceTypeSequence().remove(i);
                break;
            }
        }
        messages.info(new BundleKey("messages", "seller.sequence.deleted"));
    }
    
    public void newInvoiceTypeSellerSequence() {
        this.selectedInvoiceTypeSellerSequence = new InvoiceTypeSellerSequence();
    }

    public void getSequenceSelected(InvoiceTypeSellerSequence invoiceTypeSellerSequence) {
        this.selectedInvoiceTypeSellerSequence = invoiceTypeSellerSequence;
        this.prefixEl = invoiceTypeSellerSequence.getPrefixEL();
        this.invoiceType = invoiceTypeSellerSequence.getInvoiceType();
        this.invoiceSequence = invoiceTypeSellerSequence.getInvoiceSequence();
        this.editSellerSequence = true;
    }
    public void resetSequenceField() {
        this.selectedInvoiceTypeSellerSequence = null;
        prefixEl = "";
        invoiceType = null;
        invoiceSequence = null;
        editSellerSequence = false;
    }
	
	public void newCustomerSequence() {
        selectedCustomerSequence = new CustomerSequence();
        selectedCustomerSequence.setSeller(getEntity());
    }
    
    public void resetCustomerSequence() {
        selectedCustomerSequence = null;
    }
    
	public void saveOrUpdateCustomerSequence() throws BusinessException {
		if (selectedCustomerSequence.isTransient() && !entity.getCustomerSequences().contains(selectedCustomerSequence)) {
		    selectedCustomerSequence.updateAudit(currentUser);
	        entity.getCustomerSequences().add(selectedCustomerSequence);
		}
		
		resetCustomerSequence();
	}
	
	public void selectCustomerSequence(CustomerSequence customerSequence) {
		this.selectedCustomerSequence = customerSequence;
	}
	
	public void deleteCustomerSequence(CustomerSequence customerSequence) throws BusinessException {
		this.customerSequenceService.remove(customerSequence);
		entity = sellerService.refreshOrRetrieve(entity);
	}

    public String getPrefixEl() {
        return prefixEl;
    }

    public void setPrefixEl(String prefixEl) {
        this.prefixEl = prefixEl;
    }
    
    public InvoiceType getInvoiceType() {
        return invoiceType;
    }

    public void setInvoiceType(InvoiceType invoiceType) {
        this.invoiceType = invoiceType;
    }

    public boolean isEditSellerSequence() {
        return editSellerSequence;
    }

    public void setEditSellerSequence(boolean editSellerSequence) {
        this.editSellerSequence = editSellerSequence;
    }

	public InvoiceSequence getInvoiceSequence() {
		return invoiceSequence;
	}

	public void setInvoiceSequence(InvoiceSequence invoiceSequence) {
		this.invoiceSequence = invoiceSequence;
	}

	public InvoiceTypeSellerSequence getSelectedInvoiceTypeSellerSequence() {
		return selectedInvoiceTypeSellerSequence;
	}

	public void setSelectedInvoiceTypeSellerSequence(InvoiceTypeSellerSequence selectedInvoiceTypeSellerSequence) {
		this.selectedInvoiceTypeSellerSequence = selectedInvoiceTypeSellerSequence;
	}

	public CustomerSequence getSelectedCustomerSequence() {
		return selectedCustomerSequence;
	}

	public void setSelectedCustomerSequence(CustomerSequence selectedCustomerSequence) {
		this.selectedCustomerSequence = selectedCustomerSequence;
	}

    public IsoIcd getIcdId() {
        return icdId;
    }

    public void setIcdId(IsoIcd icdId) {
        this.icdId = icdId;
    }

}