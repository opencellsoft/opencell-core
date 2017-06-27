package org.meveo.model.filter;

import java.util.ArrayList;
import java.util.List;

import javax.persistence.CollectionTable;
import javax.persistence.Column;
import javax.persistence.ElementCollection;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.JoinColumn;
import javax.persistence.Table;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

import org.hibernate.annotations.GenericGenerator;
import org.hibernate.annotations.Parameter;
import org.meveo.model.BaseEntity;
import org.meveo.validation.constraint.ClassName;

/**
 * @author Edward P. Legaspi
 **/
@Entity
@Table(name = "meveo_filter_selector")
@GenericGenerator(name = "ID_GENERATOR", strategy = "org.hibernate.id.enhanced.SequenceStyleGenerator", parameters = {@Parameter(name = "sequence_name", value = "meveo_filter_selector_seq"), })
public class FilterSelector extends BaseEntity {

	private static final long serialVersionUID = -7068163052219180546L;

	@ClassName
	@Size(max = 100)
	@NotNull
	@Column(name = "target_entity", length = 100, nullable = false)
	private String targetEntity;

	@Column(name = "alias", length = 50)
	@Size(max = 50)
	private String alias;

	/**
	 * List of field names to display or export.
	 */
	@ElementCollection(fetch = FetchType.LAZY)
	@CollectionTable(name = "meveo_filter_selector_display_fields", joinColumns = @JoinColumn(name = "filter_selector_id"))
	@Column(name = "display_field")
	private List<String> displayFields = new ArrayList<String>();

	@ElementCollection(fetch = FetchType.LAZY)
	@CollectionTable(name = "meveo_filter_selector_export_fields", joinColumns = @JoinColumn(name = "filter_selector_id"))
	@Column(name = "export_field")
	private List<String> exportFields = new ArrayList<String>();

	/**
	 * List of fields to ignore if foreign key not found.
	 */
	@ElementCollection(fetch = FetchType.LAZY)
	@CollectionTable(name = "meveo_filter_selector_ignore_fields", joinColumns = @JoinColumn(name = "filter_selector_id"))
	@Column(name = "ignored_field")
	private List<String> ignoreIfNotFoundForeignKeys = new ArrayList<String>();

	public String getTargetEntity() {
		return targetEntity;
	}

	public void setTargetEntity(String targetEntity) {
		this.targetEntity = targetEntity;
	}

	public String getAlias() {
		return alias;
	}

	public void setAlias(String alias) {
		this.alias = alias;
	}

	@Override
	public boolean equals(Object obj) {

        if (this == obj) {
            return true;
        } else if (obj == null) {
            return false;
        } else if (!(obj instanceof FilterSelector)) {
            return false;
        }
        
		FilterSelector other = (FilterSelector) obj;
		return (other.getId() != null) && other.getId().equals(this.getId());
	}

	public List<String> getDisplayFields() {
		return displayFields;
	}

	public void setDisplayFields(List<String> displayFields) {
		this.displayFields = displayFields;
	}

	public List<String> getExportFields() {
		return exportFields;
	}

	public void setExportFields(List<String> exportFields) {
		this.exportFields = exportFields;
	}

	public List<String> getIgnoreIfNotFoundForeignKeys() {
		return ignoreIfNotFoundForeignKeys;
	}

	public void setIgnoreIfNotFoundForeignKeys(List<String> ignoreIfNotFoundForeignKeys) {
		this.ignoreIfNotFoundForeignKeys = ignoreIfNotFoundForeignKeys;
	}

}
