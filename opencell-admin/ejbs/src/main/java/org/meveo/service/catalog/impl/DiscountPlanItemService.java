/*
 * (C) Copyright 2015-2020 Opencell SAS (https://opencellsoft.com/) and contributors.
 *
 * This program is free software: you can redistribute it and/or modify it under the terms of the GNU Affero General
 * Public License as published by the Free Software Foundation, either version 3 of the License, or (at your option)
 * any later version.
 *
 * THERE IS NO WARRANTY FOR THE PROGRAM, TO THE EXTENT PERMITTED BY APPLICABLE LAW. EXCEPT WHEN
 * OTHERWISE STATED IN WRITING THE COPYRIGHT HOLDERS AND/OR OTHER PARTIES PROVIDE THE PROGRAM "AS
 * IS" WITHOUT WARRANTY OF ANY KIND, EITHER EXPRESSED OR IMPLIED, INCLUDING, BUT NOT LIMITED TO, THE
 * IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE. THE ENTIRE RISK AS TO
 * THE QUALITY AND PERFORMANCE OF THE PROGRAM IS WITH YOU. SHOULD THE PROGRAM PROVE DEFECTIVE,
 * YOU ASSUME THE COST OF ALL NECESSARY SERVICING, REPAIR OR CORRECTION.
 *
 * For more information on the GNU Affero General Public License, please consult
 * <https://www.gnu.org/licenses/agpl-3.0.en.html>.
 */

package org.meveo.service.catalog.impl;

import static org.apache.commons.lang3.StringUtils.isNotBlank;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.inject.Inject;

import org.meveo.admin.exception.BusinessException;
import org.meveo.commons.utils.NumberUtils;
import org.meveo.commons.utils.QueryBuilder;
import org.meveo.commons.utils.StringUtils;
import org.meveo.model.BaseEntity;
import org.meveo.model.article.AccountingArticle;
import org.meveo.model.billing.BillingAccount;
import org.meveo.model.billing.ChargeInstance;
import org.meveo.model.billing.Invoice;
import org.meveo.model.billing.SubCategoryInvoiceAgregate;
import org.meveo.model.billing.WalletInstance;
import org.meveo.model.catalog.DiscountPlan;
import org.meveo.model.catalog.DiscountPlanItem;
import org.meveo.model.catalog.DiscountPlanItemTypeEnum;
import org.meveo.model.catalog.DiscountPlanStatusEnum;
import org.meveo.model.catalog.DiscountPlanTypeEnum;
import org.meveo.model.catalog.OfferTemplate;
import org.meveo.model.catalog.PricePlanMatrix;
import org.meveo.model.catalog.PricePlanMatrixLine;
import org.meveo.model.catalog.PricePlanMatrixVersion;
import org.meveo.model.cpq.AttributeValue;
import org.meveo.model.cpq.Product;
import org.meveo.model.cpq.commercial.InvoiceLine;
import org.meveo.service.base.PersistenceService;
import org.meveo.service.base.ValueExpressionWrapper;
import org.meveo.service.billing.impl.ChargeInstanceService;
import org.meveo.service.billing.impl.InvoiceLineService;

/**
 * @author Edward P. Legaspi
 * @author R.AITYAAZZA
 * @version 11.0
 **/
@Stateless
public class DiscountPlanItemService extends PersistenceService<DiscountPlanItem> {

	@EJB
	private DiscountPlanService discountPlanService;
	
	@Inject
	private PricePlanMatrixVersionService pricePlanMatrixVersionService;
	
	@Inject
    private ChargeInstanceService<ChargeInstance> chargeInstanceService;
	
	@Inject
	PricePlanMatrixService pricePlanMatrixService;
	
	@Inject
	InvoiceLineService invoiceLinesService;
	
	private final static BigDecimal HUNDRED = new BigDecimal("100");

    public DiscountPlanItem findByCode(String code) {
        QueryBuilder qb = new QueryBuilder(DiscountPlanItem.class, "d");
        qb.addCriterion("d.code", "=", code, true);
        try {
            return (DiscountPlanItem) qb.getQuery(getEntityManager()).getSingleResult();
        } catch (Exception e) {
            return null;
        }
    }

	@Override
	public void create(DiscountPlanItem dpi) throws BusinessException {
        DiscountPlan discountPlan = discountPlanService.findById(dpi.getDiscountPlan().getId());
        if (!discountPlan.getStatus().equals(DiscountPlanStatusEnum.DRAFT)) {
            throw new BusinessException("only discount plan items attached to DRAFT discount plans can be created");
        }
        dpi.setDiscountPlan(discountPlan);
        super.create(dpi);
        // Needed to refresh DiscountPlan as DiscountPlan.discountPlanItems field as it
        // is cached
        // refresh(dpi.getDiscountPlan());
    }

	@Override
	public DiscountPlanItem update(DiscountPlanItem dpi) throws BusinessException {
        DiscountPlan discountPlan = discountPlanService.findById(dpi.getDiscountPlan().getId());
        if (!discountPlan.getStatus().equals(DiscountPlanStatusEnum.DRAFT)) {
            throw new BusinessException("only discount plan items attached to DRAFT discount plans can be updated");
        }
        dpi.setDiscountPlan(discountPlan);
        dpi = super.update(dpi);
        // Needed to refresh DiscountPlan as DiscountPlan.discountPlanItems field as it
        // is cached
        // refresh(dpi.getDiscountPlan());
        return dpi;
    }

