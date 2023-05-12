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

package org.meveo.api.catalog;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import javax.ejb.Stateless;
import javax.inject.Inject;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.BooleanUtils;
import org.apache.commons.lang3.StringUtils;
import org.meveo.admin.exception.BusinessException;
import org.meveo.api.BaseApi;
import org.meveo.api.dto.catalog.DiscountPlanItemDto;
import org.meveo.api.dto.catalog.TradingDiscountPlanItemDto;
import org.meveo.api.dto.response.PagingAndFiltering;
import org.meveo.api.dto.response.catalog.DiscountPlanItemsResponseDto;
import org.meveo.api.exception.*;
import org.meveo.api.restful.util.GenericPagingAndFilteringUtils;
import org.meveo.model.article.AccountingArticle;
import org.meveo.model.billing.InvoiceCategory;
import org.meveo.model.billing.InvoiceSubCategory;
import org.meveo.model.billing.TradingCurrency;
import org.meveo.model.catalog.DiscountPlan;
import org.meveo.model.catalog.DiscountPlanItem;
import org.meveo.model.catalog.DiscountPlanStatusEnum;
import org.meveo.model.catalog.PricePlanMatrix;
import org.meveo.model.catalog.PricePlanMatrixVersion;
import org.meveo.model.catalog.TradingDiscountPlanItem;
import org.meveo.model.cpq.enums.VersionStatusEnum;
import org.meveo.model.crm.custom.CustomFieldInheritanceEnum;
import org.meveo.service.admin.impl.TradingCurrencyService;
import org.meveo.service.billing.impl.article.AccountingArticleService;
import org.meveo.service.catalog.impl.DiscountPlanItemService;
import org.meveo.service.catalog.impl.DiscountPlanService;
import org.meveo.service.catalog.impl.InvoiceCategoryService;
import org.meveo.service.catalog.impl.InvoiceSubCategoryService;
import org.meveo.service.catalog.impl.PricePlanMatrixService;
import org.meveo.service.catalog.impl.TradingDiscountPlanItemService;

/**
 * 
 * @author Tyshan Shi(tyshan@manaty.net)
 * @since Aug 1, 2016 9:46:32 PM
 *
 */
@Stateless
public class DiscountPlanItemApi extends BaseApi {

    @Inject
    private DiscountPlanService discountPlanService;

    @Inject
    private DiscountPlanItemService discountPlanItemService;

    @Inject
    private InvoiceCategoryService invoiceCategoryService;

    @Inject
    private InvoiceSubCategoryService invoiceSubCategoryService;

    @Inject
    private PricePlanMatrixService pricePlanMatrixService;

    @Inject
    private AccountingArticleService accountingArticleService;
    
    @Inject
    private TradingCurrencyService tradingCurrencyService;
    
    @Inject
    private TradingDiscountPlanItemService tradingDiscountPlanItemService;
    
    /**
     * creates a discount plan item
     * 
     * @param postData posted data
     * 
     * @throws MeveoApiException meveo api exception
     * @throws BusinessException business exception.
     */
    public DiscountPlanItem create(DiscountPlanItemDto postData) throws MeveoApiException, BusinessException {
    	if (StringUtils.isBlank(postData.getDiscountPlanCode())) {
            missingParameters.add("discountPlanCode");
        }
		if (postData.getDiscountValue() == null && postData.getDiscountValueEL() == null) {
			missingParameters.add("discountValue, discountValueEL");
		}
		if (postData.getDiscountPlanItemType() == null) {
			missingParameters.add("discountPlanItemType");
		}
		
        handleMissingParameters();

        DiscountPlanItem discountPlanItem = discountPlanItemService.findByCode(postData.getCode());
        if (discountPlanItem != null && postData.getCode() != null) {
            throw new EntityAlreadyExistsException(DiscountPlanItem.class, postData.getCode());
        }
        discountPlanItem = toDiscountPlanItem(postData, null);
        DiscountPlan discountPlan = discountPlanItem.getDiscountPlan();
        if(BooleanUtils.isTrue(discountPlan.getApplicableOnDiscountedPrice()) || (appProvider.isActivateCascadingDiscounts() && !discountPlan.getApplicableOnDiscountedPrice())){
            List<DiscountPlanItem> items = discountPlanItemService.findBySequence(discountPlan.getId(), discountPlanItem.getSequence());
            if(CollectionUtils.isNotEmpty(items)) {
                throw  new BusinessApiException("The sequence of this discount plan item already exist");
            }
        }
        // populate customFields
        try {
            populateCustomFields(postData.getCustomFields(), discountPlanItem, true);
        } catch (MissingParameterException | InvalidParameterException e) {
            log.error("Failed to associate custom field instance to an entity: {}", e.getMessage());
            throw e;
        } catch (Exception e) {
            log.error("Failed to associate custom field instance to an entity", e);
            throw e;
        }
        
        discountPlanItemService.create(discountPlanItem);
        discountPlanItem.setCode(discountPlanItem.getId().toString());
        if(postData.getSequence() == null) {
        	discountPlanItemService.setDisountPlanItemSequence(discountPlanItem);
        }
        discountPlanItemService.update(discountPlanItem);
        return discountPlanItem;
    }

