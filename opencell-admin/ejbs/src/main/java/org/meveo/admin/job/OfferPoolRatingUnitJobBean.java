package org.meveo.admin.job;

import java.math.BigDecimal;
import java.util.Date;
import java.util.List;
import java.util.Map;

import javax.ejb.Stateless;
import javax.ejb.TransactionAttribute;
import javax.ejb.TransactionAttributeType;
import javax.inject.Inject;

import org.meveo.admin.exception.BusinessException;
import org.meveo.jpa.JpaAmpNewTx;
import org.meveo.model.billing.RatedTransaction;
import org.meveo.model.billing.RatedTransactionStatusEnum;
import org.meveo.model.billing.UsageChargeInstance;
import org.meveo.model.billing.WalletOperation;
import org.meveo.model.billing.WalletOperationStatusEnum;
import org.meveo.model.catalog.PricePlanMatrix;
import org.meveo.model.catalog.ServiceTemplate;
import org.meveo.model.crm.Provider;
import org.meveo.model.jobs.JobExecutionResultImpl;
import org.meveo.service.billing.impl.RatedTransactionService;
import org.meveo.service.billing.impl.WalletOperationService;
import org.meveo.service.catalog.impl.PricePlanMatrixService;
import org.meveo.service.catalog.impl.ServiceTemplateService;
import org.meveo.service.crm.impl.CustomFieldInstanceService;
import org.meveo.util.ApplicationProvider;
import org.slf4j.Logger;

/**
 * Calculate the overrun by Agency-Offer-ChargeType and create WalletOperation
 *
 * @author BEN AICHA Amine
 * @author BOUKAYOUA Mounir
 */
@Stateless
public class OfferPoolRatingUnitJobBean {

    private static final String CF_POOL_PER_OFFER_MAP = "POOL_PER_OFFER_MAP";

    private static final String OVER_CHARGE_INSTANCE_QUERY = "Select uci \n" + "From UsageChargeInstance As uci \n"
            + "Where uci.serviceInstance = :serviceInstance And uci.code = :code";

    @Inject
    private Logger log;

    @Inject
    private PricePlanMatrixService pricePlanMatrixService;

    @Inject
    private ServiceTemplateService serviceTemplateService;

    @Inject
    private WalletOperationService walletOperationService;

    @Inject
    private RatedTransactionService ratedTransactionService;

    @Inject
    private CustomFieldInstanceService cfiService;

    @Inject
    @ApplicationProvider
    protected Provider appProvider;

