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
package org.meveo.admin.action.billing;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashSet;
import java.util.List;

import javax.faces.application.FacesMessage;
import javax.faces.context.FacesContext;
import javax.inject.Inject;
import javax.inject.Named;

import org.meveo.admin.action.BaseBean;
import org.meveo.admin.action.CustomFieldBean;
import org.meveo.admin.exception.BusinessException;
import org.meveo.admin.web.converter.InvoiceCatSubCatModel;
import org.meveo.model.Auditable;
import org.meveo.model.billing.BillingAccount;
import org.meveo.model.billing.CategoryInvoiceAgregate;
import org.meveo.model.billing.Invoice;
import org.meveo.model.billing.InvoiceAgregate;
import org.meveo.model.billing.InvoiceCategory;
import org.meveo.model.billing.InvoiceSubCategory;
import org.meveo.model.billing.SubCategoryInvoiceAgregate;
import org.meveo.model.billing.Tax;
import org.meveo.model.billing.TaxInvoiceAgregate;
import org.meveo.service.base.PersistenceService;
import org.meveo.service.base.local.IPersistenceService;
import org.meveo.service.billing.impl.BillingAccountService;
import org.meveo.service.billing.impl.InvoiceAgregateService;
import org.meveo.service.billing.impl.InvoiceService;
import org.meveo.service.billing.impl.InvoiceTypeService;
import org.meveo.service.billing.impl.RatedTransactionService;
import org.meveo.service.billing.impl.XMLInvoiceCreator;
import org.meveo.service.catalog.impl.InvoiceCategoryService;
import org.meveo.service.catalog.impl.InvoiceSubCategoryService;
import org.meveo.service.payments.impl.CustomerAccountService;
import org.omnifaces.cdi.ViewScoped;
import org.primefaces.event.CellEditEvent;
import org.primefaces.event.SelectEvent;

/**
 * Standard backing bean for {@link Invoice} (extends {@link BaseBean} that
 * provides almost all common methods to handle entities filtering/sorting in
 * datatable, their create, edit, view, delete operations). It works with Manaty
 * custom JSF components.
 */
@Named
@ViewScoped
public class AggregatedDetailInvoiceBean extends CustomFieldBean<Invoice> {

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

	@Inject
	RatedTransactionService ratedTransactionService;

	@Inject
	InvoiceAgregateService invoiceAgregateService;

	@Inject
	InvoiceTypeService invoiceTypeService;

	@Inject
	XMLInvoiceCreator xmlInvoiceCreator;

	@Inject
	private InvoiceCategoryService invoiceCategoryService;

	@Inject
	private InvoiceSubCategoryService invoiceSubCategoryService;

	private long billingAccountId;
	private Invoice invoiceToAdd;
	private Invoice selectedInvoice;
	private CategoryInvoiceAgregate selectedCategoryInvoiceAgregate;
	private SubCategoryInvoiceAgregate selectedSubCategoryInvoiceAgregate;
	private InvoiceCatSubCatModel selectedInvoiceCatSubCatModel = new InvoiceCatSubCatModel();

	/**
	 * Constructor. Invokes super constructor and provides class type of this
	 * bean for {@link BaseBean}.
	 */
	public AggregatedDetailInvoiceBean() {
		super(Invoice.class);
	}

	@Override
	public Invoice initEntity() {
		Invoice invoice = super.initEntity();

		return invoice;
	}

	/**
	 * @see org.meveo.admin.action.BaseBean#getPersistenceService()
	 */
	@Override
	protected IPersistenceService<Invoice> getPersistenceService() {
		return invoiceService;
	}

	public void onRowSelect(SelectEvent event) {
		invoiceToAdd = (Invoice) event.getObject();
		if (invoiceToAdd != null && !entity.getLinkedInvoices().contains(invoiceToAdd)) {
			// linkedInvoices.add(invoiceToAdd);
			entity.getLinkedInvoices().add(invoiceToAdd);
		}
	}

	public void deleteLinkedInvoice() {
		entity.getLinkedInvoices().remove(selectedInvoice);
		selectedInvoice = null;
	}

