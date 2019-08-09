package org.meveo.apiv2.services.generic.JsonGenericApiMapper;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.core.util.VersionUtil;
import com.fasterxml.jackson.databind.SerializerProvider;
import com.fasterxml.jackson.databind.module.SimpleModule;
import com.fasterxml.jackson.databind.ser.std.StdSerializer;
import org.hibernate.proxy.HibernateProxy;
import org.hibernate.proxy.LazyInitializer;
import org.meveo.commons.utils.PersistenceUtils;
import org.meveo.model.BusinessEntity;

import java.io.IOException;

 class LazyProxyModule extends SimpleModule {
    private static final String NAME = "CustomLazyProxyModule";
    private static final VersionUtil VERSION_UTIL = new VersionUtil() {};

    LazyProxyModule() {
        super(NAME, VERSION_UTIL.version());
        addSerializer(HibernateProxy.class, new LazyProxySerializer());
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
             LazyInitializer hibernateLazyInitializer = value.getHibernateLazyInitializer();
             final Object unProxyDependency;
             try {
                 unProxyDependency = PersistenceUtils.initializeAndUnproxy(value);
                 gen.writeStartObject();
                 gen.writeObjectField("id", hibernateLazyInitializer.getIdentifier());
                 if (hibernateLazyInitializer != null){
                     if (unProxyDependency instanceof BusinessEntity){
                         gen.writeStringField("code", ((BusinessEntity)unProxyDependency).getCode());
                     }
                 }
                 gen.writeEndObject();
             }catch (Exception e){
                 gen.writeStartObject();
                 gen.writeObjectField("id", hibernateLazyInitializer.getIdentifier());
                 gen.writeEndObject();
             }
         }
     }

 }