package org.meveo.service.billing.impl;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.Optional;

import javax.ejb.EJBTransactionRolledbackException;
import javax.ejb.Stateless;
import javax.inject.Inject;

import org.meveo.admin.exception.BusinessException;
import org.meveo.admin.exception.RatingException;
import org.meveo.model.RatingResult;
import org.meveo.model.billing.BillingAccount;
import org.meveo.model.billing.ChargeApplicationModeEnum;
import org.meveo.model.billing.InstanceStatusEnum;
import org.meveo.model.billing.OneShotChargeInstance;
import org.meveo.model.billing.Subscription;
import org.meveo.model.billing.WalletOperation;
import org.meveo.model.catalog.ChargeTemplate;
import org.meveo.model.catalog.OneShotChargeTemplate;
import org.meveo.model.rating.EDR;
import org.meveo.model.shared.DateUtils;
import org.meveo.service.catalog.impl.OneShotChargeTemplateService;
import org.meveo.service.tax.TaxClassService;

@Stateless
public class OneShotRatingService extends RatingService implements Serializable {

    private static final long serialVersionUID = 6554942821072192230L;

    @Inject
    private OneShotChargeTemplateService oneShotChargeTemplateService;

    @Inject
    private BillingAccountService billingAccountService;

    @Inject
    private WalletOperationService walletOperationService;

    @Inject
    private EdrService edrService;

    @Inject
    private TaxClassService taxClassService;

