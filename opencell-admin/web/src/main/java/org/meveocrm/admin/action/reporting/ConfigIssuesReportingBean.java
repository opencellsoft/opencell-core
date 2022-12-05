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

package org.meveocrm.admin.action.reporting;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import jakarta.faces.view.ViewScoped;
import jakarta.inject.Inject;
import jakarta.inject.Named;

import org.meveo.admin.action.BaseBean;
import org.meveo.commons.utils.ParamBean;
import org.meveo.model.BaseEntity;
import org.meveo.model.billing.InvoiceCategory;
import org.meveo.model.billing.InvoiceSubCategory;
import org.meveo.model.billing.Tax;
import org.meveo.model.billing.TradingLanguage;
import org.meveo.model.billing.WalletOperationStatusEnum;
import org.meveo.model.catalog.CounterTemplate;
import org.meveo.model.catalog.OneShotChargeTemplate;
import org.meveo.model.catalog.RecurringChargeTemplate;
import org.meveo.model.catalog.ServiceTemplate;
import org.meveo.model.catalog.UsageChargeTemplate;
import org.meveo.model.rating.EDRStatusEnum;
import org.meveo.model.scripts.ScriptInstance;
import org.meveo.model.tax.TaxCategory;
import org.meveo.service.base.local.IPersistenceService;
import org.meveo.service.billing.impl.TradingLanguageService;
import org.meveo.service.billing.impl.WalletOperationService;
import org.meveo.service.catalog.impl.CounterTemplateService;
import org.meveo.service.catalog.impl.InvoiceCategoryService;
import org.meveo.service.catalog.impl.OneShotChargeTemplateService;
import org.meveo.service.catalog.impl.RecurringChargeTemplateService;
import org.meveo.service.catalog.impl.ServiceTemplateService;
import org.meveo.service.catalog.impl.TaxService;
import org.meveo.service.catalog.impl.UsageChargeTemplateService;
import org.meveo.service.script.ScriptInstanceService;
import org.meveo.service.tax.TaxCategoryService;
import org.primefaces.event.TabChangeEvent;

/**
 * @author Wassim Drira
 * @author melyoussoufi
 * @lastModifiedVersion 7.2.0
 *
 */
@Named
@ViewScoped
public class ConfigIssuesReportingBean extends BaseBean<BaseEntity> {

    private static final long serialVersionUID = 1L;

    @Inject
    private WalletOperationService walletOperationService;

    @Inject
    private UsageChargeTemplateService usageChargeTemplateService;

    @Inject
    private RecurringChargeTemplateService recurringChargeTemplateService;

    @Inject
    private OneShotChargeTemplateService oneShotChargeTemplateService;

    @Inject
    private TaxService taxService;

    @Inject
    private TradingLanguageService tradingLanguageService;

    @Inject
    private InvoiceCategoryService invoiceCategoryService;

    @Inject
    private TaxCategoryService taxCategoryService;

    @Inject
    private ServiceTemplateService serviceTemplateService;

    @Inject
    private CounterTemplateService counterTemplateService;

    @Inject
    private ScriptInstanceService scriptInstanceService;

    private List<Entry<String, String>> jaspers;

    private Integer nbrTaxesNotAssociated;
    private Integer nbrLanguageNotAssociated;
    private Integer nbrInvCatNotAssociated;
    private Integer nbrTaxCategoryNotAssociated;
    private Integer nbrServiceWithNotOffer;
    private Integer nbrUsagesChrgNotAssociated;
    private Integer nbrCounterWithNotService;
    private Integer nbrRecurringChrgNotAssociated;
    private Integer nbrTerminationChrgNotAssociated;
    private Integer nbrSubscriptionChrgNotAssociated;
    private Integer nbrScriptInstanceWithError;
    private Integer nbrJasperNotFound;

