package org.meveo.apiv2.generic.core.mapper.module;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.core.JsonStreamContext;
import com.fasterxml.jackson.databind.JsonSerializer;
import com.fasterxml.jackson.databind.SerializerProvider;
import com.fasterxml.jackson.databind.ser.std.StdSerializer;
import org.meveo.model.IEntity;

import java.io.IOException;
import java.util.Set;

import static org.meveo.apiv2.generic.core.mapper.module.GenericModule.DATA_ROOT_ELEMENT;

class IEntityBeanSerializer extends StdSerializer<IEntity> implements GenericSerializer{
    private final Set<String> nestedEntities;
    private final Set<IEntity> sharedEntityToSerialize;
    private final JsonSerializer<IEntity> defaultSerializer;

    IEntityBeanSerializer(JsonSerializer<IEntity> defaultSerializer, Set<String> nestedEntities, Set<IEntity> sharedEntityToSerialize) {
        super(IEntity.class);
        this.defaultSerializer = defaultSerializer;
        this.nestedEntities = nestedEntities;
        this.sharedEntityToSerialize = sharedEntityToSerialize;
    }

    @Override
    public void serialize(IEntity value, JsonGenerator gen, SerializerProvider serializers) throws IOException {
        JsonStreamContext outputContext = gen.getOutputContext();

        boolean exists = sharedEntityToSerialize.stream().anyMatch(e -> isMatch(e, value));
        if (exists
                || DATA_ROOT_ELEMENT.equals(outputContext.getCurrentName())
                || (gen.getOutputContext().getParent() != null && gen.getOutputContext().getParent().getParent() != null && DATA_ROOT_ELEMENT.equals(gen.getOutputContext().getParent().getParent().getCurrentName()))
                || isNestedEntityCandidate(getPathToRoot(gen), outputContext.getCurrentName())) {
            sharedEntityToSerialize.removeIf(e -> isMatch(e, value));
            this.defaultSerializer.serialize(value, gen, serializers);
        } else {
            gen.writeStartObject();
            gen.writeFieldName("id");
            gen.writeObject(value.getId());
            gen.writeEndObject();
        }
    }

    private boolean isMatch(IEntity e, IEntity value) {
        return e.getClass().equals(value.getClass()) &&
                (e.getId() != null ? e.getId().equals(value.getId()) : e.equals(value));
    }

    @Override
    public Set<String> getNestedEntities() {
        return nestedEntities;
    }
}