    /**
     * updates the description of an existing discount plan item.
     * 
     * @param postData posted data to API containing discount plan infos
     * 
     * @throws MeveoApiException meveo api exception
     * @throws BusinessException business exception
     */
    public DiscountPlanItem update(DiscountPlanItemDto postData) throws MeveoApiException, BusinessException {

        if (StringUtils.isBlank(postData.getCode())) {
            missingParameters.add("discountPlanItemCode");
        }
        handleMissingParameters();

        DiscountPlanItem discountPlanItem = discountPlanItemService.findByCode(postData.getCode());

        if (discountPlanItem == null) {
            throw new EntityDoesNotExistsException(DiscountPlanItem.class, postData.getCode());
        }
        discountPlanItem = toDiscountPlanItem(postData, discountPlanItem);
        DiscountPlan discountPlan = discountPlanItem.getDiscountPlan();
        if(BooleanUtils.isTrue(discountPlan.getApplicableOnDiscountedPrice()) || (appProvider.isActivateCascadingDiscounts() && !discountPlan.getApplicableOnDiscountedPrice())){
            List<DiscountPlanItem> items = discountPlanItemService.findBySequence(discountPlan.getId(), discountPlanItem.getSequence());
            Long discountPlanItemId = discountPlanItem.getId();
            items = items.stream().filter(dpi -> dpi.getId() != discountPlanItemId).collect(Collectors.toList());
            if(CollectionUtils.isNotEmpty(items)) {
                throw  new BusinessApiException("The sequence of this discount plan item already exist");
            }
        }
        if(postData.getSequence() == null) {
        	discountPlanItemService.setDisountPlanItemSequence(discountPlanItem);
        }
        // populate customFields
        try {
            populateCustomFields(postData.getCustomFields(), discountPlanItem, false);
        } catch (MissingParameterException | InvalidParameterException e) {
            log.error("Failed to associate custom field instance to an entity: {}", e.getMessage());
            throw e;
        } catch (Exception e) {
            log.error("Failed to associate custom field instance to an entity", e);
            throw e;
        }

        return discountPlanItemService.update(discountPlanItem);
    }

    /**
     * find a discount plan item by code.
     * 
     * @param discountPlanItemCode discount plan code
     * @return discount plan
     * @throws MeveoApiException meveo api exception.
     */
    public DiscountPlanItemDto find(String discountPlanItemCode) throws MeveoApiException {

        if (StringUtils.isBlank(discountPlanItemCode)) {
            missingParameters.add("discountPlanItemCode");
            handleMissingParameters();
        }

        DiscountPlanItem discountPlanItem = discountPlanItemService.findByCode(discountPlanItemCode);
        if (discountPlanItem == null) {
            throw new EntityDoesNotExistsException(DiscountPlanItem.class, discountPlanItemCode);
        }

        return new DiscountPlanItemDto(discountPlanItem, entityToDtoConverter.getCustomFieldsDTO(discountPlanItem, CustomFieldInheritanceEnum.INHERIT_NO_MERGE));
    }

    /**
     * delete a discount plan item by code.
     * 
     * @param discountPlanItemCode discount plan item code
     * @throws MeveoApiException meveo api exception.
     * @throws BusinessException busines exception.
     */
    public void remove(String discountPlanItemCode) throws MeveoApiException, BusinessException {

        if (StringUtils.isBlank(discountPlanItemCode)) {
            missingParameters.add("discountPlanItemCode");
            handleMissingParameters();
        }

        DiscountPlanItem discountPlanItem = discountPlanItemService.findByCode(discountPlanItemCode);
        if (discountPlanItem == null) {
            throw new EntityDoesNotExistsException(DiscountPlanItem.class, discountPlanItemCode);
        }
        discountPlanItemService.remove(discountPlanItem);
    }