    List<Tax> taxesNotAssociatedList = new ArrayList<Tax>();
    List<UsageChargeTemplate> usagesWithNotPricePlList = new ArrayList<UsageChargeTemplate>();
    List<RecurringChargeTemplate> recurringWithNotPricePlanList = new ArrayList<RecurringChargeTemplate>();
    List<OneShotChargeTemplate> oneShotChrgWithNotPricePlanList = new ArrayList<OneShotChargeTemplate>();
    List<TradingLanguage> languagesNotAssociatedList = new ArrayList<TradingLanguage>();
    List<InvoiceCategory> invoiceCatNotAssociatedList = new ArrayList<InvoiceCategory>();
    List<TaxCategory> taxCatNotAssociatedList = new ArrayList<TaxCategory>();
    List<ServiceTemplate> servicesWithNotOfferList = new ArrayList<ServiceTemplate>();
    List<UsageChargeTemplate> usagesChrgNotAssociatedList = new ArrayList<UsageChargeTemplate>();
    List<CounterTemplate> counterWithNotServicList = new ArrayList<CounterTemplate>();
    List<RecurringChargeTemplate> recurringNotAssociatedList = new ArrayList<RecurringChargeTemplate>();
    List<OneShotChargeTemplate> terminationNotAssociatedList = new ArrayList<OneShotChargeTemplate>();
    List<OneShotChargeTemplate> subNotAssociatedList = new ArrayList<OneShotChargeTemplate>();
    List<ScriptInstance> scriptInstanceWithErrorList = new ArrayList<ScriptInstance>();

    private ConfigIssuesReportingDTO walletReportConfig;

    private ConfigIssuesReportingDTO edrReportConfig;

    public void constructTaxesNotAssociated(TabChangeEvent event) {
        taxesNotAssociatedList = taxService.getTaxesNotAssociated();
    }

    public void constructLanguagesNotAssociated(TabChangeEvent event) {
        languagesNotAssociatedList = tradingLanguageService.getLanguagesNotAssociated();
    }

    public void constructInvoiceCatNotAssociated(TabChangeEvent event) {
        invoiceCatNotAssociatedList = invoiceCategoryService.getInvoiceCatNotAssociated();
    }

    public void constructTaxCategoryNotAssociated(TabChangeEvent event) {
        taxCatNotAssociatedList = taxCategoryService.getTaxCategoryNotAssociated();
    }

    public void constructServicesWithNotOffer(TabChangeEvent event) {
        servicesWithNotOfferList = serviceTemplateService.getServicesWithNotOffer();
    }

    public void constructUsagesChrgNotAssociated(TabChangeEvent event) {
        usagesChrgNotAssociatedList = usageChargeTemplateService.getUsagesChrgNotAssociated();
    }

    public void constructCounterWithNotService(TabChangeEvent event) {
        counterWithNotServicList = counterTemplateService.getCounterWithNotService();
    }

    public void constructRecurringNotAssociated(TabChangeEvent event) {
        recurringNotAssociatedList = recurringChargeTemplateService.getRecurringChrgNotAssociated();
    }

    public void constructTermChrgNotAssociated(TabChangeEvent event) {
        terminationNotAssociatedList = oneShotChargeTemplateService.getTerminationChrgNotAssociated();
    }

    public void constructSubChrgNotAssociated(TabChangeEvent event) {
        subNotAssociatedList = oneShotChargeTemplateService.getSubscriptionChrgNotAssociated();
    }

    public void constructScriptInstancesWithError(TabChangeEvent event) {
        scriptInstanceWithErrorList = scriptInstanceService.getScriptInstancesWithError();
    }

    public void constructWalletOperation(TabChangeEvent event) {

        if (walletReportConfig == null) {
            walletReportConfig = new ConfigIssuesReportingDTO();
            List<Object[]> WOStatus = walletOperationService.getNbrWalletsOperationByStatus();
            for (Object[] s : WOStatus) {
                if (s[0] == null || WalletOperationStatusEnum.OPEN.equals(s[0])) {
                    walletReportConfig.setNbrWalletOpOpen(((Long) s[1]).intValue());
                } else if (WalletOperationStatusEnum.RERATED.equals(s[0])) {
                    walletReportConfig.setNbrWalletOpRerated(((Long) s[1]).intValue());
                } else if (WalletOperationStatusEnum.RESERVED.equals(s[0])) {
                    walletReportConfig.setNbrWalletOpReserved(((Long) s[1]).intValue());
                } else if (WalletOperationStatusEnum.CANCELED.equals(s[0])) {
                    walletReportConfig.setNbrWalletOpCancled(((Long) s[1]).intValue());
                } else if (WalletOperationStatusEnum.TO_RERATE.equals(s[0])) {
                    walletReportConfig.setNbrWalletOpTorerate(((Long) s[1]).intValue());
                } else if (WalletOperationStatusEnum.TREATED.equals(s[0])) {
                    walletReportConfig.setNbrWalletOpTreated(((Long) s[1]).intValue());
                } else if (WalletOperationStatusEnum.SCHEDULED.equals(s[0])) {
                    walletReportConfig.setNbrWalletOpScheduled(((Long) s[1]).intValue());
                } else if (WalletOperationStatusEnum.REJECTED.equals(s[0])) {
                    walletReportConfig.setNbrWalletOpRejected(((Long) s[1]).intValue());
                }
            }
        }
    }