	@SuppressWarnings({ "rawtypes", "unchecked" })
	public void deleteAllLinkedInvoice() {
		entity.setLinkedInvoices(new HashSet());
		selectedInvoice = null;
	}

	public void importFromLinkedInvoices() {
		if (entity.getLinkedInvoices() != null && entity.getLinkedInvoices().size() > 0) {
			Auditable auditable = new Auditable();
			auditable.setCreator(getCurrentUser());
			auditable.setCreated(new Date());

			for (Invoice i : entity.getLinkedInvoices()) {
				i = invoiceService.findById(i.getId());
				for (InvoiceAgregate invoiceAgregate : i.getInvoiceAgregates()) {
					if (invoiceAgregate instanceof CategoryInvoiceAgregate) {
						CategoryInvoiceAgregate categoryInvoiceAgregate = (CategoryInvoiceAgregate) invoiceAgregate;

						CategoryInvoiceAgregate newCategoryInvoiceAgregate = new CategoryInvoiceAgregate(categoryInvoiceAgregate);
						newCategoryInvoiceAgregate.setSubCategoryInvoiceAgregates(null);
						newCategoryInvoiceAgregate.setInvoice(entity);
						newCategoryInvoiceAgregate.setAuditable(auditable);
						entity.addInvoiceAggregate(newCategoryInvoiceAgregate);

						if (categoryInvoiceAgregate.getSubCategoryInvoiceAgregates() != null) {
							for (SubCategoryInvoiceAgregate subCategoryInvoiceAgregate : categoryInvoiceAgregate.getSubCategoryInvoiceAgregates()) {

								SubCategoryInvoiceAgregate newSubCategoryInvoiceAgregate = new SubCategoryInvoiceAgregate(subCategoryInvoiceAgregate);
								newSubCategoryInvoiceAgregate.setInvoice(entity);
								newSubCategoryInvoiceAgregate.setAuditable(auditable);
								newSubCategoryInvoiceAgregate.setOldAmountWithoutTax(subCategoryInvoiceAgregate.getAmountWithoutTax());
								newSubCategoryInvoiceAgregate.setOldAmountWithTax(subCategoryInvoiceAgregate.getAmountWithTax());
								newSubCategoryInvoiceAgregate.setAmountWithTax(subCategoryInvoiceAgregate.getAmountWithTax());
								newSubCategoryInvoiceAgregate.setAmountTax(subCategoryInvoiceAgregate.getAmountTax());
								newSubCategoryInvoiceAgregate.setCategoryInvoiceAgregate(newCategoryInvoiceAgregate);

								entity.addInvoiceAggregate(newSubCategoryInvoiceAgregate);

								newCategoryInvoiceAgregate.addSubCategoryInvoiceAggregate(newSubCategoryInvoiceAgregate);

								if (subCategoryInvoiceAgregate.getSubCategoryTaxes() != null) {
									for (Tax tax : subCategoryInvoiceAgregate.getSubCategoryTaxes()) {
										newSubCategoryInvoiceAgregate.addSubCategoryTax(tax);
									}
								}
							}
						}
					} else if (invoiceAgregate instanceof TaxInvoiceAgregate) {
						TaxInvoiceAgregate taxInvoiceAgregate = (TaxInvoiceAgregate) invoiceAgregate;
						TaxInvoiceAgregate newTaxInvoiceAgregate = new TaxInvoiceAgregate(taxInvoiceAgregate);

						newTaxInvoiceAgregate.setInvoice(entity);
						newTaxInvoiceAgregate.setAuditable(auditable);
						newTaxInvoiceAgregate.setTax(taxInvoiceAgregate.getTax());
						entity.addInvoiceAggregate(newTaxInvoiceAgregate);
					}
				}
			}
		}
	}

