package org.meveo.apiv2.models;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import org.immutables.value.Value;
import org.meveo.apiv2.models.ImmutableResource;

@Value.Immutable
@JsonSerialize(as = org.meveo.apiv2.models.ImmutableProducts.class)
@JsonDeserialize(as = org.meveo.apiv2.models.ImmutableProducts.class)
public interface Products extends PaginatedResource<Product> { }