    @Override
    public void remove(DiscountPlanItem dpi) throws BusinessException {
        DiscountPlan discountPlan = discountPlanService.findById(dpi.getDiscountPlan().getId());
        if (!discountPlan.getStatus().equals(DiscountPlanStatusEnum.DRAFT)) {
            throw new BusinessException("only discount plan items attached to DRAFT discount plans can be removed");
        }
        super.remove(dpi);
        // Needed to remove from DiscountPlan.discountPlanItems field as it is cached
        dpi.getDiscountPlan().getDiscountPlanItems().remove(dpi);
    }

    /**
     * Determine a discount amount or percent to apply
     *
     * @param invoice Invoice to apply discount on
     * @param scAggregate Subcategory aggregate to apply discount on
     * @param amount Amount to apply discount on
     * @param discountPlanItem Discount configuration
     * @return A discount percent (0-100)
     */
    public BigDecimal getDiscountAmountOrPercent(Invoice invoice, SubCategoryInvoiceAgregate scAggregate, BigDecimal amount, DiscountPlanItem discountPlanItem,Product product, Set<AttributeValue> attributeValues) {
        BigDecimal computedDiscount = discountPlanItem.getDiscountValue();

        final String dpValueEL = discountPlanItem.getDiscountValueEL();
        if (isNotBlank(dpValueEL)) {
            final BigDecimal evalDiscountValue = evaluateDiscountPercentExpression(dpValueEL, scAggregate == null ? null : scAggregate.getBillingAccount(), 
            																					scAggregate == null ? null : scAggregate.getWallet(), invoice, amount);
            log.debug("for discountPlan {} percentEL -> {}  on amount={}", discountPlanItem.getCode(), computedDiscount, amount);
            if (evalDiscountValue != null) {
                computedDiscount = evalDiscountValue;
            }
        }else if(discountPlanItem.getPricePlanMatrix()!=null){ 
        	PricePlanMatrix pricePlan = discountPlanItem.getPricePlanMatrix();
        	PricePlanMatrixVersion ppmVersion = pricePlanMatrixVersionService.getLastPublishedVersion(pricePlan.getCode());
        	if(ppmVersion!=null && product!=null) {
        		PricePlanMatrixLine pricePlanMatrixLine = pricePlanMatrixVersionService.loadPrices(ppmVersion, product.getCode(),attributeValues);
        		computedDiscount=pricePlanMatrixLine.getPriceWithoutTax();
        	} 
        		
        }
        if (computedDiscount == null || amount == null) {
            return BigDecimal.ZERO;
        }

        return computedDiscount;
    }

    /**
     * @param expression el expression
     * @param userAccount user account
     * @param wallet wallet
     * @param invoice invoice
     * @param subCatTotal total of sub category
     * @return amount
     * @throws BusinessException business exception
     */
    private BigDecimal evaluateDiscountPercentExpression(String expression, BillingAccount billingAccount, WalletInstance wallet, Invoice invoice, BigDecimal subCatTotal) throws BusinessException {

        if (StringUtils.isBlank(expression)) {
            return null;
        }
        Map<Object, Object> userMap = new HashMap<Object, Object>();
        userMap.put(ValueExpressionWrapper.VAR_CUSTOMER_ACCOUNT, billingAccount != null ? billingAccount.getCustomerAccount() : null);
        userMap.put(ValueExpressionWrapper.VAR_BILLING_ACCOUNT, billingAccount);
        userMap.put("iv", invoice);
        userMap.put("invoice", invoice);
        userMap.put("wa", wallet);
        userMap.put("amount", subCatTotal);

        BigDecimal result = ValueExpressionWrapper.evaluateExpression(expression, userMap, BigDecimal.class);
        return result;
    }

