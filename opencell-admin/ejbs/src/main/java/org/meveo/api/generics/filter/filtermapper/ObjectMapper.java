package org.meveo.api.generics.filter.filtermapper;


import java.util.Collections;
import java.util.Map;
import java.util.TreeMap;
import java.util.function.Function;

import org.apache.commons.lang3.reflect.FieldUtils;
import org.meveo.admin.util.pagination.PaginationConfiguration;
import org.meveo.api.generics.filter.FactoryFilterMapper;
import org.meveo.api.generics.filter.FilterMapper;
import org.meveo.model.BaseEntity;
import org.meveo.service.base.PersistenceService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ObjectMapper extends FilterMapper {
    private final Class<?> type;
    private final Function<Class, PersistenceService> serviceFunction;
    private static final Logger log = LoggerFactory.getLogger(ObjectMapper.class);

    public ObjectMapper(String property, Object value, Class<?> type, Function<Class, PersistenceService> serviceFunction) {
        super(property, value);
        this.type = type;
        this.serviceFunction = serviceFunction;
    }

    @Override
    public Object mapStrategy(Object value) {
        Object target = null;
        try {
            if(value instanceof Map && !((Map) value).containsKey("id")){
                final Object targetInstanceHolder = type.newInstance();
                Map<String, Object> innerValue = (Map) value;
                Map<String, Object> castMap = new TreeMap<String, Object>();
                innerValue.keySet()
                        .stream()
                        .map(key -> Collections.singletonMap(key, new FactoryFilterMapper().create(key.replaceFirst("[a-zA-Z]* ",""), innerValue.get(key), null, serviceFunction, type).map()))
                        .flatMap(entries -> entries.entrySet().stream())
                        .forEach(entry -> {
                            try {
                                FieldUtils.writeField(targetInstanceHolder, entry.getKey().replaceFirst("[a-zA-Z]* ",""), entry.getValue(), true);
                                if(innerValue.get(entry.getKey()).getClass()!=entry.getValue().getClass()){
                                	castMap.put(entry.getKey(), entry.getValue());
                                }
                            } catch (IllegalAccessException e) {
                                log.error("error = {}", e);
                            }
                        });
                target = targetInstanceHolder;
                castMap.entrySet().stream().forEach(entry->innerValue.put(entry.getKey(), entry.getValue()));
                if(target instanceof BaseEntity && ((BaseEntity) target).isTransient()){// handel inlist in the reference filters by verifing if the map value is a list or single value
                    target = serviceFunction.apply(target.getClass()).list(new PaginationConfiguration(innerValue));
                }
            } else{
                target = new FactoryFilterMapper().create("id", value, null, serviceFunction, type).map();
            }

        } catch (InstantiationException | IllegalAccessException e) {
            log.error("error = {}", e);
        }
        return target;
    }
}
