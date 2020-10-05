package org.meveo.api.custom;

import org.meveo.service.custom.CustomTableService;

import java.util.HashMap;
import java.util.Map;

public class CustomTableServiceMock extends CustomTableService {

    @Override
    public String getTableNameForClass(Class entityClass) {
        return "tableName";
    }

    @Override
    public Map<String, Object> findByClassAndId(String className, Long id) {
        return new HashMap<>();
    }
}
