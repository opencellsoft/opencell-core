package org.meveo.model.crm;

import java.io.Serializable;

import org.meveo.commons.utils.ReflectionUtils;
import org.meveo.model.BusinessEntity;
import org.meveo.model.customEntities.CustomEntityInstance;

public class EntityReferenceWrapper implements Serializable {

    private static final long serialVersionUID = -4756870628233941711L;

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

    public EntityReferenceWrapper(String classname, String classnameCode, String code) {
        this.classname = classname;
        this.classnameCode = classnameCode;
        this.code = code;
    }

    private String classname;

    private String classnameCode;

    private String code;

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
        return String.format("EntityReferenceWrapper [classname=%s, classnameCode=%s, code=%s]", classname, classnameCode, code);
    }
}