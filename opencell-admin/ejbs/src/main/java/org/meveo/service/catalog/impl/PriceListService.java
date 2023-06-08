package org.meveo.service.catalog.impl;

import static org.meveo.model.pricelist.PriceListStatusEnum.ACTIVE;
import static org.meveo.model.pricelist.PriceListStatusEnum.DRAFT;

import java.util.Date;
import java.util.List;

import javax.persistence.TypedQuery;

import org.apache.commons.lang3.StringUtils;
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
	 * @param pPriceListCriteria
	 * @return List of {@link PriceList}
	 */
	public List<PriceList> getPriceList(PriceListCriteria pPriceListCriteria) {
    	TypedQuery<PriceList> query = getEntityManager().createQuery(getPriceListQuery(pPriceListCriteria.getSortOrder(), pPriceListCriteria.getSortBy(), false), PriceList.class);
    	return query.setParameter("activeStatus", List.of(ACTIVE))
					.setParameter("currentDate", new Date())
					.setParameter("brandId", pPriceListCriteria.getBrandId())
					.setParameter("customerCategoryId", pPriceListCriteria.getCustomerCategoryId())
					.setParameter("creditCategoryId", pPriceListCriteria.getCreditCategoryId())
					.setParameter("countryId", pPriceListCriteria.getCountryId())
					.setParameter("currencyId", pPriceListCriteria.getCurrencyId())
					.setParameter("titleId", pPriceListCriteria.getTitleId())
					.setParameter("paymentMethod", pPriceListCriteria.getPaymentMethodEnum())
					.setParameter("sellerId", pPriceListCriteria.getSellerId())
					.setParameter("attachedPriceListId", pPriceListCriteria.getAttachedPriceListId())
					.setFirstResult(pPriceListCriteria.getOffset().intValue())
					.setMaxResults(pPriceListCriteria.getLimit().intValue())
					.getResultList();
    }
    
	/**
	 * Get Price List using criteria
	 * @param pPriceListCriteria
	 * @return Count of {@link PriceList}
	 */
	public Long count(PriceListCriteria pPriceListCriteria) {
    	TypedQuery<Long> query = getEntityManager().createQuery(getPriceListQuery(pPriceListCriteria.getSortOrder(), pPriceListCriteria.getSortBy(), true), Long.class);
    	return query.setParameter("activeStatus", List.of(ACTIVE))
        	.setParameter("currentDate", new Date())
            .setParameter("brandId", pPriceListCriteria.getBrandId())
            .setParameter("customerCategoryId", pPriceListCriteria.getCustomerCategoryId())
            .setParameter("creditCategoryId", pPriceListCriteria.getCreditCategoryId())
            .setParameter("countryId", pPriceListCriteria.getCountryId())
            .setParameter("currencyId", pPriceListCriteria.getCurrencyId())
            .setParameter("titleId", pPriceListCriteria.getTitleId())
            .setParameter("paymentMethod", pPriceListCriteria.getPaymentMethodEnum())
            .setParameter("sellerId", pPriceListCriteria.getSellerId())
            .setParameter("attachedPriceListId", pPriceListCriteria.getAttachedPriceListId())
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

	/**
	 * Get Active Price List
	 * @return List of {@link PriceList}
	 */
	public List<PriceList> getActivePriceList() {
		TypedQuery<PriceList> query = getEntityManager().createNamedQuery("PriceList.getActivePriceList", PriceList.class);
		return query.getResultList();
	}
}
