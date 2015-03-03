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

import java.awt.Color;
import java.io.IOException;
import java.io.OutputStream;
import java.util.Arrays;
import java.util.Date;
import java.util.List;

import javax.enterprise.inject.Produces;
import javax.faces.context.FacesContext;
import javax.faces.event.ValueChangeEvent;
import javax.inject.Inject;
import javax.inject.Named;
import javax.servlet.http.HttpServletResponse;

import org.jboss.seam.international.status.Messages;
import org.jboss.seam.international.status.builder.BundleKey;
import org.meveo.admin.action.AccountBean;
import org.meveo.admin.action.BaseBean;
import org.meveo.admin.action.CustomFieldEnabledBean;
import org.meveo.admin.exception.BusinessException;
import org.meveo.admin.exception.DuplicateDefaultAccountException;
import org.meveo.admin.util.ListItemsSelector;
import org.meveo.commons.utils.ParamBean;
import org.meveo.model.billing.BankCoordinates;
import org.meveo.model.billing.BillingAccount;
import org.meveo.model.billing.BillingProcessTypesEnum;
import org.meveo.model.billing.BillingRun;
import org.meveo.model.billing.BillingRunStatusEnum;
import org.meveo.model.billing.CounterInstance;
import org.meveo.model.billing.Invoice;
import org.meveo.model.crm.AccountLevelEnum;
import org.meveo.model.payments.CustomerAccount;
import org.meveo.model.shared.Name;
import org.meveo.service.base.PersistenceService;
import org.meveo.service.base.local.IPersistenceService;
import org.meveo.service.billing.impl.BillingAccountService;
import org.meveo.service.billing.impl.BillingRunService;
import org.meveo.service.billing.impl.InvoiceService;
import org.meveo.service.billing.impl.RatedTransactionService;
import org.meveo.service.payments.impl.CustomerAccountService;
import org.omnifaces.cdi.ViewScoped;

import com.lowagie.text.Document;
import com.lowagie.text.DocumentException;
import com.lowagie.text.Element;
import com.lowagie.text.PageSize;
import com.lowagie.text.pdf.BaseFont;
import com.lowagie.text.pdf.PdfContentByte;
import com.lowagie.text.pdf.PdfGState;
import com.lowagie.text.pdf.PdfReader;
import com.lowagie.text.pdf.PdfStamper;

/**
 * Standard backing bean for {@link BillingAccount} (extends {@link BaseBean}
 * that provides almost all common methods to handle entities filtering/sorting
 * in datatable, their create, edit, view, delete operations). It works with
 * Manaty custom JSF components.
 */
@Named
@ViewScoped
@CustomFieldEnabledBean(accountLevel = AccountLevelEnum.BA)
public class BillingAccountBean extends AccountBean<BillingAccount> {

	private static final long serialVersionUID = 1L;

	/**
	 * Injected
	 * 
	 * @{link BillingAccount} service. Extends {@link PersistenceService}.
	 */
	@Inject
	private BillingAccountService billingAccountService;

	@Inject
	private InvoiceService invoiceService;

	@Inject
	private BillingRunService billingRunService;

	private Long customerAccountId;

	@Inject
	private Messages messages;

	@Inject
	private RatedTransactionService ratedTransactionService;

	private boolean returnToAgency;

	@Inject
	private CustomerAccountService customerAccountService;

	/** Selected billing account in exceptionelInvoicing page. */
	private ListItemsSelector<BillingAccount> itemSelector;

	private CounterInstance selectedCounterInstance;

	/**
	 * Constructor. Invokes super constructor and provides class type of this
	 * bean for {@link BaseBean}.
	 */
	public BillingAccountBean() {
		super(BillingAccount.class);
	}