	public List<CategoryInvoiceAgregate> getCategoryInvoiceAggregates() {
		List<CategoryInvoiceAgregate> result = new ArrayList<>();
		if (entity.getInvoiceAgregates() != null) {
			for (InvoiceAgregate invoiceAgregate : entity.getInvoiceAgregates()) {
				if (invoiceAgregate instanceof CategoryInvoiceAgregate) {
					CategoryInvoiceAgregate cat = (CategoryInvoiceAgregate) invoiceAgregate;
					if (!result.contains(cat)) {
						result.add(cat);
					}
				}
			}
		}

		return result;
	}

	public List<SubCategoryInvoiceAgregate> getSubCategoryInvoiceAggregates(CategoryInvoiceAgregate cat) {
		List<SubCategoryInvoiceAgregate> result = new ArrayList<>();
		if (cat.getSubCategoryInvoiceAgregates() == null)
			return result;

		for (SubCategoryInvoiceAgregate subCat : cat.getSubCategoryInvoiceAgregates()) {
			result.add(subCat);
		}

		return result;
	}

	public void deleteLinkedInvoiceCategory() {
		if (entity.getInvoiceAgregates() != null) {
			entity.getInvoiceAgregates().remove(selectedCategoryInvoiceAgregate);
		}
	}

	public void deleteLinkedInvoiceSubCategory() {
		if (entity.getInvoiceAgregates() != null) {
			for (InvoiceAgregate invoiceAgregate : entity.getInvoiceAgregates()) {
				if (invoiceAgregate instanceof CategoryInvoiceAgregate) {
					CategoryInvoiceAgregate cat = (CategoryInvoiceAgregate) invoiceAgregate;
					if (cat.equals(selectedSubCategoryInvoiceAgregate.getCategoryInvoiceAgregate())) {
						cat.getSubCategoryInvoiceAgregates().remove(selectedSubCategoryInvoiceAgregate);
						entity.getInvoiceAgregates().remove(selectedSubCategoryInvoiceAgregate);
					}
				}
			}
		}
	}

	public void onCellEdit(CellEditEvent event) {
		Object oldValue = event.getOldValue();
		Object newValue = event.getNewValue();

		if (newValue != null && !newValue.equals(oldValue)) {
			FacesMessage msg = new FacesMessage(FacesMessage.SEVERITY_INFO, "Cell Changed", "Old: " + oldValue + ", New:" + newValue);
			FacesContext.getCurrentInstance().addMessage(null, msg);
		}
	}

	public BigDecimal computeTotalAmountWithoutTax() {
		BigDecimal total = new BigDecimal(0);
		if (entity.getInvoiceAgregates() != null) {
			for (InvoiceAgregate invoiceAgregate : entity.getInvoiceAgregates()) {
				if (invoiceAgregate instanceof SubCategoryInvoiceAgregate) {
					SubCategoryInvoiceAgregate subCategoryInvoiceAgregate = (SubCategoryInvoiceAgregate) invoiceAgregate;
					if (subCategoryInvoiceAgregate.getAmountWithoutTax() != null) {
						total = total.add(subCategoryInvoiceAgregate.getAmountWithoutTax());
					}
				}
			}
		}

		return total;
	}

	public BigDecimal computeTotalAmountTax() {
		BigDecimal total = new BigDecimal(0);
		if (entity.getInvoiceAgregates() != null) {
			for (InvoiceAgregate invoiceAgregate : entity.getInvoiceAgregates()) {
				if (invoiceAgregate instanceof SubCategoryInvoiceAgregate) {
					SubCategoryInvoiceAgregate subCategoryInvoiceAgregate = (SubCategoryInvoiceAgregate) invoiceAgregate;
					if (subCategoryInvoiceAgregate.getAmountTax() != null) {
						total = total.add(subCategoryInvoiceAgregate.getAmountTax());
					}
				}
			}
		}

		return total;
	}

	public BigDecimal computeTotalAmountWithTax() {
		BigDecimal total = new BigDecimal(0);
		if (entity.getInvoiceAgregates() != null) {
			for (InvoiceAgregate invoiceAgregate : entity.getInvoiceAgregates()) {
				if (invoiceAgregate instanceof SubCategoryInvoiceAgregate) {
					SubCategoryInvoiceAgregate subCategoryInvoiceAgregate = (SubCategoryInvoiceAgregate) invoiceAgregate;
					if (subCategoryInvoiceAgregate.getAmountWithTax() != null) {
						total = total.add(subCategoryInvoiceAgregate.getAmountWithTax());
					}
				}
			}
		}

		return total;
	}

