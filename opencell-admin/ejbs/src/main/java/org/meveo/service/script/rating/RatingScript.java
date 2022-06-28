package org.meveo.service.script.rating;


import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

import org.meveo.admin.exception.BusinessException;
import org.meveo.admin.exception.InvalidELException;
import org.meveo.admin.exception.NoPricePlanException;
import org.meveo.admin.exception.PriceELErrorException;
import org.meveo.model.billing.Amounts;
import org.meveo.model.billing.BillingAccount;
import org.meveo.model.billing.ChargeInstance;
import org.meveo.model.billing.RecurringChargeInstance;
import org.meveo.model.billing.ServiceInstance;
import org.meveo.model.billing.Subscription;
import org.meveo.model.billing.WalletOperation;
import org.meveo.model.catalog.ChargeTemplate;
import org.meveo.model.catalog.OfferTemplate;
import org.meveo.model.catalog.PricePlanMatrix;
import org.meveo.model.catalog.PricePlanMatrixLine;
import org.meveo.model.catalog.PricePlanMatrixVersion;
import org.meveo.model.catalog.RecurringChargeTemplate;
import org.meveo.model.cpq.contract.ContractItem;
import org.meveo.model.crm.Customer;
import org.meveo.model.crm.Provider;
import org.meveo.model.payments.CustomerAccount;
import org.meveo.service.billing.impl.RatingService;
import org.meveo.service.billing.impl.UsageRatingService;
import org.meveo.service.catalog.impl.PricePlanMatrixService;
import org.meveo.service.catalog.impl.PricePlanMatrixVersionService;
import org.meveo.service.script.Script;
import org.meveo.service.tax.TaxMappingService;
import org.meveo.service.tax.TaxMappingService.TaxInfo;

public class RatingScript extends Script {



    private TaxMappingService taxMappingService = (TaxMappingService) getServiceInterface(TaxMappingService.class.getSimpleName());
    private PricePlanMatrixVersionService pricePlanMatrixVersionService = (PricePlanMatrixVersionService) getServiceInterface(PricePlanMatrixVersionService.class.getSimpleName());
    private PricePlanMatrixService pricePlanMatrixService = (PricePlanMatrixService) getServiceInterface(PricePlanMatrixService.class.getSimpleName());
    private UsageRatingService ratingService = (UsageRatingService) getServiceInterface(UsageRatingService.class.getSimpleName());
    
    private Provider appProvider;
    final private static BigDecimal HUNDRED = new BigDecimal("100");
    @Override
    public void execute(Map<String, Object> executeContext) throws BusinessException {
        log.info("Execute executeContext:{}", executeContext);
         appProvider = (Provider) executeContext.get(Script.CONTEXT_APP_PROVIDER);
         
        WalletOperation wo = (WalletOperation) executeContext.get(Script.CONTEXT_ENTITY);
        if (wo == null) {
            throw new BusinessException("Wallet operation is null!");
        }
        log.info("walletOperation {}", wo);
        OfferTemplate offer = wo.getOfferTemplate();
        log.info("offer {}", offer);
        recalculateWoRated(wo,appProvider);
    }

