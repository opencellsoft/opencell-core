package org.meveo.model.crm;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;

import org.meveo.commons.utils.ReflectionUtils;
import org.meveo.model.BusinessEntity;
import org.meveo.model.IReferenceEntity;
import org.meveo.model.customEntities.CustomEntityInstance;

import com.fasterxml.jackson.annotation.JsonInclude;

/**
 * Represents a custom field value type - reference to an Meveo entity identified by a classname and code. In case a class is a generic Custom Entity Template a classnameCode is
 * required to identify a concrete custom entity template by its code
 * 
 * @author Andrius Karpavicius
 **/
@JsonInclude(JsonInclude.Include.NON_NULL)
public class EntityReferenceWrapper implements Serializable, IReferenceEntity {

    private static final long serialVersionUID = -4756870628233941711L;

    /**
     * Classname of an entity
     */
    private String classname;

    /**
     * Custom entity template code - applicable and required when reference is to Custom Entity Template type
     */
    private String classnameCode;

    /**
     * Entity code
     */
    private String code;

    public EntityReferenceWrapper() {
    }

    public EntityReferenceWrapper(BusinessEntity entity) {
        super();
        if (entity == null) {
            return;
        }
        classname = ReflectionUtils.getCleanClassName(entity.getClass().getName());
        if (entity instanceof CustomEntityInstance) {
            classnameCode = ((CustomEntityInstance) entity).getCetCode();
        }
        code = entity.getCode();
    }

    public EntityReferenceWrapper(IReferenceEntity entity) {
        super();
        if (entity == null) {
            return;
        }
        classname = ReflectionUtils.getCleanClassName(entity.getClass().getName());
        if (entity instanceof CustomEntityInstance) {
            classnameCode = ((CustomEntityInstance) entity).getCetCode();
        }
        code = entity.getReferenceCode();
    }

    public EntityReferenceWrapper(String classname, String classnameCode, String code) {
        this.classname = classname;
        this.classnameCode = classnameCode;
        this.code = code;
    }

    public String getClassname() {
        return classname;
    }

    public void setClassname(String classname) {
        this.classname = classname;
    }

    public String getClassnameCode() {
        return classnameCode;
    }

    public void setClassnameCode(String classnameCode) {
        this.classnameCode = classnameCode;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public boolean isEmpty() {
        return code == null;
    }

    @Override
    public String toString() {
        return String.format("EntityReferenceWrapper [classname=%s, classnameCode=%s, code=%s, referenceCode=%s]", classname, classnameCode, code, getReferenceCode());
    }

    @Override
    public String getReferenceCode() {
        return getCode();
    }

    @Override
    public void setReferenceCode(Object value) {
        setCode(value.toString());
    }

    @Override
    public String getReferenceDescription() {
        return null;
    }

    /**
     * Convert fields to a map of values
     * 
     * @return A map of field values
     */
    public Map<String, String> toMap() {

        Map<String, String> map = new HashMap<>();
        map.put("code", code);
        map.put("classname", classname);
        if (classnameCode != null) {
            map.put("classnameCode", classnameCode);
        }

        return map;
    }
}