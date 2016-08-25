package org.meveo.service.index;

import org.meveo.model.BusinessEntity;

public class ElasticSearchClassInfo {

    private Class<? extends BusinessEntity> clazz;

    private String cetCode;

    public ElasticSearchClassInfo(Class<? extends BusinessEntity> clazz, String cetCode) {
        super();
        this.clazz = clazz;
        this.cetCode = cetCode;
    }

    public Class<? extends BusinessEntity> getClazz() {
        return clazz;
    }

    public String getCetCode() {
        return cetCode;
    }

    @Override
    public String toString() {
        return String.format("ElasticSearchClassInfo [clazz=%s, cetCode=%s]", clazz, cetCode);
    }
}