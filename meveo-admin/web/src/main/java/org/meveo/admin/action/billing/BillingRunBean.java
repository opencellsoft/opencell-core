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

import java.util.Arrays;
import java.util.Date;
import java.util.List;

import javax.enterprise.context.ConversationScoped;
import javax.enterprise.inject.Instance;
import javax.enterprise.inject.Produces;
import javax.faces.event.ValueChangeEvent;
import javax.faces.model.DataModel;
import javax.faces.model.ListDataModel;
import javax.inject.Inject;
import javax.inject.Named;

import org.jboss.seam.international.status.Messages;
import org.jboss.seam.international.status.builder.BundleKey;
import org.jboss.solder.servlet.http.RequestParam;
import org.meveo.admin.action.BaseBean;
import org.meveo.admin.exception.BusinessException;
import org.meveo.admin.util.ListItemsSelector;
import org.meveo.commons.utils.ParamBean;
import org.meveo.model.billing.BillingProcessTypesEnum;
import org.meveo.model.billing.BillingRun;
import org.meveo.model.billing.BillingRunStatusEnum;
import org.meveo.model.billing.Invoice;
import org.meveo.model.billing.PostInvoicingReportsDTO;
import org.meveo.model.billing.PreInvoicingReportsDTO;
import org.meveo.model.billing.RejectedBillingAccount;
import org.meveo.service.base.PersistenceService;
import org.meveo.service.base.local.IPersistenceService;
import org.meveo.service.billing.impl.BillingRunService;
import org.meveo.service.billing.impl.RatedTransactionService;
import org.omnifaces.cdi.ViewScoped;
import org.primefaces.model.SortOrder;

/**
 * Standard backing bean for {@link BillingRun} (extends {@link BaseBean} that
 * provides almost all common methods to handle entities filtering/sorting in
 * datatable, their create, edit, view, delete operations). It works with Manaty
 * custom JSF components.
 */
@Named
@ViewScoped
public class BillingRunBean extends BaseBean<BillingRun> {

	private static final long serialVersionUID = 1L;

	/**
	 * Injected
	 * 
	 * @{link Invoice} service. Extends {@link PersistenceService}.
	 */
	@Inject
	private BillingRunService billingRunService;

	@Inject
	@RequestParam
	private Instance<Boolean> preReport;

	@Inject
	@RequestParam
	private Instance<Boolean> postReport;

	@Inject
	private RatedTransactionService ratedTransactionService;

	private ListItemsSelector<Invoice> itemSelector;

	private boolean launchInvoicingRejectedBA = false;

	@Inject
	private Messages messages;

	private DataModel<Invoice> invoicesModel;

	/**
	 * Constructor. Invokes super constructor and provides class type of this
	 * bean for {@link BaseBean}.
	 */
	public BillingRunBean() {
		super(BillingRun.class);
	}

	/**
	 * Factory method for entity to edit. If objectId param set load that entity
	 * from database, otherwise create new.
	 * 
	 * @throws IllegalAccessException
	 * @throws InstantiationException
	 */
	public BillingRun initEntity() {
		BillingRun billingRun = super.initEntity();
		getPersistenceService().refresh(billingRun);

		try {
			log.info("postReport.get()=" + postReport.get());
			if (billingRun.getId() == null) {
				billingRun.setProcessType(BillingProcessTypesEnum.MANUAL);
			}

			if (billingRun != null && billingRun.getId() != null && preReport.get() != null && preReport.get()) {
				PreInvoicingReportsDTO preInvoicingReportsDTO = billingRunService
						.generatePreInvoicingReports(billingRun);
				billingRun.setPreInvoicingReports(preInvoicingReportsDTO);
			} else if (billingRun != null && billingRun.getId() != null && postReport.get() != null && postReport.get()) {
				PostInvoicingReportsDTO postInvoicingReportsDTO = billingRunService
						.generatePostInvoicingReports(billingRun);
				billingRun.setPostInvoicingReports(postInvoicingReportsDTO);
			}

			invoicesModel = new ListDataModel<Invoice>(billingRun.getInvoices());
		} catch (BusinessException e) {
			log.error("Failed to initialize an object", e);
		}

		return billingRun;
	}

