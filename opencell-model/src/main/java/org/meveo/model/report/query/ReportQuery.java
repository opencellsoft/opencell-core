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

    /**
     * @deprecated use instead advancedQuery
     */
    @ElementCollection
    @CollectionTable(name = "report_query_fields", joinColumns = @JoinColumn(name = "id"))
    @Column(name = "field")
    @Deprecated
    private List<String> fields;

    /**
     * @deprecated use instead advancedQuery
     */
    @Type(type = "json")
    @Column(name = "filters", columnDefinition = "jsonb")
    @Deprecated
    private Map<String, Object> filters;

    @Type(type = "longText")
    @Column(name = "generated_query")
    private String generatedQuery;

    /**
     * @deprecated use instead advancedQuery
     */
    @Column(name = "sort_by")
    @Deprecated
    private String sortBy;

    /**
     * @deprecated use instead advancedQuery
     */
    @Enumerated(value = STRING)
    @Column(name = "sort_order", length = 15)
    @Deprecated
    private SortOrderEnum sortOrder;

    /**
     * @deprecated use instead advancedQuery
     */
    @Type(type = "json")
    @Column(name = "query_parameters", columnDefinition = "jsonb")
    @Deprecated
    private Map<String, Object> queryParameters;

    @Type(type = "json")
    @Column(name = "aliases", columnDefinition = "jsonb")
    private Map<String, String> aliases;

    @Type(type = "json")
    @Column(name = "advanced_query", columnDefinition = "jsonb")
    private Map<String, Object> advancedQuery;

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

    public Map<String, Object> getFilters() {
        return filters;
    }

    public void setFilters(Map<String, Object> filters) {
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

	/**
	 * @return the queryParameters
	 */
	public Map<String, Object> getQueryParameters() {
		return queryParameters;
	}

	/**
	 * @param queryParameters the queryParameters to set
	 */
	public void setQueryParameters(Map<String, Object> queryParameters) {
		this.queryParameters = queryParameters;
	}

	/**
	 * @return the aliases
	 */
	public Map<String, String> getAliases() {
		return aliases;
	}

	/**
	 * @param aliases the aliases to set
	 */
	public void setAliases(Map<String, String> aliases) {
		this.aliases = aliases;
	}

	/**
	 * @return the advancedQuery
	 */
	public Map<String, Object> getAdvancedQuery() {
		return advancedQuery;
	}

	/**
	 * @param advancedQuery the advancedQuery to set
	 */
	public void setAdvancedQuery(Map<String, Object> advancedQuery) {
		this.advancedQuery = advancedQuery;
	}
	
    
}
