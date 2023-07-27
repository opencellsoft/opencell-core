package org.meveo.service.mediation;

import java.math.BigDecimal;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

import javax.ejb.Stateless;
import javax.inject.Inject;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.meveo.admin.exception.BusinessException;
import org.meveo.model.RatingResult;
import org.meveo.model.billing.ChargeInstance;
import org.meveo.model.billing.InvoiceLineStatusEnum;
import org.meveo.model.billing.RatedTransaction;
import org.meveo.model.billing.RatedTransactionStatusEnum;
import org.meveo.model.billing.WalletOperation;
import org.meveo.model.billing.WalletOperationStatusEnum;
import org.meveo.model.catalog.UsageChargeTemplate;
import org.meveo.model.mediation.EdrVersioningRule;
import org.meveo.model.mediation.MediationSetting;
import org.meveo.model.rating.CDR;
import org.meveo.model.rating.CDRStatusEnum;
import org.meveo.model.rating.EDR;
import org.meveo.model.rating.EDRStatusEnum;
import org.meveo.service.base.PersistenceService;
import org.meveo.service.base.ValueExpressionWrapper;
import org.meveo.service.billing.impl.EdrService;
import org.meveo.service.billing.impl.RatedTransactionService;
import org.meveo.service.billing.impl.UsageRatingService;
import org.meveo.service.billing.impl.WalletOperationService;
import org.meveo.service.catalog.impl.UsageChargeTemplateService;

@Stateless
public class MediationsettingService extends PersistenceService<MediationSetting> {

    @Inject
    private WalletOperationService walletOperationService;

    @Inject
    private RatedTransactionService ratedTransactionService;

    @Inject
    private UsageRatingService usageRatingService;

    @Inject
    private UsageChargeTemplateService usageChargeTemplateService;

    @Inject
    private EdrService edrService;

    // Use MEDIATION_SETTING_ID to remember the mediation setting and look it up by ID next time.
    private static Long MEDIATION_SETTING_ID = null;

    public String getEventKeyFromEdrVersionRule(EDR edr) {
        MediationSetting mediationSetting = getMediationSetting();
        if (mediationSetting == null || !mediationSetting.isEnableEdrVersioning()) {
            return null;
        }
        Comparator<EdrVersioningRule> sortByPriority = (EdrVersioningRule edrV1, EdrVersioningRule edrV2) -> edrV1.getPriority().compareTo(edrV2.getPriority());
        return Optional.ofNullable(mediationSetting.getRules().stream().sorted(sortByPriority).filter(edrVersion -> {
            try {
                return ValueExpressionWrapper.evaluateExpression(edrVersion.getCriteriaEL(), Boolean.class, edr);
            } catch (Exception e) {
                log.warn("cant evaluate expression : " + edrVersion.getCriteriaEL(), e);
            }
            return false;
        }).findFirst()).map(edrVersion -> {
            try {
                return edrVersion != null ? ValueExpressionWrapper.evaluateExpression(edrVersion.get().getKeyEL(), String.class, edr) : null;
            } catch (Exception e) {
                log.warn("cant evaluate expression : " + edrVersion.get().getKeyEL(), e);
            }
            return null;
        }).get();
    }