	/**
	 * Factory method for entity to edit. If objectId param set load that entity
	 * from database, otherwise create new.
	 * 
	 * @throws IllegalAccessException
	 * @throws InstantiationException
	 */
	@Override
	public BillingAccount initEntity() {
		super.initEntity();
		returnToAgency = !(entity.getInvoicePrefix() == null);

		if (entity.getId() == null && customerAccountId != null) {
			CustomerAccount customerAccount = customerAccountService.findById(customerAccountId);
			entity.setCustomerAccount(customerAccount);
			populateAccounts(customerAccount);

			// check if has default
			if (!customerAccount.getDefaultLevel()) {
				entity.setDefaultLevel(true);
			}
		}

		if (entity.getName() == null) {
			entity.setName(new Name());
		}

		if (entity.getBankCoordinates() == null) {
			entity.setBankCoordinates(new BankCoordinates());
		}

		selectedCounterInstance = entity.getCounters() != null && entity.getCounters().size() > 0 ? entity
				.getCounters().values().iterator().next() : null;

		return entity;
	}

	public void setCustomerAccountId(Long customerAccountId) {
		this.customerAccountId = customerAccountId;
	}

	public Long getCustomerAccountId() {
		return customerAccountId;
	}

	/**
	 * Conversation is ended and user is redirected from edit to his previous
	 * window.
	 * 
	 * @see org.meveo.admin.action.BaseBean#saveOrUpdate(org.meveo.model.IEntity)
	 */
	@Override
	public String saveOrUpdate(boolean killConversation) {
		try {
			if (entity.getDefaultLevel() != null && entity.getDefaultLevel()) {
				if (billingAccountService.isDuplicationExist(entity)) {
					entity.setDefaultLevel(false);
					throw new DuplicateDefaultAccountException();
				}
			}

			CustomerAccount customerAccount = entity.getCustomerAccount();
			if (customerAccount != null) {
				List<BillingAccount> billingAccounts = billingAccountService.listByCustomerAccount(customerAccount);
				if (billingAccounts != null) {
					if (!billingAccounts.contains(entity)) {
						customerAccount.getBillingAccounts().add(entity);
					}
				}
			}

			if (entity.isTransient()) {
				billingAccountService.initBillingAccount(entity);
			}

			super.saveOrUpdate(killConversation);

			log.debug("isAttached={}", getPersistenceService().getEntityManager().contains(entity));

			return "/pages/billing/billingAccounts/billingAccountDetail.xhtml?edit=false&billingAccountId="
					+ entity.getId() + "&faces-redirect=true&includeViewParams=true";
		} catch (DuplicateDefaultAccountException e1) {
			messages.error(new BundleKey("messages", "error.account.duplicateDefautlLevel"));
		} catch (Exception e) {
			log.error(e.getMessage());
			messages.error(new BundleKey("messages", "javax.el.ELException"));
		}
		return null;
	}

	/**
	 * @see org.meveo.admin.action.BaseBean#getPersistenceService()
	 */
	@Override
	protected IPersistenceService<BillingAccount> getPersistenceService() {
		return billingAccountService;
	}

	public void terminateAccount() {
		log.debug("terminateAccount billingAccountId: {}", entity.getId());
		try {
			billingAccountService.billingAccountTermination(entity, entity.getTerminationDate(),
					entity.getTerminationReason(), getCurrentUser());
			messages.info(new BundleKey("messages", "resiliation.resiliateSuccessful"));
		} catch (BusinessException e) {
			log.error(e.getMessage());
			messages.error(e.getMessage());
		} catch (Exception e) {
			log.error(e.getMessage());
			messages.error(e.getMessage());
		}
	}

	public String cancelAccount() {
		log.info("cancelAccount billingAccountId:" + entity.getId());
		try {
			billingAccountService.billingAccountCancellation(entity, new Date(), getCurrentUser());
			messages.info(new BundleKey("messages", "cancellation.cancelSuccessful"));
			return "/pages/billing/billingAccounts/billingAccountDetail.xhtml?objectId=" + entity.getId()
					+ "&edit=false";
		} catch (BusinessException e) {
			log.error(e.getMessage());
			messages.error(e.getMessage());
		} catch (Exception e) {
			log.error(e.getMessage());
			messages.error(e.getMessage());
		}
		return null;
	}

	public String closeAccount() {
		log.info("closeAccount billingAccountId:" + entity.getId());
		try {
			billingAccountService.closeBillingAccount(entity, getCurrentUser());
			messages.info(new BundleKey("messages", "close.closeSuccessful"));
			return "/pages/billing/billingAccounts/billingAccountDetail.xhtml?objectId=" + entity.getId()
					+ "&edit=false";
		} catch (BusinessException e) {
			log.error(e.getMessage());
			messages.error(e.getMessage());
		} catch (Exception e) {
			log.error(e.getMessage());
			messages.error(e.getMessage());
		}
		return null;
	}

