package org.meveo.model.crm;

import org.meveo.model.BusinessEntity;

public class EntityReferenceWrapper {

    public EntityReferenceWrapper() {
    }

    public EntityReferenceWrapper(BusinessEntity entity) {
        super();
        if (entity == null) {
            return;
        }
        classname = entity.getClass().getName();
        code = entity.getCode();
    }

    public EntityReferenceWrapper(String classname, String code) {
        this.classname = classname;
        this.code = code;
    }

    private String classname;

    private String code;

    public String getClassname() {
        return classname;
    }

    public void setClassname(String classname) {
        this.classname = classname;
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
        return String.format("BusinessEntityWrapper [classname=%s, code=%s]", classname, code);
    }
}
