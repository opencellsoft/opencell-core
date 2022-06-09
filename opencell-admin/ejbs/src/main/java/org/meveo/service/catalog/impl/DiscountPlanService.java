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

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import javax.ejb.Stateless;
import javax.inject.Inject;

import org.meveo.admin.exception.BusinessException;
import org.meveo.api.exception.EntityDoesNotExistsException;
import org.meveo.api.exception.MissingParameterException;
import org.meveo.commons.utils.NumberUtils;
import org.meveo.commons.utils.StringUtils;
import org.meveo.model.BaseEntity;
import org.meveo.model.IDiscountable;
import org.meveo.model.admin.Seller;
import org.meveo.model.article.AccountingArticle;
import org.meveo.model.billing.BillingAccount;
import org.meveo.model.billing.ChargeInstance;
import org.meveo.model.billing.Invoice;
import org.meveo.model.billing.ServiceInstance;
import org.meveo.model.billing.Subscription;
import org.meveo.model.billing.WalletInstance;
import org.meveo.model.billing.WalletOperation;
import org.meveo.model.catalog.DiscountPlan;
import org.meveo.model.catalog.DiscountPlanItem;
import org.meveo.model.catalog.DiscountPlanItemTypeEnum;
import org.meveo.model.catalog.DiscountPlanStatusEnum;
import org.meveo.model.catalog.DiscountPlanTypeEnum;
import org.meveo.model.catalog.OfferTemplate;
import org.meveo.model.cpq.Product;
import org.meveo.model.billing.InvoiceLine;
import org.meveo.model.cpq.offer.QuoteOffer;
import org.meveo.model.quote.QuoteProduct;
import org.meveo.model.quote.QuoteVersion;
import org.meveo.service.base.BusinessService;
import org.meveo.service.base.ValueExpressionWrapper;
import org.meveo.service.billing.impl.WalletOperationService;
import org.meveo.service.billing.impl.article.AccountingArticleService;
import org.meveo.service.tax.TaxMappingService;
import org.meveo.service.tax.TaxMappingService.TaxInfo;

/**
 * @author Edward P. Legaspi
 **/
@Stateless
public class DiscountPlanService extends BusinessService<DiscountPlan> {


    @Inject
    private AccountingArticleService accountingArticleService;
    
    @Inject
    private DiscountPlanItemService discountPlanItemService;
    
    @Inject
    private WalletOperationService walletOperationService;

    @Inject
    private TaxMappingService taxMappingService;
    
	@Override
	public void create(DiscountPlan entity) throws BusinessException {
		// check date
		if (!entity.isValid()) {
			log.error("Invalid effectivity dates");
			throw new BusinessException("Invalid effectivity dates");
		}
		if (entity.getDiscountPlanType() != null && (entity.getDiscountPlanType().equals(DiscountPlanTypeEnum.QUOTE) || entity.getDiscountPlanType()
				.equals(DiscountPlanTypeEnum.INVOICE) || entity.getDiscountPlanType().equals(DiscountPlanTypeEnum.INVOICE_LINE)) || entity.getInitialQuantity() == null) {
			entity.setInitialQuantity(0L);
		}

		if (entity.getStatus().equals(DiscountPlanStatusEnum.DRAFT) || entity.getStatus().equals(DiscountPlanStatusEnum.ACTIVE)) {
			entity.setStatusDate(new Date());
			super.create(entity);
		} else {
			log.error("Only status DRAFT and ACTIVE are allowed to create a discount plan: {}", entity.getCode());
			throw new BusinessException("Only status DRAFT and ACTIVE are allowed to create a discount plan: " + entity.getCode());
		}

	}

	@Override
	public DiscountPlan update(DiscountPlan entity) throws BusinessException {
		// check date
		if (!entity.isValid()) {
			log.error("Invalid effectivity dates");
			throw new BusinessException("Invalid effectivity dates");
		}
		return super.update(entity);
	}


