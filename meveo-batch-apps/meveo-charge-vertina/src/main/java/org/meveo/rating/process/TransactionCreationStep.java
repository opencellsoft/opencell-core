/*
* (C) Copyright 2009-2013 Manaty SARL (http://manaty.net/) and contributors.
*
* Licensed under the GNU Public Licence, Version 2.0 (the "License");
* you may not use this file except in compliance with the License.
* You may obtain a copy of the License at
*
*     http://www.gnu.org/licenses/gpl-2.0.txt
*
* Unless required by applicable law or agreed to in writing, software
* distributed under the License is distributed on an "AS IS" BASIS,
* WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
* See the License for the specific language governing permissions and
* limitations under the License.
*/
package org.meveo.rating.process;

import java.math.BigDecimal;
import java.math.RoundingMode; 
import java.util.List;

import javax.persistence.EntityManager;

import org.apache.log4j.Logger;
import org.meveo.commons.utils.NumberUtils;
import org.meveo.commons.utils.QueryBuilder;
import org.meveo.config.MeveoConfig;
import org.meveo.core.inputhandler.TaskExecution;
import org.meveo.core.process.step.AbstractProcessStep;
import org.meveo.core.process.step.StepExecution;
import org.meveo.model.admin.OudayaConfiguration;
import org.meveo.model.billing.ApplicationTypeEnum;
import org.meveo.model.billing.CatMessages;
import org.meveo.model.billing.ChargeApplication;
import org.meveo.model.billing.RatedTransaction;
import org.meveo.model.billing.RatedTransactionStatusEnum;
import org.meveo.model.billing.TradingLanguage;
import org.meveo.model.catalog.ChargeTemplate;
import org.meveo.model.catalog.DiscountPlanMatrix;
import org.meveo.model.catalog.PricePlanMatrix;
import org.meveo.model.crm.Provider;
import org.meveo.persistence.MeveoPersistence;
import org.meveo.rating.ticket.RatingTicket;
import org.meveo.vertina.VertinaConfig;
import org.meveo.vertina.constants.VertinaConstants;

/**
 * Create a transaction, that will be inserted to db on commit step.
 * 
 * @author Ignas Lelys
 * @created Jan 28, 2011
 * 
 */
public class TransactionCreationStep extends AbstractProcessStep<RatingTicket> {

    private static final Logger logger = Logger.getLogger(TransactionCreationStep.class);

    private static final BigDecimal HUNDRED = new BigDecimal("100");

    public TransactionCreationStep(AbstractProcessStep<RatingTicket> nextStep, MeveoConfig config) {
        super(nextStep, config);
    }

    /**
     * @see org.meveo.core.process.step.AbstractProcessStep#execute(org.meveo.core.process.step.StepExecution)
     */
    @Override
    protected boolean execute(StepExecution<RatingTicket> stepExecution) {
        boolean result = false;
        TaskExecution<RatingTicket> taskExecution = stepExecution.getTaskExecution();
        RatingTicket ticket = stepExecution.getTicket();
        if (ticket.chargeApplication != null) {
            // TODO temp solution until #621 ticket will be solved.
            if (taskExecution.getProvider() == null) {
                taskExecution.setProvider(ticket.chargeApplication.getProvider());
            }
            result = createTransactionFromChargeApplication(stepExecution, ticket.chargeApplication);
        } else if (ticket.ratedTransaction != null) {
            // TODO temp solution until #621 ticket will be solved.
            if (taskExecution.getProvider() == null) {
                taskExecution.setProvider(ticket.ratedTransaction.getProvider());
            }
            result = updateTransaction(stepExecution, ticket.ratedTransaction);
        }
        return result;
    }

