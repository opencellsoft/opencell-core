package org.meveo.service.catalog.impl;

import org.meveo.model.pricelist.PriceList;
import org.meveo.model.pricelist.PriceListStatusEnum;
import org.meveo.service.base.BusinessService;

import java.util.Date;
import java.util.List;

import static org.meveo.model.pricelist.PriceListStatusEnum.*;

public class PriceListService extends BusinessService<PriceList> {
    public List<PriceList> getExpiredOpenPriceList() {
        return getEntityManager().createNamedQuery("PriceList.getExpiredOpenPriceList")
                                 .setParameter("untilDate", new Date())
                                 .setParameter("openStatus", List.of(DRAFT, ACTIVE))
                                 .getResultList();
    }
}
