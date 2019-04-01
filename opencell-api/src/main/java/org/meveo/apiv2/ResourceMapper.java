package org.meveo.apiv2;

import org.meveo.apiv2.common.LinkGenerator;
import org.meveo.apiv2.models.ImmutableResource;
import org.meveo.apiv2.models.Resource;
import org.meveo.model.BaseEntity;

import java.util.List;

public abstract class ResourceMapper<T extends Resource, E extends BaseEntity> {
    protected abstract T toResource(E entity);
    protected abstract E toEntity(T resource);

    protected Resource[] getImmutableResources(List<? extends BaseEntity> elements, Class resource) {
        return elements==null ? new Resource[]{} : elements.stream()
                .map(element -> getImmutableResource(resource, element))
                .toArray(Resource[]::new);
    }

    protected <O extends BaseEntity> ImmutableResource getImmutableResource(Class resource,O element) {
        LinkGenerator.SelfLinkGenerator resourceLinkBuilder = new LinkGenerator.SelfLinkGenerator(resource)
                .withGetAction().withPostAction().withPatchAction().withDeleteAction();
        return element != null ? ImmutableResource.builder()
                .id(element.getId())
                .addLinks(resourceLinkBuilder.withId(element.getId()).build())
                .build() : ImmutableResource.builder().build();
    }
}
