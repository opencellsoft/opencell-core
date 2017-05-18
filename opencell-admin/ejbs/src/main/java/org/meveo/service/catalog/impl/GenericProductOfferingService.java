package org.meveo.service.catalog.impl;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.ejb.Stateless;
import javax.inject.Inject;

import org.meveo.admin.exception.BusinessException;
import org.meveo.model.DatePeriod;
import org.meveo.model.catalog.ProductOffering;
import org.meveo.service.base.AuditableMultilanguageService;
import org.meveo.service.crm.impl.CustomFieldInstanceService;

/**
 * @author Andrius Karpavicius
 */
@Stateless
public class GenericProductOfferingService<T extends ProductOffering> extends AuditableMultilanguageService<T> {

    private static String FIND_CODE_BY_DATE_CLAUSE = "((be.validity.from IS NULL and be.validity.to IS NULL) or (be.validity.from<=:date and :date<be.validity.to) or (be.validity.from<=:date and be.validity.to IS NULL) or (be.validity.from IS NULL and :date<be.validity.to))";

    @Inject
    protected CustomFieldInstanceService customFieldInstanceService;

    /**
     * Find matching or overlapping versions for a given Product offering code and date range
     * 
     * @param code Product offering code
     * @param from Date period start date
     * @param to Date period end date
     * @param entityId Identifier of an entity to ignore (as not to match itself in case of update)
     * @param ignoreOpenDates Should periods with open start or end dates be ignored for matching
     * @return Matched product offerings
     */
    public List<ProductOffering> getMatchingVersions(String code, Date from, Date to, Long entityId, boolean ignoreOpenDates) {

        List<ProductOffering> versions = getEntityManager().createNamedQuery("ProductOffering.findMatchingVersions", ProductOffering.class).setParameter("clazz", getEntityClass())
            .setParameter("code", code).setParameter("id", entityId == null ? -10000L : entityId).getResultList(); // Pass a non-existing entity id in case it is null

        List<ProductOffering> matchedVersions = new ArrayList<ProductOffering>();

        for (ProductOffering version : versions) {

            DatePeriod datePeriod = version.getValidity();

            // Periods with open start or end dates be ignored
            if (ignoreOpenDates) {
                if (datePeriod == null) {
                    continue;

                } else if (datePeriod.isCorrespondsToPeriod(from, to, false)) {

                    if (datePeriod.getFrom() == null || datePeriod.getTo() == null) {
                        continue;
                    }
                    matchedVersions.add(version);
                }

                // Any null or matching periods are selected
            } else {
                if (datePeriod == null || datePeriod.isCorrespondsToPeriod(from, to, false)) {
                    matchedVersions.add(version);
                }
            }
        }

        return matchedVersions;

    }

