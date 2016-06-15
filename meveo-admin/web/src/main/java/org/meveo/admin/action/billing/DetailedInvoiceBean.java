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

import java.io.File;
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

import org.meveo.admin.action.BaseBean;
import org.meveo.admin.action.CustomFieldBean;
import org.meveo.admin.exception.BusinessException;
import org.meveo.admin.job.PDFParametersConstruction;
import org.meveo.model.Auditable;
import org.meveo.model.billing.BillingAccount;
import org.meveo.model.billing.CategoryInvoiceAgregate;
import org.meveo.model.billing.Invoice;
import org.meveo.model.billing.InvoiceCategory;
import org.meveo.model.billing.InvoiceSubCategory;
import org.meveo.model.billing.InvoiceSubcategoryCountry;
import org.meveo.model.billing.RatedTransaction;
import org.meveo.model.billing.RatedTransactionStatusEnum;
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
import org.meveo.service.billing.impl.WalletOperationService;
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
public class DetailedInvoiceBean extends CustomFieldBean<Invoice> {

	private static final long serialVersionUID = 1L;
	
	@Inject
	private PDFParametersConstruction pDFParametersConstruction;

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
	WalletOperationService walletOperationService;
	 
	
	@Inject
	private InvoiceSubCategoryService invoiceSubCategoryService;
	
	@Inject
	private InvoiceCategoryService invoiceCategoryService; 
	
	

	@Inject
	InvoiceTypeService invoiceTypeService;

	@Inject
	XMLInvoiceCreator xmlInvoiceCreator; 

	private long billingAccountId;
	private Invoice invoiceToAdd;
	private Invoice selectedInvoice; 
	private SubCategoryInvoiceAgregate selectedSubCategoryInvoiceAgregate;
	private InvoiceSubCategory selectedInvoiceSubCategory;  
	private List<SubCategoryInvoiceAgregate> tempSubCategoryInvoiceAggregates = new ArrayList<>();  
	private BigDecimal quantity;
	private BigDecimal amountWithoutTax;
	private BigDecimal unitAmountWithoutTax; 
	private RatedTransaction selectedRatedTransaction;
	private List<SelectItem> invoiceCategoriesGUI;
	private List<CategoryInvoiceAgregate> tempCategoryInvoiceAggregates = new ArrayList<>(); 
	private String description;
	private boolean includeBalance;

