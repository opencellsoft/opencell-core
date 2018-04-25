package org.meveo.service.base;

import java.util.Date;

import org.meveo.model.BusinessEntity;

/**
 * Indicates a service for versioned business entities
 * 
 * @author Andrius Karpavicius
 * @since 5.1
 */
public interface IVersionedBusinessEntityService<T extends BusinessEntity> {

    /**
     * Find a particular entity version, STRICTLY matching validity start and end dates
     * 
     * @param code Entity code
     * @param from Validity date range start date
     * @param to Validity date range end date
     * @return Entity
     */
    T findByCode(String code, Date from, Date to);

    /**
     * Find a particular entity version, valid on a given date. If date is null, a current date will be used
     * 
     * @param code Entity code
     * @param date Date to match
     * @return Entity
     */
    T findByCode(String code, Date date);

    /**
     * Find a particular entity version by a code. A current date will be used to select a valid version.
     * 
     * @param code Entity code
     * @return Entity
     */
    T findByCode(String code);

    /**
     * Find a particular entity version, attempting to match validity start and end dates. First both dates will be tried to match, then only start date as a single date and then
     * current date as a single date<br>
     * 
     * if you don't provide any date, it will consider today. <br>
     * If you provide both dates - it will do a strict match. <br>
     * If you provide only To date - it will do a strict match with starting date as null.<br>
     * If you provide only From date - it will do:<br>
     * - a strict match with end date as null<br>
     * - and then a single date match for that date.<br>
     * - and then a single date match for today<br>
     * 
     * @param code Product offering code
     * @param from Validity date range start date
     * @param to Validity date range end date
     * @return Product offering
     */
    public default T findByCodeBestValidityMatch(String code, Date from, Date to) {

        // Strict match by noth dates
        T entity = findByCode(code, from, to);

        // If only TO date is provided, only a strict check should be done. This is to solve a problem - if FROM date is NULL, it will lookup by todays date
        if (entity == null && (from != null || to == null)) {
            entity = findByCode(code, from);
            if (entity == null) {
                entity = findByCode(code);
            }
        }
        return entity;
    }
}