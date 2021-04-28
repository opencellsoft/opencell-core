package org.meveo.apiv2.generic.core.mapper.module;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.core.JsonStreamContext;
import com.fasterxml.jackson.databind.SerializerProvider;
import com.fasterxml.jackson.databind.ser.std.StdSerializer;
import org.hibernate.Hibernate;
import org.hibernate.proxy.HibernateProxy;
import org.meveo.model.IEntity;

import java.io.IOException;
import java.util.Set;

class LazyProxySerializer extends StdSerializer<HibernateProxy> implements GenericSerializer{
    private final Set<String> nestedEntities;
    private final Set<IEntity> sharedEntityToSerialize;

    LazyProxySerializer(Set<String> nestedEntities, Set<IEntity> sharedEntityToSerialize) {
        super(HibernateProxy.class);
        this.nestedEntities = nestedEntities;
        this.sharedEntityToSerialize = sharedEntityToSerialize;
    }

    @Override
    public void serialize(HibernateProxy value, JsonGenerator gen, SerializerProvider serializers) throws IOException {
        JsonStreamContext outputContext = gen.getOutputContext();
        if (sharedEntityToSerialize.contains(value) || isNestedEntityCandidate(getPathToRoot(gen), outputContext.getCurrentName())) {
            Hibernate.initialize(value);
            Object implementation = value.getHibernateLazyInitializer().getImplementation();
            sharedEntityToSerialize.add((IEntity) implementation);
            gen.writeObject(implementation);
        } else {
            gen.writeStartObject();
            gen.writeFieldName("id");
            gen.writeObject(value.getHibernateLazyInitializer().getIdentifier());
            gen.writeEndObject();
        }
    }

    @Override
    public Set<String> getNestedEntities() {
        return nestedEntities;
    }
}