    @SuppressWarnings("unchecked")
    public boolean applyEdrVersioningRule(List<EDR> edrs, CDR cdr, boolean isTriggeredEdr) {

        MediationSetting mediationSetting = getMediationSetting();
        if (mediationSetting == null || !mediationSetting.isEnableEdrVersioning()) {
            return false;
        }

        Iterator<EDR> edrIterate = edrs.iterator();
        boolean isRated = false;

        while (edrIterate.hasNext()) {
            EDR edr = edrIterate.next();
            if (edr.getId() == null) {
                edrService.create(edr);
            }

            String errorMessage = "Error evaluating %s  [id= %d, \"%s\"] for CDR: [%s] : %s";
            Optional<EdrVersioningRule> edrVersionRuleOption = mediationSetting.getRules().stream().filter(edrVersion -> {
                String errorMsg = String.format(errorMessage, "criteriaEL", edrVersion.getId(), edrVersion.getCriteriaEL(), isTriggeredEdr ? "FROM_TRIGGERED_EDR : " + edr : cdr, "%s");
                Boolean eval = (Boolean) evaluateEdrVersion(edrVersion.getId(), edrVersion.getCriteriaEL(), edr, cdr, errorMsg, Boolean.class, edrIterate);
                return eval == null ? false : eval;
            }).findFirst();

            if (edrVersionRuleOption.isPresent()) {
                EdrVersioningRule edrVersionRule = edrVersionRuleOption.get();
                String errorMsg = String.format(errorMessage, "eventKeyEl", edrVersionRule.getId(), edrVersionRule.getCriteriaEL(), isTriggeredEdr ? "FROM_TRIGGERED_EDR : " + edr : cdr, "%s");
                String keyEvent = (String) evaluateEdrVersion(edrVersionRule.getId(), edrVersionRule.getKeyEL(), edr, cdr, errorMsg, String.class, edrIterate);

                if (StringUtils.isNotEmpty(keyEvent) && edr.getRejectReason() == null) { // test si cdr est rejete
                    edr.setEventKey(keyEvent);
                    errorMsg = String.format(errorMessage, "isNewVersionEL", edrVersionRule.getId(), edrVersionRule.getCriteriaEL(), cdr, "%s");

                    List<EDR> previousEdrs = this.findByEventKey(keyEvent);

                    if (CollectionUtils.isEmpty(previousEdrs)) {
                        edr.setEventVersion(1);
                        continue;
                    }

                    EDR previousEdr = previousEdrs.get(0);

                    boolean isNewVersion = (boolean) evaluateEdrVersion(edrVersionRule.getId(), edrVersionRule.getIsNewVersionEL(), edr, cdr, errorMsg, Boolean.class, previousEdr, edrIterate);

                    if (isNewVersion) {

                        // When previous EDR with status : OPEN, CANCELLED, REJECTED
                        if (previousEdr.getStatus() != EDRStatusEnum.RATED) { // all status : OPEN, CANCELLED, REJECTED
                            previousEdr.setStatus(EDRStatusEnum.CANCELLED);
                            previousEdr.setRejectReason("Received new version EDR[id=" + edr.getId() + "]");
                            edr.setEventVersion(previousEdr.getEventVersion() + 1);

                            // When previous EDR with status RATED
                        } else {

                            // check if wallet operation related to EDR is treated
                            List<WalletOperation> wos = (List<WalletOperation>) walletOperationService.getEntityManager()
                                .createQuery("from WalletOperation wo where wo.edr.id=:edrId and  wo.status in ('TREATED', 'TO_RERATE', 'OPEN', 'SCHEDULED' )").setParameter("edrId", previousEdr.getId()).getResultList();

                            // Any WO is billed already - exists RT with status Billed
                            boolean billedTransaction = wos.stream().anyMatch(wo -> wo.getRatedTransaction() != null && wo.getRatedTransaction().getStatus() == RatedTransactionStatusEnum.BILLED && wo.getRatedTransaction().getInvoiceLine().getStatus() == InvoiceLineStatusEnum.BILLED);
                            if (billedTransaction) {
                                if (cdr != null) {
                                    cdr.setStatus(CDRStatusEnum.DISCARDED);
                                    cdr.setRejectReason("EDR[id=" + previousEdr.getId() + ", eventKey=" + keyEvent + "] has already been invoiced");
                                }
                                if (edr.getId() != null)
                                    edrService.remove(edr);
                                edrIterate.remove();
                                continue;

                                // All wallet operation that have a status OPEN
                            } else {
                                edr.setStatus(EDRStatusEnum.RATED);
                                edr.setEventVersion(previousEdr.getEventVersion() + 1);
                                previousEdr.setStatus(EDRStatusEnum.CANCELLED);
                                previousEdr.setRejectReason("Received new version EDR[id=" + edr.getId() + "]");
                                RatingResult rating = usageRatingService.rateUsage(edr, true, true, 0, 0, null, false);
                                if (rating.getWalletOperations().size() == 0) {
                                    throw new BusinessException("Error while rating new Edr version : " + edr.getEventVersion());
                                }
                                for (WalletOperation wo : wos) {
                                    boolean oldChargeTriggerdNext = isChargeHasTriggeredNexCharge(wo, edr);
                                    for (WalletOperation woToRerate : rating.getWalletOperations()) {
                                        boolean newChargeTriggerdNext = isChargeHasTriggeredNexCharge(woToRerate, edr);
                                        torerateWalletOperation(wo, woToRerate, edr);
                                        if (oldChargeTriggerdNext && newChargeTriggerdNext) {
                                            break;
                                        }
                                    }
                                    isRated = true;
                                    manageTriggeredEdr(wo, edr);
                                }

                            }
                        }
                    } else {
                        if (cdr != null) {
                            cdr.setStatus(CDRStatusEnum.DISCARDED);
                            String msgError = "Newer version already exists EDR[id=" + previousEdrs.get(0).getId() + "]";
                            cdr.setRejectReason(msgError);
                        }
                        if (edr.getId() != null)
                            edrService.remove(edr);
                        edrIterate.remove();
                    }
                }

            }

        }
        return isRated;
    }