	@Produces
	@Named("billingRunInvoices")
	@ConversationScoped
	public List<Invoice> getBillingRunInvoices() {
		if (entity == null) {
			return null;
		}
		return entity.getInvoices();
	}

	/**
	 * @see org.meveo.admin.action.BaseBean#getPersistenceService()
	 */
	@Override
	protected IPersistenceService<BillingRun> getPersistenceService() {
		return billingRunService;
	}

	public String launchRecurringInvoicing() {
		log.info("launchInvoicing billingRun BillingCycle={}", entity.getBillingCycle().getCode());
		try {
			ParamBean param = ParamBean.getInstance();
			String allowManyInvoicing = param.getProperty("billingRun.allowManyInvoicing", "true");
			boolean isAllowed = Boolean.parseBoolean(allowManyInvoicing);
			log.info("launchInvoicing allowManyInvoicing={}", isAllowed);

			if (billingRunService.isActiveBillingRunsExist(getCurrentProvider()) && !isAllowed) {
				messages.error(new BundleKey("messages", "error.invoicing.alreadyLunched"));
				return null;
			}

			entity.setStatus(BillingRunStatusEnum.NEW);
			entity.setProcessDate(new Date());
			entity.setProvider(entity.getBillingCycle().getProvider());
			billingRunService.create(entity);
            return "billingRuns";
            
		} catch (Exception e) {
			log.error(e.getMessage());
			messages.error(e.getMessage());
		}
		return null;
	}

	public String confirmInvoicing() {
		try {
			// statusMessages.add("facturation confirmee avec succes");
			entity.setStatus(BillingRunStatusEnum.ON_GOING);
			billingRunService.update(entity);
            return "billingRuns";

		} catch (Exception e) {
			log.error(e.getMessage());
			messages.error(e.getMessage());
		}
		return null;
	}

	public String validateInvoicing() {
		try {
			entity.setStatus(BillingRunStatusEnum.CONFIRMED);
			billingRunService.update(entity);
			if (launchInvoicingRejectedBA) {
				BillingRun billingRun = new BillingRun();
				billingRun.setStatus(BillingRunStatusEnum.NEW);
				billingRun.setProcessDate(new Date());
				billingRun.setProcessType(BillingProcessTypesEnum.MANUAL);
				billingRun.setProvider(getCurrentProvider());
				String selectedBillingAccounts = "";
				String sep = "";
				boolean isBillable = false;
				for (RejectedBillingAccount ba : entity.getRejectedBillingAccounts()) {
					selectedBillingAccounts = selectedBillingAccounts + sep + ba.getId();
					sep = ",";
					if (!isBillable && ratedTransactionService.isBillingAccountBillable(ba.getBillingAccount())) {
						isBillable = true;
						break;
					}
				}
				if (!isBillable) {
					messages.error(new BundleKey("messages", "error.invoicing.noTransactions"));
					return null;
				}
				log.info("selectedBillingAccounts=" + selectedBillingAccounts);
				billingRun.setSelectedBillingAccounts(selectedBillingAccounts);
				billingRunService.create(billingRun);
			}
            return "billingRuns";
            
		} catch (Exception e) {
			log.error(e.getMessage());
			messages.error(e.getMessage());
		}
		return null;
	}

	public String cancelInvoicing() {
		try {
			entity.setStatus(BillingRunStatusEnum.CANCELED);
			billingRunService.update(entity);
			return "billingRuns";
		} catch (Exception e) {
			log.error(e.getMessage());
			messages.error(e.getMessage());
		}
		return null;
	}

