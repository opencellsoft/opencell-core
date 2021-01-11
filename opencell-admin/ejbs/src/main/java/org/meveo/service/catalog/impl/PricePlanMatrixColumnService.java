package org.meveo.service.catalog.impl;

import org.meveo.model.catalog.PricePlanMatrixColumn;
import org.meveo.model.cpq.Attribute;
import org.meveo.model.cpq.Product;
import org.meveo.service.base.BusinessService;

import javax.ejb.Stateless;
import javax.persistence.NoResultException;
import java.util.Collections;
import java.util.List;

@Stateless
public class PricePlanMatrixColumnService extends BusinessService<PricePlanMatrixColumn> {

    public List<PricePlanMatrixColumn> findByAttributes(List<Attribute> attributes) {
        try {
            return getEntityManager().createNamedQuery("PricePlanMatrixColumn.findByAttributes", PricePlanMatrixColumn.class)
                    .setParameter("attributes", attributes)
                    .getResultList();
        }catch (NoResultException exp){
            return Collections.emptyList();
        }
    }

    public List<PricePlanMatrixColumn> findByProduct(Product product) {
        try {
            return getEntityManager().createNamedQuery("PricePlanMatrixColumn.findByProduct", PricePlanMatrixColumn.class)
                    .setParameter("product", product)
                    .getResultList();
        }catch (NoResultException exp){
            return Collections.emptyList();
        }
    }
}
