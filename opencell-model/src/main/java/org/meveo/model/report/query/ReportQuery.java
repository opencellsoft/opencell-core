package org.meveo.model.report.query;

import static jakarta.persistence.EnumType.STRING;

import java.sql.Types;
import java.util.List;
import java.util.Map;

import org.hibernate.annotations.GenericGenerator;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.annotations.Parameter;
import org.hibernate.type.SqlTypes;
import org.meveo.model.BusinessEntity;

import jakarta.persistence.CollectionTable;
import jakarta.persistence.Column;
import jakarta.persistence.ElementCollection;
import jakarta.persistence.Entity;
import jakarta.persistence.Enumerated;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.NamedQueries;
import jakarta.persistence.NamedQuery;
import jakarta.persistence.Table;

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

    @JdbcTypeCode(SqlTypes.JSON)
    @Column(name = "filters", columnDefinition = "jsonb")
    private Map<String, String> filters;

    @JdbcTypeCode(Types.LONGVARCHAR)
    @Column(name = "generated_query")
    private String generatedQuery;

    @Column(name = "sort_by")
    private String sortBy;

    @Enumerated(value = STRING)
    @Column(name = "sort_order", length = 15)
    private SortOrderEnum sortOrder;

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

    public String getSortBy() {
        return sortBy;
    }

    public void setSortBy(String sortBy) {
        this.sortBy = sortBy;
    }

    public SortOrderEnum getSortOrder() {
        return sortOrder;
    }

    public void setSortOrder(SortOrderEnum sortOrder) {
        this.sortOrder = sortOrder;
    }
}