package org.meveo.model.filter;

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
	@Size(max = 255)
	@NotNull
	@Column(name = "TARGET_ENTITY", length = 255, nullable = false)
	private String targetEntity;

	/**
	 * List of field names to display or export.
	 */
	@ElementCollection(fetch = FetchType.LAZY)
	@CollectionTable(name = "MEVEO_PROJECTOR_FIELD_NAME", joinColumns = @JoinColumn(name = "PROJECTOR_ID"))
	@Column(name = "FIELD_NAME")
	private List<String> fieldNames;

	/**
	 * List of fields to ignore if foreign key not found.
	 */
	@ElementCollection(fetch = FetchType.LAZY)
	@CollectionTable(name = "MEVEO_PROJECTOR_IGNORE_FIELD", joinColumns = @JoinColumn(name = "PROJECTOR_ID"))
	@Column(name = "IGNORED_FIELD")
	private List<String> ignoreIfNotFoundForeignKey;

	public String getTargetEntity() {
		return targetEntity;
	}

	public void setTargetEntity(String targetEntity) {
		this.targetEntity = targetEntity;
	}

	public List<String> getFieldNames() {
		return fieldNames;
	}

	public void setFieldNames(List<String> fieldNames) {
		this.fieldNames = fieldNames;
	}

	public List<String> getIgnoreIfNotFoundForeignKey() {
		return ignoreIfNotFoundForeignKey;
	}

	public void setIgnoreIfNotFoundForeignKey(List<String> ignoreIfNotFoundForeignKey) {
		this.ignoreIfNotFoundForeignKey = ignoreIfNotFoundForeignKey;
	}

}
