package org.meveo.model.filter;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.JoinColumn;
import javax.persistence.OneToOne;
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

	@OneToOne(cascade = CascadeType.ALL)
	@JoinColumn(name = "PROJECTOR_ID")
	private Projector projector;

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

	public Projector getProjector() {
		return projector;
	}

	public void setProjector(Projector projector) {
		this.projector = projector;
	}
	
	@Override
	public boolean equals(Object other) {
		if (other == null || !(other instanceof FilterSelector)) {
			return false;
		}
		FilterSelector o = (FilterSelector) other;
		return (o.getId() != null) && o.getId().equals(this.getId());
	}

}