    /**
     * create if the the discount plan item is not existed, updates if exists.
     * 
     * @param postData posted data
     * 
     * @throws MeveoApiException meveo api exception
     * @throws BusinessException business exception.s
     */
    public void createOrUpdate(DiscountPlanItemDto postData) throws MeveoApiException, BusinessException {

        if (!StringUtils.isBlank(postData.getCode()) && discountPlanItemService.findByCode(postData.getCode()) != null) {
            update(postData);
        } else {
            create(postData);
        }
    }

    /**
     * retrieves all discount plan item of the user
     * 
     * @return list of disount plan item
     * @throws MeveoApiException meveo api exception.
     */
    public List<DiscountPlanItemDto> list() throws MeveoApiException {
        List<DiscountPlanItemDto> discountPlanItemDtos = new ArrayList<>();
        List<DiscountPlanItem> discountPlanItems = discountPlanItemService.list();
        if (discountPlanItems != null && !discountPlanItems.isEmpty()) {
            DiscountPlanItemDto dpid = null;
            for (DiscountPlanItem dpi : discountPlanItems) {
                dpid = new DiscountPlanItemDto(dpi, entityToDtoConverter.getCustomFieldsDTO(dpi, CustomFieldInheritanceEnum.INHERIT_NO_MERGE));
                discountPlanItemDtos.add(dpid);
            }
        }
        return discountPlanItemDtos;
    }

    private void processAccountingArticles(DiscountPlanItemDto postData, DiscountPlanItem discountPlanItem) {
		Set<String> accountingArticleCodes = postData.getTargetAccountingArticleCodes();
		if(accountingArticleCodes != null && !accountingArticleCodes.isEmpty()){
			Set<AccountingArticle> accountingArticles=new HashSet<AccountingArticle>();
			for(String code:accountingArticleCodes) {
				AccountingArticle accountingArticle=accountingArticleService.findByCode(code);
				if(accountingArticle == null) {
					throw new EntityDoesNotExistsException(AccountingArticle.class,code);
				}
				accountingArticles.add(accountingArticle);
			}
			discountPlanItem.setTargetAccountingArticle(accountingArticles);
		}else {
			discountPlanItem.setTargetAccountingArticle(null);
		}
	}

    public DiscountPlanItemsResponseDto list(PagingAndFiltering pagingAndFiltering) {
        DiscountPlanItemsResponseDto result = new DiscountPlanItemsResponseDto();
        result.setPaging( pagingAndFiltering );

        List<DiscountPlanItem> discountPlanItems = discountPlanItemService.list( GenericPagingAndFilteringUtils.getInstance().getPaginationConfiguration() );
        if (discountPlanItems != null) {
            for (DiscountPlanItem discountPlanItem : discountPlanItems) {
                result.getDiscountPlanItems().add(new DiscountPlanItemDto(discountPlanItem,
                        entityToDtoConverter.getCustomFieldsDTO(discountPlanItem, CustomFieldInheritanceEnum.INHERIT_NO_MERGE)));
            }
        }

        return result;
    }

