package org.meveocrm.admin.action.reporting;

import java.util.ArrayList;
import java.util.List;

import javax.annotation.PostConstruct;
import javax.faces.bean.RequestScoped;
import javax.inject.Inject;
import javax.inject.Named;

import org.meveo.admin.action.admin.CurrentProvider;
import org.meveo.model.billing.InvoiceCategory;
import org.meveo.model.billing.InvoiceSubCategory;
import org.meveo.model.billing.Tax;
import org.meveo.model.billing.TradingLanguage;
import org.meveo.model.billing.WalletOperationStatusEnum;
import org.meveo.model.catalog.CounterTemplate;
import org.meveo.model.catalog.OneShotChargeTemplate;
import org.meveo.model.catalog.OneShotChargeTemplateTypeEnum;
import org.meveo.model.catalog.RecurringChargeTemplate;
import org.meveo.model.catalog.ServiceTemplate;
import org.meveo.model.catalog.UsageChargeTemplate;
import org.meveo.model.crm.Provider;
import org.meveo.model.rating.EDRStatusEnum;
import org.meveo.service.billing.impl.TradingLanguageService;
import org.meveo.service.billing.impl.WalletOperationService;
import org.meveo.service.catalog.impl.CounterTemplateService;
import org.meveo.service.catalog.impl.InvoiceCategoryService;
import org.meveo.service.catalog.impl.InvoiceSubCategoryService;
import org.meveo.service.catalog.impl.OneShotChargeTemplateService;
import org.meveo.service.catalog.impl.RecurringChargeTemplateService;
import org.meveo.service.catalog.impl.ServiceTemplateService;
import org.meveo.service.catalog.impl.TaxService;
import org.meveo.service.catalog.impl.UsageChargeTemplateService;
import org.primefaces.event.TabChangeEvent;
 
@Named
@RequestScoped
public class ConfigIssuesReportingBean{

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
		private InvoiceSubCategoryService invoiceSubCategoryService;
		
		@Inject
		private ServiceTemplateService serviceTemplateService;
		
		@Inject
		private CounterTemplateService counterTemplateService;
		
		
		@Inject
		@CurrentProvider
		protected Provider currentProvider;
		
		List<Tax> taxesNotAssociatedList=new ArrayList<Tax>();
		List<UsageChargeTemplate> usagesWithNotPricePlList = new ArrayList<UsageChargeTemplate>();
		List<RecurringChargeTemplate> recurringWithNotPricePlanList = new ArrayList<RecurringChargeTemplate>();
		List<OneShotChargeTemplate> oneShotChrgWithNotPricePlanList = new ArrayList<OneShotChargeTemplate>();
		List<TradingLanguage> languagesNotAssociatedList = new ArrayList<TradingLanguage>();
		List<InvoiceCategory> invoiceCatNotAssociatedList = new ArrayList<InvoiceCategory>();
		List<InvoiceSubCategory> invoiceSubCatNotAssociatedList = new ArrayList<InvoiceSubCategory>();
	    List<ServiceTemplate> servicesWithNotOfferList = new ArrayList<ServiceTemplate>();
	    List<UsageChargeTemplate> usagesChrgNotAssociatedList=new ArrayList<UsageChargeTemplate>();
	    List<CounterTemplate> counterWithNotServicList = new ArrayList<CounterTemplate>();
	    List<RecurringChargeTemplate> recurringNotAssociatedList = new ArrayList<RecurringChargeTemplate>();
	    List<OneShotChargeTemplate> terminationNotAssociatedList=new ArrayList<OneShotChargeTemplate>();
	    List<OneShotChargeTemplate> subNotAssociatedList=new ArrayList<OneShotChargeTemplate>();
		
		
		
		
	 
	public int getNbrUsagesWithNotPricePlan(){ 
		   return usageChargeTemplateService.getNbrUsagesChrgWithNotPricePlan(currentProvider);
		     }
    public int getNbrRecurringWithNotPricePlan(){ 
		    return recurringChargeTemplateService.getNbrRecurringChrgWithNotPricePlan(currentProvider);
		     }
	public int getNbrOneShotWithNotPricePlan(){ 
		   return oneShotChargeTemplateService.getNbrOneShotWithNotPricePlan(currentProvider);
		     }	
	
	
     
     public void constructChargesWithNotPricePlan(TabChangeEvent event){
    	 usagesWithNotPricePlList= usageChargeTemplateService.getUsagesChrgWithNotPricePlan(currentProvider);
    	 recurringWithNotPricePlanList= recurringChargeTemplateService.getRecurringChrgWithNotPricePlan(currentProvider);
    	 oneShotChrgWithNotPricePlanList= oneShotChargeTemplateService.getOneShotChrgWithNotPricePlan(currentProvider);
     }
     
     public void constructTaxesNotAssociated(TabChangeEvent event){
    	 taxesNotAssociatedList=taxService.getTaxesNotAssociated(currentProvider);
     }
     public void constructLanguagesNotAssociated(TabChangeEvent event){
    	 languagesNotAssociatedList= tradingLanguageService.getLanguagesNotAssociated(currentProvider);
     }
     
     public void constructInvoiceCatNotAssociated(TabChangeEvent event){
    	 invoiceCatNotAssociatedList= invoiceCategoryService.getInvoiceCatNotAssociated(currentProvider);
     }
     
