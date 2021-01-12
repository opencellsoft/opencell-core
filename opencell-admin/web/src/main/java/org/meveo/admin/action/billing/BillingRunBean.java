/*
 * (C) Copyright 2015-2020 Opencell SAS (https://opencellsoft.com/) and contributors.
 *
 * This program is free software: you can redistribute it and/or modify it under the terms of the GNU Affero General
 * Public License as published by the Free Software Foundation, either version 3 of the License, or (at your option)
 * any later version.
 *
 * THERE IS NO WARRANTY FOR THE PROGRAM, TO THE EXTENT PERMITTED BY APPLICABLE LAW. EXCEPT WHEN
 * OTHERWISE STATED IN WRITING THE COPYRIGHT HOLDERS AND/OR OTHER PARTIES PROVIDE THE PROGRAM "AS
 * IS" WITHOUT WARRANTY OF ANY KIND, EITHER EXPRESSED OR IMPLIED, INCLUDING, BUT NOT LIMITED TO, THE
 * IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE. THE ENTIRE RISK AS TO
 * THE QUALITY AND PERFORMANCE OF THE PROGRAM IS WITH YOU. SHOULD THE PROGRAM PROVE DEFECTIVE,
 * YOU ASSUME THE COST OF ALL NECESSARY SERVICING, REPAIR OR CORRECTION.
 *
 * For more information on the GNU Affero General Public License, please consult
 * <https://www.gnu.org/licenses/agpl-3.0.en.html>.
 */
package org.meveo.admin.action.billing;

import java.util.Date;
import java.util.List;

import javax.enterprise.context.ConversationScoped;
import javax.enterprise.inject.Produces;
import javax.faces.view.ViewScoped;
import javax.inject.Inject;
import javax.inject.Named;

import org.jboss.seam.international.status.builder.BundleKey;
import org.meveo.admin.action.BaseBean;
import org.meveo.admin.action.CustomFieldBean;
import org.meveo.admin.exception.BusinessException;
import org.meveo.commons.utils.ParamBean;
import org.meveo.model.billing.BillingCycle;
import org.meveo.model.billing.BillingProcessTypesEnum;
import org.meveo.model.billing.BillingRun;
import org.meveo.model.billing.BillingRunStatusEnum;
import org.meveo.model.billing.Invoice;
import org.meveo.model.billing.PostInvoicingReportsDTO;
import org.meveo.model.billing.PreInvoicingReportsDTO;
import org.meveo.model.shared.DateUtils;
import org.meveo.service.base.local.IPersistenceService;
import org.meveo.service.billing.impl.BillingRunService;
import org.meveo.service.billing.impl.InvoiceService;
import org.omnifaces.cdi.Param;
import org.primefaces.model.SortOrder;

/**
 * Standard backing bean for {@link BillingRun} (extends {@link BaseBean} that provides almost all common methods to handle entities filtering/sorting in datatable, their create,
 * edit, view, delete operations). It works with Manaty custom JSF components.
 * 
 * @author Edward P. Legaspi
 * @author Wassim Drira
 * @author Abdellatif BARI
 * @lastModifiedVersion 5.3
 * 
 */
@Named
@ViewScoped
public class BillingRunBean extends CustomFieldBean<BillingRun> {

    private static final long serialVersionUID = 1L;

    @Inject
    private BillingRunService billingRunService;

    @Inject
    @Param
    private Boolean preReport;

    @Inject
    @Param
    private Boolean postReport;

    private boolean launchInvoicingRejectedBA = false;

    /**
     * Constructor. Invokes super constructor and provides class type of this bean for {@link BaseBean}.
     */
    public BillingRunBean() {
        super(BillingRun.class);
    }