    public DiscountPlanItem toDiscountPlanItem(DiscountPlanItemDto source, DiscountPlanItem target) throws MeveoApiException {
        DiscountPlanItem discountPlanItem = target;
        if (discountPlanItem == null) {
            discountPlanItem = new DiscountPlanItem();
            discountPlanItem.setCode("");
            if (source.isDisabled() != null) {
                discountPlanItem.setDisabled(source.isDisabled());
            }
        }

        if (!StringUtils.isBlank(source.getDiscountPlanCode())) {
            DiscountPlan discountPlan = discountPlanService.findByCode(source.getDiscountPlanCode());
            if (discountPlan == null) {
                throw new EntityDoesNotExistsException(DiscountPlan.class, source.getDiscountPlanCode());
            }
            if (discountPlanItem.getDiscountPlan() != null && discountPlan != discountPlanItem.getDiscountPlan()) {
                throw new MeveoApiException("Parent discountPlan " + discountPlanItem.getDiscountPlan().getCode() + " of item " + source.getCode()
                        + " NOT match with DTO discountPlan " + source.getDiscountPlanCode());
            }
            discountPlanItem.setDiscountPlan(discountPlan);
        }

        if (!StringUtils.isBlank(source.getInvoiceCategoryCode())) {
            InvoiceCategory invoiceCategory = invoiceCategoryService.findByCode(source.getInvoiceCategoryCode());
            if (invoiceCategory == null) {
                throw new EntityDoesNotExistsException(InvoiceCategory.class, source.getInvoiceCategoryCode());
            }
            discountPlanItem.setInvoiceCategory(invoiceCategory);
        }

        if (!StringUtils.isBlank(source.getInvoiceSubCategoryCode())) {
            InvoiceSubCategory invoiceSubCategory = invoiceSubCategoryService.findByCode(source.getInvoiceSubCategoryCode());
            if (invoiceSubCategory == null) {
                throw new EntityDoesNotExistsException(InvoiceSubCategory.class, source.getInvoiceSubCategoryCode());
            }
            discountPlanItem.setInvoiceSubCategory(invoiceSubCategory);
        }
        if (!StringUtils.isBlank(source.getPricePlanMatrixCode())) {
        PricePlanMatrix pricePlanMatrix = pricePlanMatrixService.findByCode(source.getPricePlanMatrixCode());
        if (pricePlanMatrix == null)
            throw new EntityDoesNotExistsException(PricePlanMatrix.class, source.getPricePlanMatrixCode());
        discountPlanItem.setPricePlanMatrix(pricePlanMatrix);
        }
        
        if (!StringUtils.isBlank(source.getAccountingArticleCode())) {
        	AccountingArticle accountingArticle = accountingArticleService.findByCode(source.getAccountingArticleCode());
        	if (accountingArticle == null){
                accountingArticle = discountPlanItemService.getDiscountDefaultAccountingArticle();
            }
        	discountPlanItem.setAccountingArticle(accountingArticle);
        }

        processAccountingArticles(source,discountPlanItem);

        if (source.getExpressionEl() != null) {
            discountPlanItem.setExpressionEl(source.getExpressionEl());
        }
        discountPlanItem.setDiscountValue(source.getDiscountValue());
        if (source.getDiscountValueEL() != null) {
            discountPlanItem.setDiscountValueEL(source.getDiscountValueEL());
        }
        if (source.getDiscountPlanItemType() != null) {
            discountPlanItem.setDiscountPlanItemType(source.getDiscountPlanItemType());
        }
        if (source.isAllowToNegate() != null) {
            discountPlanItem.setAllowToNegate(source.isAllowToNegate());
        }
        if(!StringUtils.isEmpty(source.getDescription())) {
        	discountPlanItem.setDescription(source.getDescription());
        }
        if(source.getPriority()!=null) {
        	discountPlanItem.setPriority(source.getPriority());
        }
        if(source.getApplyByArticle()!=null) {
            discountPlanItem.setApplyByArticle(source.getApplyByArticle()); 
            }
        
        if (source.getSequence() != null) {
        	discountPlanItem.setSequence(source.getSequence());
        }
        discountPlanItem.setLastDiscount(source.getLastDiscount()); 
        return discountPlanItem;
    }

    /**
     * Enable or disable Discount plan item
     * 
     * @param code Discount plan item code
     * @param enable Should Discount plan item be enabled
     * @throws EntityDoesNotExistsException Entity does not exist
     * @throws MissingParameterException Missing parameters
     * @throws BusinessException A general business exception
     */
    public void enableOrDisable(String code, boolean enable) throws EntityDoesNotExistsException, MissingParameterException, BusinessException {

        if (StringUtils.isBlank(code)) {
            missingParameters.add("code");
        }

        handleMissingParameters();

        DiscountPlanItem discountPlanItem = discountPlanItemService.findByCode(code);
        if (discountPlanItem == null) {
            throw new EntityDoesNotExistsException(DiscountPlanItem.class, code);
        }
        if (enable) {
            discountPlanItemService.enable(discountPlanItem);
        } else {
            discountPlanItemService.disable(discountPlanItem);
        }
    }
    
    public TradingDiscountPlanItem createTradingDiscountPlanItem(TradingDiscountPlanItemDto dto) throws MeveoApiException, BusinessException {
    	TradingDiscountPlanItem entity = dtoToEntity(dto, new TradingDiscountPlanItem());
        tradingDiscountPlanItemService.create(entity);
        return entity;
    }
    
