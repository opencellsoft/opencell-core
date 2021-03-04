package org.meveo.apiv2.generic.core.mapper.module;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.JsonSerializer;
import com.fasterxml.jackson.databind.SerializerProvider;
import com.fasterxml.jackson.databind.ser.BeanSerializerFactory;
import com.fasterxml.jackson.databind.ser.std.StdSerializer;
import com.fasterxml.jackson.databind.type.TypeFactory;
import org.meveo.apiv2.generic.GenericPaginatedResource;
import org.meveo.model.IEntity;

import java.io.IOException;
import java.util.List;
import java.util.Set;

class ListCustomSerializer extends StdSerializer<List> implements GenericSerializer{

    private JsonSerializer<Object> serializer;
    private final Set<String> nestedEntities;
    private final Set<IEntity> sharedEntityToSerialize;

    ListCustomSerializer(Set<String> nestedEntities, Set<IEntity> sharedEntityToSerialize) {
        super(List.class);
        this.nestedEntities = nestedEntities;
        this.sharedEntityToSerialize = sharedEntityToSerialize;
    }

    @Override
    public boolean isEmpty(SerializerProvider provider, List value) {
        return super.isEmpty(provider, value) || value.isEmpty();
    }

    @Override
    public void serialize(List list, JsonGenerator gen, SerializerProvider provider) throws IOException {
        if(shouldReturnOnlyIds(list, gen.getCurrentValue(), gen.getOutputContext().getCurrentName(), getPathToRoot(gen))){
            List<? extends IEntity> listIEntity = (List<? extends IEntity>) list;
            gen.writeStartArray(listIEntity.size());
			for (IEntity iEntity : listIEntity) {
				final Long id = (Long) iEntity.getId();
				if (id != null) {
					gen.writeStartObject();
					gen.writeFieldName("id");
					gen.writeNumber(id);
					gen.writeEndObject();
				}
			}
            gen.writeEndArray();
        }else if(!list.isEmpty() && list.get(0) instanceof IEntity){
            List<? extends IEntity> listIEntity = (List<? extends IEntity>) list;
            gen.writeStartArray(listIEntity.size());
            for (IEntity iEntity : listIEntity) {
                sharedEntityToSerialize.add(iEntity);
                gen.writeObject(iEntity);
            }
            gen.writeEndArray();
        }else{
            resolveSerializer(provider).serialize(list, gen, provider);
        }
    }

    private boolean shouldReturnOnlyIds(List list, Object currentValue, String currentName, String pathToRoot) {
        return (!list.isEmpty() && list.get(0) instanceof IEntity)
                && !(currentValue instanceof GenericPaginatedResource)
                && !isNestedEntityCandidate(pathToRoot, currentName);
    }

    private JsonSerializer resolveSerializer(SerializerProvider provider) throws JsonMappingException {
        if(serializer == null){
            serializer = BeanSerializerFactory.instance.createSerializer(provider, TypeFactory.defaultInstance().constructType(List.class));
        }
        return serializer;
    }

    @Override
    public Set<String> getNestedEntities() {
        return nestedEntities;
    }
}
