package com.matooma.script;

import java.math.BigDecimal;
import java.math.MathContext;
import java.math.RoundingMode;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang3.StringUtils;
import org.meveo.admin.exception.BusinessException;
import org.meveo.model.billing.RatedTransaction;
import org.meveo.model.billing.RatedTransactionStatusEnum;
import org.meveo.model.billing.ServiceInstance;
import org.meveo.model.billing.Subscription;
import org.meveo.model.billing.UserAccount;
import org.meveo.model.billing.WalletOperation;
import org.meveo.model.billing.WalletOperationStatusEnum;
import org.meveo.model.catalog.OfferTemplate;
import org.meveo.model.catalog.ServiceTemplate;
import org.meveo.model.crm.Provider;
import org.meveo.model.shared.DateUtils;
import org.meveo.service.billing.impl.RatedTransactionService;
import org.meveo.service.billing.impl.WalletOperationService;
import org.meveo.service.catalog.impl.ServiceTemplateService;
import org.meveo.service.crm.impl.CustomFieldInstanceService;
import org.meveo.service.script.Script;

/**
 * Update pool offer map
 * 
 * Pack subscription started notification <br>
 * Class: ServiceInstance <br>
 * Event : Created
 *
 * @author Amine BEN AICHA
 *
 */
public class UpdatePoolOfferNotifScript extends Script {

    private static final long serialVersionUID = 1L;

    private CustomFieldInstanceService cfiService = (CustomFieldInstanceService) getServiceInterface(CustomFieldInstanceService.class.getSimpleName());
    private ServiceTemplateService serviceTemplateService = (ServiceTemplateService) getServiceInterface(ServiceTemplateService.class.getSimpleName());
    private WalletOperationService walletOperationService = (WalletOperationService) getServiceInterface(WalletOperationService.class.getSimpleName());
    private RatedTransactionService ratedTransactionService = (RatedTransactionService) getServiceInterface(RatedTransactionService.class.getSimpleName());

    private static final String CF_POOL_PER_OFFER_MAP = "POOL_PER_OFFER_MAP";

    private static final String OVERAGE_WO_QUERY = "from WalletOperation wo " + "where wo.code like '%_USG_OVER' and wo.subscription.userAccount = :userAccount "
            + "and wo.parameter1=:chargeType and wo.status!='CANCELED' " + "and wo.offerTemplate=:offer and wo.amountWithoutTax > 0 "
            + "and wo.operationDate >= :fromDate and wo.operationDate < :toDate " + "order by wo.id";

    private Provider provider;
    private RoundingMode roundingMode;
    private int setPrecision;

    @SuppressWarnings("unchecked")
    @Override
    public void execute(Map<String, Object> context) throws BusinessException {
        try {
            provider = (Provider) context.get(Script.CONTEXT_APP_PROVIDER);

            String migrationMonth = (String) provider.getCfValue("migrationMonth");
            // Check if not in PROD/RECETTE conditions
            if (!StringUtils.isBlank(migrationMonth)) {
                // In migration this notification will be ignored
                return;
            }

            roundingMode = provider.getRoundingMode().getRoundingMode();
            setPrecision = provider.getRounding();

            ServiceInstance serviceInstance = (ServiceInstance) context.get(Script.CONTEXT_ENTITY);
            Subscription subscription = serviceInstance.getSubscription();

            OfferTemplate offer = subscription.getOffer();
            String sharingLevel = (String) offer.getCfValue("sharingLevel");

            String createdWithTransfer = (String) cfiService.getCFValue(subscription, "createdWithTransfer");
            boolean isCreatedWithTransfer = Boolean.parseBoolean(createdWithTransfer);

            if (!isCreatedWithTransfer && sharingLevel != null && sharingLevel.equals("OF")) {

                ServiceTemplate serviceTemplate = serviceInstance.getServiceTemplate();
                UserAccount userAccount = subscription.getUserAccount();
                String serviceCode = serviceTemplate.getCode();
                String chargeType = StringUtils.substringBetween(serviceCode, "USG_", "_USAGE");
                Date subscriptionDate = subscription.getSubscriptionDate();

                Map<String, Double> poolPerOfferMap = (Map<String, Double>) cfiService.getCFValue(serviceTemplate, CF_POOL_PER_OFFER_MAP, subscriptionDate);

                if (poolPerOfferMap == null) {
                    log.warn("UA '{}' SUB '{}' SERVICE '{}' : No version of {} found for date {}", userAccount.getCode(), subscription.getCode(), serviceCode,
                        CF_POOL_PER_OFFER_MAP, subscriptionDate);
                    poolPerOfferMap = new HashMap<>();
                }

                Double poolOfferRaw = (Double) cfiService.getCFValue(serviceTemplate, "volume");
                BigDecimal poolOffer = convertToVolumeUnit(BigDecimal.valueOf(poolOfferRaw), serviceInstance);

                String keyNumberOfCards = userAccount.getCode() + "_number_of_cards";
                Double numberOfCards = poolPerOfferMap.get(keyNumberOfCards);
                if (numberOfCards == null) {
                    log.warn("UA '{}' SUB '{}' SERVICE '{}' : no number_of_cards pool for date {}", userAccount.getCode(), subscription.getCode(), serviceCode, subscriptionDate);
                    numberOfCards = 0D;
                }

                String keyInitial = userAccount.getCode() + "_initial";
                Double initPoolOffer = poolPerOfferMap.get(keyInitial);
                if (initPoolOffer == null) {
                    log.warn("UA '{}' SUB '{}' SERVICE '{}' : no initial pool for date {}", userAccount.getCode(), subscription.getCode(), serviceCode, subscriptionDate);
                    initPoolOffer = 0D;
                }

                String keyValue = userAccount.getCode() + "_value";
                Double valuePoolOffer = poolPerOfferMap.get(keyValue);
                if (valuePoolOffer == null) {
                    log.warn("UA '{}' SUB '{}' SERVICE '{}' : no value pool for date {}", userAccount.getCode(), subscription.getCode(), serviceCode, subscriptionDate);
                    valuePoolOffer = 0D;
                }

                BigDecimal newInitPoolOffer = BigDecimal.valueOf(initPoolOffer).add(poolOffer);

                BigDecimal rest = adjustExistingOverageWOs(serviceCode, chargeType, offer, subscription, serviceInstance, poolOffer);

                BigDecimal newValuePoolOffer = BigDecimal.valueOf(valuePoolOffer).add(rest);

                poolPerOfferMap.put(keyNumberOfCards, ++numberOfCards);
                poolPerOfferMap.put(keyInitial, newInitPoolOffer.doubleValue());
                poolPerOfferMap.put(keyValue, newValuePoolOffer.doubleValue());

                cfiService.setCFValue(serviceTemplate, CF_POOL_PER_OFFER_MAP, poolPerOfferMap, subscriptionDate);
                serviceTemplateService.update(serviceTemplate);
            }
        } catch (Exception e) {
            log.error(e.getMessage(), e);
            throw new BusinessException(this.getClass().getSimpleName() + " : " + e.getClass().getSimpleName() + " : " + e.getMessage());
        }
    }