    private boolean isChargeHasTriggeredNexCharge(WalletOperation wo, EDR edr) {
        ChargeInstance usageChargeInstance = wo.getChargeInstance();
        boolean triggerNextCharge = false;
        if (usageChargeInstance != null && usageChargeInstance.getChargeTemplate() != null) {
            UsageChargeTemplate usageChargeTemplate = usageChargeTemplateService.findById(usageChargeInstance.getChargeTemplate().getId());
            triggerNextCharge = usageChargeTemplate.getTriggerNextCharge();
            if (!StringUtils.isBlank(usageChargeTemplate.getTriggerNextChargeEL())) {
                triggerNextCharge = usageRatingService.evaluateBooleanExpression(usageChargeTemplate.getTriggerNextChargeEL(), edr, wo);
            }
        }
        return triggerNextCharge;
    }

    private void torerateWalletOperation(WalletOperation wo, WalletOperation woToRerate, EDR edr) {
    	if(woToRerate.getDiscountValue()==null) {
            List<WalletOperation> discountWos=walletOperationService.findByDiscountedWo(wo.getId());
            if(CollectionUtils.isNotEmpty(discountWos)) {
                List<Long> discountWoId = discountWos.stream().map(WalletOperation::getId).collect(Collectors.toList());
                //discountWoId.add(wo.getId());
                List<WalletOperation> triggeredWo = (List<WalletOperation>) walletOperationService.getEntityManager().createNamedQuery("WalletOperation.findByTriggerdEdr").setParameter("rerateWalletOperationIds", discountWoId).getResultList();
                discountWos.addAll(triggeredWo);
	            discountWos.addAll(triggeredWo.stream().flatMap(wl -> walletOperationService.findByDiscountedWo(wl.getId()).stream()).collect(Collectors.toList()));
                for (WalletOperation wallet : discountWos) {
                    if (!wallet.getStatus().equals(WalletOperationStatusEnum.CANCELED)) {
                        wallet.setStatus(WalletOperationStatusEnum.CANCELED);
                        walletOperationService.update(wallet);
                        if(wallet.getEdr() != null){
                            wallet.getEdr().setStatus(EDRStatusEnum.CANCELLED);
                            edrService.update(wallet.getEdr());
                        }
                        if (wallet.getRatedTransaction() != null && wallet.getRatedTransaction().getStatus() != RatedTransactionStatusEnum.BILLED) {
                            wallet.getRatedTransaction().setStatus(RatedTransactionStatusEnum.CANCELED);
                            ratedTransactionService.update(wallet.getRatedTransaction());
                        }
                    }
                }
            }
    		wo.setStatus(WalletOperationStatusEnum.TO_RERATE);
    		wo.setEdr(edr);
    		wo.setAccountingArticle(woToRerate.getAccountingArticle());
    		wo.setAccountingCode(woToRerate.getAccountingCode());
    		wo.setAmountTax(woToRerate.getAmountTax());
    		wo.setAmountWithoutTax(woToRerate.getAmountWithoutTax());
    		wo.setAmountWithTax(woToRerate.getAmountWithTax());
    		wo.setBillingAccount(woToRerate.getBillingAccount());
    		wo.setChargeInstance(woToRerate.getChargeInstance());
    		wo.setChargeMode(woToRerate.getChargeMode());
    		wo.setParameter1(woToRerate.getParameter1());
    		wo.setParameter2(woToRerate.getParameter2());
    		wo.setParameter3(woToRerate.getParameter3());
    		wo.setParameterExtra(woToRerate.getParameterExtra());
    		wo.setTax(woToRerate.getTax());
    		wo.setTaxClass(woToRerate.getTaxClass());
    		wo.setTaxPercent(woToRerate.getTaxPercent());
    		wo.setUnitAmountTax(woToRerate.getUnitAmountTax());
    		wo.setUnitAmountWithTax(woToRerate.getUnitAmountWithTax());
    		wo.setUnitAmountWithoutTax(woToRerate.getUnitAmountWithoutTax());
    		wo.setSubscriptionDate(woToRerate.getSubscriptionDate());
    		wo.setInvoiceSubCategory(woToRerate.getInvoiceSubCategory());
    		wo.setUserAccount(woToRerate.getUserAccount());
    		wo.setType(woToRerate.getType());
    		wo.setSubscription(woToRerate.getSubscription());
    		wo.setCurrency(woToRerate.getCurrency());
    		wo.setCounter(woToRerate.getCounter());
    		wo.setDescription(woToRerate.getDescription());
    		wo.setInputUnitDescription(woToRerate.getInputUnitDescription());
    		wo.setInputUnitOfMeasure(woToRerate.getInputUnitOfMeasure());
    		wo.setInputQuantity(woToRerate.getInputQuantity());
    		wo.setQuantity(woToRerate.getQuantity());
    		wo.setCode(woToRerate.getCode());
    		wo.setTradingCurrency(woToRerate.getBillingAccount().getTradingCurrency());
            if(wo.getTradingCurrency() != null && wo.getTradingCurrency().getCurrentRate() != null) {
                final BigDecimal rate = wo.getTradingCurrency().getCurrentRate();
                wo.setTransactionalUnitAmountTax(wo.getUnitAmountTax().multiply(rate));
                wo.setTransactionalUnitAmountWithTax(wo.getUnitAmountWithTax().multiply(rate));
                wo.setTransactionalUnitAmountWithoutTax(wo.getUnitAmountWithoutTax().multiply(rate));
                wo.setTransactionalAmountTax(wo.getAmountTax().multiply(rate));
                wo.setTransactionalAmountWithTax(wo.getTransactionalAmountWithTax().multiply(rate));
                wo.setTransactionalAmountWithoutTax(wo.getAmountWithoutTax().multiply(rate));
            }
    		walletOperationService.update(wo);

    		RatedTransaction ratedTransaction = wo.getRatedTransaction();
    		if (ratedTransaction != null) {
    			ratedTransactionService.update(ratedTransaction, RatedTransactionStatusEnum.CANCELED);
    		}
    	}
    }

