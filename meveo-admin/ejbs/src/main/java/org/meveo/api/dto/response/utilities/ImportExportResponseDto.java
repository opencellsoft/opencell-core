package org.meveo.api.dto.response.utilities;

import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlRootElement;

import org.meveo.api.dto.ActionStatusEnum;
import org.meveo.api.dto.response.BaseResponse;
import org.meveo.export.ExportImportStatistics;

/**
 * @author Andrius Karpavicius
 **/
@XmlRootElement(name = "ImportExportResponse")
@XmlAccessorType(XmlAccessType.FIELD)
public class ImportExportResponseDto extends BaseResponse {

    private static final long serialVersionUID = 1L;

    private String executionId;

    /**
     * Summary of entities imported/exported per entity class
     */
    private Map<String, Integer> summary = null;

    /**
     * Stores a list of field names that were not imported because of differences between original and current model - fields do not exist in current model
     */
    private Map<String, FieldsNotImportedStringCollectionDto> fieldsNotImported = null;

    /**
     * Occurred exception
     */
    private String exceptionMessage;

    /**
     * Occurred error message key
     */
    private String errorMessageKey;

    public ImportExportResponseDto() {
        super();
    }

    public ImportExportResponseDto(ActionStatusEnum status, String errorCode, String message) {
        super(status, errorCode, message);
    }

    public ImportExportResponseDto(String executionId) {
        super();
        this.executionId = executionId;
    }

    @SuppressWarnings("rawtypes")
    public ImportExportResponseDto(String executionId, ExportImportStatistics statistics) {
        super();
        this.executionId = executionId;
        if (statistics.getException() != null) {
            this.exceptionMessage = statistics.getException().getClass().getSimpleName() + ": " + statistics.getException().getMessage();
        }
        this.errorMessageKey = statistics.getErrorMessageKey();

        if (!statistics.getFieldsNotImported().isEmpty()) {
            fieldsNotImported = new HashMap<String, FieldsNotImportedStringCollectionDto>();
            for (Map.Entry<String, Collection<String>> entry : statistics.getFieldsNotImported().entrySet()) {
                fieldsNotImported.put(entry.getKey(), new FieldsNotImportedStringCollectionDto(entry.getValue()));
            }
        }
        this.summary = new HashMap<String, Integer>();
        for (Entry<Class, Integer> summaryInfo : statistics.getSummary().entrySet()) {
            this.summary.put(summaryInfo.getKey().getName(), summaryInfo.getValue());
        }

    }

    public String getExecutionId() {
        return executionId;
    }

    public void setExecutionId(String executionId) {
        this.executionId = executionId;
    }

    public Map<String, Integer> getSummary() {
        return summary;
    }

    public void setSummary(Map<String, Integer> summary) {
        this.summary = summary;
    }

    public String getExceptionMessage() {
        return exceptionMessage;
    }

    public void setExceptionMessage(String exceptionMessage) {
        this.exceptionMessage = exceptionMessage;
    }

    public boolean isDone() {
        return exceptionMessage != null || summary != null;
    }

    @Override
    public String toString() {
        final int maxLen = 10;
        return String.format("ImportExportResponseDto [executionId=%s, summary=%s, fieldsNotImported=%s, exception=%s]", executionId,
            summary != null ? toString(summary.entrySet(), maxLen) : null, fieldsNotImported != null ? toString(fieldsNotImported.entrySet(), maxLen) : null, exceptionMessage);
    }

    private String toString(Collection<?> collection, int maxLen) {
        StringBuilder builder = new StringBuilder();
        builder.append("[");
        int i = 0;
        for (Iterator<?> iterator = collection.iterator(); iterator.hasNext() && i < maxLen; i++) {
            if (i > 0)
                builder.append(", ");
            builder.append(iterator.next());
        }
        builder.append("]");
        return builder.toString();
    }

    /**
     * Determine if request has failed
     * 
     * @return
     */
    public boolean isFailed() {
        return exceptionMessage != null || errorMessageKey != null || getActionStatus().getStatus() == ActionStatusEnum.FAIL;
    }

    /**
     * Get a failure message as a message file key
     * 
     * @return
     */
    public String getFailureMessageKey() {
        return errorMessageKey;
    }

    /**
     * Get a failure message as a complete message
     * 
     * @return
     */
    public String getFailureMessage() {
        if (errorMessageKey != null) {
            return null;
        }
        if (exceptionMessage != null) {
            return exceptionMessage;
        } else {
            return getActionStatus().getMessage();
        }
    }

    public Map<String, FieldsNotImportedStringCollectionDto> getFieldsNotImported() {
        return fieldsNotImported;
    }

    public void setFieldsNotImported(Map<String, FieldsNotImportedStringCollectionDto> fieldsNotImported) {
        this.fieldsNotImported = fieldsNotImported;
    }
}