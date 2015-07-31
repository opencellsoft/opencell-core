package org.meveo.model.filter;

import java.util.ArrayList;
import java.util.List;

import javax.persistence.CollectionTable;
import javax.persistence.Column;
import javax.persistence.ElementCollection;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.JoinColumn;
import javax.persistence.OneToOne;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

import org.meveo.model.BusinessEntity;
import org.meveo.validation.constraint.ClassName;

/**
 * @author Edward P. Legaspi
 **/
@Entity
@Table(name = "MEVEO_PROJECTOR")
@SequenceGenerator(name = "ID_GENERATOR", sequenceName = "MEVEO_PROJECTOR_SEQ")
public class Projector extends BusinessEntity {

	private static final long serialVersionUID = -6179228494065206254L;

	@ClassName
	@Size(max = 100)
	@NotNull
	@Column(name = "TARGET_ENTITY", length = 100, nullable = false)
	private String targetEntity;

	/**
	 * List of field names to display or export.
	 */
	@ElementCollection(fetch = FetchType.LAZY)
	@CollectionTable(name = "MEVEO_PROJECTOR_DISPLAY_FIELDS", joinColumns = @JoinColumn(name = "PROJECTOR_ID"))
	@Column(name = "DISPLAY_FIELD")
	private List<String> displayFields = new ArrayList<String>();

	@ElementCollection(fetch = FetchType.LAZY)
	@CollectionTable(name = "MEVEO_PROJECTOR_EXPORT_FIELDS", joinColumns = @JoinColumn(name = "PROJECTOR_ID"))
	@Column(name = "EXPORT_FIELD")
	private List<String> exportFields = new ArrayList<String>();

	/**
	 * List of fields to ignore if foreign key not found.
	 */
	@ElementCollection(fetch = FetchType.LAZY)
	@CollectionTable(name = "MEVEO_PROJECTOR_IGNORE_FIELDS", joinColumns = @JoinColumn(name = "PROJECTOR_ID"))
	@Column(name = "IGNORED_FIELD")
	private List<String> ignoreIfNotFoundForeignKeys = new ArrayList<String>();

	@OneToOne(mappedBy = "projector")
	public FilterSelector filterSelector;

	public String getTargetEntity() {
		return targetEntity;
	}

	public void setTargetEntity(String targetEntity) {
		this.targetEntity = targetEntity;
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

	public FilterSelector getFilterSelector() {
		return filterSelector;
	}

	public void setFilterSelector(FilterSelector filterSelector) {
		this.filterSelector = filterSelector;
	}

	public List<String> getIgnoreIfNotFoundForeignKeys() {
		return ignoreIfNotFoundForeignKeys;
	}

	public void setIgnoreIfNotFoundForeignKeys(List<String> ignoreIfNotFoundForeignKeys) {
		this.ignoreIfNotFoundForeignKeys = ignoreIfNotFoundForeignKeys;
	}

}
