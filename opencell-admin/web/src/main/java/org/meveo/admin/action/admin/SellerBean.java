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
package org.meveo.admin.action.admin;

import java.util.HashMap;
import java.util.Map;

import javax.faces.context.FacesContext;
import javax.faces.view.ViewScoped;
import javax.inject.Inject;
import javax.inject.Named;

import org.jboss.seam.international.status.builder.BundleKey;
import org.meveo.admin.action.BaseBean;
import org.meveo.admin.action.CustomFieldBean;
import org.meveo.admin.exception.BusinessException;
import org.meveo.admin.web.interceptor.ActionMethod;
import org.meveo.model.admin.Seller;
import org.meveo.model.billing.InvoiceSequence;
import org.meveo.model.billing.InvoiceType;
import org.meveo.model.billing.InvoiceTypeSellerSequence;
import org.meveo.model.shared.Address;
import org.meveo.model.shared.ContactInformation;
import org.meveo.service.admin.impl.SellerService;
import org.meveo.service.base.PersistenceService;
import org.meveo.service.base.local.IPersistenceService;

/**
 * @author akadid abdelmounaim
 * @lastModifiedVersion 5.0
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

    private InvoiceTypeSellerSequence selectedInvoiceTypeSellerSequence;
    private String prefixEl;
    private InvoiceType invoiceType;
    private InvoiceSequence invoiceSequence;
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
        // if (entity.getAddress() == null) {
        // entity.setAddress(new Address());
        // }
        // if (entity.getContactInformation() == null) {
        // entity.setContactInformation(new ContactInformation());
        // }
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
    protected String getListViewName() {
        return "sellers";
    }

    @Override
    protected String getDefaultSort() {
        return "code";
    }

    @Override
    @ActionMethod
    public String saveOrUpdate(boolean killConversation) throws BusinessException {

        return super.saveOrUpdate(killConversation);
    }

    public void saveOrUpdateSequence() throws BusinessException {
        if (!editSellerSequence) {
            if (entity.isContainsInvoiceTypeSequence(invoiceType)) {
                messages.error(new BundleKey("messages", "seller.sellerSequence.unique"));
                FacesContext.getCurrentInstance().validationFailed();
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

}