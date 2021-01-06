package org.meveo.service.catalog.impl;

import org.meveo.jpa.JpaAmpNewTx;
import org.meveo.model.catalog.PricePlanMatrixLine;
import org.meveo.model.catalog.PricePlanMatrixValue;
import org.meveo.service.base.PersistenceService;

import javax.ejb.Stateless;
import javax.ejb.TransactionAttribute;
import javax.ejb.TransactionAttributeType;
import javax.inject.Inject;
import java.util.List;

@Stateless
public class PricePlanMatrixLineService extends PersistenceService<PricePlanMatrixLine> {

    @Inject
    private PricePlanMatrixValueService pricePlanMatrixValueService;

    @JpaAmpNewTx
    @TransactionAttribute(TransactionAttributeType.REQUIRES_NEW)
    public PricePlanMatrixLine addPricePlanMatrixLine(PricePlanMatrixLine pricePlanMatrixLine, List<PricePlanMatrixValue> pricePlanMatrixValues) {
        create(pricePlanMatrixLine);

        pricePlanMatrixValues.stream()
                .forEach(value -> {
                    value.setPricePlanMatrixLine(pricePlanMatrixLine);
                    pricePlanMatrixValueService.create(value);
                });

        return pricePlanMatrixLine;
    }
}
