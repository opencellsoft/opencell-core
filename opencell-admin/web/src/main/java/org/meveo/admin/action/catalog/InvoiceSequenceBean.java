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
package org.meveo.admin.action.catalog;

import java.util.ArrayList;
import java.util.List;

import jakarta.faces.view.ViewScoped;
import jakarta.inject.Inject;
import jakarta.inject.Named;

import org.meveo.admin.action.BaseBean;
import org.meveo.admin.action.admin.ViewBean;
import org.meveo.admin.exception.BusinessException;
import org.meveo.admin.web.interceptor.ActionMethod;
import org.meveo.model.billing.InvoiceSequence;
import org.meveo.model.billing.InvoiceType;
import org.meveo.service.base.PersistenceService;
import org.meveo.service.base.local.IPersistenceService;
import org.meveo.service.billing.impl.InvoiceSequenceService;
import org.meveo.service.billing.impl.InvoiceTypeService;
import org.primefaces.model.DualListModel;

@Named
@ViewScoped
@ViewBean
public class InvoiceSequenceBean extends BaseBean<InvoiceSequence> {

	private static final long serialVersionUID = 1L;

	/**
	 * Injected @{link InvoiceType} service. Extends
	 * {@link PersistenceService}.
	 */
	@Inject
	private InvoiceTypeService invoiceTypeService;
	
	@Inject
	private InvoiceSequenceService invoiceSequenceService;

	private DualListModel<InvoiceType> invoiceTypesDM;

	/**
	 * Constructor. Invokes super constructor and provides class type of this
	 * bean for {@link BaseBean}.
	 */
	public InvoiceSequenceBean() {
		super(InvoiceSequence.class);
	}

    @Override
    @ActionMethod
    public String saveOrUpdate(boolean killConversation) throws BusinessException {
        log.trace("saving new InvoiceType={}", entity.getCode());
        /*
        getEntity().getInvoiceTypes().clear();
        getEntity().getInvoiceTypes().addAll(invoiceTypeService.refreshOrRetrieve(invoiceTypesDM.getTarget()));
        
        if (entity.getCurrentInvoiceNb() != null
                && entity.getCurrentInvoiceNb().longValue() < invoiceSequenceService.getMaxCurrentInvoiceNumber(entity.getCode()).longValue()) {
            messages.error(new BundleKey("messages", "invoice.downgrade.cuurrentNb.error.msg"));
            return null;
        }
        */
        
        super.saveOrUpdate(killConversation);
        return getListViewName();
    }

	/**
	 * @see org.meveo.admin.action.BaseBean#getPersistenceService()
	 */
	@Override
	protected IPersistenceService<InvoiceSequence> getPersistenceService() {
		return invoiceSequenceService;
	}

	@Override
	protected String getDefaultSort() {
		return "code";
	}
	
    /**
     * Standard method for custom component with listType="pickList".
     * 
     * @return dualListModel of InvoiceType
     */
    public DualListModel<InvoiceType> getDualListModel() {
        if (invoiceTypesDM == null) {
            List<InvoiceType> perksSource = invoiceTypeService.listActive();
            List<InvoiceType> perksTarget = new ArrayList<InvoiceType>();
            perksTarget.addAll(getEntity().getInvoiceTypes());
            perksSource.removeAll(perksTarget);
            invoiceTypesDM = new DualListModel<InvoiceType>(perksSource, perksTarget);
        }
        return invoiceTypesDM;
    }

    public void setDualListModel(DualListModel<InvoiceType> invoiceTypesDM) {
        this.invoiceTypesDM = invoiceTypesDM;
    }
        
    
}