    public BigDecimal getDiscountAmount(BigDecimal amountToApplyDiscountOn, DiscountPlanItem discountPlanItem, Product product, List<AttributeValue> attributeValues)
            throws BusinessException {


        if (BigDecimal.ZERO.compareTo(amountToApplyDiscountOn) == 0) {
            return null;
        }


        BigDecimal discountValue = getDiscountAmountOrPercent(null, null, amountToApplyDiscountOn, discountPlanItem,product,Set.copyOf(attributeValues));

        if (BigDecimal.ZERO.compareTo(discountValue) == 0) {
            return BigDecimal.ZERO;
        }

        BigDecimal discountAmount = null;

        // Percent based discount
        if (discountPlanItem.getDiscountPlanItemType() == DiscountPlanItemTypeEnum.PERCENTAGE) {

        	discountAmount= amountToApplyDiscountOn.abs().multiply(discountValue.negate().divide(HUNDRED));

            // Amount based discount
        } else {

            discountAmount = discountValue.negate();

            // If the discount and the aggregate are of opposite signs, then the absolute value of the discount must not be greater than the absolute value of the
            // considered invoice aggregate
            if (!((discountAmount.compareTo(BigDecimal.ZERO) < 0 && amountToApplyDiscountOn.compareTo(BigDecimal.ZERO) < 0)
                    || (discountAmount.compareTo(BigDecimal.ZERO) > 0 && amountToApplyDiscountOn.compareTo(BigDecimal.ZERO) > 0)) && (discountAmount.abs().compareTo(amountToApplyDiscountOn.abs()) > 0)) {

            	discountAmount=amountToApplyDiscountOn.negate();
            }
        }

        if (discountAmount == null || discountAmount.compareTo(BigDecimal.ZERO) == 0) {
            return BigDecimal.ZERO;
        }


        return discountAmount;

    }
    public List<DiscountPlanItem> getApplicableDiscountPlanItems(BillingAccount billingAccount, DiscountPlan discountPlan, OfferTemplate offer, Product product, AccountingArticle accountingArticle)
            throws BusinessException {
        List<DiscountPlanItem> applicableDiscountPlanItems = new ArrayList<>(); 
                List<DiscountPlanItem> discountPlanItems = getActiveDiscountPlanItem(discountPlan.getId());
                Long lowPriority=null;
                for (DiscountPlanItem discountPlanItem : discountPlanItems) {
                	
                    if ((lowPriority==null ||lowPriority.equals(discountPlanItem.getPriority())) && discountPlanItem.isActive() && (discountPlanItem.getTargetAccountingArticle().isEmpty()  || accountingArticle == null || (discountPlanItem.getTargetAccountingArticle().contains(accountingArticle)) && discountPlanService.matchDiscountPlanExpression(discountPlanItem.getExpressionEl(), billingAccount,null,offer, product, null))) {
                    	lowPriority=lowPriority!=null?lowPriority:discountPlanItem.getPriority();
                    	applicableDiscountPlanItems.add(discountPlanItem);
                    }
                } 
        return applicableDiscountPlanItems;
    }
    
    /**
     * Get active price plans for a given charge code. Only these are applicable for rating.
     * 
     * @param chargeCode Charge code
     * @return A list of applicable price plans matching a charge code and ordered by priority
     */
    public List<DiscountPlanItem> getActiveDiscountPlanItem(Long discountPlanId) {
        return getEntityManager().createNamedQuery("DiscountPlanItem.getActiveDiscountPlanItem", DiscountPlanItem.class).setParameter("discountPlanId", discountPlanId).getResultList();
    }

    public List<InvoiceLine> applyDiscounts(InvoiceLine invoiceLine, DiscountPlanTypeEnum... discountPlanTypeEnums) {
        List<DiscountPlanTypeEnum> types = discountPlanTypeEnums != null ? Arrays.asList(discountPlanTypeEnums) : new ArrayList<DiscountPlanTypeEnum>();
        if (invoiceLine.getDiscountPlan() == null || (!types.contains(invoiceLine.getDiscountPlan().getDiscountPlanType()))) {
            return new ArrayList<InvoiceLine>();
        }
        List<InvoiceLine> discountPrices = new ArrayList<>();
        AccountingArticle accountintArticle = invoiceLine.getAccountingArticle();
        Product product = invoiceLine.getProduct();
        OfferTemplate offerTemplate = invoiceLine.getOfferTemplate();
        BigDecimal amountWithoutTax = invoiceLine.getAmountWithoutTax();
        BigDecimal discountAmount = BigDecimal.ZERO;
        boolean isDiscountApplicable = discountPlanService.isDiscountPlanApplicable(invoiceLine.getBillingAccount(), invoiceLine.getDiscountPlan(), offerTemplate, product, invoiceLine.getValueDate());
        if (isDiscountApplicable) {
            List<DiscountPlanItem> discountItems = getApplicableDiscountPlanItems(invoiceLine.getBillingAccount(), invoiceLine.getDiscountPlan(), offerTemplate, product, accountintArticle);
            for (DiscountPlanItem discountPlanItem : discountItems) {
                AccountingArticle discountAccountingArticle = discountPlanItem.getAccountingArticle();
                @SuppressWarnings("unchecked")
                List<AttributeValue> attributesValues = new ArrayList(invoiceLine.getServiceInstance().getAttributeInstances());
                discountAmount = discountAmount.add(getDiscountAmount(amountWithoutTax, discountPlanItem, product, attributesValues));
                if (discountAmount != null && discountAmount.abs().compareTo(BigDecimal.ZERO) > 0) {
                    BigDecimal[] amounts = NumberUtils.computeDerivedAmounts(discountAmount, discountAmount, invoiceLine.getTaxRate(), appProvider.isEntreprise(), BaseEntity.NB_DECIMALS, RoundingMode.HALF_UP);
                    invoiceLinesService.createInvoiceLine(null, discountAccountingArticle, invoiceLine.getProductVersion(), invoiceLine.getOrderLot(), invoiceLine.getOfferTemplate(),invoiceLine.getOrderOffer(), amounts[0], amounts[1], amounts[2], invoiceLine.getTaxRate());
                }
            }

        }
        return discountPrices;
    }
}