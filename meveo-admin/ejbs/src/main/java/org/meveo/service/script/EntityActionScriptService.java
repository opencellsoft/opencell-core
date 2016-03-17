package org.meveo.service.script;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.annotation.PostConstruct;
import javax.ejb.Singleton;
import javax.ejb.Startup;
import javax.inject.Inject;
import javax.persistence.NoResultException;

import org.meveo.admin.exception.BusinessException;
import org.meveo.admin.util.ResourceBundle;
import org.meveo.commons.utils.QueryBuilder;
import org.meveo.model.ICustomFieldEntity;
import org.meveo.model.admin.User;
import org.meveo.model.crm.Provider;
import org.meveo.model.scripts.EntityActionScript;
import org.meveo.model.scripts.ScriptSourceTypeEnum;
import org.meveo.service.crm.impl.CustomFieldException;
import org.meveo.service.crm.impl.CustomFieldTemplateService;

@Singleton
@Startup
public class EntityActionScriptService extends CustomScriptService<EntityActionScript, ScriptInterface> {

    @Inject
    private ResourceBundle resourceMessages;

    /**
     * Find a list of entity actions/scripts corresponding to a given entity
     * 
     * @param entity Entity that entity actions/scripts apply to
     * @param provider Provider
     * @return A map of entity actions/scripts mapped by a action code
     */
    public Map<String, EntityActionScript> findByAppliesTo(ICustomFieldEntity entity, Provider provider) {
        try {
            if (entity instanceof Provider) {
                return findByAppliesTo(CustomFieldTemplateService.calculateAppliesToValue(entity), (Provider) entity);
            } else {
                return findByAppliesTo(CustomFieldTemplateService.calculateAppliesToValue(entity), provider);
            }

        } catch (CustomFieldException e) {
            // Its ok, handles cases when value that is part of CFT.AppliesTo calculation is not set yet on entity
            return new HashMap<String, EntityActionScript>();
        }
    }

    /**
     * Find a list of entity actions/scripts corresponding to a given entity
     * 
     * @param appliesTo Entity (CFT appliesTo code) that entity actions/scripts apply to
     * @param provider Provider
     * @return A map of entity actions/scripts mapped by a action code
     */
    @SuppressWarnings("unchecked")
    public Map<String, EntityActionScript> findByAppliesTo(String appliesTo, Provider provider) {

        // Handles cases when creating a new provider
        if (provider.getId() == null) {
            return new HashMap<String, EntityActionScript>();
        }

        QueryBuilder qb = new QueryBuilder(EntityActionScript.class, "s", null, provider);
        qb.addCriterion("s.appliesTo", "=", appliesTo, true);

        List<EntityActionScript> scripts = (List<EntityActionScript>) qb.getQuery(getEntityManager()).getResultList();

        Map<String, EntityActionScript> scriptMap = new HashMap<String, EntityActionScript>();
        for (EntityActionScript script : scripts) {
            scriptMap.put(script.getCode(), script);
        }
        return scriptMap;
    }

    /**
     * Find a specific entity action/script by a code
     * 
     * @param code Entity action/script code. MUST be in a format of <localCode>|<appliesTo>
     * @param entity Entity that entity actions/scripts apply to
     * @param provider Provider
     * @return Entity action/script
     * @throws CustomFieldException An exception when AppliesTo value can not be calculated
     */
    public EntityActionScript findByCodeAndAppliesTo(String code, ICustomFieldEntity entity, Provider provider) throws CustomFieldException {
        return findByCodeAndAppliesTo(code, CustomFieldTemplateService.calculateAppliesToValue(entity), provider);
    }

    /**
     * Find a specific entity action/script by a code
     * 
     * @param code Entity action/script code. MUST be in a format of <localCode>|<appliesTo>
     * @param appliesTo Entity (CFT appliesTo code) that entity actions/scripts apply to
     * @param provider Provider
     * @return Entity action/script
     */
    public EntityActionScript findByCodeAndAppliesTo(String code, String appliesTo, Provider provider) {

        QueryBuilder qb = new QueryBuilder(EntityActionScript.class, "s", null, provider);
        qb.addCriterion("s.code", "=", code, true);
        qb.addCriterion("s.appliesTo", "=", appliesTo, true);
        try {
            return (EntityActionScript) qb.getQuery(getEntityManager()).getSingleResult();
        } catch (NoResultException e) {
            return null;
        }
    }

    @Override
    public void create(EntityActionScript script, User creator) throws BusinessException {
        String className = getClassName(script.getScript());
        if (className == null) {
            throw new RuntimeException(resourceMessages.getString("message.scriptInstance.sourceInvalid"));
        }

        super.create(script, creator);
    }

    @Override
    public EntityActionScript update(EntityActionScript script, User updater) throws BusinessException {

        String className = getClassName(script.getScript());
        if (className == null) {
            throw new RuntimeException(resourceMessages.getString("message.scriptInstance.sourceInvalid"));
        }

        script = super.update(script, updater);

        // Needed to reestablish again script.localCode value
        script.setCode(script.getCode());

        return script;
    }

    /**
     * Compile all scriptInstances
     */
    @PostConstruct
    void compileAll() {

        List<EntityActionScript> scriptInstances = findByType(ScriptSourceTypeEnum.JAVA);
        compile(scriptInstances);
    }

}