    @SuppressWarnings({ "unchecked" })
    @JpaAmpNewTx
    @TransactionAttribute(TransactionAttributeType.REQUIRES_NEW)
    public void execute(JobExecutionResultImpl result, Long walletOperationId) throws BusinessException {
        log.info("Check overage usage on walletOperationId={}", walletOperationId);
        try {
            WalletOperation walletOperation = walletOperationService.findById(walletOperationId);

            ServiceTemplate serviceTemplate = walletOperation.getServiceInstance().getServiceTemplate();

            String overageUnit = (String) cfiService.getCFValue(serviceTemplate, "overageUnit");
            Double overagePrice = (Double) cfiService.getCFValue(serviceTemplate, "overagePrice");
            Double multiplier = ((Map<String, Double>) cfiService.getInheritedCFValueByKey(appProvider, "CF_P_USAGE_UNITS", overageUnit)).get("multiplier");

            String agencyCode = walletOperation.getSubscription().getUserAccount().getCode();
            String agencyCounterKey = agencyCode + "_value";

            Map<String, Double> offerAgenciesCountersMap = (Map<String, Double>) cfiService.getCFValue(serviceTemplate, CF_POOL_PER_OFFER_MAP, walletOperation.getOperationDate());
            if (offerAgenciesCountersMap == null || offerAgenciesCountersMap.get(agencyCounterKey) == null) {
                throw new IllegalStateException(String.format("Pool not yet initialized or does not exist for usage date %tF" + " Offer='%s' ServiceTemplate='%s' Agency='%s'",
                    walletOperation.getOperationDate(), walletOperation.getOfferCode(), serviceTemplate.getCode(), agencyCode));
            }
            Double agencyCounter = offerAgenciesCountersMap.get(agencyCounterKey);

            // Counter consumed
            BigDecimal initialQuantity = walletOperation.getQuantity();
            BigDecimal newAgencyCounter = BigDecimal.valueOf(agencyCounter).subtract(initialQuantity);

            if (newAgencyCounter.compareTo(BigDecimal.ZERO) < 0) {
                BigDecimal overageQuantity = newAgencyCounter.negate();
                BigDecimal restQuantity = initialQuantity.subtract(overageQuantity);
                if (restQuantity.compareTo(BigDecimal.ZERO) > 0) {
                    createOverageWO(true, walletOperation, overageQuantity, overageUnit, overagePrice, multiplier);
                    walletOperation.setQuantity(restQuantity);
                    walletOperation.setParameter2("DEDUCTED_FROM_POOL");
                    walletOperationService.update(walletOperation);
                } else {
                    createOverageWO(false, walletOperation, overageQuantity, overageUnit, overagePrice, multiplier);
                }
                // Cancel RT if already exists
                RatedTransaction ratedTransaction = walletOperation.getRatedTransaction();
                if (ratedTransaction != null) {
                    if (ratedTransaction.getStatus() == RatedTransactionStatusEnum.OPEN) {
                        ratedTransaction.setStatus(RatedTransactionStatusEnum.CANCELED);
                        ratedTransaction.setParameterExtra("CANCELED by adding WO:" + walletOperation.getId());
                        ratedTransactionService.update(ratedTransaction);
                    }
                }
                offerAgenciesCountersMap.put(agencyCounterKey, 0D);
            } else {
                offerAgenciesCountersMap.put(agencyCounterKey, newAgencyCounter.doubleValue());
            }

            if (agencyCounter > 0) {
                cfiService.setCFValue(serviceTemplate, CF_POOL_PER_OFFER_MAP, offerAgenciesCountersMap, walletOperation.getOperationDate());
                serviceTemplateService.update(serviceTemplate);
            }

            result.registerSucces();

        } catch (Exception e) {
            log.error("Failed to check overage usage for WalletOperationId={}: {}", walletOperationId, e);
            result.registerError("Error on processing WOId=" + walletOperationId + ": " + e.getMessage());
        }
    }

