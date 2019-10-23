package org.meveo.apiv2.services.generic.JsonGenericApiMapper;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.core.util.VersionUtil;
import com.fasterxml.jackson.databind.SerializerProvider;
import com.fasterxml.jackson.databind.module.SimpleModule;
import com.fasterxml.jackson.databind.ser.impl.IndexedListSerializer;
import com.fasterxml.jackson.databind.ser.std.StdSerializer;
import com.fasterxml.jackson.databind.type.TypeFactory;
import org.hibernate.proxy.HibernateProxy;
import org.meveo.apiv2.generic.GenericPaginatedResource;
import org.meveo.model.BaseEntity;

import java.io.IOException;
import java.util.List;
import java.util.Set;

class GenericModule extends SimpleModule {
    private static final String NAME = "CustomLazyProxyModule";
    private static final VersionUtil VERSION_UTIL = new VersionUtil() {};
    private final Set<String> nestedEntities;

    GenericModule(Set<String> nestedEntities) {
        super(NAME, VERSION_UTIL.version());
        addSerializer(HibernateProxy.class, new LazyProxySerializer());
        addSerializer(List.class, new ListCustomSerializer());
        this.nestedEntities = nestedEntities;
    }

    private class LazyProxySerializer extends StdSerializer<HibernateProxy> {

         LazyProxySerializer(Class<HibernateProxy> t) {
             super(t);
         }

         LazyProxySerializer() {
             this(null);
         }

         @Override
         public void serialize(HibernateProxy value, JsonGenerator gen, SerializerProvider serializers) throws IOException {
             gen.writeObject(value.getHibernateLazyInitializer().getIdentifier());
         }
     }

    private class ListCustomSerializer extends StdSerializer<List> {

        private IndexedListSerializer indexedListSerializer;

        ListCustomSerializer() {
            this(List.class);
            indexedListSerializer = new IndexedListSerializer(TypeFactory.defaultInstance().constructType(List.class), false, null, null);
        }
        ListCustomSerializer(Class<List> t) {
            super(t);
            indexedListSerializer = new IndexedListSerializer(TypeFactory.defaultInstance().constructType(List.class), false, null, null);
        }

        @Override
        public boolean isEmpty(SerializerProvider provider, List value) {
            return super.isEmpty(provider, value) || value.isEmpty();
        }

        @Override
        public void serialize(List list, JsonGenerator gen, SerializerProvider provider) throws IOException {
            if(shouldReturnOnlyIds(list, gen.getCurrentValue(), gen.getOutputContext().getCurrentName())){
                List<? extends BaseEntity> listBaseEntity = (List<? extends BaseEntity>) list;
                gen.writeStartArray(listBaseEntity.size());
                for (int i=0; i<listBaseEntity.size(); i++){
                    gen.writeNumber(listBaseEntity.get(i).getId());
                }
                gen.writeEndArray();
            }else {
                indexedListSerializer.serialize(list, gen, provider);
            }
        }

        private boolean shouldReturnOnlyIds(List list, Object currentValue, String currentName) {
            return (!list.isEmpty() && list.get(0) instanceof BaseEntity)
                    && !(currentValue instanceof GenericPaginatedResource)
                    && !(nestedEntities != null && nestedEntities.contains(currentName));
        }

    }
}