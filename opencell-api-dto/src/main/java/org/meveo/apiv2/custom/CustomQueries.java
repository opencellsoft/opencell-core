package org.meveo.apiv2.custom;

import org.immutables.value.Value;
import org.meveo.apiv2.models.PaginatedResource;

@Value.Immutable
public interface CustomQueries extends PaginatedResource<CustomQuery>  {
}