	// TODO: @Factory("getInvoices")
	@Produces
	@Named("getInvoices")
	public List<Invoice> getInvoices() {
		return entity != null ? entity.getInvoices() : null;
	}

	public void generatePDF(long invoiceId) {
		Invoice invoice = invoiceService.findById(invoiceId);
		byte[] invoicePdf = invoice.getPdf();
		FacesContext context = FacesContext.getCurrentInstance();
		String invoiceFilename = null;
		if (invoice.getBillingRun().getStatus() == BillingRunStatusEnum.VALIDATED) {
			invoiceFilename = invoice.getInvoiceNumber() + ".pdf";
		} else {
			invoiceFilename = "unvalidated-invoice.pdf";
		}
		HttpServletResponse response = (HttpServletResponse) context.getExternalContext().getResponse();
		response.setContentType("application/pdf"); // fill in
		response.setHeader("Content-disposition", "attachment; filename=" + invoiceFilename);

		try {
			OutputStream os = response.getOutputStream();
			Document document = new Document(PageSize.A4);
			if (invoice.getBillingRun().getStatus() != BillingRunStatusEnum.VALIDATED) {
				// Add watemark image
				PdfReader reader = new PdfReader(invoicePdf);
				int n = reader.getNumberOfPages();
				PdfStamper stamp = new PdfStamper(reader, os);
				PdfContentByte over = null;
				BaseFont bf = BaseFont.createFont(BaseFont.HELVETICA, BaseFont.WINANSI, BaseFont.EMBEDDED);
				PdfGState gs = new PdfGState();
				gs.setFillOpacity(0.5f);
				int i = 1;
				while (i <= n) {
					over = stamp.getOverContent(i);
					over.setGState(gs);
					over.beginText();
					System.out.println("top=" + document.top() + ",bottom=" + document.bottom());
					over.setTextMatrix(document.top(), document.bottom());
					over.setFontAndSize(bf, 150);
					over.setColorFill(Color.GRAY);
					over.showTextAligned(Element.ALIGN_CENTER, "TEST", document.getPageSize().getWidth() / 2, document
							.getPageSize().getHeight() / 2, 45);
					over.endText();
					i++;
				}

				stamp.close();
			} else {
				os.write(invoicePdf); // fill in PDF with bytes
			}

			// contentType
			os.flush();
			os.close();
			context.responseComplete();
		} catch (IOException e) {
			log.error(e.getMessage());
		} catch (DocumentException e) {
			log.error(e.getMessage());
		}
	}

	public boolean pdfExists(long invoiceId) {
		Invoice invoice = invoiceService.findById(invoiceId);
		if (invoice != null && invoice.getPdf() != null) {
			return true;
		}
		return false;
	}

	public String launchExceptionalInvoicing() {
		log.info("launchExceptionelInvoicing...");
		try {
			ParamBean param = ParamBean.getInstance();
			String allowManyInvoicing = param.getProperty("billingRun.allowManyInvoicing", "true");
			boolean isAllowed = Boolean.parseBoolean(allowManyInvoicing);
			log.info("launchInvoicing allowManyInvoicing=#", isAllowed);
			if (billingRunService.isActiveBillingRunsExist(getCurrentProvider()) && !isAllowed) {
				messages.error(new BundleKey("messages", "error.invoicing.alreadyLunched"));
				return null;
			}

			BillingRun billingRun = new BillingRun();
			billingRun.setStatus(BillingRunStatusEnum.NEW);
			billingRun.setProcessDate(new Date());
			billingRun.setProcessType(BillingProcessTypesEnum.MANUAL);
			billingRun.setProvider(getCurrentProvider());
			String selectedBillingAccounts = "";
			String sep = "";
			boolean isBillable = false;
			for (BillingAccount ba : getSelectedEntities()) {
				selectedBillingAccounts = selectedBillingAccounts + sep + ba.getId();
				sep = ",";
				if (!isBillable && ratedTransactionService.isBillingAccountBillable(ba)) {
					isBillable = true;
				}
			}
			if (!isBillable) {
				messages.error(new BundleKey("messages", "error.invoicing.noTransactions"));
				return null;
			}
			log.info("selectedBillingAccounts=" + selectedBillingAccounts);
			billingRun.setSelectedBillingAccounts(selectedBillingAccounts);
			billingRunService.create(billingRun);
			return "/pages/billing/invoicing/billingRuns.xhtml?edit=false";
		} catch (Exception e) {
			log.error("launchExceptionelInvoicing", e);
			messages.error(e.getMessage());
		}
		return null;
	}

