package org.meveo.apiv2.export;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import org.immutables.value.Value;

import javax.annotation.Nullable;
import java.util.Map;

@Value.Immutable
@Value.Style(jdkOnly = true)
@JsonDeserialize(builder = ImmutableExportResponse.Builder.class)
public interface ExportResponse {

    String getExecutionId();

    @Nullable
    Map<String, Integer> getSummary();

    @Nullable
    Map<String, String> getFieldsNotImported();

    @Nullable
    String getExceptionMessage();

    @Nullable
    String getErrorMessageKey();

    @Nullable
    byte[] getFileContent();
}
