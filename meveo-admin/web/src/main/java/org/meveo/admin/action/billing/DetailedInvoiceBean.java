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
import java.util.Map.Entry;

import javax.faces.model.SelectItem;
import javax.faces.model.SelectItemGroup;
import javax.inject.Inject;
import javax.inject.Named;

import org.jboss.seam.international.status.builder.BundleKey;
import org.meveo.admin.action.BaseBean;
import org.meveo.admin.action.CustomFieldBean;
import org.meveo.admin.exception.BusinessException;
import org.meveo.commons.utils.StringUtils;
import org.meveo.model.Auditable;
import org.meveo.model.billing.BillingAccount;
import org.meveo.model.billing.CategoryInvoiceAgregate;
import org.meveo.model.billing.Invoice;
import org.meveo.model.billing.InvoiceCategory;
import org.meveo.model.billing.InvoiceSubCategory;
import org.meveo.model.billing.RatedTransaction;
import org.meveo.model.billing.RatedTransactionStatusEnum;
import org.meveo.model.billing.SubCategoryInvoiceAgregate;
import org.meveo.model.billing.TaxInvoiceAgregate;
import org.meveo.model.billing.UserAccount;
import org.meveo.service.base.PersistenceService;
import org.meveo.service.base.local.IPersistenceService;
import org.meveo.service.billing.impl.BillingAccountService;
import org.meveo.service.billing.impl.InvoiceAgregateHandler;
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

	private BillingAccount billingAccount;
	private Invoice invoiceToAdd;
	private Invoice selectedInvoice;
	private InvoiceSubCategory selectedInvoiceSubCategory;
	private List<SubCategoryInvoiceAgregate> tempSubCategoryInvoiceAggregates = new ArrayList<>();
	private BigDecimal quantity;
	private BigDecimal amountWithoutTax;
	private BigDecimal unitAmountWithoutTax;
	private RatedTransaction selectedRatedTransaction;
	private List<SelectItem> invoiceCategoriesGUI;
	private String description;
	private boolean includeBalance;
	// //
	private InvoiceAgregateHandler agregateHandler = new InvoiceAgregateHandler();
	private List<SubCategoryInvoiceAgregate> subCategoryInvoiceAggregates = new ArrayList<SubCategoryInvoiceAgregate>();

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
		invoice.setDueDate(new Date());
		invoice.setInvoiceDate(new Date());
		billingAccount = invoice.getBillingAccount();
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

	}

	@SuppressWarnings({ "rawtypes", "unchecked" })
	public void deleteAllLinkedInvoice() throws BusinessException {
		entity.setLinkedInvoices(new HashSet());
		selectedInvoice = null;

	}

	public List<RatedTransaction> getRatedTransactions(SubCategoryInvoiceAgregate subCat) {
		if (subCat == null) {
			return null;
		}
		return subCat.getRatedtransactions();
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

	private void addDetailedInvoiceLines(InvoiceSubCategory selectInvoiceSubCat)  {
		try{
			if (entity.getBillingAccount() == null) {
				messages.error("BillingAccount is required.");
				return;
			}
			billingAccount = entity.getBillingAccount();
			if(selectInvoiceSubCat == null){
				messages.error("Invoice sub category is required.");
				return;
			}
			if(StringUtils.isBlank(quantity)){
				messages.error("Quantity is required.");
				return;
			}
			if(StringUtils.isBlank(unitAmountWithoutTax)){
				messages.error("UnitAmountWithoutTax is required.");
				return;
			}

			billingAccount = billingAccountService.refreshOrRetrieve(billingAccount);
			List<UserAccount> userAccounts = billingAccount.getUsersAccounts();
			if (userAccounts == null || userAccounts.isEmpty()) {
				messages.error("BillingAccount with code=" + entity.getBillingAccount().getCode() + " has no userAccount.");
				return;
			}
			UserAccount userAccount = userAccounts.get(0);

			selectInvoiceSubCat = invoiceSubCategoryService.refreshOrRetrieve(selectInvoiceSubCat);

			RatedTransaction ratedTransaction = new RatedTransaction();
			ratedTransaction.setUsageDate(new Date());
			ratedTransaction.setUnitAmountWithoutTax(unitAmountWithoutTax);
			ratedTransaction.setQuantity(quantity);
			ratedTransaction.setStatus(RatedTransactionStatusEnum.BILLED);
			ratedTransaction.setWallet(userAccount.getWallet());
			ratedTransaction.setBillingAccount(billingAccount);
			ratedTransaction.setInvoiceSubCategory(selectInvoiceSubCat);
			ratedTransaction.setProvider(getCurrentProvider());
			ratedTransaction.setCode("RT_" + selectInvoiceSubCat.getCode());
			ratedTransaction.setDescription(description);
			ratedTransaction.setInvoice(entity);
			ratedTransaction.setInvoiceSubCategory(selectInvoiceSubCat);

			agregateHandler.addRT(ratedTransaction);
			updateAmountsAndLines(agregateHandler, billingAccount);
		}catch(BusinessException be){
			messages.error(be.getMessage());
			return;
		}catch(Exception e){
			messages.error(e.getMessage());
			return;
		}

	}
	
	/**
	 * Recompute  agregates
	 * 
	 * @param agregateHandler
	 * @param billingAccount
	 * @throws BusinessException
	 */
	public void updateAmountsAndLines(InvoiceAgregateHandler agregateHandler, BillingAccount billingAccount) throws BusinessException {
		subCategoryInvoiceAggregates = new ArrayList<SubCategoryInvoiceAgregate>(agregateHandler.getSubCatInvAgregateMap().values());
		entity.setAmountWithoutTax(agregateHandler.getInvoiceAmountWithoutTax());
		entity.setAmountTax(agregateHandler.getInvoiceAmountTax());
		entity.setAmountWithTax(agregateHandler.getInvoiceAmountWithTax());

		BigDecimal netToPay = entity.getAmountWithTax();
		if (!getCurrentProvider().isEntreprise() && isIncludeBalance()) {
			BigDecimal balance  =  customerAccountService.customerAccountBalanceDue(null, billingAccount.getCustomerAccount().getCode(), entity.getDueDate(), entity.getProvider());

			if (balance == null) {
				throw new BusinessException("account balance calculation failed");
			}
			netToPay = entity.getAmountWithTax().add(balance);
		}
		entity.setNetToPay(netToPay);
	}

	/**
	 * Called whene  a line is deleted from the dataList detailInvoice
	 * @throws BusinessException
	 */
	public void deleteRatedTransactionLine() throws BusinessException {
		agregateHandler.removeRT(selectedRatedTransaction);
		updateAmountsAndLines(agregateHandler, selectedRatedTransaction.getBillingAccount());
	}


	/**
	 *  Called whene quantity or unitAmout are changed in the dataList detailInvoice
	 * @param ratedTx
	 * @throws BusinessException
	 */
	public void reComputeAmountWithoutTax(RatedTransaction ratedTx) throws BusinessException {
		agregateHandler.reset();
		for (SubCategoryInvoiceAgregate subcat : subCategoryInvoiceAggregates) {
			for (RatedTransaction rt : subcat.getRatedtransactions()) {
				rt.setAmountWithoutTax(null);
				agregateHandler.addRT(rt);
			}
		}
		updateAmountsAndLines(agregateHandler, ratedTx.getBillingAccount());
	}

	@Override
	public String saveOrUpdate(boolean killConversation) throws BusinessException {

		Auditable auditable = new Auditable();
		auditable.setCreator(getCurrentUser());
		auditable.setCreated(new Date());

		billingAccount = billingAccountService.refreshOrRetrieve(entity.getBillingAccount());
		entity.setBillingAccount(billingAccount);
		
		entity.setInvoiceNumber(invoiceService.getInvoiceNumber(entity, getCurrentUser()));
		
		invoiceService.create(entity, getCurrentUser());

		invoiceService.commit();
		for (Entry<String, CategoryInvoiceAgregate> entry : agregateHandler.getCatInvAgregateMap().entrySet()) {

			CategoryInvoiceAgregate catInvAgr = entry.getValue();
			catInvAgr.setAuditable(auditable);
			catInvAgr.setInvoice(entity);
			invoiceAgregateService.create(catInvAgr, currentUser);
		}
		for (SubCategoryInvoiceAgregate subcat : subCategoryInvoiceAggregates) {
			subcat.setAuditable(auditable);
			subcat.setInvoice(entity);
			invoiceAgregateService.create(subcat, currentUser);
			for (RatedTransaction rt : subcat.getRatedtransactions()) {
				rt.setInvoice(entity);
				ratedTransactionService.create(rt, currentUser);
			}
		}
		for (Entry<String, TaxInvoiceAgregate> entry : agregateHandler.getTaxInvAgregateMap().entrySet()) {
			TaxInvoiceAgregate taxInvAgr = entry.getValue();
			taxInvAgr.setAuditable(auditable);
			taxInvAgr.setInvoice(entity);
			invoiceAgregateService.create(taxInvAgr, currentUser);
		}

		try {
			invoiceService.generateXmlAndPdfInvoice(entity, getCurrentUser());
		} catch (Exception e) {
			messages.error("Error generating xml / pdf invoice=" + e.getMessage());
		}

		return getListViewName();
	}

	/**
	 * Includ a copy from linkedIncoice's RatedTransaction
	 * @throws BusinessException
	 */
	public void importFromLinkedInvoices() throws BusinessException {
		if (entity.getBillingAccount() == null || entity.getBillingAccount().isTransient()) {
			messages.error("BillingAccount is required.");
			return;
		}
		BillingAccount billingAccount = billingAccountService.refreshOrRetrieve(entity.getBillingAccount());
		if (entity.getLinkedInvoices() != null && entity.getLinkedInvoices().size() > 0) {			
			for (Invoice invoice : entity.getLinkedInvoices()) {				
				invoice = invoiceService.findById(invoice.getId());				
				for (RatedTransaction rt : invoice.getRatedTransactions()) {
					RatedTransaction newRT = new RatedTransaction();
					newRT.setUsageDate(new Date());
					newRT.setUnitAmountWithoutTax(rt.getUnitAmountWithoutTax());
					newRT.setQuantity(rt.getQuantity());
					newRT.setStatus(RatedTransactionStatusEnum.BILLED);
					newRT.setWallet(rt.getWallet());
					newRT.setBillingAccount(billingAccount);
					newRT.setInvoiceSubCategory(rt.getInvoiceSubCategory());
					newRT.setProvider(getCurrentProvider());
					newRT.setCode(rt.getCode());
					newRT.setDescription(rt.getDescription());
					newRT.setInvoice(entity);							
					agregateHandler.addRT(newRT);
				}

			}
			updateAmountsAndLines(agregateHandler, billingAccount);
		} else {
			messages.info(new BundleKey("messages", "message.invoice.addAggregate.linked.null"));
		}

	}
	
	/**
	 * Include  original opened ratedTransaction
	 * @throws BusinessException
	 */

	public void importOpenedRT() throws BusinessException {
		if (entity.getBillingAccount() == null || entity.getBillingAccount().isTransient()) {
			messages.error("BillingAccount is required.");
			return;
		}
		billingAccount = billingAccountService.refreshOrRetrieve(entity.getBillingAccount());
		List<UserAccount> userAccounts = billingAccount.getUsersAccounts();
		if (userAccounts == null || userAccounts.isEmpty()) {
			messages.error("BillingAccount with code=" + entity.getBillingAccount().getCode() + " has no userAccount.");
			return;
		}		
		UserAccount userAccount = userAccounts.get(0);

		if(entity.getInvoiceType().equals(invoiceTypeService.getCommercialCode())){
			List<RatedTransaction> openedRT = ratedTransactionService.openRTbySubCat(userAccount.getWallet(), null);
			for(RatedTransaction ratedTransaction : openedRT){
				ratedTransaction.setStatus(RatedTransactionStatusEnum.BILLED);
				ratedTransaction.setInvoice(entity);
				ratedTransactionService.update(ratedTransaction, currentUser);
				agregateHandler.addRT(ratedTransaction);
			}
			updateAmountsAndLines(agregateHandler, entity.getBillingAccount());
		}
	}

	public List<SelectItem> getInvoiceCatSubCats() {
		if (invoiceCategoriesGUI != null) {
			return invoiceCategoriesGUI;
		}

		invoiceCategoriesGUI = new ArrayList<>();

		List<InvoiceCategory> invoiceCategories = invoiceCategoryService.list();
		for (InvoiceCategory ic : invoiceCategories) {
			SelectItemGroup selectItemGroup = new SelectItemGroup(ic.getCode());
			List<SelectItem> subCats = new ArrayList<>();
			for (InvoiceSubCategory invoiceSubCategory : ic.getInvoiceSubCategories()) {
				subCats.add(new SelectItem(invoiceSubCategory, invoiceSubCategory.getCode()));
			}
			selectItemGroup.setSelectItems(subCats.toArray(new SelectItem[subCats.size()]));
			invoiceCategoriesGUI.add(selectItemGroup);
		}

		return invoiceCategoriesGUI;
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



	public BigDecimal getQuantity() {
		return quantity;
	}

	public void setQuantity(BigDecimal quantity) {
		this.quantity = quantity;
	}

	public void setAmountWithoutTax(BigDecimal amountWithoutTax) {
		this.amountWithoutTax = amountWithoutTax;
	}

	public List<SubCategoryInvoiceAgregate> getTempSubCategoryInvoiceAggregates() {
		return tempSubCategoryInvoiceAggregates;
	}

	public void setTempSubCategoryInvoiceAggregates(List<SubCategoryInvoiceAgregate> tempSubCategoryInvoiceAggregates) {
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

	public void setSelectedInvoiceSubCategory(InvoiceSubCategory selectedInvoiceSubCategory) {
		this.selectedInvoiceSubCategory = selectedInvoiceSubCategory;
	}

	public RatedTransaction getSelectedRatedTransaction() {
		return selectedRatedTransaction;
	}

	public void setSelectedRatedTransaction(RatedTransaction selectedRatedTransaction) {
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

	public boolean isIncludeBalance() {
		return includeBalance;
	}

	public void setIncludeBalance(boolean includeBalance) {		
		this.includeBalance = includeBalance;
		try {
			updateAmountsAndLines(agregateHandler, billingAccount);
		} catch (BusinessException be) {
			messages.error(be.getMessage());
			return;		
		}		
	}

	/**
	 * @return the subCategoryInvoiceAggregates
	 */
	public List<SubCategoryInvoiceAgregate> getSubCategoryInvoiceAggregates() {
		return subCategoryInvoiceAggregates;
	}

	/**
	 * @param subCategoryInvoiceAggregates
	 *            the subCategoryInvoiceAggregates to set
	 */
	public void setSubCategoryInvoiceAggregates(List<SubCategoryInvoiceAgregate> subCategoryInvoiceAggregates) {
		this.subCategoryInvoiceAggregates = subCategoryInvoiceAggregates;
	}

}
