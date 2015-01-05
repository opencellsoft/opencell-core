package org.meveo.admin.action;

import org.meveo.model.IEntity;

public abstract class StatefulBaseBean<T extends IEntity> extends BaseBean<T> {

	private static final long serialVersionUID = 3189710213607743903L;

	// @PersistenceContext(unitName = "MeveoAdmin", type =
	// PersistenceContextType.EXTENDED)
	// private EntityManager em;

	public StatefulBaseBean() {
		super();
	}

	public StatefulBaseBean(Class<T> clazz) {
		super(clazz);
	}

}