    /**
     * Factory method for entity to edit. If objectId param set load that entity from database, otherwise create new.
     * 
     * @return billing run
     */
    @Override
    public BillingRun initEntity() {
        BillingRun billingRun = super.initEntity();
        getPersistenceService().refresh(billingRun);

        try {
            log.info("postReport {}", postReport);
            if (billingRun.getId() == null) {
                billingRun.setProcessType(BillingProcessTypesEnum.MANUAL);
            }

            if ((billingRun != null) && (billingRun.getId() != null) && (preReport != null) && preReport) {
                PreInvoicingReportsDTO preInvoicingReportsDTO = billingRunService.generatePreInvoicingReports(billingRun);
                billingRun.setPreInvoicingReports(preInvoicingReportsDTO);
            } else if ((billingRun != null) && (billingRun.getId() != null) && (postReport != null) && postReport) {
                PostInvoicingReportsDTO postInvoicingReportsDTO = billingRunService.generatePostInvoicingReports(billingRun);
                billingRun.setPostInvoicingReports(postInvoicingReportsDTO);
            }

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

    public BillingCycle getBillingCycle() {
        return entity.getBillingCycle();
    }

    public void setBillingCycle(BillingCycle billingCycle) {
        if (billingCycle != null) {
            entity.setBillingCycle(billingCycle);
            if (entity.getProcessDate() == null) {
                entity.setProcessDate(new Date());
            }
            log.debug("setBillingCycle {}, invoicedate={}, lastTransactionDate={}", billingCycle.getCode(), entity.getInvoiceDate(), entity.getLastTransactionDate());

            if (billingCycle.getInvoiceDateProductionDelayEL() != null) {
                entity.setInvoiceDate(DateUtils.addDaysToDate(entity.getProcessDate(), InvoiceService.resolveInvoiceDateDelay(billingCycle.getInvoiceDateProductionDelayEL(), entity)));
            } else {
                entity.setInvoiceDate(entity.getProcessDate());
            }
            if (billingCycle.getLastTransactionDateEL() != null) {
                entity.setLastTransactionDate(BillingRunService.resolveLastTransactionDate(billingCycle.getLastTransactionDateEL(), entity));

            } else if (billingCycle.getLastTransactionDateDelayEL() != null) {
                entity.setLastTransactionDate(DateUtils.addDaysToDate(entity.getProcessDate(), BillingRunService.resolveLastTransactionDateDelay(billingCycle.getLastTransactionDateDelayEL(), entity)));

            } else {
                entity.setLastTransactionDate(DateUtils.addDaysToDate(entity.getProcessDate(), 1));
            }
            log.debug("after setBillingCycle invoicedate={}, lastTransactionDate={}", entity.getInvoiceDate(), entity.getLastTransactionDate());
        }
    }

    public String launchRecurringInvoicing() {
        log.info("launchInvoicing billingRun BillingCycle={}, invoicedate={}, lastTransactionDate={}", entity.getBillingCycle(), entity.getInvoiceDate(), entity.getLastTransactionDate());
        try {
            ParamBean param = paramBeanFactory.getInstance();
            String allowManyInvoicing = param.getProperty("billingRun.allowManyInvoicing", "true");
            boolean isAllowed = Boolean.parseBoolean(allowManyInvoicing);
            log.info("launchInvoicing allowManyInvoicing={}", isAllowed);

            if (billingRunService.isActiveBillingRunsExist() && !isAllowed) {
                messages.error(new BundleKey("messages", "error.invoicing.alreadyLunched"));
                return null;
            }

            entity.setStatus(BillingRunStatusEnum.NEW);
            entity.setProcessDate(new Date());

            customFieldDataEntryBean.saveCustomFieldsToEntity(entity, true);
            billingRunService.create(entity);

            return "billingRuns";

        } catch (Exception e) {
            log.error("Failed to launch invoicing", e);
            messages.error(new BundleKey("messages", "error.execution"));
        }
        return null;
    }

    public String confirmInvoicing() {
        try {
            // statusMessages.add("facturation confirmee avec succes");
            entity.setStatus(BillingRunStatusEnum.PREVALIDATED);
            billingRunService.update(entity);
            return "billingRuns";

        } catch (Exception e) {
            log.error("Failed to confirm invoicing", e);
            messages.error(new BundleKey("messages", "error.execution"));
        }
        return null;
    }

    public String validateInvoicing() {
        try {
            entity = billingRunService.refreshOrRetrieve(entity);
            entity.setStatus(BillingRunStatusEnum.POSTVALIDATED);
            billingRunService.update(entity);
            if (launchInvoicingRejectedBA) {
                boolean isBillable = billingRunService.launchInvoicingRejectedBA(entity);
                if (!isBillable) {
                    messages.error(new BundleKey("messages", "error.invoicing.noTransactions"));
                    return null;
                }
            }
            return "billingRuns";

        } catch (Exception e) {
            log.error("Failed to validate invoicing", e);
            messages.error(new BundleKey("messages", "error.execution"));
        }
        return null;
    }
    
    public Boolean canBeValidated() {
    	 return !billingRunService.isBillingRunContainingRejectedInvoices(entity.getId());
    }

    public String cancel() {
        try {
            entity = billingRunService.cancelBillingRun(entity);
            return "billingRuns";

        } catch (Exception e) {
            log.error("Failed to cancel billing run", e);
            messages.error(new BundleKey("messages", "error.execution"));
        }
        return null;
    }

    /**
     * Cancel billing run and mark associated wallet operations to be re-rated
     * 
     * @return View name
     */
    public String rerate() {
        try {
            entity = billingRunService.rerateBillingRun(entity);
            return "billingRuns";

        } catch (Exception e) {
            log.error("Failed to rerate billing run", e);
            messages.error(new BundleKey("messages", "error.execution"));
        }
        return null;
    }

    public String preInvoicingRepport(long id) {
        try {
            return "/pages/billing/invoicing/preInvoicingReports.xhtml?edit=false&preReport=true&objectId=" + id;

        } catch (Exception e) {
            log.error("Failed to retrieve pre-invoicing report", e);
            messages.error(new BundleKey("messages", "error.execution"));
        }
        return null;
    }

    public String postInvoicingRepport(long id) {
        try {
            return "/pages/billing/invoicing/postInvoicingReports.xhtml?edit=false&postReport=true&objectId=" + id;

        } catch (Exception e) {
            log.error("Failed to retrieve post-invoicing report", e);
            messages.error(new BundleKey("messages", "error.execution"));
        }
        return null;
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
}