    /**
     * Apply/rate a one shot charge. Change charge instance status to CLOSED.
     * 
     * @param chargeInstance Charge instance to apply
     * @param inputQuantity Quantity to apply
     * @param quantityInChargeUnits Quantity to apply in charge units
     * @param applicationDate Charge application date
     * @param orderNumberOverride Order number to override
     * @param chargeMode Charge mode
     * @param isVirtual Is it a virtual charge
     * @param failSilently If true, any error will be reported and returned in the rating result instead of throwing an exception
     * @return Rating result containing a rated wallet operation (persisted) and triggered EDRs (persisted)
     * @throws BusinessException General exception.
     * @throws RatingException EDR rejection due to lack of funds, data validation, inconsistency or other rating related failure
     */
    public RatingResult rateOneShotCharge(OneShotChargeInstance chargeInstance, BigDecimal inputQuantity, BigDecimal quantityInChargeUnits, Date applicationDate, String orderNumberOverride,
            ChargeApplicationModeEnum chargeMode, boolean isVirtual, boolean failSilently) throws BusinessException, RatingException {

        if (applicationDate == null) {
            applicationDate = new Date();
        }

        if (chargeMode == null) {
            chargeMode = ChargeApplicationModeEnum.SUBSCRIPTION;
        }

        if (!RatingService.isORChargeMatch(chargeInstance)) {
            log.debug("Not rating oneshot chargeInstance {}/{}, filter expression or service attributes evaluated to FALSE", chargeInstance.getId(), chargeInstance.getCode());
            return new RatingResult();
        }

        Subscription subscription = chargeInstance.getSubscription();

        log.debug("Will rate a one shot charge subscription {}, quantity {}, applicationDate {}, chargeInstance {}/{}/{}", subscription.getId(), quantityInChargeUnits, applicationDate, chargeInstance.getId(),
            chargeInstance.getCode(), chargeInstance.getDescription());

        // Check if there is any attribute with value FALSE, indicating that service instance is not active
        if (anyFalseAttributeMatch(chargeInstance)) {
            return new RatingResult();
        }

        RatingResult ratingResult = null;
        try {
            ratingResult = rateChargeAndInstantiateTriggeredEDRs(chargeInstance, applicationDate, inputQuantity, quantityInChargeUnits, orderNumberOverride, null, null, null, chargeMode, null, null, false, isVirtual);

            final List<WalletOperation> walletOperations = ratingResult.getWalletOperations();
			incrementAccumulatorCounterValues(walletOperations, ratingResult, isVirtual);
            chargeInstance.setWalletOperations(walletOperations);
            if (!isVirtual && !walletOperations.isEmpty()) {
                if (ratingResult.getTriggeredEDRs() != null) {
                    for (EDR triggeredEdr : ratingResult.getTriggeredEDRs()) {
                        edrService.create(triggeredEdr);
                    }
                }
                for (WalletOperation walletOperation : walletOperations) {
                    walletOperationService.chargeWalletOperation(walletOperation);
                }

                OneShotChargeTemplate oneShotChargeTemplate = null;

                ChargeTemplate chargeTemplate = chargeInstance.getChargeTemplate();

                if (chargeTemplate instanceof OneShotChargeTemplate) {
                    oneShotChargeTemplate = (OneShotChargeTemplate) chargeInstance.getChargeTemplate();
                } else {
                    oneShotChargeTemplate = oneShotChargeTemplateService.findById(chargeTemplate.getId());
                }

                boolean immediateInvoicing = (oneShotChargeTemplate != null && oneShotChargeTemplate.getImmediateInvoicing() != null) ? oneShotChargeTemplate.getImmediateInvoicing() : false;

                if (Boolean.TRUE.equals(immediateInvoicing)) {

                    BillingAccount billingAccount = subscription.getUserAccount().getBillingAccount();

                    int delay = 0;
                    if (billingAccount.getBillingCycle().getInvoiceDateDelayEL() != null) {
                        delay = InvoiceService.resolveImmediateInvoiceDateDelay(billingAccount.getBillingCycle().getInvoiceDateDelayEL(), walletOperations.get(0), billingAccount);
                    }

                    Date nextInvoiceDate = DateUtils.addDaysToDate(billingAccount.getNextInvoiceDate(), -delay);
                    nextInvoiceDate = DateUtils.setTimeToZero(nextInvoiceDate);
                    applicationDate = DateUtils.setTimeToZero(applicationDate);

                    if (nextInvoiceDate == null || applicationDate.after(nextInvoiceDate)) {
                        billingAccount.setNextInvoiceDate(applicationDate);
                        billingAccountService.update(billingAccount);
                    }
                }

                // OSO SI Virtual : clean WalletOperation.SI if it is a SI Virtual (id==null)
                ratingResult.getWalletOperations().forEach(walletOperation -> walletOperation.setServiceInstance(
                        walletOperation.getServiceInstance() != null && walletOperation.getServiceInstance().getId() == null
                                ? null : walletOperation.getServiceInstance()));

            }

            // Mark charge instance as closed
            chargeInstance.setStatus(InstanceStatusEnum.CLOSED);

            // DIRTY FIX : pression sur une livraison OSO
            // Le service retournait bien le proxy hibernate pour recuperer la TaxClass, mais apres un merge du 15/02, ca ne fonctionne plus
            // Donc je re-recuperer l'objet par son ID et on eteint le feux
            Optional.ofNullable(ratingResult.getWalletOperations()).orElse(Collections.emptyList())
                    .forEach(wo -> {
                        if (wo.getTaxClass() != null) {
                            wo.setTaxClass(taxClassService.findById(wo.getTaxClass().getId()));
                        }
                    });

            return ratingResult;
        
        } catch (EJBTransactionRolledbackException e) {
            if (ratingResult != null) {
                revertCounterChanges(ratingResult.getCounterChanges());
            }
            throw e;
            
        } catch (Exception e) {
            if (ratingResult != null) {
                revertCounterChanges(ratingResult.getCounterChanges());
            }

            if (failSilently) {
                log.debug("Failed to rate a one shot charge subscription {}, quantity {}, applicationDate {}, chargeInstance {}/{}/{}", subscription.getId(), quantityInChargeUnits, applicationDate,
                    chargeInstance.getId(), chargeInstance.getCode(), chargeInstance.getDescription(), e);

                return new RatingResult(e);
            } else {
                throw e;
            }
        }
    }

}