/*
 * (C) Copyright 2015-2020 Opencell SAS (https://opencellsoft.com/) and contributors.
 *
 * This program is free software: you can redistribute it and/or modify it under the terms of the GNU Affero General
 * Public License as published by the Free Software Foundation, either version 3 of the License, or (at your option)
 * any later version.
 *
 * THERE IS NO WARRANTY FOR THE PROGRAM, TO THE EXTENT PERMITTED BY APPLICABLE LAW. EXCEPT WHEN
 * OTHERWISE STATED IN WRITING THE COPYRIGHT HOLDERS AND/OR OTHER PARTIES PROVIDE THE PROGRAM "AS
 * IS" WITHOUT WARRANTY OF ANY KIND, EITHER EXPRESSED OR IMPLIED, INCLUDING, BUT NOT LIMITED TO, THE
 * IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE. THE ENTIRE RISK AS TO
 * THE QUALITY AND PERFORMANCE OF THE PROGRAM IS WITH YOU. SHOULD THE PROGRAM PROVE DEFECTIVE,
 * YOU ASSUME THE COST OF ALL NECESSARY SERVICING, REPAIR OR CORRECTION.
 *
 * For more information on the GNU Affero General Public License, please consult
 * <https://www.gnu.org/licenses/agpl-3.0.en.html>.
 */

package org.meveo.apiv2.ordering;

import org.meveo.apiv2.ordering.common.LinkGenerator;
import org.meveo.apiv2.models.ImmutableResource;
import org.meveo.apiv2.models.Resource;
import org.meveo.model.BaseEntity;

import java.util.List;

public abstract class ResourceMapper<T extends Resource, E extends BaseEntity> {
    protected abstract T toResource(E entity);
    protected abstract E toEntity(T resource);

    protected Resource[] getImmutableResources(List<? extends BaseEntity> elements, Class resource) {
        return elements==null ? new Resource[]{} : elements.stream()
                .map(element -> buildImmutableResource(resource, element))
                .toArray(Resource[]::new);
    }

    protected <O extends BaseEntity> ImmutableResource buildImmutableResource(Class resource,O element) {
        LinkGenerator.SelfLinkGenerator resourceLinkBuilder = new LinkGenerator.SelfLinkGenerator(resource)
                .withGetAction().withPostAction().withPatchAction().withDeleteAction();
        return element != null ? ImmutableResource.builder()
                .id(element.getId())
                .addLinks(resourceLinkBuilder.withId(element.getId()).build())
                .build() : ImmutableResource.builder().build();
    }
}
