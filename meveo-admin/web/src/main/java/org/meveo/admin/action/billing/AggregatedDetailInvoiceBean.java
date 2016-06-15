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
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import javax.faces.application.FacesMessage;
import javax.faces.context.FacesContext;
import javax.faces.model.SelectItem;
import javax.faces.model.SelectItemGroup;
import javax.inject.Inject;
import javax.inject.Named;

import org.jboss.seam.international.status.builder.BundleKey;
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
import org.meveo.model.billing.InvoiceSubcategoryCountry;
import org.meveo.model.billing.SubCategoryInvoiceAgregate;
import org.meveo.model.billing.Tax;
import org.meveo.model.billing.TaxInvoiceAgregate;
import org.meveo.model.billing.UserAccount;
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
import org.primefaces.event.ItemSelectEvent;
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
	private InvoiceSubCategory selectedInvoiceSubCategory;
	private List<CategoryInvoiceAgregate> tempCategoryInvoiceAggregates = new ArrayList<>();
	private List<TaxInvoiceAgregate> tempTaxInvoiceAggregates = new ArrayList<>();
	private List<SelectItem> invoiceCategoriesGUI;
	private String description;
	private BigDecimal amountWithoutTax;
	private boolean includeBalance;

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

	public void importFromLinkedInvoices() throws BusinessException {
		if (entity.getBillingAccount() == null || entity.getBillingAccount().isTransient()) {
			messages.error("BillingAccount is required.");
			return;
		}

		if (entity.getDueDate() == null) {
			messages.error("Due date is required.");
			return;
		}

		BillingAccount billingAccount = billingAccountService.refreshOrRetrieve(entity.getBillingAccount());
		List<UserAccount> userAccounts = billingAccount.getUsersAccounts();
		if (userAccounts == null || userAccounts.isEmpty()) {
			messages.error("BillingAccount with code=" + entity.getBillingAccount().getCode() + " has no userAccount.");
			return;
		}
		// TODO : userAccount on dto ?
		UserAccount userAccount = userAccounts.get(0);

		if (entity.getLinkedInvoices() != null && entity.getLinkedInvoices().size() > 0) {
			Auditable auditable = new Auditable();
			auditable.setCreator(getCurrentUser());
			auditable.setCreated(new Date());

			for (Invoice i : entity.getLinkedInvoices()) {
				i = invoiceService.findById(i.getId());
				for (InvoiceAgregate invoiceAgregate : i.getInvoiceAgregates()) {
					if (invoiceAgregate instanceof CategoryInvoiceAgregate) {
						CategoryInvoiceAgregate categoryInvoiceAgregate = (CategoryInvoiceAgregate) invoiceAgregate;
						categoryInvoiceAgregate = (CategoryInvoiceAgregate) invoiceAgregateService.refreshOrRetrieve(categoryInvoiceAgregate);

						CategoryInvoiceAgregate newCategoryInvoiceAgregate = new CategoryInvoiceAgregate(categoryInvoiceAgregate);
						// newCategoryInvoiceAgregate.setInvoice(entity);
						newCategoryInvoiceAgregate.setAuditable(auditable);
						newCategoryInvoiceAgregate.setDescription(categoryInvoiceAgregate.getDescription());
						newCategoryInvoiceAgregate.setBillingRun(null);
						newCategoryInvoiceAgregate.setUserAccount(userAccount);
						newCategoryInvoiceAgregate.setBillingAccount(categoryInvoiceAgregate.getBillingAccount());

						if (!tempCategoryInvoiceAggregates.contains(newCategoryInvoiceAgregate)) {
							tempCategoryInvoiceAggregates.add(newCategoryInvoiceAgregate);
						}

						if (categoryInvoiceAgregate.getSubCategoryInvoiceAgregates() != null) {
							for (SubCategoryInvoiceAgregate subCategoryInvoiceAgregate : categoryInvoiceAgregate.getSubCategoryInvoiceAgregates()) {

								SubCategoryInvoiceAgregate newSubCategoryInvoiceAgregate = new SubCategoryInvoiceAgregate(subCategoryInvoiceAgregate);
								// newSubCategoryInvoiceAgregate.setInvoice(entity);
								newSubCategoryInvoiceAgregate.setAuditable(auditable);
								newSubCategoryInvoiceAgregate.setCategoryInvoiceAgregate(newCategoryInvoiceAgregate);
								newSubCategoryInvoiceAgregate.setDescription(subCategoryInvoiceAgregate.getDescription());
								newSubCategoryInvoiceAgregate.setBillingRun(null);
								newSubCategoryInvoiceAgregate.setWallet(userAccount.getWallet());
								newSubCategoryInvoiceAgregate.setBillingAccount(billingAccount);
								newSubCategoryInvoiceAgregate.setUserAccount(userAccount);

								if (!newCategoryInvoiceAgregate.getSubCategoryInvoiceAgregates().contains(newSubCategoryInvoiceAgregate)) {
									newCategoryInvoiceAgregate.addSubCategoryInvoiceAggregate(newSubCategoryInvoiceAgregate);

									if (subCategoryInvoiceAgregate.getSubCategoryTaxes() != null) {
										for (Tax tax : subCategoryInvoiceAgregate.getSubCategoryTaxes()) {
											newSubCategoryInvoiceAgregate.addSubCategoryTax(tax);
										}
									}
								}
							}
						}
					}
				}
			}
		} else {
			messages.info(new BundleKey("messages", "message.invoice.addAggregate.linked.null"));
		}

		refreshTotal();
	}

	public List<CategoryInvoiceAgregate> getCategoryInvoiceAggregates() {
		List<CategoryInvoiceAgregate> result = new ArrayList<>();
		if (tempCategoryInvoiceAggregates != null) {
			for (CategoryInvoiceAgregate cat : tempCategoryInvoiceAggregates) {
				if (!result.contains(cat)) {
					result.add(cat);
				}
			}
		}

		return result;
	}

	public List<SubCategoryInvoiceAgregate> getSubCategoryInvoiceAggregates(CategoryInvoiceAgregate cat) {
		if (cat == null)
			return null;
		List<SubCategoryInvoiceAgregate> result = new ArrayList<>();
		if (cat.getSubCategoryInvoiceAgregates() == null)
			return result;

		for (SubCategoryInvoiceAgregate subCat : cat.getSubCategoryInvoiceAgregates()) {
			result.add(subCat);
		}

		return result;
	}

	public void deleteLinkedInvoiceCategory() throws BusinessException {
		tempCategoryInvoiceAggregates.remove(selectedCategoryInvoiceAgregate);
		refreshTotal();
	}

	public void deleteLinkedInvoiceSubCategory() throws BusinessException {
		for (CategoryInvoiceAgregate cat : tempCategoryInvoiceAggregates) {
			if (cat.equals(selectedSubCategoryInvoiceAgregate.getCategoryInvoiceAgregate())) {
				cat.getSubCategoryInvoiceAgregates().remove(selectedSubCategoryInvoiceAgregate);
			}
		}
		refreshTotal();
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
		for (CategoryInvoiceAgregate cat : tempCategoryInvoiceAggregates) {
			for (SubCategoryInvoiceAgregate subCat : cat.getSubCategoryInvoiceAgregates()) {
				if (subCat.getAmountWithoutTax() != null) {
					total = total.add(subCat.getAmountWithoutTax());
				}
			}
		}

		return total;
	}

	public BigDecimal computeTotalAmountTax() {
		BigDecimal total = new BigDecimal(0);
		for (CategoryInvoiceAgregate cat : tempCategoryInvoiceAggregates) {
			for (SubCategoryInvoiceAgregate subCat : cat.getSubCategoryInvoiceAgregates()) {
				if (subCat.getAmountTax() != null) {
					total = total.add(subCat.getAmountTax());
				}
			}
		}

		return total;
	}

	public BigDecimal computeTotalAmountWithTax() {
		BigDecimal total = new BigDecimal(0);
		for (CategoryInvoiceAgregate cat : tempCategoryInvoiceAggregates) {
			for (SubCategoryInvoiceAgregate subCat : cat.getSubCategoryInvoiceAgregates()) {
				if (subCat.getAmountWithTax() != null) {
					total = total.add(subCat.getAmountWithTax());
				}
			}
		}

		return total;
	}

	public String getNetToPay() throws BusinessException {
		if (entity.getNetToPay() == null) {
			return "";
		}

		return entity.getNetToPay().setScale(2, RoundingMode.HALF_UP).toString();
	}

	public void handleDateSelect(SelectEvent event) {
	}

	public void handleTypeChange(final ItemSelectEvent event) {
		log.debug("valueChange");
	}

	public List<SelectItem> getInvoiceCatSubCats() {
		if (invoiceCategoriesGUI != null) {
			return invoiceCategoriesGUI;
		}

		invoiceCategoriesGUI = new ArrayList<>();

		List<InvoiceCategory> invoiceCategories = invoiceCategoryService.list();
		for (InvoiceCategory ic : invoiceCategories) {
			SelectItemGroup g1 = new SelectItemGroup(ic.getCode());
			List<SelectItem> subCats = new ArrayList<>();
			for (InvoiceSubCategory is : ic.getInvoiceSubCategories()) {
				subCats.add(new SelectItem(is, is.getCode()));
			}
			g1.setSelectItems(subCats.toArray(new SelectItem[subCats.size()]));
			invoiceCategoriesGUI.add(g1);
		}

		return invoiceCategoriesGUI;
	}

	public void addAggregatedLine() throws BusinessException {
		if (entity.getBillingAccount() == null || entity.getBillingAccount().isTransient()) {
			messages.error("BillingAccount is required.");
			return;
		}

		if (entity.getDueDate() == null) {
			messages.error("Due date is required.");
			return;
		}

		BillingAccount billingAccount = billingAccountService.refreshOrRetrieve(entity.getBillingAccount());
		List<UserAccount> userAccounts = billingAccount.getUsersAccounts();
		if (userAccounts == null || userAccounts.isEmpty()) {
			messages.error("BillingAccount with code=" + entity.getBillingAccount().getCode() + " has no userAccount.");
			return;
		}
		// TODO : userAccount on dto ?
		UserAccount userAccount = userAccounts.get(0);

		selectedInvoiceSubCategory = invoiceSubCategoryService.refreshOrRetrieve(selectedInvoiceSubCategory);
		InvoiceCategory ic = (InvoiceCategory) selectedInvoiceSubCategory.getInvoiceCategory();

		Auditable auditable = new Auditable();
		auditable.setCreator(getCurrentUser());
		auditable.setCreated(new Date());

		SubCategoryInvoiceAgregate newSubCategoryInvoiceAgregate = new SubCategoryInvoiceAgregate();
		newSubCategoryInvoiceAgregate.setInvoiceSubCategory(selectedInvoiceSubCategory);
		// newSubCategoryInvoiceAgregate.setInvoice(entity);
		newSubCategoryInvoiceAgregate.setAuditable(auditable);
		newSubCategoryInvoiceAgregate.setAmountWithoutTax(amountWithoutTax);
		newSubCategoryInvoiceAgregate.setInvoiceSubCategory(selectedInvoiceSubCategory);
		newSubCategoryInvoiceAgregate.setDescription(description);
		newSubCategoryInvoiceAgregate.setBillingRun(null);
		newSubCategoryInvoiceAgregate.setWallet(userAccount.getWallet());
		newSubCategoryInvoiceAgregate.setBillingAccount(billingAccount);
		newSubCategoryInvoiceAgregate.setUserAccount(userAccount);
		newSubCategoryInvoiceAgregate.setQuantity(new BigDecimal(1));

		Tax currentTax = null;
		List<Tax> taxes = new ArrayList<Tax>();
		for (InvoiceSubcategoryCountry invoicesubcatCountry : selectedInvoiceSubCategory.getInvoiceSubcategoryCountries()) {
			if (invoicesubcatCountry.getTradingCountry().getCountryCode().equalsIgnoreCase(billingAccount.getTradingCountry().getCountryCode())
					&& invoiceSubCategoryService.matchInvoicesubcatCountryExpression(invoicesubcatCountry.getFilterEL(), billingAccount, entity)) {
				if (!taxes.contains(invoicesubcatCountry.getTax())) {
					taxes.add(invoicesubcatCountry.getTax());
				}

				if (currentTax == null) {
					currentTax = invoicesubcatCountry.getTax();
				}
			}
		}
		if (currentTax == null) {
			messages.error("Cant find tax for InvoiceSubCategorywith code=" + selectedInvoiceSubCategory.getCode() + ".");
		}

		newSubCategoryInvoiceAgregate.setAmountWithTax(getAmountWithTax(currentTax, newSubCategoryInvoiceAgregate.getAmountWithoutTax()));
		newSubCategoryInvoiceAgregate.setAmountTax(getAmountTax(newSubCategoryInvoiceAgregate.getAmountWithTax(), newSubCategoryInvoiceAgregate.getAmountWithoutTax()));

		// find category
		boolean found = false;
		CategoryInvoiceAgregate foundCategoryInvoiceAgregate = null;
		for (CategoryInvoiceAgregate ia : tempCategoryInvoiceAggregates) {
			foundCategoryInvoiceAgregate = (CategoryInvoiceAgregate) ia;
			if (foundCategoryInvoiceAgregate.getInvoiceCategory().getCode().equals(ic.getCode())) {
				found = true;
				break;
			}
		}

		CategoryInvoiceAgregate newCategoryInvoiceAgregate = null;
		if (!found) {
			// create invoice category
			newCategoryInvoiceAgregate = new CategoryInvoiceAgregate();
			newCategoryInvoiceAgregate.setInvoiceCategory(ic);
			// newCategoryInvoiceAgregate.setInvoice(entity);
			newCategoryInvoiceAgregate.setAmountWithoutTax(newSubCategoryInvoiceAgregate.getAmountWithoutTax());
			newCategoryInvoiceAgregate.setAuditable(auditable);
			newCategoryInvoiceAgregate.setDescription("");
			newCategoryInvoiceAgregate.setAccountingCode(selectedInvoiceSubCategory.getAccountingCode());
			newCategoryInvoiceAgregate.setBillingRun(null);
			newCategoryInvoiceAgregate.setUserAccount(userAccount);
			newCategoryInvoiceAgregate.setBillingAccount(billingAccount);
			newCategoryInvoiceAgregate.addSubCategoryInvoiceAggregate(newSubCategoryInvoiceAgregate);

			tempCategoryInvoiceAggregates.add(newCategoryInvoiceAgregate);
		} else {
			// update total
			foundCategoryInvoiceAgregate.addAmountWithoutTax(newSubCategoryInvoiceAgregate.getAmountWithoutTax());
			foundCategoryInvoiceAgregate.addSubCategoryInvoiceAggregate(newSubCategoryInvoiceAgregate);
		}

		newSubCategoryInvoiceAgregate.setCategoryInvoiceAgregate(newCategoryInvoiceAgregate);

		refreshTotal();

		selectedInvoiceSubCategory = new InvoiceSubCategory();
	}

	@SuppressWarnings("unused")
	private void addInvoiceCategory(InvoiceCatSubCatModel invoiceCatSubCatModel) {
		InvoiceCategory ic = (InvoiceCategory) invoiceCatSubCatModel.getEntity();
		Auditable auditable = new Auditable();
		auditable.setCreator(getCurrentUser());
		auditable.setCreated(new Date());

		CategoryInvoiceAgregate newCategoryInvoiceAgregate = new CategoryInvoiceAgregate();
		newCategoryInvoiceAgregate.setInvoiceCategory(ic);
		// newCategoryInvoiceAgregate.setInvoice(entity);
		newCategoryInvoiceAgregate.setAuditable(auditable);
		// newCategoryInvoiceAgregate.setAmountWithTax(selectedInvoiceCatSubCatModel.getAmountWithTax());
		// newCategoryInvoiceAgregate.setAmountWithoutTax(selectedInvoiceCatSubCatModel.getAmountWithoutTax());
		// newCategoryInvoiceAgregate.setDescription(selectedInvoiceCatSubCatModel.getDescription());
		newCategoryInvoiceAgregate.setBillingRun(null);
		newCategoryInvoiceAgregate.setUserAccount(null);
		newCategoryInvoiceAgregate.setBillingAccount(null);

		tempCategoryInvoiceAggregates.add(newCategoryInvoiceAgregate);
	}

	private BigDecimal getAmountWithTax(Tax tax, BigDecimal amountWithoutTax) {
		Integer rounding = tax.getProvider().getRounding() == null ? 2 : tax.getProvider().getRounding();
		BigDecimal ttc = amountWithoutTax.add(amountWithoutTax.multiply(tax.getPercent()).divide(new BigDecimal(100), rounding, RoundingMode.HALF_UP));
		return ttc;
	}

	private BigDecimal getAmountTax(BigDecimal amountWithTax, BigDecimal amountWithoutTax) {
		return amountWithTax.subtract(amountWithoutTax);
	}

	@Override
	public String saveOrUpdate(boolean killConversation) throws BusinessException {
		// needed for invoice sequence no
		BillingAccount billingAccount = billingAccountService.refreshOrRetrieve(entity.getBillingAccount());
		entity.setBillingAccount(billingAccount);

		entity.setInvoiceDate(new Date());
		entity.setInvoiceNumber(invoiceService.getInvoiceNumber(entity, getCurrentUser()));

		// String result = super.saveOrUpdate(killConversation);
		invoiceService.createNewTx(entity, getCurrentUser());

		invoiceService.commit();

		refreshTotal(true);

		try {
			invoiceService.generateXmlAndPdfInvoice(entity, getCurrentUser());
		} catch (Exception e) {
			messages.error("Error generating xml / pdf invoice=" + e.getMessage());
		}

		return getListViewName();
	}

	public void refreshTotal() throws BusinessException {
		refreshTotal(false);
	}

	public void refreshTotal(boolean create) throws BusinessException {
		BillingAccount billingAccount = billingAccountService.refreshOrRetrieve(entity.getBillingAccount());
		List<UserAccount> userAccounts = billingAccount.getUsersAccounts();
		if (userAccounts == null || userAccounts.isEmpty()) {
			messages.error("BillingAccount with code=" + entity.getBillingAccount().getCode() + " has no userAccount.");
			return;
		}
		// TODO : userAccount on dto ?
		UserAccount userAccount = userAccounts.get(0);

		Map<Long, TaxInvoiceAgregate> taxInvoiceAgregateMap = new HashMap<Long, TaxInvoiceAgregate>();

		Auditable auditable = new Auditable();
		auditable.setCreator(getCurrentUser());
		auditable.setCreated(new Date());

		BigDecimal invoiceAmountWithoutTax = BigDecimal.ZERO;
		BigDecimal invoiceAmountTax = BigDecimal.ZERO;
		BigDecimal invoiceAmountWithTax = BigDecimal.ZERO;
		for (CategoryInvoiceAgregate cat : tempCategoryInvoiceAggregates) {
			if (create) {
				cat.setInvoice(entity);
				invoiceAgregateService.create(cat, getCurrentUser());
			}

			BigDecimal catAmountWithoutTax = BigDecimal.ZERO;
			BigDecimal catAmountTax = BigDecimal.ZERO;
			BigDecimal catAmountWithTax = BigDecimal.ZERO;

			for (SubCategoryInvoiceAgregate subCat : cat.getSubCategoryInvoiceAgregates()) {
				if (create) {
					subCat.setInvoice(entity);
					invoiceAgregateService.create(subCat, getCurrentUser());
				}
				catAmountWithoutTax = catAmountWithoutTax.add(subCat.getAmountWithoutTax());
				catAmountTax = catAmountTax.add(subCat.getAmountTax());
				catAmountWithTax = catAmountWithTax.add(subCat.getAmountWithTax());

				BigDecimal subCatAmountWithTax = BigDecimal.ZERO;
				BigDecimal subCatAmountTax = BigDecimal.ZERO;

				Tax currentTax = null;
				List<Tax> taxes = new ArrayList<Tax>();
				InvoiceSubCategory invoiceSubCategory = invoiceSubCategoryService.findByCode(subCat.getInvoiceSubCategory().getCode(), getCurrentProvider());
				for (InvoiceSubcategoryCountry invoicesubcatCountry : invoiceSubCategory.getInvoiceSubcategoryCountries()) {
					if (invoicesubcatCountry.getTradingCountry().getCountryCode().equalsIgnoreCase(billingAccount.getTradingCountry().getCountryCode())
							&& invoiceSubCategoryService.matchInvoicesubcatCountryExpression(invoicesubcatCountry.getFilterEL(), billingAccount, entity)) {
						if (!taxes.contains(invoicesubcatCountry.getTax())) {
							taxes.add(invoicesubcatCountry.getTax());
						}
						if (currentTax == null) {
							currentTax = invoicesubcatCountry.getTax();
						}
					}
				}

				subCat.setSubCategoryTaxes(new HashSet<Tax>(Arrays.asList(currentTax)));

				subCatAmountWithTax = subCatAmountWithTax.add(getAmountWithTax(currentTax, subCat.getAmountWithoutTax()));
				subCatAmountTax = getAmountTax(subCatAmountWithTax, subCat.getAmountWithoutTax());

				subCat.setAmountWithTax(subCatAmountWithTax);
				subCat.setAmountTax(subCatAmountTax);

				for (Tax tax : taxes) {
					TaxInvoiceAgregate invoiceAgregateTax = null;
					Long taxId = tax.getId();

					if (taxInvoiceAgregateMap.containsKey(taxId)) {
						invoiceAgregateTax = taxInvoiceAgregateMap.get(taxId);
					} else {
						invoiceAgregateTax = new TaxInvoiceAgregate();
						if (create) {
							invoiceAgregateTax.setInvoice(entity);
						}
						invoiceAgregateTax.setBillingRun(null);
						invoiceAgregateTax.setTax(tax);
						invoiceAgregateTax.setAccountingCode(tax.getAccountingCode());
						invoiceAgregateTax.setTaxPercent(tax.getPercent());
						invoiceAgregateTax.setUserAccount(userAccount);
						invoiceAgregateTax.setAmountWithoutTax(BigDecimal.ZERO);
						invoiceAgregateTax.setAmountTax(BigDecimal.ZERO);
						invoiceAgregateTax.setBillingAccount(billingAccount);
						invoiceAgregateTax.setUserAccount(userAccount);
						invoiceAgregateTax.setAuditable(auditable);
					}

					invoiceAgregateTax.setAmountWithoutTax(invoiceAgregateTax.getAmountWithoutTax().add(subCat.getAmountWithoutTax()));
					//invoiceAgregateTax.setAmountWithTax(invoiceAgregateTax.getAmountWithTax().add(subCat.getAmountWithTax()));
					invoiceAgregateTax.setAmountTax(invoiceAgregateTax.getAmountTax().add(subCat.getAmountTax()));

					taxInvoiceAgregateMap.put(taxId, invoiceAgregateTax);
				}
			}

			cat.setAmountWithoutTax(catAmountWithoutTax);
			cat.setAmountTax(catAmountTax);
			cat.setAmountWithTax(catAmountWithTax);

			invoiceAmountWithoutTax = invoiceAmountWithoutTax.add(cat.getAmountWithoutTax());
			invoiceAmountTax = invoiceAmountTax.add(cat.getAmountTax());
			invoiceAmountWithTax = invoiceAmountWithTax.add(cat.getAmountWithTax());
		}

		if (create) {
			for (Entry<Long, TaxInvoiceAgregate> entry : taxInvoiceAgregateMap.entrySet()) {
				invoiceAgregateService.create(entry.getValue(), currentUser);
			}
		}

		entity.setAmountWithoutTax(invoiceAmountWithoutTax);
		entity.setAmountTax(invoiceAmountTax);
		entity.setAmountWithTax(invoiceAmountWithTax);

		BigDecimal netToPay = entity.getAmountWithTax();
		if (!getCurrentProvider().isEntreprise() && includeBalance) {
			BigDecimal balance = customerAccountService.customerAccountBalanceDue(null, entity.getBillingAccount().getCustomerAccount().getCode(), entity.getDueDate(),
					entity.getProvider());

			if (balance == null) {
				throw new BusinessException("account balance calculation failed");
			}
			netToPay = entity.getAmountWithTax().add(balance);
		}
		entity.setNetToPay(netToPay);

		if (create) {
			invoiceService.update(entity, getCurrentUser());
		}
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

	public List<CategoryInvoiceAgregate> getTempCategoryInvoiceAggregates() {
		return tempCategoryInvoiceAggregates;
	}

	public void setTempCategoryInvoiceAggregates(List<CategoryInvoiceAgregate> tempCategoryInvoiceAggregates) {
		this.tempCategoryInvoiceAggregates = tempCategoryInvoiceAggregates;
	}

	public List<TaxInvoiceAgregate> getTempTaxInvoiceAggregates() {
		return tempTaxInvoiceAggregates;
	}

	public void setTempTaxInvoiceAggregates(List<TaxInvoiceAgregate> tempTaxInvoiceAggregates) {
		this.tempTaxInvoiceAggregates = tempTaxInvoiceAggregates;
	}

	public List<SelectItem> getInvoiceCategoriesGUI() {
		return invoiceCategoriesGUI;
	}

	public void setInvoiceCategoriesGUI(List<SelectItem> invoiceCategoriesGUI) {
		this.invoiceCategoriesGUI = invoiceCategoriesGUI;
	}

	public InvoiceSubCategory getSelectedInvoiceSubCategory() {
		return selectedInvoiceSubCategory;
	}

	public void setSelectedInvoiceSubCategory(InvoiceSubCategory selectedInvoiceSubCategory) {
		this.selectedInvoiceSubCategory = selectedInvoiceSubCategory;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public BigDecimal getAmountWithoutTax() {
		return amountWithoutTax;
	}

	public void setAmountWithoutTax(BigDecimal amountWithoutTax) {
		this.amountWithoutTax = amountWithoutTax;
	}

	public boolean isIncludeBalance() {
		return includeBalance;
	}

	public void setIncludeBalance(boolean includeBalance) {
		this.includeBalance = includeBalance;
	}

}