    @SuppressWarnings("unchecked")
    private void manageTriggeredEdr(WalletOperation walletOperation, EDR edr) {
        if (walletOperation.getEdr() != null) {
            List<EDR> tEdrs = (List<EDR>) edrService.getEntityManager().createNamedQuery("EDR.getByWO").setParameter("WO_IDS", List.of(walletOperation.getId())).getResultList();
            tEdrs = tEdrs.stream().filter(e -> e.getStatus() != EDRStatusEnum.CANCELLED).collect(Collectors.toList());
            if (CollectionUtils.isNotEmpty(tEdrs)) {
                for (EDR triggeredEdr : tEdrs) {
                    triggeredEdr.setStatus(EDRStatusEnum.CANCELLED);
                    RatingResult ratingResult = usageRatingService.rateChargeAndInstantiateTriggeredEDRs(walletOperation.getChargeInstance(), edr.getEventDate(), edr.getQuantity(), null, null, null, null, null, null,
                        edr, null, false, true);
                    List<WalletOperation> walletOperations = walletOperationService.getEntityManager().createQuery("from WalletOperation wo where wo.edr.id=:edrId and discountValue = null").setParameter("edrId", triggeredEdr.getId())
                        .getResultList();
                    if (CollectionUtils.isNotEmpty(walletOperations)) {
                        WalletOperation trigWallet = walletOperations.get(0);
                        trigWallet.setStatus(WalletOperationStatusEnum.CANCELED);
                        RatedTransaction ratedTransaction = trigWallet.getRatedTransaction();
                        if (ratedTransaction != null) {
                            ratedTransactionService.update(ratedTransaction, RatedTransactionStatusEnum.CANCELED);
                        }
                        walletOperationService.cancelDiscountedWalletOperation(walletOperations.stream().map(WalletOperation::getId).collect(Collectors.toList()));
                    }
                    List<EDR> edrs = usageRatingService.instantiateTriggeredEDRs(ratingResult.getWalletOperations().get(0), edr, true, false);
                    edrs.forEach(e -> {
                        e.setStatus(edr.getStatus());
                        e.setWalletOperation(walletOperation);
                        e.setEventVersion(edr.getEventVersion());
                        e.setEventKey(getEventKeyFromEdrVersionRule(e));
                        edrService.create(e);
                    });
                }
            }
        }
    }

