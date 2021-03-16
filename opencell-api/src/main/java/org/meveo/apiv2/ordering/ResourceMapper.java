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

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

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
    
	public E initEntity(T resource, E entity) throws Exception {

		final Class<? extends Resource> ressourceClass = resource.getClass();
		final Class<? extends BaseEntity> entityClass = entity.getClass();
		List<String> ressourceFieldsNames = Arrays.asList(ressourceClass.getDeclaredFields()).stream().map(x->x.getName()).collect(Collectors.toList());
		List<Field> matchedFieldsByName = Arrays.asList(entityClass.getDeclaredFields()).stream()
	     .filter(x -> ressourceFieldsNames.contains(x.getName())).collect(Collectors.toList());
		
		for (Field field : matchedFieldsByName) {
				final Field resourceField = ressourceClass.getDeclaredField(field.getName());
				if(resourceField!=null && resourceField.getType().equals(field.getType()) ) {
					field.setAccessible(true);
					resourceField.setAccessible(true);
					Object value = resourceField.get(resource);
					field.set(entity, value);
				}
		}
		return entity;
	}
	
	public T initResource(Class<? extends T> resourceClass, E entity) throws Exception {

		final Class<? extends BaseEntity> entityClass = entity.getClass();
		
		Method builderMethod = resourceClass.getMethod("builder");
		Object builder = builderMethod.invoke(null);
		final Class builderClass = builder.getClass();
		final Method build = builderClass.getMethod("build");
		
		List<String> ressourceFieldsNames = Arrays.asList(resourceClass.getDeclaredFields()).stream().map(x->x.getName()).collect(Collectors.toList());
		List<Field> matchedFieldsByName = Arrays.asList(entityClass.getDeclaredFields()).stream()
	     .filter(x -> ressourceFieldsNames.contains(x.getName())).collect(Collectors.toList());
		
		for (Field field : matchedFieldsByName) {
			final Field resourceField = resourceClass.getDeclaredField(field.getName());
			if(resourceField!=null && resourceField.getType().equals(field.getType()) ) {
				Method accessor = builderClass.getMethod(field.getName(), field.getType());
				field.setAccessible(true);
				Object value = field.get(entity);
				accessor.invoke(builder, value);
			}
		}
		return (T)build.invoke(builder);
	}
}
