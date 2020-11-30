package org.meveo.api.dto;

import static java.util.stream.Collectors.joining;
import static javax.xml.bind.annotation.XmlAccessType.FIELD;

import org.meveo.model.bi.DataCollector;

import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import java.util.Map;

@XmlRootElement
@XmlAccessorType(FIELD)
public class DataCollectorDto extends EnableBusinessDto {

    @XmlAttribute(required = true)
    private String code;
    private String description;

    /**
     * String represents the SQL query to execute by the Data Selector
     *  SQL query format example : FROM Table WHERE CONDITION
     */
    @XmlAttribute(required = true)
    private String sqlQuery;

    /**
     * Aliases represents fields and their aliases used in the SQL query
     * Map : key represent the field
     *       value: field alias
     */
    @XmlAttribute(required = true)
    private Map<String, String> aliases;

    /**
     *  Parameters used in the SQL query
     */
    private Map<String, String> parameters;

    /**
     * Custom table code
     *  Existing custom table to be used by the data collector
     */
    private String customTableCode;

    /**
     * Custom table information used to create new custom entity for the data collector
     * Custom table to use for data selector to store SQL query result
     */
    @XmlElement(name = "customTable")
    private CustomEntityTemplateDto entityTemplateDto;

    @Override
    public String getCode() {
        return code;
    }

    @Override
    public void setCode(String code) {
        this.code = code;
    }

    @Override
    public String getDescription() {
        return description;
    }

    @Override
    public void setDescription(String description) {
        this.description = description;
    }

    public String getSqlQuery() {
        return sqlQuery;
    }

    public Map<String, String> getAliases() {
        return aliases;
    }

    public void setSqlQuery(String sqlQuery) {
        this.sqlQuery = sqlQuery;
    }

    public void setAliases(Map<String, String> aliases) {
        this.aliases = aliases;
    }

    public CustomEntityTemplateDto getEntityTemplateDto() {
        return entityTemplateDto;
    }

    public void setEntityTemplateDto(CustomEntityTemplateDto entityTemplateDto) {
        this.entityTemplateDto = entityTemplateDto;
    }

    public String getCustomTableCode() {
        return customTableCode;
    }

    public void setCustomTableCode(String customTableCode) {
        this.customTableCode = customTableCode;
    }

    public Map<String, String> getParameters() {
        return parameters;
    }

    public void setParameters(Map<String, String> parameters) {
        this.parameters = parameters;
    }

    public String buildQuery() {
        String columns = aliases.entrySet()
                                .stream()
                                .map(entry -> entry.getKey() + " as " + entry.getValue())
                                .collect(joining(", "));
        return  "SELECT " + columns + " " + sqlQuery;
    }

    public static DataCollectorDto from(DataCollector dataCollector) {
        DataCollectorDto dto = new DataCollectorDto();
        dto.setCode(dataCollector.getCode());
        dto.setDescription(dataCollector.getDescription());
        dto.setSqlQuery(dataCollector.getSqlQuery());
        dto.setAliases(dataCollector.getAliases());
        dto.setParameters(dataCollector.getParameters());
        return dto;
    }
}