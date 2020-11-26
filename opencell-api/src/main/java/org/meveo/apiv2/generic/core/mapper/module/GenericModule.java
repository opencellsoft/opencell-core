package org.meveo.apiv2.generic.core.mapper.module;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.util.VersionUtil;
import com.fasterxml.jackson.databind.*;
import com.fasterxml.jackson.databind.module.SimpleModule;
import com.fasterxml.jackson.databind.ser.BeanSerializerModifier;
import org.hibernate.proxy.HibernateProxy;
import org.meveo.model.IEntity;
import org.meveo.model.billing.ChargeInstance;
import org.meveo.model.billing.Tax;
import org.meveo.model.payments.PaymentMethod;

import java.io.IOException;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

public class GenericModule extends SimpleModule {
    private static final String NAME = "GenericModule";
    private static final VersionUtil VERSION_UTIL = new VersionUtil() {};
    static final String DATA_ROOT_ELEMENT = "data";

    GenericModule(Set<String> nestedEntitiesToLoad, Long nestedDepth) {
        super(NAME, VERSION_UTIL.version());
        Set<IEntity> sharedEntityToSerialize = new HashSet<>();
        Set<String> nestedEntities = nestedEntitiesToLoad
                .parallelStream()
                .map(pattern -> pattern.toLowerCase())
                .map(pattern -> recursivelySplitPattern(pattern))
                .flatMap(x -> x.stream())
                .collect(Collectors.toSet());
        setSerializerModifier(new BeanSerializerModifier() {
            @SuppressWarnings("unchecked")
            @Override
            public JsonSerializer<?> modifySerializer(SerializationConfig serializationConfig, BeanDescription beanDescription, JsonSerializer<?> jsonSerializer) {
                if (IEntity.class.isAssignableFrom(beanDescription.getBeanClass()) && !HibernateProxy.class.isAssignableFrom(beanDescription.getBeanClass())) {
                    return new IEntityBeanSerializer((JsonSerializer<IEntity>)jsonSerializer, nestedEntities, sharedEntityToSerialize);
                }
                return jsonSerializer;
            }
        });
        addSerializer(HibernateProxy.class, new LazyProxySerializer(nestedEntities, sharedEntityToSerialize));
        addSerializer(List.class, new ListCustomSerializer(nestedEntities, sharedEntityToSerialize, nestedDepth));
        addDeserializer(ChargeInstance.class, new ChargeInstanceDeserializer());
        addDeserializer(PaymentMethod.class, new PaymentDeserializer());
        addKeyDeserializer(Tax.class, new KeyDeserializer() {
            @Override
            public Object deserializeKey(String key, DeserializationContext ctxt)
                    throws IOException, JsonProcessingException {
                try {
                    return Class.forName(key);
                } catch (ClassNotFoundException e) {
                    throw new RuntimeException(e);
                }
            }
        });
    }

    private Set<String> recursivelySplitPattern(String patternToSplit) {
        Set<String> set = new HashSet();
        set.add("data." + patternToSplit);
        if(patternToSplit.contains(".")){
            set.addAll(recursivelySplitPattern(patternToSplit.substring(0, patternToSplit.lastIndexOf("."))));
        }
        return set;
    }

    public static class Builder {
        private Set<String> nestedEntities;
        private Long nestedDepth;

        public Builder withEntityToLoad(Set<String> nestedEntities){
            this.nestedEntities = nestedEntities;
            return this;
        }

        public Builder withNestedDepth(Long nestedDepth){
            this.nestedDepth = nestedDepth;
            return this;
        }

        public static Builder getBuilder(){
            return new Builder();
        }

        public GenericModule build(){
            if(nestedEntities != null){
                return new GenericModule(nestedEntities, nestedDepth == null ?  0 : nestedDepth);
            }
            return new GenericModule(Collections.emptySet(), nestedDepth == null ? 0 : nestedDepth);
        }
    }

}