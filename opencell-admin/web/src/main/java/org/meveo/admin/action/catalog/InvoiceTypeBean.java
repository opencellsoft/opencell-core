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
package org.meveo.admin.action.catalog;

import java.util.ArrayList;
import java.util.List;

import javax.faces.view.ViewScoped;
import javax.inject.Inject;
import javax.inject.Named;

import org.jboss.seam.international.status.builder.BundleKey;
import org.meveo.admin.action.BaseBean;
import org.meveo.admin.exception.BusinessException;
import org.meveo.admin.web.interceptor.ActionMethod;
import org.meveo.model.billing.InvoiceType;
import org.meveo.service.base.PersistenceService;
import org.meveo.service.base.local.IPersistenceService;
import org.meveo.service.billing.impl.InvoiceTypeService;
import org.primefaces.model.DualListModel;

@Named
@ViewScoped
public class InvoiceTypeBean extends BaseBean<InvoiceType> {

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
        if (entity.getSequence() != null && entity.getSequence().getCurrentInvoiceNb() != null
                && entity.getSequence().getCurrentInvoiceNb().longValue() < invoiceTypeService.getMaxCurrentInvoiceNumber(entity.getCode()).longValue()) {
            messages.error(new BundleKey("messages", "invoice.downgrade.cuurrentNb.error.msg"));
            return null;
        }
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
	protected String getListViewName() {
		return "invoiceTypes";
	}

	@Override
	public String getNewViewName() {
		return "invoiceTypeDetail";
	}

	@Override
	protected String getDefaultSort() {
		return "code";
	}
	
    /**
     * Standard method for custom component with listType="pickList".
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
		return invoiceTypeService.getAdjustementCode();
	}
    
    public String getCommercialCode() {
		return invoiceTypeService.getCommercialCode();
	}
}