    private void createOverageWO(boolean isNew, WalletOperation includedWO, BigDecimal overageQuantity, String overageUnit, Double overagePrice, Double multiplier) {

        WalletOperation overageWO = includedWO;
        if (isNew) {
            overageWO = new WalletOperation();
        }

        // check and find over usage charge instance
        String chargeType = includedWO.getParameter1();
        String overChargeCode = "CH_M2M_USG_" + chargeType + "_OVER";
        UsageChargeInstance overChargeInstance = walletOperationService.getEntityManager().createQuery(OVER_CHARGE_INSTANCE_QUERY, UsageChargeInstance.class)
            .setParameter("serviceInstance", includedWO.getServiceInstance()).setParameter("code", overChargeCode).getSingleResult();
        if (overChargeInstance == null) {
            throw new IllegalStateException(String.format("No over chargeCodeInstance with code=%s " + "is defined on ServiceInstance[id=%s, code=%s]", overChargeCode,
                includedWO.getServiceInstance().getId(), includedWO.getServiceInstance().getCode()));
        }
        overageWO.setChargeInstance(overChargeInstance);

        // check and find over usage plan price
        List<PricePlanMatrix> overPricePlanList = pricePlanMatrixService.getActivePricePlansByChargeCode(overChargeCode);
        if (overPricePlanList == null || overPricePlanList.isEmpty()) {
            throw new IllegalStateException("No PP is defined for Over UCI with code" + overChargeCode);
        }
        overageWO.setPriceplan(overPricePlanList.get(0));

        // WO's amounts
        BigDecimal quantity = overageQuantity.divide(BigDecimal.valueOf(multiplier), appProvider.getRounding(), appProvider.getRoundingMode().getRoundingMode());
        BigDecimal taxPercent = includedWO.getTaxPercent();

        BigDecimal unitAmountWithoutTax = BigDecimal.valueOf(overagePrice);
        BigDecimal unitAmountTax = unitAmountWithoutTax.multiply(taxPercent).divide(BigDecimal.valueOf(100), appProvider.getRounding(),
            appProvider.getRoundingMode().getRoundingMode());
        BigDecimal unitAmountWithTax = unitAmountWithoutTax.add(unitAmountTax);
        BigDecimal amountWithoutTax = unitAmountWithoutTax.multiply(quantity);
        BigDecimal amountTax = unitAmountTax.multiply(quantity);
        BigDecimal amountWithTax = amountWithoutTax.add(amountTax);

        overageWO.setTaxPercent(taxPercent);
        overageWO.setInputQuantity(overageQuantity);
        overageWO.setQuantity(quantity);
        overageWO.setUnitAmountWithoutTax(unitAmountWithoutTax);
        overageWO.setUnitAmountTax(unitAmountTax);
        overageWO.setUnitAmountWithTax(unitAmountWithTax);
        overageWO.setAmountWithoutTax(amountWithoutTax);
        overageWO.setAmountTax(amountTax);
        overageWO.setAmountWithTax(amountWithTax);
        overageWO.setInputUnitDescription(overChargeInstance.getChargeTemplate().getInputUnitDescription());
        overageWO.setRatingUnitDescription(overageUnit);

        // WO's code and description
        String agencyCode = includedWO.getSubscription().getUserAccount().getCode();
        overageWO.setCode("POOL#" + includedWO.getOfferCode() + "#" + agencyCode + "#CH_M2M_USG_" + chargeType + "_OVER");

        String descriptionI18n = overChargeInstance.getDescription();
        if (!chargeType.contains("SMS") && !chargeType.contains("MMS")) {
            descriptionI18n = descriptionI18n.concat(" ").concat("(").concat(overageUnit).concat(")");
        }
        overageWO.setDescription(descriptionI18n);

        // other fields
        overageWO.setInputUnitDescription(includedWO.getInputUnitDescription());
        overageWO.setSubscriptionDate(includedWO.getSubscriptionDate());
        overageWO.setType(includedWO.getType());
        overageWO.setSubscription(includedWO.getSubscription());
        overageWO.setServiceInstance(includedWO.getServiceInstance());
        overageWO.setSeller(includedWO.getSeller());
        overageWO.setOperationDate(includedWO.getOperationDate());
        overageWO.setInvoicingDate(includedWO.getInvoicingDate());
        overageWO.setInvoiceSubCategory(includedWO.getInvoiceSubCategory());
        overageWO.setTax(includedWO.getTax());
        overageWO.setWallet(includedWO.getWallet());
        overageWO.setOrderNumber(includedWO.getOrderNumber());
        overageWO.setCurrency(includedWO.getCurrency());
        overageWO.setOfferTemplate(includedWO.getOfferTemplate());
        overageWO.setOfferCode(includedWO.getOfferCode());
        overageWO.setParameter1(chargeType);
        overageWO.setParameter2(includedWO.getParameter2());
        overageWO.setParameter3(includedWO.getParameter3());
        overageWO.setStatus(WalletOperationStatusEnum.OPEN);
        overageWO.setBillingAccount(includedWO.getBillingAccount());
        overageWO.setUpdated(new Date());

        if (overageWO.getId() == null) {
            walletOperationService.create(overageWO);
        } else {
            walletOperationService.update(overageWO);
        }
        log.debug("Pool shared over usage WO created={}", overageWO);
    }
}