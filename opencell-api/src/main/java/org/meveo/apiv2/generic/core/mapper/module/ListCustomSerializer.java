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
import java.util.Collection;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

class ListCustomSerializer extends StdSerializer<Collection> implements GenericSerializer{

    private JsonSerializer<Object> serializer;
    private final Set<String> nestedEntities;
    private final Set<IEntity> sharedEntityToSerialize;
    private final Long nestedDepth;

    ListCustomSerializer(Set<String> nestedEntities, Set<IEntity> sharedEntityToSerialize, Long nestedDepth) {
        super(Collection.class);
        this.nestedEntities = nestedEntities;
        this.sharedEntityToSerialize = sharedEntityToSerialize;
        this.nestedDepth = nestedDepth;
    }

    @Override
    public boolean isEmpty(SerializerProvider provider, Collection value) {
        return super.isEmpty(provider, value) || value.isEmpty();
    }
    @Override
    public void serialize(Collection collection, JsonGenerator gen, SerializerProvider provider) throws IOException {
        Object currentValue = gen.getCurrentValue();
        String currentName = gen.getOutputContext().getCurrentName();
        String pathToRoot = getPathToRoot(gen);
        if(!collection.isEmpty() && collection.iterator().next() instanceof IEntity){
            Collection<? extends IEntity> collectionIEntity = (Collection<? extends IEntity>) collection;
            boolean nestedEntityCandidate = isNestedEntityCandidate(pathToRoot, currentName);

            boolean isDepthToBig;
            List<String> referencedNestedEntitiesOnPath = referencedNestedEntitiesOnPath(pathToRoot);

            if(referencedNestedEntitiesOnPath.isEmpty()){
                isDepthToBig = pathToRoot.split("\\.").length <= nestedDepth + 1;
            } else {
                String referencedNestedEntity = referencedNestedEntitiesOnPath.get(0);
                isDepthToBig = pathToRoot.split("\\.").length - referencedNestedEntity.split("\\.").length <= nestedDepth;
            }

            if (currentValue instanceof GenericPaginatedResource || nestedEntityCandidate || isDepthToBig) {
                gen.writeStartArray(collectionIEntity.size());
                for (IEntity iEntity : collectionIEntity) {
                    sharedEntityToSerialize.add(iEntity);
                    gen.writeObject(iEntity);
                }
                gen.writeEndArray();
            } else {
                gen.writeStartArray(collectionIEntity.size());
                for (IEntity iEntity : collectionIEntity) {
                    gen.writeStartObject();
                    gen.writeFieldName("id");
                    gen.writeNumber((Long) iEntity.getId());
                    gen.writeEndObject();
                }
                gen.writeEndArray();
            }
        }

        else{
            resolveSerializer(provider).serialize(collection, gen, provider);
        }
    }

    private List<String> referencedNestedEntitiesOnPath(String pathToRoot) {
        return getNestedEntities()
                .stream()
                .filter(n -> pathToRoot.toLowerCase().contains(n.toLowerCase()))
                .sorted((a, b) -> Integer.compare(b.split("\\.").length, a.split("\\.").length))
                .collect(Collectors.toList());
    }

    private JsonSerializer resolveSerializer(SerializerProvider provider) throws JsonMappingException {
        if(serializer == null){
            serializer = BeanSerializerFactory.instance.createSerializer(provider, TypeFactory.defaultInstance().constructType(Collection.class));
        }
        return serializer;
    }

    @Override
    public Set<String> getNestedEntities() {
        return nestedEntities;
    }
}