     public void constructInvoiceSubCatNotAssociated(TabChangeEvent event){
    	 invoiceSubCatNotAssociatedList= invoiceSubCategoryService.getInvoiceSubCatNotAssociated(currentProvider);
     }

     public void constructServicesWithNotOffer(TabChangeEvent event){
    	 servicesWithNotOfferList=serviceTemplateService.getServicesWithNotOffer(currentProvider);
     }
     
     public void constructUsagesChrgNotAssociated(TabChangeEvent event){
    	 usagesChrgNotAssociatedList= usageChargeTemplateService.getUsagesChrgNotAssociated(currentProvider);
     }
     
     public void constructCounterWithNotService(TabChangeEvent event){
    	 counterWithNotServicList= counterTemplateService.getCounterWithNotService(currentProvider);	 
     } 
     public void constructRecurringNotAssociated(TabChangeEvent event){
    	 recurringNotAssociatedList= recurringChargeTemplateService.getRecurringChrgNotAssociated(currentProvider);
     }
     public void constructTermChrgNotAssociated(TabChangeEvent event){
    	 terminationNotAssociatedList= oneShotChargeTemplateService.getTerminationChrgNotAssociated(currentProvider);
     }
     public void constructSubChrgNotAssociated(TabChangeEvent event){
    	 subNotAssociatedList= oneShotChargeTemplateService.getSubscriptionChrgNotAssociated(currentProvider);
     }
     
  
	       ConfigIssuesReportingDTO reportConfigDto;
	       
	        @PostConstruct
            public void init(){;
		    reportConfigDto = new ConfigIssuesReportingDTO();
		    reportConfigDto.setNbrChargesWithNotPricePlan(getNbrRecurringWithNotPricePlan()+getNbrUsagesWithNotPricePlan()+getNbrOneShotWithNotPricePlan());
		    
		    reportConfigDto.setNbrWalletOpOpen(walletOperationService.getNbrWalletOperationByStatus(WalletOperationStatusEnum.OPEN, currentProvider).intValue());
		    reportConfigDto.setNbrWalletOpRerated(walletOperationService.getNbrWalletOperationByStatus(WalletOperationStatusEnum.RERATED, currentProvider).intValue());
		    reportConfigDto.setNbrWalletOpReserved(walletOperationService.getNbrWalletOperationByStatus(WalletOperationStatusEnum.RESERVED, currentProvider).intValue());
		    reportConfigDto.setNbrWalletOpCancled(walletOperationService.getNbrWalletOperationByStatus(WalletOperationStatusEnum.CANCELED, currentProvider).intValue());
		    reportConfigDto.setNbrWalletOpTorerate(walletOperationService.getNbrWalletOperationByStatus(WalletOperationStatusEnum.TO_RERATE, currentProvider).intValue()); 
		    reportConfigDto.setNbrWalletOpTreated(walletOperationService.getNbrWalletOperationByStatus(WalletOperationStatusEnum.TREATED, currentProvider).intValue());
		    reportConfigDto.setNbrEdrOpen(walletOperationService.getNbrEdrByStatus(EDRStatusEnum.OPEN, currentProvider).intValue());
		    reportConfigDto.setNbrEdrRated(walletOperationService.getNbrEdrByStatus(EDRStatusEnum.RATED, currentProvider).intValue());
		    reportConfigDto.setNbrEdrRejected(walletOperationService.getNbrEdrByStatus(EDRStatusEnum.REJECTED, currentProvider).intValue());
		    
		    reportConfigDto.setNbrTaxesNotAssociated(taxService.getNbTaxesNotAssociated(currentProvider));
		    reportConfigDto.setNbrLanguagesNotAssociated(tradingLanguageService.getNbLanguageNotAssociated(currentProvider));
		    reportConfigDto.setNbrInvoiceCatNotAssociated(invoiceCategoryService.getNbInvCatNotAssociated(currentProvider));
		    reportConfigDto.setNbrInvoiceSubCatNotAssociated(invoiceSubCategoryService.getNbInvSubCatNotAssociated(currentProvider));
		    
		    reportConfigDto.setNbrServicesWithNotOffer(serviceTemplateService.getNbServiceWithNotOffer(currentProvider));
		    
		    reportConfigDto.setNbrUsagesChrgNotAssociated(usageChargeTemplateService.getNbrUsagesChrgNotAssociated(currentProvider));
		    reportConfigDto.setNbrCountersNotAssociated(counterTemplateService.getNbrCounterWithNotService(currentProvider));
		    
		    reportConfigDto.setNbrRecurringChrgNotAssociated(recurringChargeTemplateService.getNbrRecurringChrgNotAssociated(currentProvider));

		    reportConfigDto.setNbrTerminationChrgNotAssociated(oneShotChargeTemplateService.getNbrTerminationChrgNotAssociated(currentProvider));
		    reportConfigDto.setNbrSubChrgNotAssociated(oneShotChargeTemplateService.getNbrSubscriptionChrgNotAssociated(currentProvider));
	        }
	        
	   
			public ConfigIssuesReportingDTO getReportConfigDto() {
				return reportConfigDto;
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
			public List<InvoiceSubCategory> getInvoiceSubCatNotAssociatedList() {
				return invoiceSubCatNotAssociatedList;
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
}