    public TradingDiscountPlanItem updateTradingDiscountPlanItem(Long tradingDiscountPlanItemId, TradingDiscountPlanItemDto dto) throws MeveoApiException, BusinessException {
    	TradingDiscountPlanItem entity = dtoToEntity(dto, checkIdAndGetEntity(tradingDiscountPlanItemId));
        tradingDiscountPlanItemService.update(entity);
        return entity;
    }

    public void deleteTradingDiscountPlanItem(Long tradingDiscountPlanItemId) throws MeveoApiException, BusinessException {
    	TradingDiscountPlanItem tradingDiscountPlanItem = checkIdAndGetEntity(tradingDiscountPlanItemId);
    	
        if (!DiscountPlanStatusEnum.DRAFT.equals(tradingDiscountPlanItem.getDiscountPlanItem().getDiscountPlan().getStatus())) {
            throw new InvalidParameterException("Suppression is not allowed since the discount plan is not DRAFT");
        }
        
    	tradingDiscountPlanItemService.remove(tradingDiscountPlanItem);
    }
    
    private TradingDiscountPlanItem dtoToEntity(TradingDiscountPlanItemDto dto, TradingDiscountPlanItem entity) {
        if (dto.getTradingCurrency() == null || (StringUtils.isBlank(dto.getTradingCurrency().getCode()) && dto.getTradingCurrency().getId() == null)) {
            missingParameters.add("tradingCurrency");
        }
        if (org.meveo.commons.utils.StringUtils.isBlank(dto.getDiscountPlanItemId())) {
            missingParameters.add("discountPlanItemId");
        }
        handleMissingParameters();
        
        TradingCurrency tradingCurrency = tradingCurrencyService.findByTradingCurrencyCodeOrId(dto.getTradingCurrency().getCode(), dto.getTradingCurrency().getId());
        if (tradingCurrency == null) {
            throw new EntityDoesNotExistsException(TradingCurrency.class, dto.getTradingCurrency().getCode(), "code", String.valueOf(dto.getTradingCurrency().getId()), "id");
        }

        if (appProvider.getCurrency() != null && appProvider.getCurrency().getCurrencyCode().equals(tradingCurrency.getCurrencyCode())) {
            throw new InvalidParameterException("Trading currency couldn't be the same as functional currency");
        }
        
        if (tradingCurrency.isDisabled()) {
            throw new InvalidParameterException("Trading currency should not be archived");
        }

        DiscountPlanItem discountPlanItem = discountPlanItemService.findById(dto.getDiscountPlanItemId());
        if (discountPlanItem == null) {
            throw new EntityDoesNotExistsException(PricePlanMatrixVersion.class, dto.getDiscountPlanItemId());
        }
        
        if (!DiscountPlanStatusEnum.DRAFT.equals(discountPlanItem.getDiscountPlan().getStatus())) {
            throw new InvalidParameterException("Creation and modification is not allowed since the discount plan is not DRAFT");
        }
        
        TradingDiscountPlanItem tdpi = tradingDiscountPlanItemService.findByDiscountPlanItemAndCurrency(discountPlanItem, tradingCurrency);
        if (tdpi != null && !tdpi.getId().equals(entity.getId())) {
            throw new BusinessException("Trading discount plan item already exist for discount Plan " + discountPlanItem.getId() + " and currency " + tradingCurrency.getCurrencyCode());
        }

        entity.setDiscountPlanItem(discountPlanItem);
        entity.setTradingCurrency(tradingCurrency);
        entity.setRate(dto.getRate());
        entity.setTradingDiscountValue(dto.getTradingDiscountValue());
        
        return entity;
    }
    
	private TradingDiscountPlanItem checkIdAndGetEntity(Long tradingDiscountPlanItemId) {
		if (org.meveo.commons.utils.StringUtils.isBlank(tradingDiscountPlanItemId)) {
            missingParameters.add("id");
            handleMissingParameters();
        }
    	
    	TradingDiscountPlanItem tradingDiscountPlanItem = tradingDiscountPlanItemService.findById(tradingDiscountPlanItemId);
    	if (tradingDiscountPlanItem == null) {
            throw new EntityDoesNotExistsException(TradingDiscountPlanItem.class, tradingDiscountPlanItemId);
        }
		return tradingDiscountPlanItem;
	}
}