	/**
	 * Item selector getter. Item selector keeps a state of multiselect
	 * checkboxes.
	 */
	// TODO: @BypassInterceptors
	public ListItemsSelector<BillingAccount> getItemSelector() {
		if (itemSelector == null) {
			itemSelector = new ListItemsSelector<BillingAccount>(false);
		}
		return itemSelector;
	}

	/**
	 * Check/uncheck all select boxes.
	 */
	public void checkUncheckAll(ValueChangeEvent event) {
		itemSelector.switchMode();
	}

	/**
	 * Listener of select changed event.
	 */
	public void selectChanged(ValueChangeEvent event) {
		BillingAccount entity = getLazyDataModel().getRowData();
		if (entity != null) {
			itemSelector.check(entity);
		}
	}

	/**
	 * Resets item selector.
	 */
	public void resetSelection() {
		if (itemSelector == null) {
			itemSelector = new ListItemsSelector<BillingAccount>(false);
		} else {
			itemSelector.reset();
		}
	}

	public boolean isReturnToAgency() {
		return returnToAgency;
	}

	public void setReturnToAgency(boolean returnToAgency) {
		this.returnToAgency = returnToAgency;
	}

	public void setInvoicePrefix() {
		if (returnToAgency) {
			String invoicePrefix = null;
			if (entity.getProvider().isEntreprise()) {
				invoicePrefix = "R_PRO_";
			} else {
				invoicePrefix = "R_PART_";
			}
			entity.setInvoicePrefix(invoicePrefix + entity.getExternalRef2());
		} else
			entity.setInvoicePrefix(null);
	}

	public void processValueChange(ValueChangeEvent value) {
		if (value != null) {
			if (value.getNewValue() instanceof String) {
				entity.setExternalRef2((String) value.getNewValue());
				setInvoicePrefix();
			}

		}
	}

	public void populateAccounts(CustomerAccount customerAccount) {
		entity.setCustomerAccount(customerAccount);
		if (billingAccountService.isDuplicationExist(entity)) {
			entity.setDefaultLevel(false);
		} else {
			entity.setDefaultLevel(true);
		}
		if (customerAccount != null && customerAccount.getProvider() != null
				&& customerAccount.getProvider().isLevelDuplication()) {

			entity.setCode(customerAccount.getCode());
			entity.setDescription(customerAccount.getDescription());
			entity.setEmail(customerAccount.getContactInformation().getEmail());
			entity.setAddress(customerAccount.getAddress());
			entity.setExternalRef1(customerAccount.getExternalRef1());
			entity.setExternalRef2(customerAccount.getExternalRef2());
			entity.setProviderContact(customerAccount.getProviderContact());
			entity.setName(customerAccount.getName());
			entity.setPaymentMethod(customerAccount.getPaymentMethod());
			entity.setProvider(customerAccount.getProvider());
			entity.setPrimaryContact(customerAccount.getPrimaryContact());
		}
	}

	@Override
	protected String getDefaultSort() {
		return "code";
	}

	@Override
	protected List<String> getListFieldsToFetch() {
		return Arrays.asList("provider", "customerAccount", "customerAccount.customer");
	}

	@Override
	protected List<String> getFormFieldsToFetch() {
		return Arrays.asList("provider", "customerAccount", "customerAccount.billingAccounts", "billingCycle");
	}

	public CounterInstance getSelectedCounterInstance() {
		if (entity == null) {
			initEntity();
		}
		return selectedCounterInstance;
	}

	public void setSelectedCounterInstance(CounterInstance selectedCounterInstance) {
		this.selectedCounterInstance = selectedCounterInstance;
	}

}