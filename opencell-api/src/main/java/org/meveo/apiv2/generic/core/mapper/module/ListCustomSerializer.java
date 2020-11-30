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
import java.util.stream.Collectors;

class ListCustomSerializer extends StdSerializer<List> implements GenericSerializer{

    private JsonSerializer<Object> serializer;
    private final Set<String> nestedEntities;
    private final Set<IEntity> sharedEntityToSerialize;
    private final Long nestedDepth;

    ListCustomSerializer(Set<String> nestedEntities, Set<IEntity> sharedEntityToSerialize, Long nestedDepth) {
        super(List.class);
        this.nestedEntities = nestedEntities;
        this.sharedEntityToSerialize = sharedEntityToSerialize;
        this.nestedDepth = nestedDepth;
    }

    @Override
    public boolean isEmpty(SerializerProvider provider, List value) {
        return super.isEmpty(provider, value) || value.isEmpty();
    }

    @Override
    public void serialize(List list, JsonGenerator gen, SerializerProvider provider) throws IOException {
        Object currentValue = gen.getCurrentValue();
        String currentName = gen.getOutputContext().getCurrentName();
        String pathToRoot = getPathToRoot(gen);

        if(!list.isEmpty() && list.get(0) instanceof IEntity){
            List<? extends IEntity> listIEntity = (List<? extends IEntity>) list;
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
                gen.writeStartArray(listIEntity.size());
                for (IEntity iEntity : listIEntity) {
                    sharedEntityToSerialize.add(iEntity);
                    gen.writeObject(iEntity);
                }
                gen.writeEndArray();
            } else {
                gen.writeStartArray(listIEntity.size());
                for (IEntity iEntity : listIEntity) {
                    gen.writeStartObject();
                    gen.writeFieldName("id");
                    gen.writeNumber((Long) iEntity.getId());
                    gen.writeEndObject();
                }
                gen.writeEndArray();
            }
        }

        else{
            resolveSerializer(provider).serialize(list, gen, provider);
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
            serializer = BeanSerializerFactory.instance.createSerializer(provider, TypeFactory.defaultInstance().constructType(List.class));
        }
        return serializer;
    }

    @Override
    public Set<String> getNestedEntities() {
        return nestedEntities;
    }
}
