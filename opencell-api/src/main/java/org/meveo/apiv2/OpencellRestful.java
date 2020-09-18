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

package org.meveo.apiv2;

import io.swagger.v3.jaxrs2.integration.resources.OpenApiResource;
import org.meveo.apiv2.exception.BadRequestExceptionMapper;
import org.meveo.apiv2.exception.NotFoundExceptionMapper;
import org.meveo.apiv2.exception.UnhandledExceptionMapper;
import org.meveo.apiv2.ordering.order.OrderResourceImpl;
import org.meveo.apiv2.ordering.orderitem.OrderItemResourceImpl;
import org.meveo.apiv2.ordering.product.ProductResourceImpl;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.ws.rs.ApplicationPath;
import javax.ws.rs.core.Application;
import java.util.HashSet;
import java.util.Set;

@ApplicationPath("/api/rest/v2/ordering")
public class OpencellRestful extends Application {
        @SuppressWarnings({ "rawtypes", "unchecked" })
        @Override
        public Set<Class<?>> getClasses() {
            Set<Class<?>> resources = new HashSet();
            resources.add(ProductResourceImpl.class);
            resources.add(OrderItemResourceImpl.class);
            resources.add(OrderResourceImpl.class);
            resources.add(NotYetImplementedResource.class);

            resources.add(NotFoundExceptionMapper.class);
            resources.add(BadRequestExceptionMapper.class);
            resources.add(UnhandledExceptionMapper.class);
            return resources;
        }

}