    /**
     * Update validity dates of previous versions of a given product offering that have conflicting validity dates
     * 
     * @param offering Product offering to consider as latest version
     * @throws BusinessException
     */
    @SuppressWarnings("unchecked")
    private void updateValidityOfPreviousVersions(T offering) throws BusinessException {

        DatePeriod currentValidity = offering.getValidity();

        List<ProductOffering> matchedVersions = getMatchingVersions(offering.getCode(), offering.getValidity().getFrom(), offering.getValidity().getTo(), offering.getId(), false);

        for (ProductOffering matchedVersion : matchedVersions) {

            DatePeriod matchedValidity = matchedVersion.getValidity();

            log.error("AKK matched version {} {}", matchedVersion.getId(), matchedValidity);

            // Latest version has no dates set, so invalidate other conflicting versions altogether
            if (currentValidity.isEmpty()) {
                if (matchedValidity.getFrom() == null) {
                    matchedValidity.setFrom(new Date());
                }
                matchedValidity.setTo(matchedValidity.getFrom());

                // Both dates are set
            } else if (currentValidity.getFrom() != null && currentValidity.getTo() != null) {

                // Set that other versions finish on a current From date
                if (matchedValidity.isEmpty() || matchedValidity.getFrom() == null || matchedValidity.getFrom().before(currentValidity.getFrom())) {
                    matchedValidity.setTo(currentValidity.getFrom());

                    // Set that other versions start on a current To date
                } else if (matchedValidity.getTo() == null || matchedValidity.getTo().after(currentValidity.getTo())) {
                    matchedValidity.setFrom(currentValidity.getTo());

                    // Invalidate previous version as dates fall in between current period
                } else {
                    if (matchedValidity.getFrom() == null) {
                        matchedValidity.setFrom(new Date());
                    }
                    matchedValidity.setTo(matchedValidity.getFrom());
                }

                // Only start date is set
            } else if (currentValidity.getFrom() != null) {

                // Set that other versions finish on a current From date
                if (matchedValidity.isEmpty() || matchedValidity.getFrom() == null || matchedValidity.getFrom().before(currentValidity.getFrom())) {
                    matchedValidity.setTo(currentValidity.getFrom());

                    // Invalidate previous version as dates fall in between current period
                } else {
                    if (matchedValidity.getFrom() == null) {
                        matchedValidity.setFrom(new Date());
                    }
                    matchedValidity.setTo(matchedValidity.getFrom());
                }

                // Only end date is set
            } else if (currentValidity.getTo() != null) {

                // Set that other versions start on a current To date
                if (matchedValidity.isEmpty() || matchedValidity.getTo() == null || matchedValidity.getTo().after(currentValidity.getTo())) {
                    matchedValidity.setFrom(currentValidity.getTo());

                    // Invalidate previous version as dates fall in between current period
                } else {
                    if (matchedValidity.getFrom() == null) {
                        matchedValidity.setFrom(new Date());
                    }
                    matchedValidity.setTo(matchedValidity.getFrom());
                }
            }

            log.error("AKK updated version {} to {}", matchedVersion.getId(), matchedValidity);
            super.update((T) matchedVersion);
        }
    }

    @Override
    public void create(T offering) throws BusinessException {

        // Change validity dates of previous versions
        updateValidityOfPreviousVersions(offering);

        super.create(offering);
    }

    @Override
    public T update(T offering) throws BusinessException {

        // Change validity dates of previous versions
        updateValidityOfPreviousVersions(offering);

        return super.update(offering);
    }

    /**
     * Find a particular product offering version, valid on a given date
     * 
     * @param code Product offering code
     * @param date Date to match
     * @return Product offering
     */
    public T findByCode(String code, Date date) {
        // Append search by a current date
        return super.findByCode(code, null, FIND_CODE_BY_DATE_CLAUSE, "date", date);
    }

    /**
     * Find a particular product offering version with additional fields to fetch. A current date will be used to select a valid version.
     * 
     * @param code Product offering code
     * @param fetchFields Additional fields to fetch
     * @return Product offering
     */
    @Override
    public T findByCode(String code, List<String> fetchFields) {

        // Append search by a current date
        return super.findByCode(code, fetchFields, FIND_CODE_BY_DATE_CLAUSE, "date", new Date());
    }

    /**
     * Find the latest version of Product offering matching a given code
     * 
     * @param code Code to match
     * @return Product offering with the highest validity start date
     */
    @SuppressWarnings("unchecked")
    protected T findTheLatestVersion(String code) {

        T latestVersion = (T) getEntityManager().createNamedQuery("ProductOffering.findLatestVersion").setParameter("clazz", getEntityClass()).setParameter("code", code)
            .setMaxResults(1).getSingleResult();
        return latestVersion;
    }

    /**
     * List active Product offerings of a current type valid on a given date
     * 
     * @param date Date to match validity
     * @return A list of Product offerings
     */
    @SuppressWarnings("unchecked")
    public List<T> listActiveByDate(Date date) {

        List<T> offers = (List<T>) getEntityManager().createNamedQuery("ProductOffering.findActiveByDate").setParameter("clazz", getEntityClass()).setParameter("date", date)
            .getResultList();

        return offers;
    }
}