	/**
	 * Constructor. Invokes super constructor and provides class type of this
	 * bean for {@link BaseBean}.
	 */
	public DetailedInvoiceBean() {
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
			entity.getLinkedInvoices().add(invoiceToAdd);
		}
	}

	public void deleteLinkedInvoice() throws BusinessException {
		entity.getLinkedInvoices().remove(selectedInvoice);
		selectedInvoice = null;
		refreshTotal();
	}

	@SuppressWarnings({ "rawtypes", "unchecked" })
	public void deleteAllLinkedInvoice() throws BusinessException {
		entity.setLinkedInvoices(new HashSet());
		selectedInvoice = null;
		refreshTotal();
	}
 

	public List<SubCategoryInvoiceAgregate> getSubCategoryInvoiceAggregates() {
		List<SubCategoryInvoiceAgregate> result = new ArrayList<>();
		if (tempSubCategoryInvoiceAggregates != null) {
			for (SubCategoryInvoiceAgregate subCat : tempSubCategoryInvoiceAggregates) {
				if (!result.contains(subCat)) {
					result.add(subCat);
				}
			}
		}

		return result;
	}

	public List<RatedTransaction> getRatedTransactions(SubCategoryInvoiceAgregate subCat) {
		if (subCat == null){
			return null;
		}
		return subCat.getRatedtransactions();
	}

	public void deleteLinkedInvoiceCategory() {
		tempSubCategoryInvoiceAggregates.remove(selectedSubCategoryInvoiceAgregate);
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

	public void addDetailInvoiceLine() throws BusinessException {
		addDetailedInvoiceLines(selectedInvoiceSubCategory);
	}
	private void addDetailedInvoiceLines(InvoiceSubCategory selectInvoiceSubCat) throws BusinessException {
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

		selectInvoiceSubCat = invoiceSubCategoryService.refreshOrRetrieve(selectInvoiceSubCat);
		InvoiceCategory invoiceCategory = (InvoiceCategory) selectInvoiceSubCat.getInvoiceCategory();

		Auditable auditable = new Auditable();
		auditable.setCreator(getCurrentUser());
		auditable.setCreated(new Date());

		
		SubCategoryInvoiceAgregate newSubCategoryInvoiceAgregate = new SubCategoryInvoiceAgregate();
		newSubCategoryInvoiceAgregate.setInvoiceSubCategory(selectInvoiceSubCat);
		newSubCategoryInvoiceAgregate.setInvoice(entity);
		newSubCategoryInvoiceAgregate.setAuditable(auditable);
		newSubCategoryInvoiceAgregate.setAmountWithoutTax(amountWithoutTax);
		newSubCategoryInvoiceAgregate.setInvoiceSubCategory(selectInvoiceSubCat);
		newSubCategoryInvoiceAgregate.setDescription(description);
		newSubCategoryInvoiceAgregate.setBillingRun(null);
		newSubCategoryInvoiceAgregate.setWallet(userAccount.getWallet());
		newSubCategoryInvoiceAgregate.setBillingAccount(billingAccount);
		newSubCategoryInvoiceAgregate.setUserAccount(userAccount);

		Tax currentTax = null;
		List<Tax> taxes = new ArrayList<Tax>();
		for (InvoiceSubcategoryCountry invoicesubcatCountry : selectInvoiceSubCat.getInvoiceSubcategoryCountries()) {
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
			messages.error("Cant find tax for InvoiceSubCategorywith code=" + selectInvoiceSubCat.getCode() + ".");
		}

		newSubCategoryInvoiceAgregate.setAmountWithTax(getAmountWithTax(currentTax, newSubCategoryInvoiceAgregate.getAmountWithoutTax()));
		newSubCategoryInvoiceAgregate.setAmountTax(getAmountTax(newSubCategoryInvoiceAgregate.getAmountWithTax(), newSubCategoryInvoiceAgregate.getAmountWithoutTax()));

		// find category
		boolean found = false;
		InvoiceSubCategory invSubCategory=null;
		CategoryInvoiceAgregate foundCategoryInvoiceAgregate = null; 
		for (SubCategoryInvoiceAgregate ia : tempSubCategoryInvoiceAggregates) { 
			invSubCategory=invoiceSubCategoryService.refreshOrRetrieve(ia.getInvoiceSubCategory());
			if (invSubCategory.getInvoiceCategory().getCode().equals(invoiceCategory.getCode())) {
				foundCategoryInvoiceAgregate = (CategoryInvoiceAgregate)ia.getCategoryInvoiceAgregate();
				found = true;
				break;
			}
		}

		CategoryInvoiceAgregate newCategoryInvoiceAgregate = null;
		if (!found) {
			// create invoice category
			newCategoryInvoiceAgregate = new CategoryInvoiceAgregate();
			newCategoryInvoiceAgregate.setInvoiceCategory(invoiceCategory);
			newCategoryInvoiceAgregate.setInvoice(entity);
			newCategoryInvoiceAgregate.setAmountWithoutTax(newSubCategoryInvoiceAgregate.getAmountWithoutTax());
			newCategoryInvoiceAgregate.setAuditable(auditable);
			newCategoryInvoiceAgregate.setDescription("");
			newCategoryInvoiceAgregate.setAccountingCode(selectInvoiceSubCat.getAccountingCode());
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
			
		
			BigDecimal amountWithoutTax = unitAmountWithoutTax.multiply(quantity);
			BigDecimal amountWithTax = getAmountWithTax(currentTax, amountWithoutTax);
			BigDecimal amountTax = getAmountTax(amountWithTax, amountWithoutTax); 
			//create ratedTransaction
			amountWithoutTax=quantity.multiply(unitAmountWithoutTax);
			RatedTransaction ratedTransaction =new RatedTransaction(); 
			
			ratedTransaction.setUnitAmountWithoutTax(unitAmountWithoutTax);
			ratedTransaction.setQuantity(quantity); 
			ratedTransaction.setAmountWithoutTax(amountWithoutTax);
			ratedTransaction.setAmountTax(amountTax);
			ratedTransaction.setAmountWithTax(amountWithTax);
			ratedTransaction.setUnitAmountWithoutTax(amountWithoutTax);
			ratedTransaction.setUnitAmountTax(amountTax);
			ratedTransaction.setUnitAmountWithTax(amountWithTax);
			ratedTransaction.setDescription(description);
			ratedTransaction.setCode(description);
			ratedTransaction.setBillingAccount(billingAccount);
			ratedTransaction.setProvider(billingAccount.getProvider());
			ratedTransaction.setStatus(RatedTransactionStatusEnum.BILLED);
			ratedTransaction.setInvoiceSubCategory(selectInvoiceSubCat);
			ratedTransaction.setInvoice(entity);  
			boolean subCatFound =false;
			for (SubCategoryInvoiceAgregate subCat : tempSubCategoryInvoiceAggregates) { 
				InvoiceSubCategory invSub=invoiceSubCategoryService.refreshOrRetrieve(subCat.getInvoiceSubCategory());
				 if(subCat.getInvoiceSubCategory().equals(selectedInvoiceSubCategory) 
				    &&selectedInvoiceSubCategory.getInvoiceCategory().equals(invSub.getInvoiceCategory())){
					 subCat.getRatedtransactions().add(ratedTransaction);
					 subCatFound=true;
				 }
			}
			if(!subCatFound){
				newSubCategoryInvoiceAgregate.getRatedtransactions().add(ratedTransaction); 
				tempSubCategoryInvoiceAggregates.add(newSubCategoryInvoiceAgregate);
			}
			refreshTotal();
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
			BigDecimal catAmountWithoutTax = BigDecimal.ZERO;
			BigDecimal catAmountTax = BigDecimal.ZERO;
			BigDecimal catAmountWithTax = BigDecimal.ZERO;

			if (create) {
				invoiceAgregateService.create(cat, getCurrentUser());
			}
			for (SubCategoryInvoiceAgregate subCat : cat.getSubCategoryInvoiceAgregates()) {
				if (create) {
					invoiceAgregateService.create(subCat, getCurrentUser());
				}
				catAmountWithoutTax = catAmountWithoutTax.add(subCat.getAmountWithoutTax());
				catAmountTax = catAmountTax.add(subCat.getAmountTax());
				catAmountWithTax = catAmountWithTax.add(subCat.getAmountWithTax());

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

				subCat.setTaxPercent(currentTax.getPercent());
				subCat.setSubCategoryTaxes(new HashSet<Tax>(Arrays.asList(currentTax)));

				for (Tax tax : taxes) {
					TaxInvoiceAgregate invoiceAgregateTax = null;
					Long taxId = tax.getId();

					if (taxInvoiceAgregateMap.containsKey(taxId)) {
						invoiceAgregateTax = taxInvoiceAgregateMap.get(taxId);
					} else {
						invoiceAgregateTax = new TaxInvoiceAgregate();
						invoiceAgregateTax.setInvoice(entity);
						invoiceAgregateTax.setBillingRun(null);
						invoiceAgregateTax.setTax(tax);
						invoiceAgregateTax.setAccountingCode(tax.getAccountingCode());
						invoiceAgregateTax.setTaxPercent(tax.getPercent());
						invoiceAgregateTax.setUserAccount(userAccount);
						invoiceAgregateTax.setAmountWithoutTax(BigDecimal.ZERO);
						invoiceAgregateTax.setAmountWithTax(BigDecimal.ZERO);
						invoiceAgregateTax.setAmountTax(BigDecimal.ZERO);
						invoiceAgregateTax.setBillingAccount(billingAccount);
						invoiceAgregateTax.setUserAccount(userAccount);
						invoiceAgregateTax.setAuditable(auditable);
					}

					invoiceAgregateTax.setAmountWithoutTax(invoiceAgregateTax.getAmountWithoutTax().add(subCat.getAmountWithoutTax()));
					invoiceAgregateTax.setAmountTax(invoiceAgregateTax.getAmountTax().add(subCat.getAmountTax()));
					invoiceAgregateTax.setAmountWithTax(invoiceAgregateTax.getAmountWithTax().add(subCat.getAmountWithTax()));

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

	@Override
	public String saveOrUpdate(boolean killConversation) throws BusinessException {
		
		BillingAccount billingAccount = billingAccountService.refreshOrRetrieve(entity.getBillingAccount());
		entity.setBillingAccount(billingAccount);
		entity.setInvoiceDate(new Date());
		if(entity.getInvoiceType().getCode().equalsIgnoreCase("ADJ")){
		entity.setAdjustedInvoice(entity);
		}
		entity.setInvoiceNumber(invoiceService.getInvoiceNumber(entity, getCurrentUser())); 
		String result = super.saveOrUpdate(killConversation);
		invoiceService.commit();
	 //create xml invoice adjustment
		String brPath = invoiceService.getBillingRunPath(entity.getBillingRun(), entity.getAuditable().getCreated(),currentUser.getProvider().getCode());
		File billingRundir = new File(brPath);		
		xmlInvoiceCreator.createXMLInvoiceAdjustment(entity.getId(), billingRundir);
		// create pdf
        Map<String, Object> parameters = pDFParametersConstruction.constructParameters(entity.getId(), currentUser, currentUser.getProvider());
		try {
			invoiceService.produceInvoiceAdjustmentPdf(parameters, currentUser);
		} catch (Exception e) {
			throw new BusinessException("Failed to generate pdf!");
		}
		return result;
	}

	
	public void deleteRatedTransactionLine() {
		for (SubCategoryInvoiceAgregate subcat : tempSubCategoryInvoiceAggregates) { 
					subcat.getRatedtransactions().remove(selectedRatedTransaction);
					break;
				}
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



	public SubCategoryInvoiceAgregate getSelectedSubCategoryInvoiceAgregate() {
		return selectedSubCategoryInvoiceAgregate;
	}

	public void setSelectedSubCategoryInvoiceAgregate(SubCategoryInvoiceAgregate selectedSubCategoryInvoiceAgregate) {
		this.selectedSubCategoryInvoiceAgregate = selectedSubCategoryInvoiceAgregate;
	}
 

	public BigDecimal getQuantity() {
		return quantity;
	}

	public void setQuantity(BigDecimal quantity) {
		this.quantity = quantity;
	}

	private BigDecimal getAmountWithTax(Tax tax, BigDecimal amountWithoutTax) {
		Integer rounding = tax.getProvider().getRounding() == null ? 2 : tax.getProvider().getRounding();
		BigDecimal ttc = amountWithoutTax.add(amountWithoutTax.multiply(tax.getPercent()).divide(new BigDecimal(100), rounding, RoundingMode.HALF_UP));
		return ttc;
	}
	
	private BigDecimal getAmountTax(BigDecimal amountWithTax, BigDecimal amountWithoutTax) {
		return amountWithTax.subtract(amountWithoutTax);
	}
	
	

	public void setAmountWithoutTax(BigDecimal amountWithoutTax) {
		this.amountWithoutTax = amountWithoutTax;
	}

	public List<SubCategoryInvoiceAgregate> getTempSubCategoryInvoiceAggregates() {
		return tempSubCategoryInvoiceAggregates;
	}

	public void setTempSubCategoryInvoiceAggregates(
			List<SubCategoryInvoiceAgregate> tempSubCategoryInvoiceAggregates) {
		this.tempSubCategoryInvoiceAggregates = tempSubCategoryInvoiceAggregates;
	}

	public BigDecimal getUnitAmountWithoutTax() {
		return unitAmountWithoutTax;
	}

	public void setUnitAmountWithoutTax(BigDecimal unitAmountWithoutTax) {
		this.unitAmountWithoutTax = unitAmountWithoutTax;
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

	public void setSelectedInvoiceSubCategory(
			InvoiceSubCategory selectedInvoiceSubCategory) {
		this.selectedInvoiceSubCategory = selectedInvoiceSubCategory;
	}

	public RatedTransaction getSelectedRatedTransaction() {
		return selectedRatedTransaction;
	}

	public void setSelectedRatedTransaction(
			RatedTransaction selectedRatedTransaction) {
		this.selectedRatedTransaction = selectedRatedTransaction;
	}

	public BigDecimal getAmountWithoutTax() {
		return amountWithoutTax;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	

	
}
