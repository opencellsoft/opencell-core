package org.meveo.apiv2.ordering.product;

import org.immutables.value.Value;
import org.meveo.apiv2.models.PaginatedResource;

@Value.Immutable
public interface Products extends PaginatedResource<Product> { }
