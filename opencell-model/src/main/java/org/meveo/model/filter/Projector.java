package org.meveo.model.filter;

import java.util.ArrayList;
import java.util.List;

import javax.persistence.CollectionTable;
import javax.persistence.Column;
import javax.persistence.ElementCollection;
import javax.persistence.FetchType;
import javax.persistence.JoinColumn;
import javax.persistence.OneToOne;

import org.meveo.model.BusinessEntity;

/**
 * @author Edward P. Legaspi
 **/
// @Entity
// @Table(name = "meveo_projector")
// @GenericGenerator(name = "ID_GENERATOR", strategy = "org.hibernate.id.enhanced.SequenceStyleGenerator", parameters = {@Parameter(name = "sequence_name", value =
// "meveo_projector_seq"), })
public class Projector extends BusinessEntity {

	private static final long serialVersionUID = -6179228494065206254L;

	/**
	 * List of field names to display or export.
	 */
	@ElementCollection(fetch = FetchType.LAZY)
	@CollectionTable(name = "meveo_projector_display_fields", joinColumns = @JoinColumn(name = "projector_id"))
	@Column(name = "display_field")
	private List<String> displayFields = new ArrayList<String>();

	@ElementCollection(fetch = FetchType.LAZY)
	@CollectionTable(name = "meveo_projector_export_fields", joinColumns = @JoinColumn(name = "projector_id"))
	@Column(name = "export_field")
	private List<String> exportFields = new ArrayList<String>();

	/**
	 * List of fields to ignore if foreign key not found.
	 */
	@ElementCollection(fetch = FetchType.LAZY)
	@CollectionTable(name = "meveo_projector_ignore_fields", joinColumns = @JoinColumn(name = "projector_id"))
	@Column(name = "ignored_field")
	private List<String> ignoreIfNotFoundForeignKeys = new ArrayList<String>();

	@OneToOne(mappedBy = "projector")
	public FilterSelector filterSelector;

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