    private void recalculateWoRated(WalletOperation wo,Provider appProvider) {

    	BigDecimal unitPriceWithoutTax = wo.getUnitAmountWithoutTax();
        BigDecimal unitPriceWithTax = wo.getUnitAmountWithTax();
        ChargeInstance chargeInstance=wo.getChargeInstance();
        RecurringChargeTemplate recChargeTemplate = null;
        if (chargeInstance != null && chargeInstance.getChargeMainType() == ChargeTemplate.ChargeMainTypeEnum.RECURRING) {
            recChargeTemplate = ((RecurringChargeInstance) chargeInstance).getRecurringChargeTemplate();
        }

        // Determine and set tax if it was not set before.
        // An absence of tax class and presence of tax means that tax was set manually and should not be recalculated at invoicing time.
        if (wo.getTax() == null) {
            TaxInfo taxInfo = taxMappingService.determineTax(wo);
            if(taxInfo==null) {
            	throw new BusinessException("No tax found for the chargeInstance "+chargeInstance.getCode());
            }
            wo.setTaxClass(taxInfo.taxClass);
            wo.setTax(taxInfo.tax);
            wo.setTaxPercent(taxInfo.tax.getPercent());
        }

        PricePlanMatrix pricePlan = null;
        // Unit price was not overridden
        if ((unitPriceWithoutTax == null && appProvider.isEntreprise()) || (unitPriceWithTax == null && !appProvider.isEntreprise())) {

            List<PricePlanMatrix> chargePricePlans = pricePlanMatrixService.getActivePricePlansByChargeCode(wo.getCode());
            if (chargePricePlans == null || chargePricePlans.isEmpty()) {
                throw new NoPricePlanException("No price plan for charge code " + wo.getCode());
            }

            // Check if unit price was not overridden by a contract
            Subscription subscription = wo.getSubscription();
            BillingAccount billingAccount = subscription.getUserAccount().getBillingAccount();
            CustomerAccount customerAccount = billingAccount.getCustomerAccount();
            Customer customer = customerAccount.getCustomer();
           
            ServiceInstance serviceInstance = chargeInstance.getServiceInstance();
            ChargeTemplate chargeTemplate = chargeInstance.getChargeTemplate();
            OfferTemplate offerTemplate = subscription.getOffer();
            ContractItem contractItem = null;

            if (unitPriceWithoutTax == null) {
                pricePlan = chargePricePlans.get(0);
                if (pricePlan == null) {
                    throw new NoPricePlanException("No price plan matched for charge code " + wo.getCode());
                }

                log.debug("Will apply priceplan {} for {}", pricePlan.getId(), wo.getCode());

                Amounts unitPrices = determineUnitPrice(pricePlan, wo);
                unitPriceWithoutTax = unitPrices.getAmountWithoutTax();
                unitPriceWithTax = unitPrices.getAmountWithTax();
            }
        }


        ratingService.calculateAmounts(wo, unitPriceWithoutTax, unitPriceWithTax);


        log.info("WalletOperation", wo);
    }

    private Amounts determineUnitPrice(PricePlanMatrix pricePlan, WalletOperation wo) throws PriceELErrorException, InvalidELException {

        BigDecimal priceWithoutTax = null;
        BigDecimal priceWithTax = null;
        
        PricePlanMatrixVersion ppmVersion = pricePlanMatrixVersionService.getLastPublishedVersion(pricePlan.getCode());
        if (ppmVersion != null) {
            if (!ppmVersion.isMatrix()) {
                if (appProvider.isEntreprise()) {
                	priceWithoutTax = ppmVersion.getAmountWithoutTax();
                } else {
                	priceWithoutTax = ppmVersion.getAmountWithTax();
                }
//                if (ppmVersion.getPriceEL() != null) {
//                	priceWithoutTax = evaluateAmountExpression(ppmVersion.getPriceEL(), wo, wo.getChargeInstance().getUserAccount(), null, priceWithoutTax);
//                    if (priceWithoutTax == null) {
//                        throw new PriceELErrorException("Can't evaluate price for price plan " + ppmVersion.getId() + " EL:" + ppmVersion.getPriceEL());
//                    }
//                }
            } else {
                PricePlanMatrixLine pricePlanMatrixLine = pricePlanMatrixVersionService.loadPrices(ppmVersion, wo);
                if(pricePlanMatrixLine!=null) {
                	priceWithoutTax = pricePlanMatrixLine.getPriceWithoutTax();
//                    String amountEL = ppmVersion.getPriceEL();
//                    if (!StringUtils.isBlank(amountEL)) {
//                    	priceWithoutTax = evaluateAmountExpression(amountEL, wo, wo.getChargeInstance().getUserAccount(), null, priceWithoutTax);
//                    }
                }
                if (priceWithoutTax == null) {
                    throw new PriceELErrorException("no price for price plan version " + ppmVersion.getId() + "and charge instance : " + wo.getChargeInstance());
                }
            }
        } else {
           log.error("The pricePlan {} has no version",pricePlan.getCode());
        }
        return new Amounts(priceWithoutTax, priceWithTax);
    }

}
