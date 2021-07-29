package org.meveo.model.report.query;

import static javax.persistence.EnumType.STRING;

import org.hibernate.annotations.GenericGenerator;
import org.hibernate.annotations.Parameter;
import org.hibernate.annotations.Type;
import org.meveo.model.BusinessEntity;

import javax.persistence.*;
import java.util.List;
import java.util.Map;

@Entity
@Table(name = "report_query")
@GenericGenerator(name = "ID_GENERATOR", strategy = "org.hibernate.id.enhanced.SequenceStyleGenerator", parameters = {
        @Parameter(name = "sequence_name", value = "report_query_seq"), })
@NamedQueries({
        @NamedQuery(name = "ReportQuery.ReportQueryByCreatorVisibilityCode", query = "SELECT rp FROM ReportQuery rp where rp.code = :code AND rp.visibility = :visibility") })
public class ReportQuery extends BusinessEntity {

    private static final long serialVersionUID = 4855020554862630670L;

    @Column(name = "target_entity")
    private String targetEntity;

    @Enumerated(value = STRING)
    @Column(name = "visibility")
    private QueryVisibilityEnum visibility;

    @ElementCollection
    @CollectionTable(name = "report_query_fields", joinColumns = @JoinColumn(name = "id"))
    @Column(name = "field")
    private List<String> fields;

    @Type(type = "json")
    @Column(name = "filters", columnDefinition = "text")
    private Map<String, String> filters;

    @Column(name = "generated_query", columnDefinition = "text")
    private String generatedQuery;

    public String getTargetEntity() {
        return targetEntity;
    }

    public void setTargetEntity(String targetEntity) {
        this.targetEntity = targetEntity;
    }

    public QueryVisibilityEnum getVisibility() {
        return visibility;
    }

    public void setVisibility(QueryVisibilityEnum visibility) {
        this.visibility = visibility;
    }

    public List<String> getFields() {
        return fields;
    }

    public void setFields(List<String> fields) {
        this.fields = fields;
    }

    public Map<String, String> getFilters() {
        return filters;
    }

    public void setFilters(Map<String, String> filters) {
        this.filters = filters;
    }

    public String getGeneratedQuery() {
        return generatedQuery;
    }

    public void setGeneratedQuery(String generatedQuery) {
        this.generatedQuery = generatedQuery;
    }
}