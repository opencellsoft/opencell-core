package org.meveocrm.admin.action.reporting;

import java.util.ArrayList;
import java.util.List;

import javax.annotation.PostConstruct;
import javax.faces.bean.RequestScoped;
import javax.inject.Inject;
import javax.inject.Named;

import org.jboss.seam.security.Identity;
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
import org.meveo.model.crm.Provider;
import org.meveo.model.rating.EDRStatusEnum;
import org.meveo.security.MeveoUser;
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
		private Identity identity;
		
		private Provider currentProvider;
		
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
		   return usageChargeTemplateService.getNbrUsagesChrgWithNotPricePlan(getCurrentProvider());
		     }
    public int getNbrRecurringWithNotPricePlan(){ 
		    return recurringChargeTemplateService.getNbrRecurringChrgWithNotPricePlan(getCurrentProvider());
		     }
	public int getNbrOneShotWithNotPricePlan(){ 
		   return oneShotChargeTemplateService.getNbrOneShotWithNotPricePlan(getCurrentProvider());
		     }	
	
	
     
     public void constructChargesWithNotPricePlan(TabChangeEvent event){
    	 usagesWithNotPricePlList= usageChargeTemplateService.getUsagesChrgWithNotPricePlan(getCurrentProvider());
    	 recurringWithNotPricePlanList= recurringChargeTemplateService.getRecurringChrgWithNotPricePlan(getCurrentProvider());
    	 oneShotChrgWithNotPricePlanList= oneShotChargeTemplateService.getOneShotChrgWithNotPricePlan(getCurrentProvider());
     }
     
     public void constructTaxesNotAssociated(TabChangeEvent event){
    	 taxesNotAssociatedList=taxService.getTaxesNotAssociated(getCurrentProvider());
     }
     public void constructLanguagesNotAssociated(TabChangeEvent event){
    	 languagesNotAssociatedList= tradingLanguageService.getLanguagesNotAssociated(getCurrentProvider());
     }
     
     public void constructInvoiceCatNotAssociated(TabChangeEvent event){
    	 invoiceCatNotAssociatedList= invoiceCategoryService.getInvoiceCatNotAssociated(getCurrentProvider());
     }
     
     public void constructInvoiceSubCatNotAssociated(TabChangeEvent event){
    	 invoiceSubCatNotAssociatedList= invoiceSubCategoryService.getInvoiceSubCatNotAssociated(getCurrentProvider());
     }

     public void constructServicesWithNotOffer(TabChangeEvent event){
    	 servicesWithNotOfferList=serviceTemplateService.getServicesWithNotOffer(getCurrentProvider());
     }
     
     public void constructUsagesChrgNotAssociated(TabChangeEvent event){
    	 usagesChrgNotAssociatedList= usageChargeTemplateService.getUsagesChrgNotAssociated(getCurrentProvider());
     }
     
     public void constructCounterWithNotService(TabChangeEvent event){
    	 counterWithNotServicList= counterTemplateService.getCounterWithNotService(getCurrentProvider());	 
     } 
     public void constructRecurringNotAssociated(TabChangeEvent event){
    	 recurringNotAssociatedList= recurringChargeTemplateService.getRecurringChrgNotAssociated(getCurrentProvider());
     }
     public void constructTermChrgNotAssociated(TabChangeEvent event){
    	 terminationNotAssociatedList= oneShotChargeTemplateService.getTerminationChrgNotAssociated(getCurrentProvider());
     }
     public void constructSubChrgNotAssociated(TabChangeEvent event){
    	 subNotAssociatedList= oneShotChargeTemplateService.getSubscriptionChrgNotAssociated(getCurrentProvider());
     }
     
  
	       ConfigIssuesReportingDTO reportConfigDto;
	       
	        @PostConstruct
            public void init(){;
		    reportConfigDto = new ConfigIssuesReportingDTO();
		    reportConfigDto.setNbrChargesWithNotPricePlan(getNbrRecurringWithNotPricePlan()+getNbrUsagesWithNotPricePlan()+getNbrOneShotWithNotPricePlan());
		    
		    reportConfigDto.setNbrWalletOpOpen(walletOperationService.getNbrWalletOperationByStatus(WalletOperationStatusEnum.OPEN, getCurrentProvider()).intValue());
		    reportConfigDto.setNbrWalletOpRerated(walletOperationService.getNbrWalletOperationByStatus(WalletOperationStatusEnum.RERATED, getCurrentProvider()).intValue());
		    reportConfigDto.setNbrWalletOpReserved(walletOperationService.getNbrWalletOperationByStatus(WalletOperationStatusEnum.RESERVED, getCurrentProvider()).intValue());
		    reportConfigDto.setNbrWalletOpCancled(walletOperationService.getNbrWalletOperationByStatus(WalletOperationStatusEnum.CANCELED, getCurrentProvider()).intValue());
		    reportConfigDto.setNbrWalletOpTorerate(walletOperationService.getNbrWalletOperationByStatus(WalletOperationStatusEnum.TO_RERATE, getCurrentProvider()).intValue()); 
		    reportConfigDto.setNbrWalletOpTreated(walletOperationService.getNbrWalletOperationByStatus(WalletOperationStatusEnum.TREATED, getCurrentProvider()).intValue());
		    reportConfigDto.setNbrEdrOpen(walletOperationService.getNbrEdrByStatus(EDRStatusEnum.OPEN, getCurrentProvider()).intValue());
		    reportConfigDto.setNbrEdrRated(walletOperationService.getNbrEdrByStatus(EDRStatusEnum.RATED, getCurrentProvider()).intValue());
		    reportConfigDto.setNbrEdrRejected(walletOperationService.getNbrEdrByStatus(EDRStatusEnum.REJECTED, getCurrentProvider()).intValue());
		    
		    reportConfigDto.setNbrTaxesNotAssociated(taxService.getNbTaxesNotAssociated(getCurrentProvider()));
		    reportConfigDto.setNbrLanguagesNotAssociated(tradingLanguageService.getNbLanguageNotAssociated(getCurrentProvider()));
		    reportConfigDto.setNbrInvoiceCatNotAssociated(invoiceCategoryService.getNbInvCatNotAssociated(getCurrentProvider()));
		    reportConfigDto.setNbrInvoiceSubCatNotAssociated(invoiceSubCategoryService.getNbInvSubCatNotAssociated(getCurrentProvider()));
		    
		    reportConfigDto.setNbrServicesWithNotOffer(serviceTemplateService.getNbServiceWithNotOffer(getCurrentProvider()));
		    
		    reportConfigDto.setNbrUsagesChrgNotAssociated(usageChargeTemplateService.getNbrUsagesChrgNotAssociated(getCurrentProvider()));
		    reportConfigDto.setNbrCountersNotAssociated(counterTemplateService.getNbrCounterWithNotService(getCurrentProvider()));
		    
		    reportConfigDto.setNbrRecurringChrgNotAssociated(recurringChargeTemplateService.getNbrRecurringChrgNotAssociated(getCurrentProvider()));

		    reportConfigDto.setNbrTerminationChrgNotAssociated(oneShotChargeTemplateService.getNbrTerminationChrgNotAssociated(getCurrentProvider()));
		    reportConfigDto.setNbrSubChrgNotAssociated(oneShotChargeTemplateService.getNbrSubscriptionChrgNotAssociated(getCurrentProvider()));
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
			
			public Provider getCurrentProvider(){
				if(currentProvider==null){
					currentProvider=((MeveoUser)identity.getUser()).getCurrentProvider();
				}
				return currentProvider;
			}
}