package org.meveo.service.tax;

import java.util.List;

import javax.ejb.Stateless;

import org.meveo.model.tax.TaxCategory;
import org.meveo.service.base.BusinessService;

/**
 * Tax category service implementation.
 */
@Stateless
public class TaxCategoryService extends BusinessService<TaxCategory> {

    public int getNbTaxCategoryNotAssociated() {
        return ((Long) getEntityManager().createNamedQuery("TaxCategory.getNbrTaxCatNotAssociated", Long.class).getSingleResult()).intValue();
    }

    public List<TaxCategory> getTaxCategoryNotAssociated() {
        return getEntityManager().createNamedQuery("TaxCategory.getTaxCatNotAssociated", TaxCategory.class).getResultList();
    }
}