	public String getNetToPay() throws BusinessException {
		if (entity.getBillingAccount().isTransient())
			return "";

		BillingAccount ba = billingAccountService.findById(entity.getBillingAccount().getId());
		BigDecimal balance = customerAccountService.customerAccountBalanceDue(null, ba.getCustomerAccount().getCode(), entity.getDueDate(), ba.getProvider());

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

	public List<InvoiceCatSubCatModel> getInvoiceCatSubCats() {
		List<InvoiceCatSubCatModel> result = new ArrayList<>();

		List<InvoiceCategory> invoiceCategories = invoiceCategoryService.list();
		for (InvoiceCategory ic : invoiceCategories) {
			result.add(new InvoiceCatSubCatModel(InvoiceCatSubCatModel.CATEGORY, ic, ic.getCode()));
			for (InvoiceSubCategory is : ic.getInvoiceSubCategories()) {
				result.add(new InvoiceCatSubCatModel(InvoiceCatSubCatModel.SUB_CATEGORY, is, is.getCode()));
			}
		}

		return result;
	}

	public void addAggregatedLine() {
		if (selectedInvoiceCatSubCatModel == null)
			return;

		if (selectedInvoiceCatSubCatModel.getType() == InvoiceCatSubCatModel.CATEGORY) {
			addInvoiceCategory(selectedInvoiceCatSubCatModel);
		} else if (selectedInvoiceCatSubCatModel.getType() == InvoiceCatSubCatModel.SUB_CATEGORY) {
			addInvoiceSubCategory(selectedInvoiceCatSubCatModel);
		}

		selectedInvoiceCatSubCatModel = new InvoiceCatSubCatModel();
	}

	private void addInvoiceCategory(InvoiceCatSubCatModel invoiceCatSubCatModel) {
		InvoiceCategory ic = (InvoiceCategory) invoiceCatSubCatModel.getEntity();
		Auditable auditable = new Auditable();
		auditable.setCreator(getCurrentUser());
		auditable.setCreated(new Date());

		CategoryInvoiceAgregate newCategoryInvoiceAgregate = new CategoryInvoiceAgregate();
		newCategoryInvoiceAgregate.setSubCategoryInvoiceAgregates(null);
		newCategoryInvoiceAgregate.setInvoiceCategory(ic);
		newCategoryInvoiceAgregate.setInvoice(entity);
		newCategoryInvoiceAgregate.setAmountWithTax(selectedInvoiceCatSubCatModel.getAmountWithTax());
		newCategoryInvoiceAgregate.setAuditable(auditable);
		newCategoryInvoiceAgregate.setDescription(selectedInvoiceCatSubCatModel.getDescription());

		entity.addInvoiceAggregate(newCategoryInvoiceAgregate);
	}

	private void addInvoiceSubCategory(InvoiceCatSubCatModel invoiceCatSubCatModel) {
		InvoiceSubCategory isc = (InvoiceSubCategory) invoiceCatSubCatModel.getEntity();
		InvoiceCategory ic = (InvoiceCategory) isc.getInvoiceCategory();

		Auditable auditable = new Auditable();
		auditable.setCreator(getCurrentUser());
		auditable.setCreated(new Date());

		SubCategoryInvoiceAgregate newSubCategoryInvoiceAgregate = new SubCategoryInvoiceAgregate();
		newSubCategoryInvoiceAgregate.setInvoice(entity);
		newSubCategoryInvoiceAgregate.setAuditable(auditable);
		newSubCategoryInvoiceAgregate.setAmountWithTax(invoiceCatSubCatModel.getAmountWithTax());
		newSubCategoryInvoiceAgregate.setAmountWithoutTax(invoiceCatSubCatModel.getAmountWithoutTax());
		newSubCategoryInvoiceAgregate.setInvoiceSubCategory(isc);

		// find category
		boolean found = false;
		CategoryInvoiceAgregate foundCategoryInvoiceAgregate = null;
		for (InvoiceAgregate ia : entity.getInvoiceAgregates()) {
			if (ia instanceof CategoryInvoiceAgregate) {
				foundCategoryInvoiceAgregate = (CategoryInvoiceAgregate) ia;
				if (foundCategoryInvoiceAgregate.getInvoiceCategory().getCode().equals(ic.getCode())) {
					found = true;
					break;
				}
			}
		}

		CategoryInvoiceAgregate newCategoryInvoiceAgregate = null;
		if (!found) {
			// create invoice category
			newCategoryInvoiceAgregate = new CategoryInvoiceAgregate();
			newCategoryInvoiceAgregate.setSubCategoryInvoiceAgregates(null);
			newCategoryInvoiceAgregate.setInvoiceCategory(ic);
			newCategoryInvoiceAgregate.setInvoice(entity);
			newCategoryInvoiceAgregate.setAmountWithTax(selectedInvoiceCatSubCatModel.getAmountWithTax());
			newCategoryInvoiceAgregate.setAmountWithoutTax(selectedInvoiceCatSubCatModel.getAmountWithoutTax());
			newCategoryInvoiceAgregate.setAuditable(auditable);

			entity.addInvoiceAggregate(newCategoryInvoiceAgregate);
		} else {
			// update total
			foundCategoryInvoiceAgregate.addAmountWithTax(newSubCategoryInvoiceAgregate.getAmountWithTax());
			foundCategoryInvoiceAgregate.addAmountWithoutTax(newSubCategoryInvoiceAgregate.getAmountWithoutTax());
		}

		newSubCategoryInvoiceAgregate.setCategoryInvoiceAgregate(newCategoryInvoiceAgregate);
		newCategoryInvoiceAgregate.addSubCategoryInvoiceAggregate(newSubCategoryInvoiceAgregate);
		entity.addInvoiceAggregate(newSubCategoryInvoiceAgregate);
	}

	public long getBillingAccountId() {
		return billingAccountId;
	}

	public void setBillingAccountId(long billingAccountId) {
		this.billingAccountId = billingAccountId;
	}

	public Invoice getInvoiceToAdd() {
		return invoiceToAdd;
	}

	public void setInvoiceToAdd(Invoice invoiceToAdd) {
		this.invoiceToAdd = invoiceToAdd;
	}

	public Invoice getSelectedInvoice() {
		return selectedInvoice;
	}

	public void setSelectedInvoice(Invoice selectedInvoice) {
		this.selectedInvoice = selectedInvoice;
	}

	public CategoryInvoiceAgregate getSelectedCategoryInvoiceAgregate() {
		return selectedCategoryInvoiceAgregate;
	}

	public void setSelectedCategoryInvoiceAgregate(CategoryInvoiceAgregate selectedCategoryInvoiceAgregate) {
		this.selectedCategoryInvoiceAgregate = selectedCategoryInvoiceAgregate;
	}

	public SubCategoryInvoiceAgregate getSelectedSubCategoryInvoiceAgregate() {
		return selectedSubCategoryInvoiceAgregate;
	}

	public void setSelectedSubCategoryInvoiceAgregate(SubCategoryInvoiceAgregate selectedSubCategoryInvoiceAgregate) {
		this.selectedSubCategoryInvoiceAgregate = selectedSubCategoryInvoiceAgregate;
	}

	public InvoiceCatSubCatModel getSelectedInvoiceCatSubCatModel() {
		if (selectedInvoiceCatSubCatModel == null) {
			selectedInvoiceCatSubCatModel = new InvoiceCatSubCatModel();
		}
		
		return selectedInvoiceCatSubCatModel;
	}

	public void setSelectedInvoiceCatSubCatModel(InvoiceCatSubCatModel selectedInvoiceCatSubCatModel) {
		this.selectedInvoiceCatSubCatModel = selectedInvoiceCatSubCatModel;
	}

}
