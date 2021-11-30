package org.meveo.apiv2.export;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import org.immutables.value.Value;

import javax.annotation.Nullable;
import java.util.List;

@Value.Immutable
@Value.Style(jdkOnly = true)
@JsonDeserialize(builder = ImmutableExportConfig.Builder.class)
public interface ExportConfig {

    enum ExportType { XML, REMOTE_INSTANCE}

    @Nullable
    ExportType getExportType();

    @Nullable
    String getInstanceCode();

    @Nullable
    String getFileName();

    @Nullable
    String getEntityClass();

    @Nullable
    List<String> getEntityCodes();

    @Nullable
    String getExportTemplateName();
}
