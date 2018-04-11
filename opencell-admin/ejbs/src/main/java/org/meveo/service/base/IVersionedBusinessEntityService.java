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
    public T findByCode(String code, Date from, Date to);
}