package org.meveo.apiv2.catalog;

import org.immutables.value.Value;
import org.meveo.apiv2.models.PaginatedResource;

@Value.Immutable
public interface PriceLists extends PaginatedResource<PriceList> {
}
