package org.meveo.apiv2.catalog;

import java.util.Date;

import javax.annotation.Nullable;

import org.immutables.value.Value.Immutable;
import org.immutables.value.Value.Style;
import org.meveo.model.cpq.enums.VersionStatusEnum;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;

@Immutable
@Style(jdkOnly = true)
@JsonDeserialize(as = ImmutableImportPricePlanVersionsItem.class)
public interface ImportPricePlanVersionsItem {

    String getFileName();

    @Nullable
    String getChargeCode();

    @Nullable
    VersionStatusEnum getStatus();

    Date getStartDate();

    @Nullable
    Date getEndDate();
}
