package org.meveo.model.filter;

import java.util.List;

import javax.persistence.Column;
import javax.persistence.DiscriminatorValue;
import javax.persistence.Entity;
import javax.persistence.Table;
import javax.validation.constraints.Size;

import org.hibernate.annotations.GenericGenerator;
import org.hibernate.annotations.Parameter;
import org.meveo.model.BaseEntity;

/**
 * @author Edward P. Legaspi
 **/
@Entity
@Table(name = "meveo_native_filter_condition")
@DiscriminatorValue(value = "NATIVE")
@GenericGenerator(name = "ID_GENERATOR", strategy = "org.hibernate.id.enhanced.SequenceStyleGenerator", parameters = {@Parameter(name = "sequence_name", value = "meveo_native_filter_condition_seq"), })
public class NativeFilterCondition extends FilterCondition {

	private static final long serialVersionUID = 8649912992576398066L;

	@Column(name = "el", length = 2000)
	@Size(max = 2000)
	private String el;

	@Column(name = "jpql", length = 2000)
    @Size(max = 2000)
	private String jpql;

	@Override
	public boolean match(BaseEntity e) {
		// evaluate el
		return false;
	}

	@Override
	public List<BaseEntity> filter(List<BaseEntity> e) {
		return null;
	}

	public String getEl() {
		return el;
	}

	public void setEl(String el) {
		this.el = el;
	}

	public String getJpql() {
		return jpql;
	}

	public void setJpql(String jpql) {
		this.jpql = jpql;
	}

}
