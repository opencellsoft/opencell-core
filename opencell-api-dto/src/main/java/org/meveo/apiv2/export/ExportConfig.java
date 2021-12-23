package org.meveo.apiv2.export;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import org.immutables.value.Value;

import javax.annotation.Nullable;
import java.util.List;

@Value.Immutable
@Value.Style(jdkOnly = true)
@JsonDeserialize(builder = ImmutableExportConfig.Builder.class)
public interface ExportConfig {

    enum ExportType {
        XML("xml"), REMOTE_INSTANCE("remoteInstance");
        private final String label;
        ExportType(String label) {
            this.label = label;
        }

        @JsonValue
        public String getLabel() {
            return label;
        }
        @JsonCreator
        static ExportType of(String type) {
            return type.equalsIgnoreCase("xml") ? XML : REMOTE_INSTANCE;
        }
    }

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
