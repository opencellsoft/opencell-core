package org.meveo.service.catalog.impl;

import javax.ejb.Stateless;
import javax.persistence.NoResultException;
import javax.persistence.TypedQuery;

import org.meveo.model.pricelist.PriceListLine;
import org.meveo.service.base.BusinessService;

@Stateless
public class PriceListLineService extends BusinessService<PriceListLine> {

    /**
     * Get applicable PriceListLine using the following criteria
     * 
     * @param pPriceListId PriceList id
     * @param pOfferTemplateId Offer Template id
     * @param pProductId Product id
     * @param pChargeTemplateId Charge Template id
     * @return {@link PriceListLine}
     */
    public PriceListLine getApplicablePriceListLine(Long pPriceListId, Long pOfferTemplateId, Long pProductId, Long pChargeTemplateId) {
        StringBuilder lStringBuilder = new StringBuilder("SELECT pll FROM PriceListLine pll WHERE pll.priceList.id = :priceListId");

        if (pOfferTemplateId != null) {
            lStringBuilder.append(" AND (pll.offerTemplate is null OR pll.offerTemplate.id = :offerId)");
        }

        if (pProductId != null) {
            lStringBuilder.append(" AND (pll.product is null OR pll.product.id = :productId)");
        }

        if (pChargeTemplateId != null) {
            lStringBuilder.append(" AND pll.chargeTemplate.id = :chargeTemplateId");
        }

        TypedQuery<PriceListLine> query = getEntityManager().createQuery(lStringBuilder.toString(), PriceListLine.class);
        query.setParameter("priceListId", pPriceListId);

        if (pOfferTemplateId != null) {
            query.setParameter("offerId", pOfferTemplateId);
        }

        if (pProductId != null) {
            query.setParameter("productId", pProductId);
        }

        if (pChargeTemplateId != null) {
            query.setParameter("chargeTemplateId", pChargeTemplateId);
        }

        try {
            return query.setMaxResults(1).setHint("org.hibernate.cacheable", true).setHint("org.hibernate.readOnly", true).getSingleResult();
        } catch (NoResultException e) {
            return null;
        }
    }
}