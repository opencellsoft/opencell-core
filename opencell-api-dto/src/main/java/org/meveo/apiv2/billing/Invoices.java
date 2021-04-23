package org.meveo.apiv2.billing;

import org.immutables.value.Value;
import org.meveo.apiv2.models.PaginatedResource;

@Value.Immutable
public interface Invoices extends PaginatedResource<Invoice> {
}
