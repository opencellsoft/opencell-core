package org.meveo.apiv2.report;

import org.immutables.value.Value;
import org.meveo.apiv2.models.PaginatedResource;

@Value.Immutable
public interface ReportQueries extends PaginatedResource<ReportQuery>  {
}