    @SuppressWarnings("unchecked")
    private BigDecimal adjustExistingOverageWOs(String serviceCode, String chargeType, OfferTemplate offer, Subscription subscription, ServiceInstance serviceInstance,
            BigDecimal poolOffer) {

        Date monthFisrtDay = DateUtils.setDayToDate(subscription.getSubscriptionDate(), 1);
        monthFisrtDay = DateUtils.setDateToStartOfDay(monthFisrtDay);
        Date nextMonthFirstDay = DateUtils.addMonthsToDate(monthFisrtDay, 1);

        Map<String, Object> params = new HashMap<>();
        params.put("chargeType", chargeType);
        params.put("userAccount", subscription.getUserAccount());
        params.put("offer", offer);
        params.put("fromDate", monthFisrtDay);
        params.put("toDate", nextMonthFirstDay);

        List<WalletOperation> walletOperations = (List<WalletOperation>) walletOperationService.executeSelectQuery(OVERAGE_WO_QUERY, params);

        for (WalletOperation walletOperation : walletOperations) {

            BigDecimal overageQuantity = walletOperation.getQuantity();

            poolOffer = poolOffer.subtract(convertToVolumeUnit(overageQuantity, serviceInstance));

            // Cancel RT if already exists
            RatedTransaction ratedTransaction = walletOperation.getRatedTransaction();
            if (ratedTransaction != null) {
                ratedTransaction = ratedTransactionService.refreshOrRetrieve(ratedTransaction);
                if (ratedTransaction.getStatus() == RatedTransactionStatusEnum.OPEN) {
                    ratedTransaction.setStatus(RatedTransactionStatusEnum.CANCELED);
                    ratedTransaction.setParameterExtra("CANCELED by adding card:" + subscription.getCode() + " to pool: " + serviceCode);
                    ratedTransactionService.update(ratedTransaction);
                }
            }

            if (poolOffer.compareTo(BigDecimal.ZERO) >= 0) {
                walletOperation.setStatus(WalletOperationStatusEnum.CANCELED);
                walletOperationService.update(walletOperation);
            } else {
                // Update the last wallet operation
                BigDecimal rest = poolOffer.negate();
                BigDecimal newOverageQuantity = convertToOverageUnit(rest, serviceInstance);
                walletOperation.setQuantity(newOverageQuantity);
                recompute(walletOperation);
                walletOperation.setStatus(WalletOperationStatusEnum.OPEN);
                walletOperationService.update(walletOperation);

                poolOffer = BigDecimal.ZERO;
                break;
            }
        }

        return poolOffer;
    }

    public void recompute(WalletOperation wo) {

        BigDecimal quantity = wo.getQuantity();

        BigDecimal amountWithoutTax = wo.getUnitAmountWithoutTax().multiply(quantity);
        BigDecimal amountTax = wo.getUnitAmountTax().multiply(quantity);
        BigDecimal amountWithTax = amountWithoutTax.add(amountTax);

        wo.setAmountWithoutTax(amountWithoutTax);
        wo.setAmountTax(amountTax);
        wo.setAmountWithTax(amountWithTax);
    }

    @SuppressWarnings("unchecked")
    private BigDecimal convertToVolumeUnit(BigDecimal quantity, ServiceInstance serviceInstance) {
        String volumeUnit = (String) cfiService.getInheritedCFValue(serviceInstance, "volumeUnit");
        Double multiplier = ((Map<String, Double>) cfiService.getCFValueByKey(provider, "CF_P_USAGE_UNITS", volumeUnit)).get("multiplier");
        return quantity.multiply(BigDecimal.valueOf(multiplier));
    }

    @SuppressWarnings("unchecked")
    private BigDecimal convertToOverageUnit(BigDecimal quantity, ServiceInstance serviceInstance) {
        String overageUnit = (String) cfiService.getInheritedCFValue(serviceInstance, "overageUnit");
        Double multiplier = ((Map<String, Double>) cfiService.getCFValueByKey(provider, "CF_P_USAGE_UNITS", overageUnit)).get("multiplier");

        MathContext mc = new MathContext(setPrecision, roundingMode);
        return quantity.divide(BigDecimal.valueOf(multiplier), mc);
    }
}