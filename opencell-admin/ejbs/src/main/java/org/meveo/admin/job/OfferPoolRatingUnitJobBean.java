package org.meveo.admin.job;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.Date;
import java.util.List;
import java.util.Map;

import javax.ejb.Stateless;
import javax.ejb.TransactionAttribute;
import javax.ejb.TransactionAttributeType;
import javax.inject.Inject;

import org.meveo.admin.exception.BusinessException;
import org.meveo.jpa.JpaAmpNewTx;
import org.meveo.model.BaseEntity;
import org.meveo.model.billing.UsageChargeInstance;
import org.meveo.model.billing.WalletOperation;
import org.meveo.model.billing.WalletOperationStatusEnum;
import org.meveo.model.catalog.PricePlanMatrix;
import org.meveo.model.catalog.ServiceTemplate;
import org.meveo.model.crm.Provider;
import org.meveo.model.jobs.JobExecutionResultImpl;
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

    private static final String OVER_CHARGE_INSTANCE_QUERY = "Select uci " +
            " From UsageChargeInstance As uci " +
            " Where uci.serviceInstance = :serviceInstance And uci.code = :code";

    @Inject
    private Logger log;

    @Inject
    private PricePlanMatrixService pricePlanMatrixService;

    @Inject
    private ServiceTemplateService serviceTemplateService;

    @Inject
    private WalletOperationService walletOperationService;

    @Inject
    private CustomFieldInstanceService cfiService;

    @Inject
    @ApplicationProvider
    protected Provider appProvider;

    @SuppressWarnings({"unchecked"})
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

            Map<String, Double> offerAgenciesCountersMap = (Map<String, Double>) cfiService.getCFValue(serviceTemplate,
                    CF_POOL_PER_OFFER_MAP, walletOperation.getOperationDate());
            if (offerAgenciesCountersMap == null || offerAgenciesCountersMap.get(agencyCounterKey) == null) {
                throw new IllegalStateException(String.format("Pool counter not yet initialized for operation date %tF " +
                        "ServiceTemplate=%s Agency=%s", walletOperation.getOperationDate(),serviceTemplate.getCode(),  agencyCode));
            }
            Double agencyCounter = offerAgenciesCountersMap.get(agencyCounterKey);

            // Counter consumed
            BigDecimal woQuantity = walletOperation.getQuantity();
            BigDecimal newAgencyCounter = BigDecimal.valueOf(agencyCounter).subtract(woQuantity);

            if (newAgencyCounter.compareTo(BigDecimal.ZERO) < 0) {
                BigDecimal overageQuantity = newAgencyCounter.negate();
                createOverageWalletOperation(walletOperation, overageQuantity, overageUnit, overagePrice, multiplier);
                offerAgenciesCountersMap.put(agencyCounterKey, 0D);
            } else {
                offerAgenciesCountersMap.put(agencyCounterKey, newAgencyCounter.doubleValue());
            }

            walletOperation.setParameter2("DEDUCTED_FROM_POOL");
            walletOperationService.update(walletOperation);

            cfiService.setCFValue(serviceTemplate, CF_POOL_PER_OFFER_MAP, offerAgenciesCountersMap, walletOperation.getOperationDate());
            serviceTemplateService.update(serviceTemplate);

            result.registerSucces();

        } catch (Exception e) {
            log.error("Failed to check overage usage for WalletOperationId={}: {}", walletOperationId, e);
            result.registerError("Error on processing WOId="+ walletOperationId + ": " +e.getMessage());
        }
    }

    private void createOverageWalletOperation(WalletOperation wo, BigDecimal overageQuantity,
                                              String overageUnit, Double overagePrice, Double multiplier) {
        WalletOperation overageWO = new WalletOperation();

        // check and find over usage charge instance
        String overChargeCode = "CH_M2M_USG_" + wo.getParameter1() + "_OVER";
        UsageChargeInstance overCharge = walletOperationService.getEntityManager()
                .createQuery(OVER_CHARGE_INSTANCE_QUERY, UsageChargeInstance.class)
                .setParameter("serviceInstance", wo.getServiceInstance())
                .setParameter("code", overChargeCode).getSingleResult();
        if(overCharge == null) {
            throw new IllegalStateException(String.format("No Over UCI with code=%s "
                    + "is defined on SI[id=%s, code=%s]", overChargeCode,
                    wo.getServiceInstance().getId(), wo.getServiceInstance().getCode()));
        }
        overageWO.setChargeInstance(overCharge);

        // check and find over usage plan price
        List<PricePlanMatrix> overPricePlanList = pricePlanMatrixService.getActivePricePlansByChargeCode(overChargeCode);
        if (overPricePlanList == null || overPricePlanList.isEmpty()) {
            throw new IllegalStateException("No PP is defined for Over UCI with code" + overChargeCode);
        }
        overageWO.setPriceplan(overPricePlanList.get(0));

        // WO's amounts
        BigDecimal quantity = overageQuantity.divide(BigDecimal.valueOf(multiplier), appProvider.getRounding(), appProvider.getRoundingMode().getRoundingMode());
        BigDecimal taxPercent = wo.getTaxPercent();

        BigDecimal unitAmountWithoutTax = BigDecimal.valueOf(overagePrice);
        BigDecimal unitAmountTax = unitAmountWithoutTax.multiply(taxPercent).divide(BigDecimal.valueOf(100), appProvider.getRounding(), appProvider.getRoundingMode().getRoundingMode());
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
        overageWO.setRatingUnitDescription(overageUnit);

        // WO's code and description
        String agencyCode = wo.getSubscription().getUserAccount().getCode();
        String serviceCode = wo.getServiceInstance().getCode();
        overageWO.setCode("POOL#" + agencyCode + "#" + serviceCode + "#_USG_OVER");

        String descriptionI18n = overCharge.getDescription();
        if (!wo.getParameter1().contains("SMS") && !wo.getParameter1().contains("MMS")) {
            descriptionI18n = descriptionI18n.concat(" ").concat("(").concat(overageUnit).concat(")");
        }
        overageWO.setDescription(descriptionI18n);

        // other fields
        overageWO.setInputUnitDescription(wo.getInputUnitDescription());
        overageWO.setSubscriptionDate(wo.getSubscriptionDate());
        overageWO.setType(wo.getType());
        overageWO.setSubscription(wo.getSubscription());
        overageWO.setServiceInstance(wo.getServiceInstance());
        overageWO.setSeller(wo.getSeller());
        overageWO.setOperationDate(wo.getOperationDate());
        overageWO.setInvoicingDate(wo.getInvoicingDate());
        overageWO.setInvoiceSubCategory(wo.getInvoiceSubCategory());
        overageWO.setTax(wo.getTax());
        overageWO.setWallet(wo.getWallet());
        overageWO.setOrderNumber(wo.getOrderNumber());
        overageWO.setCurrency(wo.getCurrency());
        overageWO.setOfferTemplate(wo.getOfferTemplate());
        overageWO.setOfferCode(wo.getOfferCode());
        overageWO.setParameter1(wo.getParameter1());
        overageWO.setParameter2(wo.getParameter2());
        overageWO.setParameter3(wo.getParameter3());
        overageWO.setStatus(WalletOperationStatusEnum.OPEN);
        overageWO.setBillingAccount(wo.getBillingAccount());
        overageWO.setUpdated(new Date());

        walletOperationService.create(overageWO);
        log.debug("Pool shared over usage WO created={}", overageWO);
    }
}