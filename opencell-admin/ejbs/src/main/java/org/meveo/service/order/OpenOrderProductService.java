package org.meveo.service.order;

import org.meveo.model.ordering.OpenOrderProduct;
import org.meveo.service.base.PersistenceService;

import javax.ejb.Stateless;

@Stateless
public class OpenOrderProductService extends PersistenceService<OpenOrderProduct> {

    public OpenOrderProduct findByProductCodeAndTemplate(String code, Long idTemplate){
        return (OpenOrderProduct) getEntityManager().createNamedQuery("OpenOrderProduct.findByCodeAndTemplate")
                .setParameter("TEMPLATE_ID", idTemplate)
                .setParameter("PRODUCT_CODE", code)
                .getSingleResult();
    }

}