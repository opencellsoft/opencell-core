/*
 * (C) Copyright 2009-2014 Manaty SARL (http://manaty.net/) and contributors.
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Affero General Public License as
 * published by the Free Software Foundation, either version 3 of the
 * License, or (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Affero General Public License for more details.
 *
 * You should have received a copy of the GNU Affero General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package org.meveo.admin.action.billing;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Set;

import javax.inject.Inject;
import javax.inject.Named;

import org.meveo.admin.action.BaseBean;
import org.meveo.admin.exception.BusinessException;
import org.meveo.model.billing.BillingAccount;
import org.meveo.model.billing.CategoryInvoiceAgregate;
import org.meveo.model.billing.Invoice;
import org.meveo.model.billing.InvoiceAgregate;
import org.meveo.model.billing.InvoiceCategory;
import org.meveo.model.billing.InvoiceCategoryDTO;
import org.meveo.model.billing.InvoiceSubCategory;
import org.meveo.model.billing.InvoiceSubCategoryDTO;
import org.meveo.model.billing.SubCategoryInvoiceAgregate;
import org.meveo.service.base.PersistenceService;
import org.meveo.service.base.local.IPersistenceService;
import org.meveo.service.billing.impl.BillingAccountService;
import org.meveo.service.billing.impl.InvoiceService;
import org.meveo.service.payments.impl.CustomerAccountService;
import org.omnifaces.cdi.ViewScoped;
import org.primefaces.model.LazyDataModel;

/**
 * Standard backing bean for {@link Invoice} (extends {@link BaseBean} that
 * provides almost all common methods to handle entities filtering/sorting in
 * datatable, their create, edit, view, delete operations). It works with Manaty
 * custom JSF components.
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

	@Inject
	CustomerAccountService customerAccountService;

	/**
	 * Constructor. Invokes super constructor and provides class type of this
	 * bean for {@link BaseBean}.
	 */
	public InvoiceBean() {
		super(Invoice.class);
	}

	/**
	 * Method, that is invoked in billing account screen. This method returns
	 * invoices associated with current Billing Account.
	 * 
	 */
	public LazyDataModel<Invoice> getBillingAccountInvoices(BillingAccount ba) {
		if (ba.getCode() == null) {
			log.warn("No billingAccount code");
		} else {
			filters.put("billingAccount", ba);
			return getLazyDataModel();
		}

		return null;
	}

	/**
	 * @see org.meveo.admin.action.BaseBean#getPersistenceService()
	 */
	@Override
	protected IPersistenceService<Invoice> getPersistenceService() {
		return invoiceService;
	}

	public List<InvoiceCategoryDTO> getInvoiceCategories() {

		LinkedHashMap<String, InvoiceCategoryDTO> headerCategories = new LinkedHashMap<String, InvoiceCategoryDTO>();
		List<CategoryInvoiceAgregate> categoryInvoiceAgregates = new ArrayList<CategoryInvoiceAgregate>();
		for (InvoiceAgregate invoiceAgregate : entity.getInvoiceAgregates()) {
			if (invoiceAgregate instanceof CategoryInvoiceAgregate) {
				CategoryInvoiceAgregate categoryInvoiceAgregate = (CategoryInvoiceAgregate) invoiceAgregate;
				categoryInvoiceAgregates.add(categoryInvoiceAgregate);
			}
		}
		Collections.sort(categoryInvoiceAgregates,
				new Comparator<CategoryInvoiceAgregate>() {
					public int compare(CategoryInvoiceAgregate c0,
							CategoryInvoiceAgregate c1) {
						if (c0.getInvoiceCategory() != null
								&& c1.getInvoiceCategory() != null
								&& c0.getInvoiceCategory().getSortIndex() != null
								&& c1.getInvoiceCategory().getSortIndex() != null) {
							return c0
									.getInvoiceCategory()
									.getSortIndex()
									.compareTo(
											c1.getInvoiceCategory()
													.getSortIndex());
						}
						return 0;
					}
				});

		for (CategoryInvoiceAgregate categoryInvoiceAgregate : categoryInvoiceAgregates) {
			InvoiceCategory invoiceCategory = categoryInvoiceAgregate
					.getInvoiceCategory();
			InvoiceCategoryDTO headerCat = null;
			if (headerCategories.containsKey(invoiceCategory.getCode())) {
				headerCat = headerCategories.get(invoiceCategory.getCode());
				headerCat.addAmountWithoutTax(categoryInvoiceAgregate
						.getAmountWithoutTax());
				headerCat.addAmountWithTax(categoryInvoiceAgregate
						.getAmountWithTax());
			} else {
				headerCat = new InvoiceCategoryDTO();
				headerCat.setDescription(invoiceCategory.getDescription());
				headerCat.setCode(invoiceCategory.getCode());
				headerCat.setAmountWithoutTax(categoryInvoiceAgregate
						.getAmountWithoutTax());
				headerCat.setAmountWithTax(categoryInvoiceAgregate
						.getAmountWithTax());
				headerCategories.put(invoiceCategory.getCode(), headerCat);
			}
			Set<SubCategoryInvoiceAgregate> subCategoryInvoiceAgregates = categoryInvoiceAgregate
					.getSubCategoryInvoiceAgregates();
			LinkedHashMap<String, InvoiceSubCategoryDTO> headerSubCategories = headerCat
					.getInvoiceSubCategoryDTOMap();
			for (SubCategoryInvoiceAgregate subCatInvoiceAgregate : subCategoryInvoiceAgregates) {
				InvoiceSubCategory invoiceSubCategory = subCatInvoiceAgregate
						.getInvoiceSubCategory();
				InvoiceSubCategoryDTO headerSUbCat = null;
				if (headerSubCategories.containsKey(invoiceSubCategory
						.getCode())) {
					headerSUbCat = headerSubCategories.get(invoiceSubCategory
							.getCode());
					headerSUbCat.addAmountWithoutTax(subCatInvoiceAgregate
							.getAmountWithoutTax());
					headerSUbCat.addAmountWithTax(subCatInvoiceAgregate
							.getAmountWithTax());
				} else {
					headerSUbCat = new InvoiceSubCategoryDTO();
					headerSUbCat.setDescription(invoiceSubCategory
							.getDescription());
					headerSUbCat.setCode(invoiceSubCategory.getCode());
					headerSUbCat.setAmountWithoutTax(subCatInvoiceAgregate
							.getAmountWithoutTax());
					headerSUbCat.setAmountWithTax(subCatInvoiceAgregate
							.getAmountWithTax());
					headerSubCategories.put(invoiceSubCategory.getCode(),
							headerSUbCat);
				}
			}
		}
		return new ArrayList<InvoiceCategoryDTO>(headerCategories.values());
	}

	public String getNetToPay() throws BusinessException {
		BigDecimal balance = customerAccountService.customerAccountBalanceDue(
				null,
				entity.getBillingAccount().getCustomerAccount().getCode(),
				entity.getDueDate());

		if (balance == null) {
			throw new BusinessException("account balance calculation failed");
		}
		BigDecimal netToPay = BigDecimal.ZERO;
		if (entity.getProvider().isEntreprise()) {
			netToPay = entity.getAmountWithTax();
		} else {
			netToPay = entity.getAmountWithTax().add(balance);
		}
		return netToPay.setScale(2, RoundingMode.HALF_UP).toString();
	}

}
