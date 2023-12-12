package org.meveo.apiv2.audit;

import org.immutables.value.Value;
import org.meveo.apiv2.models.PaginatedResource;

@Value.Immutable
public interface AuditDataConfigurationListDto extends PaginatedResource<AuditDataConfigurationDto> {
}