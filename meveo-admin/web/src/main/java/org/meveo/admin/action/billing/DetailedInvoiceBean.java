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
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Map;

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
import org.meveo.model.catalog.ChargeTemplate;
import org.meveo.service.base.PersistenceService;
import org.meveo.service.base.local.IPersistenceService;
import org.meveo.service.billing.impl.BillingAccountService;
import org.meveo.service.billing.impl.InvoiceAgregateService;
import org.meveo.service.billing.impl.InvoiceService;
import org.meveo.service.billing.impl.InvoiceTypeService;
import org.meveo.service.billing.impl.RatedTransactionService;
import org.meveo.service.billing.impl.WalletOperationService;
import org.meveo.service.billing.impl.XMLInvoiceCreator;
import org.meveo.service.catalog.impl.ChargeTemplateService;
import org.meveo.service.catalog.impl.InvoiceCategoryService;
import org.meveo.service.catalog.impl.InvoiceSubCategoryService;
import org.meveo.service.payments.impl.CustomerAccountService;
import org.omnifaces.cdi.ViewScoped;
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
	ChargeTemplateService chargeTemplateService;
	
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
	private List<TaxInvoiceAgregate> tempTaxInvoiceAggregates = new ArrayList<>();
	private String description;

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

	public void deleteLinkedInvoice() {
		entity.getLinkedInvoices().remove(selectedInvoice);
		selectedInvoice = null;
	}

	@SuppressWarnings({ "rawtypes", "unchecked" })
	public void deleteAllLinkedInvoice() {
		entity.setLinkedInvoices(new HashSet());
		selectedInvoice = null;
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
		if (subCat == null)
			return null;
		List<RatedTransaction> result = new ArrayList<>();
		if (subCat.getRatedtransactions() == null)
			return result;

		for (RatedTransaction rt : subCat.getRatedtransactions()) {
			result.add(rt);
		}

		return result;
	}

	public void deleteLinkedInvoiceCategory() {
		tempSubCategoryInvoiceAggregates.remove(selectedSubCategoryInvoiceAgregate);
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

		if (netToPay == null) {
			return "";
		}

		return netToPay.setScale(2, RoundingMode.HALF_UP).toString();
	}

	public void handleDateSelect(SelectEvent event) {
	}

	 

	public void addDetailInvoiceLine() throws BusinessException {
		addInvoiceSubCatAndRatedTransaction(selectedInvoiceSubCategory);
	}
 
	 
	private void addInvoiceSubCatAndRatedTransaction(InvoiceSubCategory isc) throws BusinessException {
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

		isc = invoiceSubCategoryService.refreshOrRetrieve(isc);
		InvoiceCategory ic = (InvoiceCategory) isc.getInvoiceCategory();

		Auditable auditable = new Auditable();
		auditable.setCreator(getCurrentUser());
		auditable.setCreated(new Date());
		//create ratedTransaction
		RatedTransaction ratedTransaction =new RatedTransaction();  
		ratedTransaction.setUnitAmountWithoutTax(unitAmountWithoutTax);
		ratedTransaction.setAmountWithoutTax(amountWithoutTax);
		ratedTransaction.setQuantity(quantity); 
		ratedTransaction.setBillingAccount(billingAccount);
		ratedTransaction.setProvider(billingAccount.getProvider());
		ratedTransaction.setStatus(RatedTransactionStatusEnum.OPEN);
		ratedTransaction.setInvoiceSubCategory(isc);
		ratedTransaction.setInvoice(entity);
		
		boolean subCatfound = false; 
		for (SubCategoryInvoiceAgregate subCat : tempSubCategoryInvoiceAggregates) { 
			InvoiceSubCategory invSub=invoiceSubCategoryService.refreshOrRetrieve(subCat.getInvoiceSubCategory());
			 if(subCat.getInvoiceSubCategory().equals(selectedInvoiceSubCategory) 
			    &&selectedInvoiceSubCategory.getInvoiceCategory().equals(invSub.getInvoiceCategory())){
				 subCat.getRatedtransactions().add(ratedTransaction);
				 subCatfound=true;
				 break;
			 }
		}
        if(!subCatfound){
		SubCategoryInvoiceAgregate newSubCategoryInvoiceAgregate = new SubCategoryInvoiceAgregate();
		newSubCategoryInvoiceAgregate.setInvoiceSubCategory(isc);
		newSubCategoryInvoiceAgregate.setInvoice(entity);
		newSubCategoryInvoiceAgregate.setAuditable(auditable);
		newSubCategoryInvoiceAgregate.setAmountWithoutTax(amountWithoutTax);
		newSubCategoryInvoiceAgregate.setInvoiceSubCategory(isc); 
		newSubCategoryInvoiceAgregate.setBillingRun(null);
		newSubCategoryInvoiceAgregate.setWallet(userAccount.getWallet());
		newSubCategoryInvoiceAgregate.setBillingAccount(billingAccount);
		newSubCategoryInvoiceAgregate.setUserAccount(userAccount);
		newSubCategoryInvoiceAgregate.getRatedtransactions().add(ratedTransaction);
		tempSubCategoryInvoiceAggregates.add(newSubCategoryInvoiceAgregate);
       
		Tax currentTax = null;
		List<Tax> taxes = new ArrayList<Tax>();
		for (InvoiceSubcategoryCountry invoicesubcatCountry : isc.getInvoiceSubcategoryCountries()) {
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
			messages.error("Cant find tax for InvoiceSubCategorywith code=" + isc.getCode() + ".");
		}

		newSubCategoryInvoiceAgregate.setAmountWithTax(getAmountWithTax(currentTax, newSubCategoryInvoiceAgregate.getAmountWithoutTax()));
		newSubCategoryInvoiceAgregate.setAmountTax(getAmountTax(newSubCategoryInvoiceAgregate.getAmountWithTax(), newSubCategoryInvoiceAgregate.getAmountWithoutTax()));
        }
        }
	
	
	@Override
	public String saveOrUpdate(boolean killConversation) throws BusinessException {
		String result = super.saveOrUpdate(killConversation);
		BillingAccount billingAccount = billingAccountService.refreshOrRetrieve(entity.getBillingAccount());
		entity.setBillingAccount(billingAccount);
		entity.setInvoiceDate(new Date());
		entity.setInvoiceNumber(invoiceService.getInvoiceNumber(entity, getCurrentUser())); 
//		// create xml invoice adjustment
//				String brPath = invoiceService.getBillingRunPath(entity.getBillingRun(), entity.getAuditable().getCreated(),currentUser.getProvider().getCode());
//				File billingRundir = new File(brPath);		
//				xmlInvoiceCreator.createXMLInvoiceAdjustment(entity.getId(), billingRundir);
//				// create pdf
//		        Map<String, Object> parameters = pDFParametersConstruction.constructParameters(entity.getId(), currentUser, currentUser.getProvider());
//				try {
//					invoiceService.produceInvoiceAdjustmentPdf(parameters, currentUser);
//				} catch (Exception e) {
//					throw new BusinessException("Failed to generate pdf!");
//				}
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
