package org.meveo.apiv2.services.generic.JsonGenericApiMapper;

import com.fasterxml.jackson.core.*;
import com.fasterxml.jackson.core.json.ReaderBasedJsonParser;
import com.fasterxml.jackson.core.util.VersionUtil;
import com.fasterxml.jackson.databind.*;
import com.fasterxml.jackson.databind.module.SimpleModule;
import com.fasterxml.jackson.databind.ser.BeanSerializerFactory;
import com.fasterxml.jackson.databind.ser.std.StdSerializer;
import com.fasterxml.jackson.databind.type.TypeFactory;
import org.hibernate.Hibernate;
import org.hibernate.proxy.HibernateProxy;
import org.meveo.apiv2.generic.GenericPaginatedResource;
import org.meveo.model.IEntity;
import org.meveo.model.payments.*;

import java.io.IOException;
import java.util.Collections;
import java.util.List;
import java.util.Set;

class GenericModule extends SimpleModule {
    private static final String NAME = "GenericModule";
    private static final VersionUtil VERSION_UTIL = new VersionUtil() {};
    // to passe as constructor param from JsonGenericMapper
    private static final String DATA_ROOT_ELEMENT = "data";
    private final Set<String> nestedEntities;

    GenericModule(Set<String> nestedEntities) {
        super(NAME, VERSION_UTIL.version());
        this.nestedEntities = nestedEntities;
        addSerializer(HibernateProxy.class, new LazyProxySerializer());
        addSerializer(List.class, new ListCustomSerializer());
        addDeserializer(PaymentMethod.class, new PaymentDeserializer());
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
             JsonStreamContext outputContext = gen.getOutputContext();
             if(DATA_ROOT_ELEMENT.equals(outputContext.getParent().getCurrentName()) || nestedEntities.contains(outputContext.getCurrentName())){
                 Hibernate.initialize(value);
                 Object implementation = value.getHibernateLazyInitializer().getImplementation();
                 gen.writeObject(implementation);
             } else {
                 gen.writeObject(value.getHibernateLazyInitializer().getIdentifier());
             }
         }
     }

    private class ListCustomSerializer extends StdSerializer<List> {

        private JsonSerializer<Object> serializer;

        ListCustomSerializer() {
            this(List.class);
        }

        ListCustomSerializer(Class<List> t) {
            super(t);
        }

        @Override
        public boolean isEmpty(SerializerProvider provider, List value) {
            return super.isEmpty(provider, value) || value.isEmpty();
        }

        @Override
        public void serialize(List list, JsonGenerator gen, SerializerProvider provider) throws IOException {
            if(shouldReturnOnlyIds(list, gen.getCurrentValue(), gen.getOutputContext().getCurrentName())){
                List<? extends IEntity> listBaseEntity = (List<? extends IEntity>) list;
                gen.writeStartArray(listBaseEntity.size());
                for (int i=0; i<listBaseEntity.size(); i++){
                    gen.writeNumber((Long) listBaseEntity.get(i).getId());
                }
                gen.writeEndArray();
            }else {
                resolveSerializer(provider).serialize(list, gen, provider);
            }
        }

        private JsonSerializer resolveSerializer(SerializerProvider provider) throws JsonMappingException {
            if(serializer == null){
                serializer = BeanSerializerFactory.instance.createSerializer(provider, TypeFactory.defaultInstance().constructType(List.class));
            }
            return serializer;
        }

        private boolean shouldReturnOnlyIds(List list, Object currentValue, String currentName) {
            return (!list.isEmpty() && list.get(0) instanceof IEntity)
                    && !(currentValue instanceof GenericPaginatedResource)
                    && !(nestedEntities != null && nestedEntities.contains(currentName));
        }
    }

    private class PaymentDeserializer extends JsonDeserializer<PaymentMethod> {

        @Override
        public PaymentMethod deserialize(JsonParser jp, DeserializationContext ctxt) throws IOException, JsonProcessingException {
            ObjectCodec codec = jp.getCodec();
            JsonNode node = codec.readTree(jp);
            if(node.get("id") != null){
                Long id = node.get("id").longValue();
                PaymentMethod paymentMethod = new PaymentMethod() {
                    @Override
                    public void updateWith(PaymentMethod otherPaymentMethod) {
                        return;
                    }
                };
                paymentMethod.setId(id);
                return PaymentMethod.class.cast(paymentMethod);
            }else if(node.get("paymentType") != null){
                JsonParser p = codec.treeAsTokens(node);
                switch (node.get("paymentType").asText()){
                    case "CHECK" :
                        return codec.readValue(p, CheckPaymentMethod.class);
                    case "CARD" :
                        return codec.readValue(p, CardPaymentMethod.class);
                    case "DIRECTDEBIT" :
                        return codec.readValue(p, DDPaymentMethod.class);
                    case "WIRETRANSFER" :
                        return codec.readValue(p, WirePaymentMethod.class);
                }
            }
            return null;
        }
    }

    static class Builder {
        private Set<String> nestedEntities;
        public Builder withEntityToLoad(Set<String> nestedEntities){
            this.nestedEntities = nestedEntities;
            return this;
        }

        public static Builder getBuilder(){
            return new Builder();
        }

        public GenericModule build(){
            if(nestedEntities != null){
                return new GenericModule(nestedEntities);
            }
            return new GenericModule(Collections.emptySet());
        }
    }

}