package org.meveo.service.catalog.impl;

import static org.meveo.model.pricelist.PriceListStatusEnum.ACTIVE;

import java.util.Date;
import java.util.List;

import javax.persistence.TypedQuery;

import org.apache.commons.lang3.StringUtils;
import org.meveo.model.pricelist.PriceList;
import org.meveo.service.base.BusinessService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class PriceListService extends BusinessService<PriceList> {

	private static final Logger LOGGER = LoggerFactory.getLogger(PriceListService.class);
    
	/**
	 * Get ExpiredOpenPriceList
	 * @return List of {@link PriceList}
	 */
	public List<PriceList> getExpiredOpenPriceList() {
		TypedQuery<PriceList> query = getEntityManager().createNamedQuery("PriceList.getExpiredOpenPriceList", PriceList.class);
        return query.setParameter("untilDate", new Date()).getResultList();
    }

	/**
	 * Get Price List using criteria
	 * @param pPriceListCriteria
	 * @return List of {@link PriceList}
	 */
	public List<PriceList> getPriceList(PriceListCriteria pPriceListCriteria) {
    	TypedQuery<PriceList> query = getEntityManager().createQuery(getPriceListQuery(pPriceListCriteria, false), PriceList.class);
		this.setQueryParams(pPriceListCriteria, query);
		return query.setFirstResult(pPriceListCriteria.getOffset().intValue())
					.setMaxResults(pPriceListCriteria.getLimit().intValue())
					.getResultList();
    }
    
	/**
	 * Get Price List using criteria
	 * @param pPriceListCriteria
	 * @return Count of {@link PriceList}
	 */
	public Long count(PriceListCriteria pPriceListCriteria) {
    	TypedQuery<Long> query = getEntityManager().createQuery(getPriceListQuery(pPriceListCriteria, true), Long.class);
    	this.setQueryParams(pPriceListCriteria, query);
		return query.getSingleResult();
    	
	}
	/**
	 * Build Query to get Price List
	 * @param pPriceListCriteria
	 * @param pIsCount Is count
	 * @return Query
	 */
	private String getPriceListQuery(PriceListCriteria pPriceListCriteria, boolean pIsCount) {
		String jpqlQuery = StringUtils.EMPTY;
		
		if(pIsCount) {
			jpqlQuery += "SELECT COUNT(pl) FROM ";
		} else {
			jpqlQuery += "SELECT pl FROM ";
		}

		jpqlQuery += "PriceList pl LEFT JOIN pl.paymentMethods pms ";

		jpqlQuery += "WHERE pl.status = :activeStatus AND :currentDate BETWEEN pl.applicationStartDate AND pl.applicationEndDate ";

    	if(pPriceListCriteria.getBrandId() != null) {
			jpqlQuery += "AND (pl.brands IS EMPTY OR EXISTS (select 1 from CustomerBrand cb where cb.id=:brandId and cb member of pl.brands)) ";
		} else {
			jpqlQuery += "AND pl.brands IS EMPTY ";
		}

		if(pPriceListCriteria.getCustomerCategoryId() != null) {
			jpqlQuery += "AND (pl.customerCategories IS EMPTY OR EXISTS (select 1 from CustomerCategory cc where cc.id=:customerCategoryId and cc member of pl.customerCategories)) ";
		} else {
			jpqlQuery += "AND pl.customerCategories IS EMPTY ";
		}

		if(pPriceListCriteria.getCreditCategoryId() != null) {
			jpqlQuery += "AND (pl.creditCategories IS EMPTY OR EXISTS (select 1 from CreditCategory crc where crc.id=:creditCategoryId and crc member of pl.creditCategories)) ";
		} else {
			jpqlQuery += "AND pl.creditCategories IS EMPTY ";
		}

		if(pPriceListCriteria.getCountryId() != null) {
			jpqlQuery += "AND (pl.countries IS EMPTY OR EXISTS (select 1 from Country cou where cou.id=:countryId and cou member of pl.countries)) ";
		} else {
			jpqlQuery += "AND pl.countries IS EMPTY ";
		}

		if(pPriceListCriteria.getCurrencyId() != null) {
			jpqlQuery += "AND (pl.currencies IS EMPTY OR EXISTS (select 1 from Currency cur where cur.id=:currencyId and cur member of pl.currencies)) ";
		} else {
			jpqlQuery += "AND pl.currencies IS EMPTY ";
		}

		if(pPriceListCriteria.getTitleId() != null) {
			jpqlQuery += "AND (pl.legalEntities IS EMPTY OR EXISTS (select 1 from Title ti where ti.id=:titleId and ti member of pl.legalEntities)) ";
		} else {
			jpqlQuery += "AND pl.legalEntities IS EMPTY ";
		}

		if(pPriceListCriteria.getSellerId() != null) {
			jpqlQuery += "AND (pl.sellers IS EMPTY OR EXISTS (select 1 from Seller sel where sel.id=:sellerId and sel member of pl.sellers)) ";
		} else {
			jpqlQuery += "AND pl.sellers IS EMPTY ";
		}

		if(pPriceListCriteria.getPaymentMethodEnum() != null) {
			jpqlQuery += "AND (pms IS NULL OR :paymentMethod = pms) ";
		} else {
			jpqlQuery += "AND (pms IS NULL) ";
		}

		if(pPriceListCriteria.getAttachedPriceListId() != null) {
			jpqlQuery += "OR pl.id = :attachedPriceListId ";
		}
    	
		if(!pIsCount) {
			if(pPriceListCriteria.getSortBy() != null) {
	    		jpqlQuery += "order by pl." + pPriceListCriteria.getSortBy() + StringUtils.SPACE + pPriceListCriteria.getSortOrder() ;
	    	} else {
	    		jpqlQuery += "order by pl.applicationStartDate " + pPriceListCriteria.getSortOrder();
	    	}
		}

		LOGGER.trace("JPQL Query to execute: {}", jpqlQuery);
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

	private void setQueryParams(PriceListCriteria pPriceListCriteria, TypedQuery<?> query) {
		query.setParameter("activeStatus", List.of(ACTIVE)).setParameter("currentDate", new Date());

		if(pPriceListCriteria.getBrandId() != null) {
			query.setParameter("brandId", pPriceListCriteria.getBrandId());
		}

		if(pPriceListCriteria.getCustomerCategoryId() != null) {
			query.setParameter("customerCategoryId", pPriceListCriteria.getCustomerCategoryId());
		}

		if(pPriceListCriteria.getCreditCategoryId() != null) {
			query.setParameter("creditCategoryId", pPriceListCriteria.getCreditCategoryId());
		}

		if(pPriceListCriteria.getCountryId() != null) {
			query.setParameter("countryId", pPriceListCriteria.getCountryId());
		}

		if(pPriceListCriteria.getCurrencyId() != null) {
			query.setParameter("currencyId", pPriceListCriteria.getCurrencyId());
		}

		if(pPriceListCriteria.getTitleId() != null) {
			query.setParameter("titleId", pPriceListCriteria.getTitleId());
		}

		if(pPriceListCriteria.getPaymentMethodEnum() != null) {
			query.setParameter("paymentMethod", pPriceListCriteria.getPaymentMethodEnum());
		}

		if(pPriceListCriteria.getSellerId() != null) {
			query.setParameter("sellerId", pPriceListCriteria.getSellerId());
		}

		if(pPriceListCriteria.getAttachedPriceListId() != null) {
			query.setParameter("attachedPriceListId", pPriceListCriteria.getAttachedPriceListId());
		}
	}
}