    public void constructEdr(TabChangeEvent event) {

        if (edrReportConfig == null) {

            edrReportConfig = new ConfigIssuesReportingDTO();

            List<Object[]> EdrStatus = walletOperationService.getNbrEdrByStatus();
            for (Object[] s : EdrStatus) {
                if (s[0] == null || EDRStatusEnum.OPEN.equals(s[0])) {
                    edrReportConfig.setNbrEdrOpen(((Long) s[1]).intValue());
                } else if (EDRStatusEnum.RATED.equals(s[0])) {
                    edrReportConfig.setNbrEdrRated(((Long) s[1]).intValue());
                } else if (EDRStatusEnum.REJECTED.equals(s[0])) {
                    edrReportConfig.setNbrEdrRejected(((Long) s[1]).intValue());
                } else if (EDRStatusEnum.MEDIATING.equals(s[0])) {
                    edrReportConfig.setNbrEdrMediating(((Long) s[1]).intValue());
                } else if (EDRStatusEnum.AGGREGATED.equals(s[0])) {
                    edrReportConfig.setNbrEdrAggregated(((Long) s[1]).intValue());
                }
            }
        }
    }

    private Map<String, String> getJasperFiles() throws IOException {
        Map<String, String> jasperFiles = new HashMap<String, String>();
        ParamBean paramBean = paramBeanFactory.getInstance();
        String jasperCommercial = paramBean.getProperty("jasper.invoiceTemplate.commercial", "invoice.jasper");
        String jasperAdjustment = paramBean.getProperty("jasper.invoiceTemplate.adjustment", "invoice.jasper");
        // check jaspers files
        File jasperDir = new File(paramBeanFactory.getChrootDir() + File.separator + "jasper");
        if (!jasperDir.exists()) {
            jasperDir.mkdirs();
        }
        log.info("Jaspers template used :" + jasperDir.getPath());
        File[] foldersList = jasperDir.listFiles();
        String commercialRep = null;
        String adjustRep = null;
        File commercialInvoice = null;
        File adjustInvoice = null;
        if (foldersList != null && foldersList.length > 0) {
            for (File f : foldersList) {
                adjustRep = f.getCanonicalPath() + File.separator + "invoiceAdjustmentPdf";
                adjustInvoice = new File(adjustRep + File.separator + jasperCommercial);
                if (!adjustInvoice.exists()) {
                    jasperFiles.put(adjustRep, jasperAdjustment);
                }
                commercialRep = f.getCanonicalPath() + File.separator + "pdf";
                commercialInvoice = new File(commercialRep + File.separator + jasperCommercial);
                if (!commercialInvoice.exists()) {
                    jasperFiles.put(commercialRep, jasperCommercial);
                }
            }
        }
        return jasperFiles;
    }

    public Integer getNbrJasperNotFound() {

        if (nbrJasperNotFound == null) {
            try {
                Map<String, String> jasperFilesList = getJasperFiles();

                if (jasperFilesList != null && jasperFilesList.size() > 0) {
                    jaspers = new ArrayList<>(jasperFilesList.entrySet());
                }
                nbrJasperNotFound = jasperFilesList.size();
            } catch (IOException e) {
                nbrJasperNotFound = -1;
            }
        }
        return nbrJasperNotFound;
    }

    public Integer getNbTaxesNotAssociated() {

        if (nbrTaxesNotAssociated == null) {
            nbrTaxesNotAssociated = taxService.getNbTaxesNotAssociated();
        }
        return nbrTaxesNotAssociated;
    }

    public Integer getNbLanguageNotAssociated() {

        if (nbrLanguageNotAssociated == null) {
            nbrLanguageNotAssociated = tradingLanguageService.getNbLanguageNotAssociated();
        }
        return nbrLanguageNotAssociated;
    }

    public Integer getNbInvCatNotAssociated() {

        if (nbrInvCatNotAssociated == null) {
            nbrInvCatNotAssociated = invoiceCategoryService.getNbInvCatNotAssociated();
        }
        return nbrInvCatNotAssociated;
    }

    public Integer getNbTaxCategoryNotAssociated() {
        if (nbrTaxCategoryNotAssociated == null) {
            nbrTaxCategoryNotAssociated = taxCategoryService.getNbTaxCategoryNotAssociated();
        }
        return nbrTaxCategoryNotAssociated;
    }

