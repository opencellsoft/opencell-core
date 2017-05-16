package org.meveo.service.catalog.impl;

import java.util.Date;
import java.util.List;

import javax.ejb.Stateless;

import org.meveo.model.DatePeriod;
import org.meveo.model.catalog.ProductOffering;
import org.meveo.service.base.BusinessService;

/**
 * @author Edward P. Legaspi
 */
@Stateless
public class ProductOfferingService extends BusinessService<ProductOffering> {

    /**
     * Find matching or overlapping version for a given Product offering code and date range
     * 
     * @param code Product offering code
     * @param from Date period start date
     * @param to Date period end date
     * @param entityId Identifier of an entity to ignore (as not to match itself in case of update)
     * @return Matched date period
     */
    public DatePeriod getMatchingVersion(String code, Date from, Date to, Long entityId) {

        List<DatePeriod> versionPeriods = getEntityManager().createNamedQuery("ProductOffering.findVersionDates", DatePeriod.class).setParameter("code", code)
            .setParameter("id", entityId == null ? -10000L : entityId).getResultList(); // Pass a non-existing entity id in case it is null

        for (DatePeriod datePeriod : versionPeriods) {
            if (datePeriod == null) {
                return new DatePeriod();
            } else if (datePeriod.isCorrespondsToPeriod(from, to, false)) {
                return datePeriod;
            }
        }

        return null;
    }
}