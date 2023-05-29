package org.meveo.service.catalog.impl;

import static org.meveo.model.pricelist.PriceListStatusEnum.ACTIVE;
import static org.meveo.model.pricelist.PriceListStatusEnum.DRAFT;

import java.util.Date;
import java.util.List;

import javax.persistence.TypedQuery;

import org.apache.commons.lang3.StringUtils;
import org.meveo.model.payments.PaymentMethodEnum;
import org.meveo.model.pricelist.PriceList;
import org.meveo.service.base.BusinessService;

public class PriceListService extends BusinessService<PriceList> {
    
	/**
	 * Get ExpiredOpenPriceList
	 * @return List of {@link PriceList}
	 */
	public List<PriceList> getExpiredOpenPriceList() {
		TypedQuery<PriceList> query = getEntityManager().createNamedQuery("PriceList.getExpiredOpenPriceList", PriceList.class);
        return query.setParameter("untilDate", new Date()).setParameter("openStatus", List.of(DRAFT, ACTIVE)).getResultList();
    }
    
	/**
	 * Get Price List using criteria
	 * @param pOffset Offset
	 * @param pLimit Limit
	 * @param pSortOrder SortOrder
	 * @param pSortBy Sort By
	 * @param pBrandId Customer Brand Id
	 * @param pCustomerCategoryId Customer Category Id
	 * @param pCreditCategoryId Credit Category Id
	 * @param pCountryId Country Id
	 * @param pCurrencyId Currency Id
	 * @param pTitleId Legal Entity Type Id
	 * @param pPaymentMethodEnum Payment Method 
	 * @param pSellerId Seller id
	 * @param pAttachedPriceListId Attached Price List to Billing Account 
	 * @return List of {@link PriceList}
	 */
	public List<PriceList> getPriceList(Long pOffset, Long pLimit, String pSortOrder, String pSortBy, Long pBrandId, Long pCustomerCategoryId, Long pCreditCategoryId, 
			Long pCountryId, Long pCurrencyId, Long pTitleId, PaymentMethodEnum pPaymentMethodEnum, Long pSellerId, Long pAttachedPriceListId) {
    	TypedQuery<PriceList> query = getEntityManager().createQuery(getPriceListQuery(pSortOrder, pSortBy, false), PriceList.class);
    	return query.setParameter("activeStatus", List.of(ACTIVE))
					.setParameter("currentDate", new Date())
					.setParameter("brandId", pBrandId)
					.setParameter("customerCategoryId", pCustomerCategoryId)
					.setParameter("creditCategoryId", pCreditCategoryId)
					.setParameter("countryId", pCountryId)
					.setParameter("currencyId", pCurrencyId)
					.setParameter("titleId", pTitleId)
					.setParameter("paymentMethod", pPaymentMethodEnum)
					.setParameter("sellerId", pSellerId)
					.setParameter("attachedPriceListId", pAttachedPriceListId)
					.setFirstResult(pOffset.intValue())
					.setMaxResults(pLimit.intValue())
					.getResultList();
    }
    
	/**
	 * Count Price List using criteria
	 * @param pSortOrder SortOrder
	 * @param pSortBy Sort By
	 * @param pBrandId Customer Brand Id
	 * @param pCustomerCategoryId Customer Category Id
	 * @param pCreditCategoryId Credit Category Id
	 * @param pCountryId Country Id
	 * @param pCurrencyId Currency Id
	 * @param pTitleId Legal Entity Type Id
	 * @param pPaymentMethodEnum Payment Method 
	 * @param pSellerId Seller id
	 * @param pAttachedPriceListId Attached Price List to Billing Account 
	 * @return Count of {@link PriceList}
	 */
	public Long count(String pSortOrder, String pSortBy, Long pBrandId, Long pCustomerCategoryId, Long pCreditCategoryId, 
			Long pCountryId, Long pCurrencyId, Long pTitleId, PaymentMethodEnum pPaymentMethodEnum, Long pSellerId, Long pAttachedPriceListId) {
    	TypedQuery<Long> query = getEntityManager().createQuery(getPriceListQuery(pSortOrder, pSortBy, true), Long.class);
    	return query.setParameter("activeStatus", List.of(ACTIVE))
        	.setParameter("currentDate", new Date())
            .setParameter("brandId", pBrandId)
            .setParameter("customerCategoryId", pCustomerCategoryId)
            .setParameter("creditCategoryId", pCreditCategoryId)
            .setParameter("countryId", pCountryId)
            .setParameter("currencyId", pCurrencyId)
            .setParameter("titleId", pTitleId)
            .setParameter("paymentMethod", pPaymentMethodEnum)
            .setParameter("sellerId", pSellerId)
            .setParameter("attachedPriceListId", pAttachedPriceListId)
            .getSingleResult();
    	
	}

	/**
	 * Build Query to get Price List
	 * @param pSortOrder SortOrder
	 * @param pSortBy SortBy
	 * @param pIsCount Is count
	 * @return Query
	 */
	private String getPriceListQuery(String pSortOrder, String pSortBy, boolean pIsCount) {
		String jpqlQuery = StringUtils.EMPTY;
		
		if(pIsCount) {
			jpqlQuery += "SELECT COUNT(pl) FROM ";
		} else {
			jpqlQuery += "SELECT pl FROM ";
		}
    	
		jpqlQuery += "PriceList pl WHERE pl.status = :activeStatus AND :currentDate BETWEEN pl.applicationStartDate AND pl.applicationEndDate AND ("
    				+ 	"EXISTS (select 1 from CustomerBrand cb where cb.id=:brandId and cb member of pl.brands) OR "
    				+ 	"EXISTS (select 1 from CustomerCategory cc where cc.id=:customerCategoryId and cc member of pl.customerCategories) OR "
    				+ 	"EXISTS (select 1 from CreditCategory crc where crc.id=:creditCategoryId and crc member of pl.creditCategories) OR "
    				+ 	"EXISTS (select 1 from Country cou where cou.id=:countryId and cou member of pl.countries) OR "
    				+ 	"EXISTS (select 1 from Currency cur where cur.id=:currencyId and cur member of pl.currencies) OR "
    				+ 	"EXISTS (select 1 from Title ti where ti.id=:titleId and ti member of pl.legalEntities) OR "
    				+ 	"EXISTS (select 1 from Seller sel where sel.id=:sellerId and sel member of pl.sellers) OR "
    				+ 	":paymentMethod member of pl.paymentMethods) OR "
    				+ 	"pl.id = :attachedPriceListId ";
    	
		if(!pIsCount) {
			if(pSortBy != null) {
	    		jpqlQuery += "order by pl." + pSortBy + StringUtils.SPACE + pSortOrder ;    		
	    	} else {
	    		jpqlQuery += "order by pl.applicationStartDate " + pSortOrder;
	    	}
		}
    	
		return jpqlQuery;
	}
}
