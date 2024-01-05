package org.meveo.api.custom;

import org.meveo.service.custom.CustomTableService;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

public class CustomTableServiceMock extends CustomTableService {

    @Override
    public String getTableNameForClass(Class entityClass) {
        return "tableName";
    }

    @Override
    public String addCurrentSchema(String tableName) {
        return tableName;
    }

    @Override
    public Map<String, Object> findByClassAndId(String className, Long id) {
        return new HashMap<>();
    }

    @Override
    public Map<String, Object> findByClassAndId(String className, Long id, Set<String> fields) {
        return new HashMap<>();
    }
}