    /**
     * @param expression EL exprestion
     * @param customerAccount customer account
     * @param billingAccount billing account
     * @param invoice invoice
     * @param dpi the discount plan instance
     * @return true/false
     * @throws BusinessException business exception.
     */
	 public boolean matchDiscountPlanExpression(String expression, IDiscountable entity,WalletOperation walletOperation,QuoteVersion quoteVersion, Invoice invoice,QuoteOffer offer,QuoteProduct product, DiscountPlan dp) throws BusinessException {
	        Boolean result = true;

        if (StringUtils.isBlank(expression)) {
            return result;
        }
        Object res = ValueExpressionWrapper.evaluateExpression(expression, Boolean.class, quoteVersion,walletOperation,  invoice, offer, product);
        Map<Object, Object> userMap = new HashMap<Object, Object>();

        if (expression.indexOf("entity") >= 0) {
            userMap.put("entity", entity);
        }
        try {
            result = (Boolean) res;
        } catch (Exception e) {
            throw new BusinessException("Expression " + expression + " do not evaluate to boolean but " + res);
        }
        return result;
    }
	 
	 public boolean matchDiscountPlanExpression(String expression, IDiscountable entity,BaseEntity...entities) throws BusinessException {
	        Boolean result = true;

	        if (StringUtils.isBlank(expression)) {
	            return result;
	        }
	        Object res = ValueExpressionWrapper.evaluateExpression(expression, Boolean.class, entities);
	        try {
	            result = (Boolean) res;
	        } catch (Exception e) {
	            throw new BusinessException("Expression " + expression + " do not evaluate to boolean but " + res);
	        }
	        return result;
	    }

	@Override
	public void remove(DiscountPlan entity) throws BusinessException {
		if (entity.getStatus().equals(DiscountPlanStatusEnum.DRAFT) || entity.getStatus().equals(DiscountPlanStatusEnum.ACTIVE)) {
			super.remove(entity);
		} else {
			log.error("Only discount plan with status DRAFT and ACTIVE is allowed to be deleted: {}", entity.getCode());
			throw new BusinessException("Only discount plan with status DRAFT and ACTIVE is allowed to be deleted: " + entity.getCode());
		}

	}

	public List<Long> getDiscountPlanToExpire(Date expireDiscountPlanToDate) {
		List<Long> ids = getEntityManager().createNamedQuery("discountPlan.getExpired", Long.class).setParameter("date", expireDiscountPlanToDate)
				.setParameter("statuses", Arrays.asList(DiscountPlanStatusEnum.ACTIVE, DiscountPlanStatusEnum.IN_USE)).getResultList();
		return ids;
	}
	
	public boolean isDiscountPlanApplicable(IDiscountable entity, DiscountPlan discountPlan,WalletOperation wo,QuoteVersion quoteVersion,QuoteOffer quoteOffer, QuoteProduct quoteProduct,Date applicationDate,InvoiceLine invoiceLine)
	{
		if (!(discountPlan.getStatus().equals(DiscountPlanStatusEnum.IN_USE)
				|| discountPlan.getStatus().equals(DiscountPlanStatusEnum.ACTIVE))) {
			return false;
		}
		if(discountPlan.getDiscountPlanType() == null)
			return false;
		OfferTemplate offer=quoteOffer!=null?quoteOffer.getOfferTemplate():wo!=null?wo.getOfferTemplate():null;
		Product product=quoteProduct!=null?quoteProduct.getProductVersion().getProduct():(wo!=null && wo.getServiceInstance().getProductVersion()!=null?wo.getServiceInstance().getProductVersion().getProduct():null);
		switch (discountPlan.getDiscountPlanType()) {
			case OFFER:
				if (offer != null && !offer.getAllowedDiscountPlans().contains(discountPlan)) {
					return false;
				}
				break;
			case PRODUCT:
				if (product != null && !product.getDiscountList().contains(discountPlan)) {
					return false;
				}
				break;
			case INVOICE_LINE:
				if (invoiceLine != null && invoiceLine.getDiscountPlan() == null) {
					return false;
				}
				break;
			default:
				break;
		}	
		applicationDate=applicationDate!=null?applicationDate:new Date();
		if (discountPlan.isActive() && discountPlan.isEffective(applicationDate)) {
			if (matchDiscountPlanExpression(discountPlan.getExpressionEl(),entity,wo,quoteVersion,null, quoteOffer, quoteProduct, discountPlan)) {
				return true;
			}
		}
		return false;
	}
	
