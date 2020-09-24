package org.meveo.service.base;

public class NativePersistenceServiceMock extends NativePersistenceService {

    @Override
    public String addCurrentSchema(String tableName) {
        return tableName;
    }
}
