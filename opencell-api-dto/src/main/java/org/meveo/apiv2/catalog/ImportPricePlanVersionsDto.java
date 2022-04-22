package org.meveo.apiv2.catalog;

import java.util.List;

import org.immutables.value.Value.Immutable;
import org.immutables.value.Value.Style;
import org.meveo.apiv2.models.Resource;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;

@Immutable
@Style(jdkOnly = true)
@JsonDeserialize(as = ImmutableImportPricePlanVersionsDto.class)
public interface ImportPricePlanVersionsDto extends Resource {

    List<ImportPricePlanVersionsItem> getPricePlanVersions();

    String getFileToImport();

}