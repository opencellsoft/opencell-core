package org.meveo.service.catalog.impl;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.ejb.Stateless;

import org.meveo.admin.exception.BusinessException;
import org.meveo.model.DatePeriod;
import org.meveo.model.catalog.ProductOffering;
import org.meveo.service.base.MultilanguageEntityService;

/**
 * @author Andrius Karpavicius
 */
@Stateless
public class GenericProductOfferingService<T extends ProductOffering> extends MultilanguageEntityService<T> {

    private static String FIND_CODE_BY_DATE_CLAUSE = "((be.validity.from IS NULL and be.validity.to IS NULL) or (be.validity.from<=:date and :date<be.validity.to) or (be.validity.from<=:date and be.validity.to IS NULL) or (be.validity.from IS NULL and :date<be.validity.to))";

    /**
     * Find matching or overlapping versions for a given Product offering code and date range
     * 
     * @param code Product offering code
     * @param from Date period start date
     * @param to Date period end date
     * @param entityId Identifier of an entity to ignore (as not to match itself in case of update)
     * @param invalidOnly Should only periods that would be invalid for given start and end dates be selected
     * @return Matched product offerings
     */
    public List<ProductOffering> getMatchingVersions(String code, Date from, Date to, Long entityId, boolean invalidOnly) {

        List<ProductOffering> versions = getEntityManager().createNamedQuery("ProductOffering.findMatchingVersions", ProductOffering.class).setParameter("clazz", getEntityClass())
            .setParameter("code", code).setParameter("id", entityId == null ? -10000L : entityId).getResultList(); // Pass a non-existing entity id in case it is null

        List<ProductOffering> matchedVersions = new ArrayList<ProductOffering>();

        for (ProductOffering version : versions) {

            DatePeriod datePeriod = version.getValidityRaw();

            // Only periods with start and end dates that would be split into two parts are selected
            if (invalidOnly) {
                if (isMatchedPeriodInvalid(datePeriod, from, to)) {
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
     * Check if matched period is invalid for given dates
     * 
     * @param matchedPeriod Matched date period
     * @param from Date to check - from
     * @param to Date to check - to
     * @return True if matched period is invalid
     */
    protected boolean isMatchedPeriodInvalid(DatePeriod matchedPeriod, Date from, Date to) {

        if (from == null && to == null) {
            return true;

        } else if (matchedPeriod == null || matchedPeriod.isEmpty()) {
            return false;

        } else if (matchedPeriod.isCorrespondsToPeriod(from, to, false)) {

            // period with start and end dates in betwen a period with start and end dates
            if (from != null && to != null && matchedPeriod.getFrom() != null && matchedPeriod.getTo() != null) {

                if (matchedPeriod.getFrom().compareTo(from) >= 0 && matchedPeriod.getTo().compareTo(to) <= 0) {
                    return true;
                } else if (matchedPeriod.getFrom().before(from) && matchedPeriod.getTo().after(to)) {
                    return true;
                }

            } else if (to == null && matchedPeriod.getFrom() != null && matchedPeriod.getFrom().compareTo(from) >= 0) {
                return true;

            } else if (from == null && matchedPeriod.getTo() != null && matchedPeriod.getTo().compareTo(to) <= 0) {
                return true;
            }
        }
        return false;

    }

    /**
     * Update validity dates of previous versions of a given product offering that have conflicting validity dates
     * 
     * @param offering Product offering to consider as latest version
     * @throws BusinessException
     */
    @SuppressWarnings("unchecked")
    private void updateValidityOfPreviousVersions(T offering) throws BusinessException {

        DatePeriod currentValidity = offering.getValidityRaw();

        List<ProductOffering> matchedVersions = getMatchingVersions(offering.getCode(), offering.getValidityRaw() != null ? offering.getValidityRaw().getFrom() : null,
            offering.getValidityRaw() != null ? offering.getValidityRaw().getTo() : null, offering.getId(), false);

        for (ProductOffering matchedVersion : matchedVersions) {

            updateValidityOfVersion(matchedVersion, currentValidity);

            super.update((T) matchedVersion);
        }
    }

    /**
     * Update validity dates of a matched product offering version
     * 
     * @param matchedVersion Matched product offering version
     * @param offeringValidity Validity of a current product offering
     */
    protected void updateValidityOfVersion(ProductOffering matchedVersion, DatePeriod offeringValidity) {

        DatePeriod matchedValidity = matchedVersion.getValidity();

        // Latest version has no dates set, so invalidate other conflicting versions altogether
        if (offeringValidity == null || offeringValidity.isEmpty()) {
            if (matchedValidity.getFrom() == null) {
                matchedValidity.setFrom(new Date());
            }
            matchedValidity.setTo(matchedValidity.getFrom());

            // Both dates are set
        } else if (offeringValidity.getFrom() != null && offeringValidity.getTo() != null) {

            // Invalidate previous version as dates fall in between current period
            if (matchedValidity.getFrom() != null && matchedValidity.getTo() != null && matchedValidity.getFrom().before(offeringValidity.getFrom())
                    && matchedValidity.getTo().after(offeringValidity.getTo())) {
                matchedValidity.setTo(matchedValidity.getFrom());

                // Set that other versions finish on a current From date
            } else if ((matchedValidity.isEmpty() || matchedValidity.getFrom() == null || matchedValidity.getFrom().before(offeringValidity.getFrom()))
                    && !(matchedValidity.getTo() != null && matchedValidity.getTo().after(offeringValidity.getTo()))) {
                matchedValidity.setTo(offeringValidity.getFrom());

                // Set that other versions start on a current To date
            } else if (matchedValidity.getTo() == null || matchedValidity.getTo().after(offeringValidity.getTo())) {
                matchedValidity.setFrom(offeringValidity.getTo());

                // Invalidate previous version as dates fall in between current period
            } else {
                if (matchedValidity.getFrom() == null) {
                    matchedValidity.setFrom(new Date());
                }
                matchedValidity.setTo(matchedValidity.getFrom());
            }

            // Only start date is set
        } else if (offeringValidity.getFrom() != null) {

            // Set that other versions finish on a current From date
            if (matchedValidity.isEmpty() || matchedValidity.getFrom() == null || matchedValidity.getFrom().before(offeringValidity.getFrom())) {
                matchedValidity.setTo(offeringValidity.getFrom());

                // Invalidate previous version as dates fall in between current period
            } else {
                if (matchedValidity.getFrom() == null) {
                    matchedValidity.setFrom(new Date());
                }
                matchedValidity.setTo(matchedValidity.getFrom());
            }

            // Only end date is set
        } else if (offeringValidity.getTo() != null) {

            // Set that other versions start on a current To date
            if (matchedValidity.isEmpty() || matchedValidity.getTo() == null || matchedValidity.getTo().after(offeringValidity.getTo())) {
                matchedValidity.setFrom(offeringValidity.getTo());

                // Invalidate previous version as dates fall in between current period
            } else {
                if (matchedValidity.getFrom() == null) {
                    matchedValidity.setFrom(new Date());
                }
                matchedValidity.setTo(matchedValidity.getFrom());
            }
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
     * Find a particular product offering version, valid on a given date. If date is null, a current date will be used
     * 
     * @param code Product offering code
     * @param date Date to match
     * @return Product offering
     */
    public T findByCode(String code, Date date) {
        // Append search by a date or a current date
        if (date == null) {
            date = new Date();
        }
        return super.findByCode(code, null, FIND_CODE_BY_DATE_CLAUSE, "date", date);
    }

    /**
     * Find a particular product offering version, STRICTLY matching validity start and end dates
     * 
     * @param code Product offering code
     * @param from Validity date range start date
     * @param to Validity date range end date
     * @return Product offering
     */
    public T findByCode(String code, Date from, Date to) {
        // Append search by a from and to dates

        if (from != null && to != null) {
            return super.findByCode(code, null, "be.validity.from=:from and be.validity.to=:to", "from", from, "to", to);

        } else if (from == null && to == null) {
            return super.findByCode(code, null, "be.validity.from is null and be.validity.to is null", "ignore me");

        } else if (from != null) {
            return super.findByCode(code, null, "be.validity.from=:from and be.validity.to is null", "from", from);

            // } else if (to != null) {
        } else {
            return super.findByCode(code, null, "be.validity.from is null and be.validity.to=:to", "to", to);
        }
    }

    /**
     * Find a particular product offering version, attempting to match validity start and end dates. First both dates will be tried to match, then only start date as a single date
     * and then current date as a single date
     * 
     * @param code Product offering code
     * @param from Validity date range start date
     * @param to Validity date range end date
     * @return Product offering
     */
    public T findByCodeBestValidityMatch(String code, Date from, Date to) {

        // Strict match by noth dates
        T offering = findByCode(code, from, to);

        // If only TO date is provided, only a strict check should be done. This is to solve a problem - if FROM date is NULL, it will lookup by todays date
        if (offering == null && (from != null || to == null)) {
            offering = findByCode(code, from);
            if (offering == null) {
                offering = findByCode(code);
            }
        }
        return offering;
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

        List<T> latestVersions = (List<T>) getEntityManager().createNamedQuery("ProductOffering.findLatestVersion").setParameter("clazz", getEntityClass())
            .setParameter("code", code).setMaxResults(2).getResultList();

        // This is to overcome a problem of sorting in descending order where null is before non-empty date
        if (latestVersions.size() > 1 && (latestVersions.get(0).getValidityRaw() == null || latestVersions.get(0).getValidityRaw().getFrom() == null)) {
            return latestVersions.get(1);
        } else {
            return latestVersions.get(0);
        }
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