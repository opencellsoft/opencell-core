package org.meveo.service.crm.impl;

import java.util.List;

import javax.ejb.Stateless;
import javax.inject.Inject;
import javax.persistence.NoResultException;
import javax.persistence.Query;

import org.meveo.commons.utils.ParamBean;
import org.meveo.commons.utils.QueryBuilder;
import org.meveo.model.AccountEntity;
import org.meveo.model.BusinessEntity;
import org.meveo.model.ICustomFieldEntity;
import org.meveo.model.IEntity;
import org.meveo.model.admin.User;
import org.meveo.model.crm.CustomFieldInstance;
import org.meveo.model.crm.CustomFieldTemplate;
import org.meveo.model.crm.CustomFieldTypeEnum;
import org.meveo.model.crm.Provider;
import org.meveo.service.base.PersistenceService;

@Stateless
public class CustomFieldInstanceService extends PersistenceService<CustomFieldInstance> {

    @Inject
    CustomFieldTemplateService cfTemplateService;

    /**
     * Get a list of custom field instances to populate a cache
     * 
     * @return A list of custom field instances
     */
    @SuppressWarnings({ "unchecked", "rawtypes" })
    public List<Object[]> getCFIForCache(Class iCFEntityClass) {

        Query query = getEntityManager().createQuery(
            "select e.id, cff.provider.id, cfi from " + iCFEntityClass.getSimpleName() + " e join e.cfFields cff join cff.customFields cfi where cfi.disabled=false");
        return query.getResultList();
    }

    // /**
    // * Convert BusinessEntityWrapper to an entity by doing a lookup in DB
    // *
    // * @param businessEntityWrapper Business entity information
    // * @return A BusinessEntity object
    // */
    // @SuppressWarnings("unchecked")
    // public BusinessEntity convertToBusinessEntityFromCfV(EntityReferenceWrapper businessEntityWrapper, Provider provider) {
    // if (businessEntityWrapper == null) {
    // return null;
    // }
    // Query query = getEntityManager().createQuery("select e from " + businessEntityWrapper.getClassname() + " e where e.code=:code and e.provider=:provider");
    // query.setParameter("code", businessEntityWrapper.getCode());
    // query.setParameter("provider", provider);
    // List<BusinessEntity> entities = query.getResultList();
    // if (entities.size() > 0) {
    // return entities.get(0);
    // } else {
    // return null;
    // }
    // }

    @SuppressWarnings("unchecked")
    public List<BusinessEntity> findBusinessEntityForCFVByCode(String className, String wildcode, Provider provider) {
        Query query = getEntityManager().createQuery("select e from " + className + " e where lower(e.code) like :code and e.provider=:provider");
        query.setParameter("code", "%" + wildcode.toLowerCase() + "%");
        query.setParameter("provider", provider);
        List<BusinessEntity> entities = query.getResultList();
        return entities;
    }

    @SuppressWarnings("unchecked")
    public List<CustomFieldInstance> findByAccount(AccountEntity account, Provider provider) {
        QueryBuilder qb = new QueryBuilder(CustomFieldInstance.class, "c", null, provider);
        qb.addCriterionEntity("account", account);

        try {
            return qb.getQuery(getEntityManager()).getResultList();
        } catch (NoResultException e) {
            return null;
        }
    }

    @SuppressWarnings({ "unchecked", "rawtypes" })
    public Object getOrCreateCFValueFromParamValue(String code, String defaultParamBeanValue, ICustomFieldEntity entity, PersistenceService pService, boolean saveInCFIfNotExist,
            User user) {

        Object value = null;
        if (entity.getCfFields() != null) {
            value = entity.getCFValue(code);
            if (value != null) {
                return value;
            }
        }
        
        value = ParamBean.getInstance().getProperty(code, defaultParamBeanValue);
        if (value == null) {
            return null;
        }
        try {
            CustomFieldTemplate cft = cfTemplateService.findByCodeAndAppliesTo(code, entity, user.getProvider());

            if (cft == null) {
                cft = new CustomFieldTemplate();
                cft.setCode(code);
                cft.setAppliesTo(cfTemplateService.calculateAppliesToValue(entity));
                cft.setActive(true);
                cft.setDescription(code);
                cft.setFieldType(CustomFieldTypeEnum.STRING);
                cft.setDefaultValue(defaultParamBeanValue);
                cft.setValueRequired(false);
                cfTemplateService.create(cft, user, user.getProvider());
            }

            CustomFieldInstance cfi = new CustomFieldInstance();
            cfi.setCode(code);
            cfi.setStringValue(value.toString());

            if (saveInCFIfNotExist) {
                if (entity.getCfFields() == null) {
                    entity.initCustomFields();
                }
                entity.getCfFields().addUpdateCFI(cfi);
                pService.update((IEntity) entity, user);
            }
        } catch (CustomFieldException e) {
            log.error("Can not determine applicable CFT type. Value from propeties file will NOT be saved as customfield");
        }
        return value;
    }
}