	public String cancelConfirmedInvoicing() {
		try {
			entity.setStatus(BillingRunStatusEnum.CANCELED);
			billingRunService.cleanBillingRun(entity);
			billingRunService.update(entity);
            return "billingRuns";
            
		} catch (Exception e) {
			log.error(e.getMessage());
			messages.error(e.getMessage());
		}
		return null;
	}

	public String rerateConfirmedInvoicing() {
		try {
			billingRunService.retateBillingRunTransactions(entity);
			return cancelConfirmedInvoicing();
			
		} catch (Exception e) {
			log.error(e.getMessage());
			messages.error(e.getMessage());
		}
		return null;
	}

	public String rerateInvoicing() {
		try {
			billingRunService.retateBillingRunTransactions(entity);
			return cancelInvoicing();
			
		} catch (Exception e) {
			log.error(e.getMessage());
			messages.error(e.getMessage());
		}
		return null;
	}

	public String preInvoicingRepport(long id) {
		try {
			return "/pages/billing/invoicing/preInvoicingReports.xhtml?edit=false&preReport=true&objectId=" + id;

		} catch (Exception e) {
			log.error(e.getMessage());
			messages.error(e.getMessage());
		}
		return null;
	}

	public String postInvoicingRepport(long id) {
		try {
			return "/pages/billing/invoicing/postInvoicingReports.xhtml?edit=false&postReport=true&objectId=" + id;

		} catch (Exception e) {
			log.error(e.getMessage());
			messages.error(e.getMessage());
		}
		return null;
	}

	public String excludeBillingAccounts() {
		try {
			log.debug("excludeBillingAccounts itemSelector.size()=#0", itemSelector.getSize());
			for (Invoice invoice : itemSelector.getList()) {
				billingRunService.deleteInvoice(invoice);
			}
			messages.info(new BundleKey("messages", "info.invoicing.billingAccountExcluded"));

		} catch (Exception e) {
			log.error("unexpected exception when excluding BillingAccounts!", e);
			messages.error(e.getMessage());
			messages.error(e.getMessage());
		}

		return "/pages/billing/invoicing/postInvoicingReports.xhtml?edit=false&postReport=true&objectId="
				+ entity.getId();
	}

	/**
	 * Item selector getter. Item selector keeps a state of multiselect
	 * checkboxes.
	 */
	@Produces
	@Named("itemSelector")
	@ConversationScoped
	public ListItemsSelector<Invoice> getItemSelector() {
		if (itemSelector == null) {
			itemSelector = new ListItemsSelector<Invoice>(false);
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

		Invoice entity = (Invoice) invoicesModel.getRowData();
		log.debug("selectChanged=#0", entity != null ? entity.getId() : null);
		if (entity != null) {
			itemSelector.check(entity);
		}
		log.debug("selectChanged itemSelector.size()=#0", itemSelector.getSize());
	}

	/**
	 * Resets item selector.
	 */
	public void resetSelection() {
		if (itemSelector == null) {
			itemSelector = new ListItemsSelector<Invoice>(false);
		} else {
			itemSelector.reset();
		}
	}

	public DataModel<Invoice> getInvoicesModel() {
		return invoicesModel;
	}

	public void setInvoicesModel(DataModel<Invoice> invoicesModel) {
		this.invoicesModel = invoicesModel;
	}

	@Override
	protected String getListViewName() {
		return "billingRuns";
	}

	@Override
	protected String getDefaultSort() {
		return "id";
	}

	@Override
	protected SortOrder getDefaultSortOrder() {
		return SortOrder.DESCENDING;
	}

	public boolean isLaunchInvoicingRejectedBA() {
		return launchInvoicingRejectedBA;
	}

	public void setLaunchInvoicingRejectedBA(boolean launchInvoicingRejectedBA) {
		this.launchInvoicingRejectedBA = launchInvoicingRejectedBA;
	}

	@Override
	protected List<String> getListFieldsToFetch() {
		return Arrays.asList("provider");
	}

	@Override
	protected List<String> getFormFieldsToFetch() {
		return Arrays.asList("provider");
	}

}
