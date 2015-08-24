package org.meveo.service.crm.impl;

import java.util.Date;
import java.util.List;

import javax.ejb.Stateless;
import javax.persistence.NoResultException;
import javax.persistence.Query;

import org.meveo.commons.utils.QueryBuilder;
import org.meveo.commons.utils.StringUtils;
import org.meveo.model.AccountEntity;
import org.meveo.model.BusinessEntity;
import org.meveo.model.IEntity;
import org.meveo.model.billing.Subscription;
import org.meveo.model.crm.CustomFieldInstance;
import org.meveo.model.crm.EntityReferenceWrapper;
import org.meveo.model.crm.Provider;
import org.meveo.model.mediation.Access;
import org.meveo.service.base.PersistenceService;

@Stateless
public class CustomFieldInstanceService extends PersistenceService<CustomFieldInstance> {

    public CustomFieldInstance findByCodeAndAccount(String code, IEntity t, Provider provider) {
        QueryBuilder qb = new QueryBuilder(CustomFieldInstance.class, "c");
        qb.addCriterion("code", "=", code, true);
        qb.addCriterionEntity("provider", provider);
        if (t instanceof AccountEntity) {
            qb.addCriterionEntity("account", t);
        } else if (t instanceof Subscription) {
            qb.addCriterionEntity("subscription", t);
        } else if (t instanceof Access) {
            qb.addCriterionEntity("access", t);
        }

        try {
            return (CustomFieldInstance) qb.getQuery(getEntityManager()).getSingleResult();
        } catch (NoResultException e) {
            return null;
        }
    }

    public CustomFieldInstance findByCodeAndAccountAndValue(String code, IEntity t, String value, Provider provider) {
        QueryBuilder qb = new QueryBuilder(CustomFieldInstance.class, "c");
        qb.addCriterion("code", "=", code, true);
        qb.addCriterionEntity("provider", provider);
        qb.addCriterion("stringValue", "=", value, true);
        if (t instanceof AccountEntity) {
            qb.addCriterionEntity("account", t);
        } else if (t instanceof Subscription) {
            qb.addCriterionEntity("subscription", t);
        } else if (t instanceof Access) {
            qb.addCriterionEntity("access", t);
        }

        try {
            return (CustomFieldInstance) qb.getQuery(getEntityManager()).getSingleResult();
        } catch (NoResultException e) {
            return null;
        }
    }

    public CustomFieldInstance findByCodeAndAccountAndValue(String code, IEntity t, Date value, Provider provider) {
        QueryBuilder qb = new QueryBuilder(CustomFieldInstance.class, "c");
        qb.addCriterion("code", "=", code, true);
        qb.addCriterionEntity("provider", provider);
        qb.addCriterionDate("dateValue", value);
        if (t instanceof AccountEntity) {
            qb.addCriterionEntity("account", t);
        } else if (t instanceof Subscription) {
            qb.addCriterionEntity("subscription", t);
        } else if (t instanceof Access) {
            qb.addCriterionEntity("access", t);
        }

        try {
            return (CustomFieldInstance) qb.getQuery(getEntityManager()).getSingleResult();
        } catch (NoResultException e) {
            return null;
        }
    }

    public CustomFieldInstance findByCodeAndAccountAndValue(String code, IEntity t, Long value, Provider provider) {
        QueryBuilder qb = new QueryBuilder(CustomFieldInstance.class, "c");
        qb.addCriterion("code", "=", code, true);
        qb.addCriterionEntity("provider", provider);
        qb.addCriterion("longValue", "=", value, true);
        if (t instanceof AccountEntity) {
            qb.addCriterionEntity("account", t);
        } else if (t instanceof Subscription) {
            qb.addCriterionEntity("subscription", t);
        } else if (t instanceof Access) {
            qb.addCriterionEntity("access", t);
        }

        try {
            return (CustomFieldInstance) qb.getQuery(getEntityManager()).getSingleResult();
        } catch (NoResultException e) {
            return null;
        }
    }

