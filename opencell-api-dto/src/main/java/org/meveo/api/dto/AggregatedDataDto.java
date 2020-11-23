package org.meveo.api.dto;

import static javax.xml.bind.annotation.XmlAccessType.FIELD;

import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlRootElement;
import java.util.List;
import java.util.Map;

@XmlRootElement
@XmlAccessorType(FIELD)
public class AggregatedDataDto extends EnableBusinessDto {

    @XmlAttribute(required = true)
    private String customTableCode;

    private String dataCollectorCode;

    /**
     * Aggregated Fields used to query data in data selector data custom table table
     *  Map : keys represent the fields
     *        value represent the aggregation function (SUM, COUNT, AVG...)
     */
    @XmlAttribute
    private Map<String, String> aggregatedFields;

    /**
     * other fields if needed to be added to the query
     */
    @XmlAttribute
    private List<String> fields;

    public String getCustomTableCode() {
        return customTableCode;
    }

    public void setCustomTableCode(String customTableCode) {
        this.customTableCode = customTableCode;
    }

    public Map<String, String> getAggregatedFields() {
        return aggregatedFields;
    }

    public void setAggregatedFields(Map<String, String> aggregatedFields) {
        this.aggregatedFields = aggregatedFields;
    }

    public List<String> getFields() {
        return fields;
    }

    public void setFields(List<String> fields) {
        this.fields = fields;
    }

    public String getDataCollectorCode() {
        return dataCollectorCode;
    }

    public void setDataCollectorCode(String dataCollectorCode) {
        this.dataCollectorCode = dataCollectorCode;
    }
}