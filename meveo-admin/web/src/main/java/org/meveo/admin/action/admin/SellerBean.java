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

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import javax.inject.Inject;
import javax.inject.Named;
import javax.persistence.Column;

import org.jboss.seam.international.status.builder.BundleKey;
import org.meveo.admin.action.BaseBean;
import org.meveo.admin.action.CustomFieldBean;
import org.meveo.admin.exception.BusinessException;
import org.meveo.admin.util.ResourceBundle;
import org.meveo.admin.web.interceptor.ActionMethod;
import org.meveo.model.admin.Seller;
import org.meveo.model.billing.InvoiceType;
import org.meveo.model.billing.Sequence;
import org.meveo.service.admin.impl.SellerService;
import org.meveo.service.base.PersistenceService;
import org.meveo.service.base.local.IPersistenceService;
import org.meveo.service.billing.impl.InvoiceTypeService;
import org.omnifaces.cdi.ViewScoped;

@Named
@ViewScoped
public class SellerBean extends CustomFieldBean<Seller> {

	private static final long serialVersionUID = 1L;

	/**
	 * Injected @{link PricePlanMatrix} service. Extends
	 * {@link PersistenceService}.
	 */
	@Inject
	private SellerService sellerService;
	
	 @Inject
	private ResourceBundle resourceMessages;
	 
	 @Inject
	 private InvoiceTypeService invoiceTypeService;
	 
	 private String prefixEl;
	 private Integer sequenceSize= 9;
	 private Long currentInvoiceNb = 0L; 
	 private String invoiceTypeCode;

	/**
	 * Constructor. Invokes super constructor and provides class type of this
	 * bean for {@link BaseBean}.
	 */
	public SellerBean() {
		super(Seller.class);
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
	protected List<String> getFormFieldsToFetch() {
		return Arrays.asList("provider");
	}

	@Override
	@ActionMethod
	public String saveOrUpdate(boolean killConversation) throws BusinessException {
		return super.saveOrUpdate(killConversation);
		// prefix must be set
//		if (entity.getCurrentInvoiceNb() != null && StringUtils.isBlank(entity.getInvoicePrefix())) {
//			messages.error(new BundleKey("messages", "message.error.seller.invoicePrefix.required"));
//			return null;
//		} else {
//			return super.saveOrUpdate(killConversation);
//		}
	}
	
	public List<Map.Entry<InvoiceType,Sequence>> getInvoiceTypeSequencesList() {
		Set<Entry<InvoiceType,Sequence>> sequencesSet = 
				entity.getInvoiceTypeSequence().entrySet();
		return new ArrayList<Map.Entry<InvoiceType,Sequence>>(sequencesSet);
	}
	public void addNewInvoiceTypeSequence() throws BusinessException{
		InvoiceType invoiceType=invoiceTypeService.findByCode(invoiceTypeCode, getCurrentProvider());
		Sequence sequence = new Sequence();
		sequence.setPrefixEL(getPrefixEl());
		sequence.setSequenceSize(getSequenceSize());
		sequence.setCurrentInvoiceNb(getCurrentInvoiceNb());
		entity.getInvoiceTypeSequence().put(invoiceType, sequence);
	
	}

	public String getPrefixEl() {
		return prefixEl;
	}

	public void setPrefixEl(String prefixEl) {
		this.prefixEl = prefixEl;
	}

	public Integer getSequenceSize() {
		return sequenceSize;
	}

	public void setSequenceSize(Integer sequenceSize) {
		this.sequenceSize = sequenceSize;
	}

	public Long getCurrentInvoiceNb() {
		return currentInvoiceNb;
	}

	public void setCurrentInvoiceNb(Long currentInvoiceNb) {
		this.currentInvoiceNb = currentInvoiceNb;
	}

	public String getInvoiceTypeCode() {
		return invoiceTypeCode;
	}

	public void setInvoiceTypeCode(String invoiceTypeCode) {
		this.invoiceTypeCode = invoiceTypeCode;
	}
	
	

}