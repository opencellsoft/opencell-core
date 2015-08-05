package org.meveo.model.filter;

import java.util.ArrayList;
import java.util.List;

import javax.persistence.CollectionTable;
import javax.persistence.Column;
import javax.persistence.ElementCollection;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.JoinColumn;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

import org.meveo.model.BaseEntity;
import org.meveo.validation.constraint.ClassName;

/**
 * @author Edward P. Legaspi
 **/
@Entity
@Table(name = "MEVEO_FILTER_SELECTOR")
@SequenceGenerator(name = "ID_GENERATOR", sequenceName = "MEVEO_FILTER_SELECTOR_SEQ")
public class FilterSelector extends BaseEntity {

	private static final long serialVersionUID = -7068163052219180546L;

	@ClassName
	@Size(max = 100)
	@NotNull
	@Column(name = "TARGET_ENTITY", length = 100, nullable = false)
	private String targetEntity;

	@Column(name = "ALIAS", length = 50, nullable = false)
	private String alias;

	/**
	 * List of field names to display or export.
	 */
	@ElementCollection(fetch = FetchType.LAZY)
	@CollectionTable(name = "MEVEO_FILTER_SELECTOR_DISPLAY_FIELDS", joinColumns = @JoinColumn(name = "FILTER_SELECTOR_ID"))
	@Column(name = "DISPLAY_FIELD")
	private List<String> displayFields = new ArrayList<String>();

	@ElementCollection(fetch = FetchType.LAZY)
	@CollectionTable(name = "MEVEO_FILTER_SELECTOR_EXPORT_FIELDS", joinColumns = @JoinColumn(name = "FILTER_SELECTOR_ID"))
	@Column(name = "EXPORT_FIELD")
	private List<String> exportFields = new ArrayList<String>();

	/**
	 * List of fields to ignore if foreign key not found.
	 */
	@ElementCollection(fetch = FetchType.LAZY)
	@CollectionTable(name = "MEVEO_FILTER_SELECTOR_IGNORE_FIELDS", joinColumns = @JoinColumn(name = "FILTER_SELECTOR_ID"))
	@Column(name = "IGNORED_FIELD")
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
	public boolean equals(Object other) {
		if (other == null || !(other instanceof FilterSelector)) {
			return false;
		}
		FilterSelector o = (FilterSelector) other;
		return (o.getId() != null) && o.getId().equals(this.getId());
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