    public Integer getNbServiceWithNotOffer() {
        if (nbrServiceWithNotOffer == null) {
            nbrServiceWithNotOffer = serviceTemplateService.getNbServiceWithNotOffer();
        }
        return nbrServiceWithNotOffer;
    }

    public Integer getNbrUsagesChrgNotAssociated() {
        if (nbrUsagesChrgNotAssociated == null) {
            nbrUsagesChrgNotAssociated = usageChargeTemplateService.getNbrUsagesChrgNotAssociated();
        }
        return nbrUsagesChrgNotAssociated;
    }

    public Integer getNbrCounterWithNotService() {
        if (nbrCounterWithNotService == null) {
            nbrCounterWithNotService = counterTemplateService.getNbrCounterWithNotService();
        }
        return nbrCounterWithNotService;
    }

    public Integer getNbrRecurringChrgNotAssociated() {
        if (nbrRecurringChrgNotAssociated == null) {
            nbrRecurringChrgNotAssociated = recurringChargeTemplateService.getNbrRecurringChrgNotAssociated();
        }
        return nbrRecurringChrgNotAssociated;
    }

    public Integer getNbrTerminationChrgNotAssociated() {
        if (nbrTerminationChrgNotAssociated == null) {
            nbrTerminationChrgNotAssociated = oneShotChargeTemplateService.getNbrTerminationChrgNotAssociated();
        }
        return nbrTerminationChrgNotAssociated;
    }

    public Integer getNbrSubscriptionChrgNotAssociated() {
        if (nbrSubscriptionChrgNotAssociated == null) {
            nbrSubscriptionChrgNotAssociated = oneShotChargeTemplateService.getNbrSubscriptionChrgNotAssociated();
        }
        return nbrSubscriptionChrgNotAssociated;
    }

    public Integer getNbrScriptInstanceWithError() {
        if (nbrScriptInstanceWithError == null) {
            nbrScriptInstanceWithError = Long.valueOf(scriptInstanceService.countScriptInstancesWithError()).intValue();
        }
        return nbrScriptInstanceWithError;
    }

    public ConfigIssuesReportingDTO getWalletReportConfig() {
        return walletReportConfig;
    }

    public ConfigIssuesReportingDTO getEdrReportConfig() {
        return edrReportConfig;
    }

    public List<Tax> getTaxesNotAssociatedList() {
        return taxesNotAssociatedList;
    }

    public List<UsageChargeTemplate> getUsagesWithNotPricePlList() {
        return usagesWithNotPricePlList;
    }

    public List<RecurringChargeTemplate> getRecurringWithNotPricePlanList() {
        return recurringWithNotPricePlanList;
    }

    public List<OneShotChargeTemplate> getOneShotChrgWithNotPricePlanList() {
        return oneShotChrgWithNotPricePlanList;
    }

    public List<TradingLanguage> getLanguagesNotAssociatedList() {
        return languagesNotAssociatedList;
    }

    public List<InvoiceCategory> getInvoiceCatNotAssociatedList() {
        return invoiceCatNotAssociatedList;
    }

    public List<TaxCategory> getTaxCategoryNotAssociatedList() {
        return taxCatNotAssociatedList;
    }

    public List<ServiceTemplate> getServicesWithNotOfferList() {
        return servicesWithNotOfferList;
    }

    public List<UsageChargeTemplate> getUsagesChrgNotAssociatedList() {
        return usagesChrgNotAssociatedList;
    }

    public List<CounterTemplate> getCounterWithNotServicList() {
        return counterWithNotServicList;
    }

    public List<RecurringChargeTemplate> getRecurringNotAssociatedList() {
        return recurringNotAssociatedList;
    }

    public List<OneShotChargeTemplate> getTerminationNotAssociatedList() {
        return terminationNotAssociatedList;
    }

    public List<OneShotChargeTemplate> getSubNotAssociatedList() {
        return subNotAssociatedList;
    }

    public List<ScriptInstance> getScriptInstanceWithErrorList() {
        return scriptInstanceWithErrorList;
    }

    @Override
    public IPersistenceService<BaseEntity> getPersistenceService() {
        throw new UnsupportedOperationException();
    }

    @Override
    public String getEditViewName() {
        return "";
    }

    public List<Entry<String, String>> getJaspers() {
        return jaspers;
    }

    public void setJaspers(List<Entry<String, String>> jaspers) {
        this.jaspers = jaspers;
    }

    public Integer getNbrJasperDir() throws IOException {
        return getJasperFiles().size();
    }

}