    private Object evaluateEdrVersion(Long idEdrVersion, String expression, EDR edr, CDR cdr, String msg, Class<?> result, EDR previousEdr, Iterator<EDR> edrIterate) {
        Object evaluted = null;
        Map<Object, Object> context = new HashMap<>();
        context.put("edr", edr);
        if (previousEdr != null)
            context.put("previous", previousEdr);
        try {
            evaluted = ValueExpressionWrapper.evaluateExpression(expression, context, result);
        } catch (Exception e) {
            msg = String.format(msg, e.getMessage());
            if (cdr != null) {
                cdr.setRejectReason(msg);
                cdr.setStatus(CDRStatusEnum.ERROR);
            }
            if (edr.getId() != null) {
                edrService.remove(edr);
            }
            edrIterate.remove();
        }
        return evaluted;
    }


    private Object evaluateEdrVersion(Long idEdrVersion, String expression, EDR edr, CDR cdr, String msg, Class<?> result, Iterator<EDR> edrIterate) {
        return evaluateEdrVersion(idEdrVersion, expression, edr, cdr, msg, result, null, edrIterate);
    }

    @SuppressWarnings("unchecked")
    public List<EDR> findByEventKey(String eventKey) {
        return getEntityManager().createNamedQuery("EDR.findEDREventVersioning").setParameter("eventKey", eventKey).setMaxResults(1).getResultList();
    }

    /**
     * Get a Mediation Setting - there should be only one in the system. Use MEDIATION_SETTING_ID to remember the mediation setting and look it up by ID next time.
     * 
     * @return Mediation setting
     */
    private MediationSetting getMediationSetting() {
        MediationSetting mediationSetting = null;

        // No mediations setting was looked up yet
        if (MEDIATION_SETTING_ID == null) {
            List<MediationSetting> mediationSettings = this.list();
            if (mediationSettings.isEmpty()) {
                return null;
            }
            mediationSetting = mediationSettings.get(0);
            MEDIATION_SETTING_ID = mediationSetting.getId();
        } else {
            mediationSetting = findById(MEDIATION_SETTING_ID);
        }
        return mediationSetting;
    }
}