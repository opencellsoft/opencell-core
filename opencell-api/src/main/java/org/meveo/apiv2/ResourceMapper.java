package org.meveo.apiv2;

import org.meveo.apiv2.common.LinkGenerator;
import org.meveo.apiv2.models.ImmutableResource;
import org.meveo.apiv2.models.Resource;
import org.meveo.model.IEntity;

import java.util.List;

public abstract class ResourceMapper<T extends Resource, E extends IEntity> {
    protected abstract T toResource(E entity);
    protected abstract E toEntity(T resource);

    protected Resource[] getImmutableResources(List<? extends IEntity> elements, Class resource) {
        return elements==null ? new Resource[]{} : elements.stream()
                .map(element -> buildImmutableResource(resource, element))
                .toArray(Resource[]::new);
    }

    protected <O extends IEntity> ImmutableResource buildImmutableResource(Class resource, O element) {
        LinkGenerator.SelfLinkGenerator resourceLinkBuilder = new LinkGenerator.SelfLinkGenerator(resource)
                .withGetAction().withPostAction().withPatchAction().withDeleteAction();
        return element != null ? ImmutableResource.builder()
                .id((Long) element.getId())
                .addLinks(resourceLinkBuilder.withId((Long) element.getId()).build())
                .build() : ImmutableResource.builder().build();
    }
}