    public CustomFieldInstance findByCodeAndAccountAndValue(String code, IEntity t, Double value, Provider provider) {
        QueryBuilder qb = new QueryBuilder(CustomFieldInstance.class, "c");
        qb.addCriterion("code", "=", code, true);
        qb.addCriterionEntity("provider", provider);
        qb.addCriterion("doubleValue", "=", value, true);
        if (t instanceof AccountEntity) {
            qb.addCriterionEntity("account", t);
        } else if (t instanceof Subscription) {
            qb.addCriterionEntity("subscription", t);
        } else if (t instanceof Access) {
            qb.addCriterionEntity("access", t);
        }

        try {
            return (CustomFieldInstance) qb.getQuery(getEntityManager()).getSingleResult();
        } catch (NoResultException e) {
            return null;
        }
    }

    public CustomFieldInstance findByCodeAndAccountAndValue(String code, IEntity t, String stringValue, Date dateValue, Long longValue, Double doubleValue, Provider provider) {
        QueryBuilder qb = new QueryBuilder(CustomFieldInstance.class, "c");
        qb.addCriterion("code", "=", code, true);
        qb.addCriterionEntity("provider", provider);
        if (!StringUtils.isBlank(stringValue)) {
            qb.addCriterion("stringValue", "=", stringValue, true);
        }
        if (dateValue != null) {
            qb.addCriterionDate("dateValue", dateValue);
        }
        if (longValue != null) {
            qb.addCriterion("longValue", "=", longValue, true);
        }
        if (doubleValue != null) {
            qb.addCriterion("doubleValue", "=", doubleValue, true);
        }
        if (t instanceof AccountEntity) {
            qb.addCriterionEntity("account", t);
        } else if (t instanceof Subscription) {
            qb.addCriterionEntity("subscription", t);
        } else if (t instanceof Access) {
            qb.addCriterionEntity("access", t);
        }

        try {
            return (CustomFieldInstance) qb.getQuery(getEntityManager()).getSingleResult();
        } catch (NoResultException e) {
            return null;
        }
    }

    @SuppressWarnings("unchecked")
    public List<CustomFieldInstance> findByCodeAndAccountAndValue(String code, String accountType, String stringValue, Date dateValue, Long longValue, Double doubleValue,
            Provider provider) {
        QueryBuilder qb = new QueryBuilder(CustomFieldInstance.class, "c");
        qb.addCriterion("code", "=", code, true);
        qb.addCriterionEntity("provider", provider);
        if (!StringUtils.isBlank(stringValue)) {
            qb.addCriterion("stringValue", "=", stringValue, true);
        }
        if (dateValue != null) {
            qb.addCriterionDate("dateValue", dateValue);
        }
        if (longValue != null) {
            qb.addCriterion("longValue", "=", longValue, true);
        }
        if (doubleValue != null) {
            qb.addCriterion("doubleValue", "=", doubleValue, true);
        }
        if (!StringUtils.isBlank(accountType)) {
            qb.addCriterion("account.accountType", "=", accountType, true);
        }

        try {
            return (List<CustomFieldInstance>) qb.getQuery(getEntityManager()).getResultList();
        } catch (NoResultException e) {
            return null;
        }
    }

    /**
     * Convert BusinessEntityWrapper to an entity by doing a lookup in DB
     * 
     * @param businessEntityWrapper Business entity information
     * @return A BusinessEntity object
     */
    @SuppressWarnings("unchecked")
    public BusinessEntity convertToBusinessEntityFromCfV(EntityReferenceWrapper businessEntityWrapper, Provider provider) {
        if (businessEntityWrapper == null) {
            return null;
        }
        Query query = getEntityManager().createQuery("select e from " + businessEntityWrapper.getClassname() + " e where e.code=:code and e.provider=:provider");
        query.setParameter("code", businessEntityWrapper.getCode());
        query.setParameter("provider", provider);
        List<BusinessEntity> entities = query.getResultList();
        if (entities.size() > 0) {
            return entities.get(0);
        } else {
            return null;
        }
    }

    @SuppressWarnings("unchecked")
    public List<BusinessEntity> findBusinessEntityForCFVByCode(String className, String wildcode, Provider provider) {
        Query query = getEntityManager().createQuery("select e from " + className + " e where lower(e.code) like :code and e.provider=:provider");
        query.setParameter("code", "%" + wildcode.toLowerCase() + "%");
        query.setParameter("provider", provider);
        List<BusinessEntity> entities = query.getResultList();
        return entities;
    }
}