	public boolean isDiscountPlanApplicable(IDiscountable entity, DiscountPlan discountPlan,Date applicationDate,BaseEntity... entities) {
		if (!(discountPlan.getStatus().equals(DiscountPlanStatusEnum.IN_USE) || discountPlan.getStatus().equals(DiscountPlanStatusEnum.ACTIVE))) {
			return false;
		}
		if(discountPlan.getDiscountPlanType() == null)
			return false;
			
		applicationDate=applicationDate!=null?applicationDate:new Date();
		
		if (discountPlan.isActive() && discountPlan.isEffective(applicationDate)) {
			if (matchDiscountPlanExpression(discountPlan.getExpressionEl(),entity,entities)) {
				return true;
			}
		}
		return false;
	}


	/**
	 * apply discount type of product
	 * @param chargeInstance
	 */
    public List<WalletOperation> applyPercentageDiscount(WalletOperation walletOperation, BillingAccount billingAccount, DiscountPlan discountPlan , boolean isVirtual) {
    	if(walletOperation == null)
    		throw new MissingParameterException("Wallet operation is null");
    	
    	ChargeInstance chargeInstance = walletOperation.getChargeInstance();
    	var accountingArticle = accountingArticleService.getAccountingArticleByChargeInstance(chargeInstance);
    	if(billingAccount == null || discountPlan == null )
    		throw new MissingParameterException("following parameters are required : billing account , discount plan");
    	
    	var discountPlanItems = discountPlanItemService.getApplicableDiscountPlanItems(billingAccount, discountPlan, walletOperation.getSubscription(), walletOperation, accountingArticle,DiscountPlanItemTypeEnum.PERCENTAGE, walletOperation.getOperationDate());
    	Seller seller = walletOperation.getSeller() != null ? walletOperation.getSeller() : walletOperation.getBillingAccount().getCustomerAccount().getCustomer().getSeller();
    	return calculateDiscountplanItems(discountPlanItems, seller, walletOperation.getBillingAccount(), walletOperation.getOperationDate(), walletOperation.getQuantity(), 
    										walletOperation.getUnitAmountWithoutTax(), walletOperation.getCode(), walletOperation.getWallet(), walletOperation.getOfferTemplate(), 
    										walletOperation.getServiceInstance(), walletOperation.getSubscription(), walletOperation.getDescription(), isVirtual, chargeInstance, walletOperation, DiscountPlanTypeEnum.PRODUCT,DiscountPlanTypeEnum.OFFER);
    	
    }
    
