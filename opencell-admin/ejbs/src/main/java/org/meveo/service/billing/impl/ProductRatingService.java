package org.meveo.service.billing.impl;

import java.io.Serializable;

import javax.ejb.EJBTransactionRolledbackException;
import javax.ejb.Stateless;
import javax.inject.Inject;

import org.meveo.admin.exception.BusinessException;
import org.meveo.admin.exception.RatingException;
import org.meveo.model.RatingResult;
import org.meveo.model.billing.ProductChargeInstance;
import org.meveo.model.billing.Subscription;
import org.meveo.model.billing.UserAccount;
import org.meveo.model.billing.WalletOperation;

/**
 * Product charge related rating
 * 
 * @author Andrius Karpavicius
 */
@Stateless
public class ProductRatingService extends RatingService implements Serializable {

    private static final long serialVersionUID = -950153661737115704L;

    @Inject
    protected WalletOperationService walletOperationService;

    /**
     * Apply/rate product charge instance
     * 
     * @param chargeInstance product charge instance
     * @param isVirtual indicates that it is virtual operation
     * @param failSilently If true, any error will be reported and returned in the rating result instead of throwing an exception
     * @return Rating result containing a rated wallet operation (persisted) and triggered EDRs (persisted)
     * @throws BusinessException business exception.
     * @throws RatingException Failed to rate a charge due to lack of funds, data validation, inconsistency or other rating related failure
     */
    public RatingResult rateProductCharge(ProductChargeInstance chargeInstance, boolean isVirtual, boolean failSilently) throws BusinessException, RatingException {

        if (!RatingService.isORChargeMatch(chargeInstance)) {
            log.debug("Not rating product chargeInstance {}/{}, filter expression or service attributes evaluated to FALSE", chargeInstance.getId(), chargeInstance.getCode());
            return new RatingResult();
        }

        UserAccount userAccount = chargeInstance.getUserAccount();
        Subscription subscription = chargeInstance.getSubscription();

        log.debug("Will rate a product charge. User account {}, subscription {}, quantity {}, date {}, charge {}/{}/{}, ", userAccount != null ? userAccount.getId() : null,
            subscription != null ? subscription.getId() : null, chargeInstance.getQuantity(), chargeInstance.getChargeDate(), chargeInstance.getId(), chargeInstance.getCode(), chargeInstance.getDescription());

        RatingResult ratingResult = null;
        try {
            ratingResult = rateChargeAndInstantiateTriggeredEDRs(chargeInstance, chargeInstance.getChargeDate(), chargeInstance.getQuantity(), null, null, null, null, null, null, null, null, false, isVirtual);

            incrementAccumulatorCounterValues(ratingResult.getWalletOperations(), ratingResult, isVirtual);

            if (!isVirtual && !ratingResult.getWalletOperations().isEmpty()) {

                for (WalletOperation walletOperation : ratingResult.getWalletOperations()) {
	                checkDiscountedWalletOpertion(walletOperation, ratingResult.getWalletOperations());
                    walletOperationService.chargeWalletOperation(walletOperation);
                }
            }

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
                return new RatingResult(e);
            } else {
                throw e;
            }
        }
    }
}