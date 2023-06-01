package org.meveo.service.catalog.impl;

import org.meveo.model.cpq.contract.ContractItem;
import org.meveo.model.pricelist.PriceListLine;
import org.meveo.service.base.BusinessService;

import javax.ejb.Stateless;
import javax.persistence.Query;
import java.util.List;

@Stateless
public class PriceListLineService extends BusinessService<PriceListLine> {

    /**
     * Get applicable PriceListLine using the following criteria
     * @param pPriceListId PriceList id
     * @param pOfferTemplateId Offer Template id
     * @param pProductId Product id
     * @param pChargeTemplateId Charge Template id
     * @return {@link PriceListLine}
     */
    public PriceListLine getApplicablePriceListLine(Long pPriceListId, Long pOfferTemplateId, Long pProductId, Long pChargeTemplateId) {
        StringBuilder lStringBuilder = new StringBuilder("SELECT pll FROM PriceListLine pll WHERE pll.priceList.id = :priceListId");

        if (pOfferTemplateId != null) {
            lStringBuilder.append(" AND pll.offerTemplate.id = :offerId");
        }

        if (pProductId != null) {
            lStringBuilder.append(" AND pll.product.id = :productId");
        }

        if (pChargeTemplateId != null) {
            lStringBuilder.append(" AND pll.chargeTemplate.id = :chargeTemplate");
        }

        Query query = getEntityManager().createQuery(lStringBuilder.toString());
        query.setParameter("priceListId", pPriceListId);

        if (pOfferTemplateId != null) {
            query.setParameter("offerId", pOfferTemplateId);
        }

        if (pProductId != null) {
            query.setParameter("productId", pProductId);
        }

        if (pChargeTemplateId != null) {
            query.setParameter("chargeTemplate", pChargeTemplateId);
        }

        List<PriceListLine> applicablePriceListLine = query.getResultList();
        return !applicablePriceListLine.isEmpty() ? applicablePriceListLine.get(0) : null;
    }
}
