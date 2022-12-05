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
import org.meveo.admin.action.CustomFieldBean;
import org.meveo.admin.exception.BusinessException;
import org.meveo.admin.web.interceptor.ActionMethod;
import org.meveo.model.billing.InvoiceType;
import org.meveo.service.base.PersistenceService;
import org.meveo.service.base.local.IPersistenceService;
import org.meveo.service.billing.impl.InvoiceTypeService;
import org.primefaces.model.DualListModel;

@Named
@ViewScoped
public class InvoiceTypeBean extends CustomFieldBean<InvoiceType> {

	private static final long serialVersionUID = 1L;

	/**
	 * Injected @{link InvoiceType} service. Extends
	 * {@link PersistenceService}.
	 */
	@Inject
	private InvoiceTypeService invoiceTypeService;
	
	private DualListModel<InvoiceType> invoiceTypesDM;

	/**
	 * Constructor. Invokes super constructor and provides class type of this
	 * bean for {@link BaseBean}.
	 */
	public InvoiceTypeBean() {
		super(InvoiceType.class);
	}

    @Override
    @ActionMethod
    public String saveOrUpdate(boolean killConversation) throws BusinessException {
        log.trace("saving new InvoiceType={}", entity.getCode());
        getEntity().getAppliesTo().clear();
        getEntity().getAppliesTo().addAll(invoiceTypeService.refreshOrRetrieve(invoiceTypesDM.getTarget()));
        return super.saveOrUpdate(killConversation);
    }

	/**
	 * @see org.meveo.admin.action.BaseBean#getPersistenceService()
	 */
	@Override
	protected IPersistenceService<InvoiceType> getPersistenceService() {
		return invoiceTypeService;
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
            if (getEntity().getAppliesTo() != null) {
                perksTarget.addAll(getEntity().getAppliesTo());
            }
            perksSource.removeAll(perksTarget);
            invoiceTypesDM = new DualListModel<InvoiceType>(perksSource, perksTarget);
        }
        return invoiceTypesDM;
    }

    public void setDualListModel(DualListModel<InvoiceType> invoiceTypesDM) {
        this.invoiceTypesDM = invoiceTypesDM;
    }
        
    public String getAdjustmentCode() {
		return String.join(",", invoiceTypeService.getListAdjustementCode());
	}
    
    public String getCommercialCode() {
		return invoiceTypeService.getCommercialCode();
	}
}