    protected boolean createTransactionFromChargeApplication(StepExecution<RatingTicket> stepExecution,
            ChargeApplication chargeApplication) {
        TaskExecution<RatingTicket> taskExecution = stepExecution.getTaskExecution();

        try {
            Integer usageQuantity = chargeApplication.getQuantity();
            if (usageQuantity == null)
                usageQuantity = 0;

            PricePlanMatrix ratePrice = (PricePlanMatrix) stepExecution.getParameter(VertinaConstants.RATE_PLAN);
            DiscountPlanMatrix discountPrice = (DiscountPlanMatrix) stepExecution
                    .getParameter(VertinaConstants.DISCOUNT_PLAN);

            BigDecimal unitPrice1 = chargeApplication.getAmountWithoutTax();

            boolean overriddenPrice = (unitPrice1 != null);
            if (!overriddenPrice) {
                if (ratePrice == null) {
                    logger.error("Error getting pricePlan for ChargeCode=" + chargeApplication.getChargeCode());
                    setNotAccepted(stepExecution, "ERROR_GETTING_PRICE");
                    return false;
                } else {
                    logger.info("found ratePrice:" + ratePrice.getId() + " price=" + ratePrice.getAmountWithoutTax()
                            + " price2=" + ratePrice.getAmountWithoutTax2());
                    unitPrice1 = ratePrice.getAmountWithoutTax();
                }
            }

            if (unitPrice1 == null) {
                unitPrice1 = BigDecimal.ZERO;
            }

            BigDecimal unitPrice2 = chargeApplication.getAmountWithoutTax();
            if (unitPrice2 == null && ratePrice != null && ratePrice.getAmountWithoutTax2() != null) {
                unitPrice2 = ratePrice.getAmountWithoutTax2();
            }

            if (unitPrice2 == null) {
                unitPrice2 = BigDecimal.ZERO;
            }
            BigDecimal unitPriceRatio = BigDecimal.ONE;
            // subscription prorata
            if (ApplicationTypeEnum.PRORATA_SUBSCRIPTION.equals(chargeApplication.getApplicationType())) {
                try {
                    if ("1".equals(chargeApplication.getParameter3())) {
                        unitPriceRatio = new BigDecimal(chargeApplication.getParameter1());
                    }
                    unitPrice1 = unitPrice1.multiply(unitPriceRatio);
                    unitPrice2 = unitPrice2.multiply(unitPriceRatio);
                } catch (Exception e) {
                    // TODO reject on failure?
                    logger.error("Error calculating unit prices.", e);
                }
            }

            if (ApplicationTypeEnum.PRORATA_TERMINATION.equals(chargeApplication.getApplicationType())) {
                try {
                    unitPriceRatio = new BigDecimal(chargeApplication.getParameter1());
                    unitPrice1 = unitPrice1.multiply(unitPriceRatio);
                    unitPrice2 = unitPrice2.multiply(unitPriceRatio);
                } catch (Exception e) {
                    // TODO reject on failure?
                    logger.error("Error calculating unit prices.", e);
                }
            }
            Provider provider = chargeApplication.getProvider();
            if (provider.getRounding() != null && provider.getRounding() > 0) {
            	unitPrice1 = NumberUtils.round(unitPrice1, provider.getRounding());
            	unitPrice2 = NumberUtils.round(unitPrice2, provider.getRounding());
            }
            BigDecimal amount1 = new BigDecimal(usageQuantity).multiply(unitPrice1);
            BigDecimal amount2 = new BigDecimal(usageQuantity).multiply(unitPrice2);

            BigDecimal amount1Discounted = BigDecimal.ZERO;
            BigDecimal amount2Discounted = BigDecimal.ZERO;

            if (overriddenPrice && discountPrice != null) {
                try {
                    BigDecimal discount = BigDecimal.ONE.subtract(discountPrice.getPercent().divide(HUNDRED));
                    amount1Discounted = amount1.multiply(discount);
                    amount2Discounted = amount2.multiply(discount);
                } catch (Exception e) {
                    // TODO reject on failure?
                    logger.error("Error calculating discount.", e);
                }
            } else {
                amount1Discounted = amount1;
                amount2Discounted = amount2;
            }

            BigDecimal amount1Tax = BigDecimal.ZERO;
            BigDecimal amount2Tax = BigDecimal.ZERO;
            if (chargeApplication.getTaxPercent() != null) {
                amount1Tax = amount1Discounted.multiply(chargeApplication.getTaxPercent()).divide(HUNDRED);
                amount2Tax = amount2Discounted.multiply(chargeApplication.getTaxPercent().divide(HUNDRED));
            }
            
            RatedTransaction transaction = new RatedTransaction();
            transaction.setProvider(chargeApplication.getProvider());
            transaction.setChargeApplication(chargeApplication);
            // FIXME: Too many requests to get the wallet : copy wallet in
            // chargeApplication
            transaction.setWallet(chargeApplication.getSubscription().getUserAccount().getWallet());
            transaction.setUsageCode(chargeApplication.getChargeCode());
            String lineDescription=chargeApplication.getDescription();
            TradingLanguage tlanguage=chargeApplication.getSubscription().getUserAccount().getBillingAccount().getTradingLanguage();
            if(tlanguage!=null){
            	 String languageCode=tlanguage.getLanguage().getLanguageCode();
                 Long chargeid=chargeApplication.getChargeInstance().getChargeTemplate().getId();
                 lineDescription=getMessageDescription(ChargeTemplate.class.getSimpleName()+"_"+chargeid,languageCode);
                 
            }
            if(lineDescription==null){
            	lineDescription=chargeApplication.getDescription();
            }
           String transactionLabel=lineDescription +" "+ transaction.getChargeApplication().getStartDate()+" "+ 
            VertinaConfig.getToLabel(chargeApplication.getSubscription().getUserAccount().getBillingAccount().getTradingLanguage().getLanguage().getLanguageCode())+" "+transaction.getChargeApplication().getEndDate();
            transaction.setPrDescription(transactionLabel);
           
            transaction.setUsageDate(chargeApplication.getApplicationDate());
            transaction.setUsageQuantity(usageQuantity);
            transaction.setUnitPrice1(unitPrice1);
            transaction.setUnitPrice2(unitPrice2);
            transaction.setUnitPriceRatio(unitPriceRatio);
            transaction.setDiscountPercent(discountPrice != null ? discountPrice.getPercent() : null);
            transaction.setInvoiceSubCategory(chargeApplication.getInvoiceSubCategory());
            //transaction.setTaxCode(chargeApplication.getTaxCode());
            transaction.setTaxPercent(chargeApplication.getTaxPercent());
            BigDecimal amount1WithTax = amount1Tax.add(amount1Discounted);
            BigDecimal amount2WithTax = amount2Tax.add(amount2Discounted);
            if (provider.getRounding() != null && provider.getRounding() > 0) {
                amount1Discounted = NumberUtils.round(amount1Discounted, provider.getRounding());
                amount1WithTax = NumberUtils.round(amount1WithTax, provider.getRounding());

                amount2Discounted = NumberUtils.round(amount2Discounted, provider.getRounding());
                amount2WithTax = NumberUtils.round(amount2WithTax, provider.getRounding());
            }

            transaction.setAmount(amount1);
            transaction.setAmountWithoutTax(amount1Discounted); // en
            transaction.setAmountTax(amount1Tax);
            transaction.setAmountWithTax(amount1WithTax);
            //transaction.setPrAmount(amount2);
            transaction.setAmount2WithoutTax(amount2Discounted);
            transaction.setAmount2Tax(amount2Tax);
            transaction.setAmount2WithTax(amount2WithTax);
            transaction.setProvider(chargeApplication.getProvider());
            transaction.setParameter1(chargeApplication.getCriteria1());
            transaction.setParameter2(chargeApplication.getCriteria2());
            transaction.setParameter3(chargeApplication.getCriteria3());
            transaction.setParameter4(RatingTicket.sdf.format(chargeApplication.getSubscriptionDate()));
            transaction.setParameter5(chargeApplication.getAmountWithoutTax() != null ? chargeApplication
                    .getAmountWithoutTax().toString() : null);
            transaction.setStatus(RatedTransactionStatusEnum.OPEN);
            putToTaskExecutionListContextParameter(VertinaConstants.LIST_OF_TRANSACTIONS_KEY, transaction,
                    taskExecution);
            putToTaskExecutionListContextParameter(VertinaConstants.PROCESSED_CHARGE_APPLICATIONS_KEY,
                    chargeApplication, taskExecution);

        } catch (Exception e) {
            logger.error("Error creating RatedTransaction", e);
            setNotAccepted(stepExecution, "ERROR_CREATING_TRANSACTION");
            return false;
        }

        return true;
    }