    public List<WalletOperation> calculateDiscountplanItems(List<DiscountPlanItem> discountPlanItems, Seller seller, BillingAccount billingAccount, Date operationDate, BigDecimal quantity, 
    										BigDecimal unitAmountWithoutTax, String discountCode, WalletInstance walletInstance, OfferTemplate offerTemplate, 
    										ServiceInstance serviceInstance, Subscription subscription, String discountDescription, boolean isVirtual, ChargeInstance chargeInstance, WalletOperation walletOperation, DiscountPlanTypeEnum... discountPlanTypeEnum) {
    	List<WalletOperation> discountWalletOperations = new ArrayList<WalletOperation>();
    	if(discountPlanItems != null && !discountPlanItems.isEmpty()) {
			 WalletOperation discountWalletOperation = null;
			 AccountingArticle discountAccountingArticle = null;
			 BigDecimal taxPercent = null;
			 BigDecimal walletOperationDiscountAmount = null;
			 BigDecimal[] amounts = null;

			 List<DiscountPlanItem> discountPlanItemsByType =  new ArrayList<DiscountPlanItem>(discountPlanItems);
			 
			 if(discountPlanTypeEnum != null) {
				 final List<DiscountPlanTypeEnum> discountPlanTypeEnumList=Arrays.asList(discountPlanTypeEnum);
				 discountPlanItemsByType = discountPlanItems.stream().filter(dpi -> discountPlanTypeEnumList.contains(dpi.getDiscountPlan().getDiscountPlanType())).collect(Collectors.toList());
			 }
			 
			 log.debug("calculateDiscountplanItems discountPlanTypeEnum={},discountPlanItems.size={},discountPlanItemsByType.size={}",discountPlanTypeEnum,discountPlanItems.size(), discountPlanItemsByType.size());
				
			 for (DiscountPlanItem discountPlanItem : discountPlanItemsByType) {
					 
					 discountWalletOperation = new WalletOperation();
					 discountAccountingArticle = discountPlanItem.getAccountingArticle();
					
	                if(discountAccountingArticle == null) {
	                	throw new EntityDoesNotExistsException("discount plan item "+discountPlanItem.getCode()+" has no accounting article  ");
	                }
	
	            	TaxInfo taxInfo = taxMappingService.determineTax(discountAccountingArticle.getTaxClass(), seller, billingAccount, null, operationDate, false, false);
	                    taxPercent = taxInfo.tax.getPercent();
	                
	                if(DiscountPlanItemTypeEnum.FIXED.equals(discountPlanItem.getDiscountPlanItemType())) {
	                	 unitAmountWithoutTax = discountPlanItem.getDiscountValue();
	                 }
	                walletOperationDiscountAmount = discountPlanItemService.getDiscountAmount(unitAmountWithoutTax, discountPlanItem,null, Collections.emptyList());
	                
	                amounts = NumberUtils.computeDerivedAmounts(walletOperationDiscountAmount, walletOperationDiscountAmount, taxPercent, appProvider.isEntreprise(), BaseEntity.NB_DECIMALS, RoundingMode.HALF_UP);
	                
	                discountWalletOperation.setAccountingArticle(discountAccountingArticle);
	                discountWalletOperation.setAccountingCode(discountAccountingArticle.getAccountingCode());
	                discountWalletOperation.setUnitAmountTax(walletOperationDiscountAmount);
	                discountWalletOperation.setAmountWithoutTax(quantity.compareTo(BigDecimal.ZERO)>0?quantity.multiply(amounts[0]):BigDecimal.ZERO);
	                discountWalletOperation.setAmountWithTax(quantity.multiply(amounts[1]));
	                discountWalletOperation.setAmountTax(quantity.multiply(amounts[2]));
	                discountWalletOperation.setTaxPercent(taxPercent);
	                discountWalletOperation.setUnitAmountWithoutTax(amounts[0]);
	                discountWalletOperation.setUnitAmountWithTax(amounts[1]);
	                discountWalletOperation.setUnitAmountTax(amounts[2]);
	                discountWalletOperation.setQuantity(quantity);
	                discountWalletOperation.setTax(taxInfo.tax);
	                discountWalletOperation.setCreated(new Date());
	                discountWalletOperation.setCode(discountCode);
	                discountWalletOperation.setSeller(seller);
	                discountWalletOperation.setBillingAccount(billingAccount);
	                discountWalletOperation.setDiscountPlan(discountPlanItem.getDiscountPlan());
	                discountWalletOperation.setWallet(walletInstance); // TODO: check scenario where walletInstance is null, because it will throw exception on TR job
	                discountWalletOperation.setOfferTemplate(offerTemplate);
	                discountWalletOperation.setServiceInstance(serviceInstance);
	                discountWalletOperation.setOperationDate(operationDate);
	                discountWalletOperation.setSubscription(subscription);
	                discountWalletOperation.setDescription(discountDescription);
	                discountWalletOperation.setChargeInstance(chargeInstance);
	                discountWalletOperation.setInputQuantity(quantity);
	                discountWalletOperation.setCurrency(walletOperation!=null?walletOperation.getCurrency():billingAccount.getTradingCurrency().getCurrency());
	                if(!isVirtual) {
	                	if(walletOperation != null && walletOperation.getId() != null)
	                		discountWalletOperation.setDiscountedWalletOperation(walletOperation.getId());
	                	walletOperationService.create(discountWalletOperation);
	                }else if(walletOperation != null) {
	                	discountWalletOperation.setUuid(walletOperation.getUuid());
	                }
	                //TODO: must have wallet operation for : link discountWallet to the current wallet, and
	                discountWalletOperations.add(discountWalletOperation);
			}
    	}
    	
    	return discountWalletOperations;
    }
	   
}