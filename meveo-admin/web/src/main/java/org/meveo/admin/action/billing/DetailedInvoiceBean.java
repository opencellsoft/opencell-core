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

import javax.faces.model.SelectItem;
import javax.faces.model.SelectItemGroup;
import javax.inject.Inject;
import javax.inject.Named;

import org.meveo.admin.action.BaseBean;
import org.meveo.admin.action.CustomFieldBean;
import org.meveo.admin.exception.BusinessException;
import org.meveo.model.Auditable;
import org.meveo.model.billing.BillingAccount;
import org.meveo.model.billing.CategoryInvoiceAgregate;
import org.meveo.model.billing.Invoice;
import org.meveo.model.billing.InvoiceAgregate;
import org.meveo.model.billing.RatedTransaction;
import org.meveo.model.billing.RatedTransactionStatusEnum;
import org.meveo.model.billing.SubCategoryInvoiceAgregate;
import org.meveo.model.billing.UserAccount;
import org.meveo.model.billing.WalletOperation;
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
	InvoiceTypeService invoiceTypeService;

	@Inject
	XMLInvoiceCreator xmlInvoiceCreator; 

	private long billingAccountId;
	private Invoice invoiceToAdd;
	private Invoice selectedInvoice; 
	private SubCategoryInvoiceAgregate selectedSubCategoryInvoiceAgregate;
	private String selectedWalletOperation;  
	private List<SubCategoryInvoiceAgregate> tempSubCategoryInvoiceAggregates = new ArrayList<>();  
	private BigDecimal quantity;
	private BigDecimal amountWithoutTax;
	private BigDecimal unitAmountWithoutTax; 
	private RatedTransaction selectedRatedTransaction;
	private List<SelectItem> invoiceCategoriesGUI;

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
		addInvoiceSubCategory(selectedWalletOperation);
	}
 
	private void addInvoiceSubCategory(String walletCode) throws BusinessException {
		if (entity.getBillingAccount() == null || entity.getBillingAccount().isTransient()) {
			messages.error("BillingAccount is required.");
			return;
		}

		if (entity.getDueDate() == null) {
			messages.error("Due date is required.");
			return;
		}   
		ChargeTemplate chrg =(ChargeTemplate) chargeTemplateService.findByCode(walletCode, getCurrentProvider()); 
		BillingAccount billingAccount = billingAccountService.refreshOrRetrieve(entity.getBillingAccount());
		List<UserAccount> userAccounts = billingAccount.getUsersAccounts();
		if (userAccounts == null || userAccounts.isEmpty()) {
			messages.error("BillingAccount with code=" + entity.getBillingAccount().getCode() + " has no userAccount.");
			return;
		}
		//create ratedTransaction
		RatedTransaction rated =new RatedTransaction();  
		rated.setUnitAmountWithoutTax(unitAmountWithoutTax);
		rated.setAmountWithoutTax(amountWithoutTax);
		rated.setQuantity(quantity); 
		rated.setBillingAccount(billingAccount);
		rated.setProvider(billingAccount.getProvider());
		rated.setStatus(RatedTransactionStatusEnum.OPEN);
		rated.setInvoiceSubCategory(chrg.getInvoiceSubCategory()); 
		// TODO : userAccount on dto ?
		UserAccount userAccount = userAccounts.get(0); 
		Auditable auditable = new Auditable();
		auditable.setCreator(getCurrentUser());
		auditable.setCreated(new Date());

		// find category
				boolean found = false;
				SubCategoryInvoiceAgregate foundSubCategoryInvoiceAgregate = null;
				for (SubCategoryInvoiceAgregate ia : tempSubCategoryInvoiceAggregates) {
					foundSubCategoryInvoiceAgregate = (SubCategoryInvoiceAgregate) ia;
					if (foundSubCategoryInvoiceAgregate.getInvoiceSubCategory().getCode().equals(chrg.getInvoiceSubCategory().getCode())) {
						foundSubCategoryInvoiceAgregate.getRatedtransactions().add(rated);
						found = true;
						break;
					}
				}
		if (!found) {
		SubCategoryInvoiceAgregate newSubCategoryInvoiceAgregate = new SubCategoryInvoiceAgregate(); 
		newSubCategoryInvoiceAgregate.setInvoice(entity);
		newSubCategoryInvoiceAgregate.setAuditable(auditable); 
		newSubCategoryInvoiceAgregate.setInvoiceSubCategory(chrg.getInvoiceSubCategory()); 
		newSubCategoryInvoiceAgregate.setBillingRun(null);
		newSubCategoryInvoiceAgregate.setWallet(userAccount.getWallet());
		newSubCategoryInvoiceAgregate.setBillingAccount(billingAccount);
		newSubCategoryInvoiceAgregate.setUserAccount(userAccount);
		newSubCategoryInvoiceAgregate.getRatedtransactions().add(rated);
		tempSubCategoryInvoiceAggregates.add(newSubCategoryInvoiceAgregate);
		}
 
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

		List<InvoiceAgregate> invoiceAgregates = invoiceAgregateService.list();
		List<SubCategoryInvoiceAgregate> subCatAgrs = new ArrayList<>(); 
		List<RatedTransaction> transactions= new ArrayList<>(); 
		for (InvoiceAgregate invAgr : invoiceAgregates) {
			System.out.println("invoice"+invAgr.getDescription());
			if(invAgr instanceof SubCategoryInvoiceAgregate){
				subCatAgrs.add((SubCategoryInvoiceAgregate) invAgr);	
			}	 
		}
		
		
		for( SubCategoryInvoiceAgregate subCat:subCatAgrs){ 
			transactions = ratedTransactionService.getRatedTransactions(subCat.getWallet(), subCat.getInvoice(),subCat.getInvoiceSubCategory());
			
			SelectItemGroup group1 = new SelectItemGroup(subCat.getInvoiceSubCategory().getInvoiceCategory().getCode());
			SelectItemGroup group11 = new SelectItemGroup(subCat.getInvoiceSubCategory().getCode());
			SelectItem group111 =null;
			
			if(transactions.size()>0){
				for(RatedTransaction rt:transactions){
					WalletOperation walletOperation=null;
					if(rt.getWalletOperationId()!=null){
						walletOperation = walletOperationService.findById(rt.getWalletOperationId());
						group111=new SelectItem(walletOperation.getCode());
						group11.setSelectItems(new SelectItem[]{group111});
						 group1.setSelectItems(new SelectItem[]{group11});
						  invoiceCategoriesGUI.add(group1);
						   
					}
				}
			}	  
			
		}
		
		 for(SelectItem s:invoiceCategoriesGUI){
	    	   System.out.println("desc"+s.getDescription() +"label"+s.getLabel()+"val"+s.getValue());
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

	public BigDecimal getAmountWithoutTax() {
		return amountWithoutTax;
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

	public String getSelectedWalletOperation() {
		return selectedWalletOperation;
	}

	public void setSelectedWalletOperation(String selectedWalletOperation) {
		this.selectedWalletOperation = selectedWalletOperation;
	}

	public RatedTransaction getSelectedRatedTransaction() {
		return selectedRatedTransaction;
	}

	public void setSelectedRatedTransaction(
			RatedTransaction selectedRatedTransaction) {
		this.selectedRatedTransaction = selectedRatedTransaction;
	}


	
}