    protected boolean updateTransaction(StepExecution<RatingTicket> stepExecution, RatedTransaction transaction) {
        TaskExecution<RatingTicket> taskExecution = stepExecution.getTaskExecution();

        RatingTicket ticket = stepExecution.getTicket();
        try {

            PricePlanMatrix ratePrice = (PricePlanMatrix) stepExecution.getParameter(VertinaConstants.RATE_PLAN);
            DiscountPlanMatrix discountPrice = (DiscountPlanMatrix) stepExecution
                    .getParameter(VertinaConstants.DISCOUNT_PLAN);

            BigDecimal unitPrice1 = ticket.amountWithoutTax;

            boolean overriddenPrice = (unitPrice1 != null);
            if (!overriddenPrice) {
                if (ratePrice == null) {
                    logger.error("Error getting pricePlan for ChargeCode=" + ticket.chargeCode);
                    setNotAccepted(stepExecution, "ERROR_GETTING_PRICE");
                    return false;
                } else {
                    logger.info("found ratePrice:" + ratePrice.getId() + " price=" + ratePrice.getAmountWithoutTax()
                            + " price2=" + ratePrice.getAmountWithoutTax2());
                    unitPrice1 = ratePrice.getAmountWithoutTax();
                }

                if (unitPrice1 == null) {
                    unitPrice1 = BigDecimal.ZERO;
                }

                BigDecimal unitPrice2 = ticket.amount2;
                if (unitPrice2 == null && ratePrice != null && ratePrice.getAmountWithoutTax2() != null) {
                    unitPrice2 = ratePrice.getAmountWithoutTax2();
                }

                if (unitPrice2 == null) {
                    unitPrice2 = BigDecimal.ZERO;
                }

                unitPrice1 = unitPrice1.multiply(transaction.getUnitPriceRatio());
                unitPrice2 = unitPrice2.multiply(transaction.getUnitPriceRatio());
                unitPrice1 = unitPrice1.setScale(4, RoundingMode.HALF_UP);
                BigDecimal amount1 = new BigDecimal(transaction.getUsageQuantity()).multiply(unitPrice1);

                unitPrice2 = unitPrice2.setScale(4, RoundingMode.HALF_UP);
                BigDecimal amount2 = new BigDecimal(transaction.getUsageQuantity()).multiply(unitPrice2);

                BigDecimal amount1Discounted = BigDecimal.ZERO;
                BigDecimal amount2Discounted = BigDecimal.ZERO;

                if (overriddenPrice && discountPrice != null) {
                    try {
                        BigDecimal discount = BigDecimal.ONE.subtract(discountPrice.getPercent().divide(HUNDRED));
                        amount1Discounted = amount1.multiply(discount);
                        amount2Discounted = amount2.multiply(discount);
                    } catch (Exception e) {
                        // TODO reject on failure?
                        logger.error("Error calculating discount.", e);
                    }
                } else {
                    amount1Discounted = amount1;
                    amount2Discounted = amount2;
                }

                BigDecimal amount1Tax = BigDecimal.ZERO;
                BigDecimal amount2Tax = BigDecimal.ZERO;
                if (transaction.getTaxPercent() != null) {
                    amount1Tax = amount1Discounted.multiply(transaction.getTaxPercent()).divide(HUNDRED);
                    amount2Tax = amount2Discounted.multiply(transaction.getTaxPercent().divide(HUNDRED));
                }

                BigDecimal amount1WithTax = amount1Tax.add(amount1Discounted);
                BigDecimal amount2WithTax = amount2Tax.add(amount2Discounted);
                Provider provider = transaction.getProvider();
                if (provider != null && (provider.getRounding() != null && provider.getRounding() > 0)) {
                    amount1Discounted = NumberUtils.round(amount1Discounted, provider.getRounding());
                    amount1WithTax = NumberUtils.round(amount1WithTax, provider.getRounding());

                    amount2Discounted = NumberUtils.round(amount2Discounted, provider.getRounding());
                    amount2WithTax = NumberUtils.round(amount2WithTax, provider.getRounding());
                }

                transaction.setUnitPrice1(unitPrice1);
                transaction.setUnitPrice2(unitPrice2);
                transaction.setDiscountPercent(discountPrice != null ? discountPrice.getPercent() : null);
                transaction.setAmount(amount1);
                transaction.setAmountWithoutTax(amount1Discounted); // en
                transaction.setAmountTax(amount1Tax);
                transaction.setAmountWithTax(amount1WithTax);
                //transaction.setPrAmount(amount2);
                transaction.setAmount2WithoutTax(amount2Discounted);
                transaction.setAmount2Tax(amount2Tax);
                transaction.setAmount2WithTax(amount2WithTax);
            }

            transaction.setStatus(RatedTransactionStatusEnum.OPEN);
            putToTaskExecutionListContextParameter(VertinaConstants.LIST_OF_TRANSACTIONS_KEY, transaction,
                    taskExecution);

        } catch (Exception e) {
            logger.error("Error updating RatedTransaction", e);
            setNotAccepted(stepExecution, "ERROR_UPDATING_TRANSACTION");
            return false;
        }

        return true;
    }
    
    
	private static String  getMessageDescription(String messageCode,String languageCode){ 
		EntityManager em = MeveoPersistence.getEntityManager();
		String result="";
		QueryBuilder qb = new QueryBuilder(CatMessages.class,"c");
	    	qb.addCriterionWildcard("c.messageCode", messageCode, true);
	    	qb.addCriterionWildcard("c.languageCode", languageCode, true);
	        List<CatMessages> catMessages=qb.getQuery(em).getResultList(); 
	        result= catMessages.size()>0?catMessages.get(0).getDescription():null;
		
		return result;	
	}

}
