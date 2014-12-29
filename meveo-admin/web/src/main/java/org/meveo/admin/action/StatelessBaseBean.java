package org.meveo.admin.action;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.PersistenceContextType;

import org.meveo.model.IEntity;

public abstract class StatelessBaseBean<T extends IEntity> extends BaseBean<T> {

	private static final long serialVersionUID = -2931133126310672792L;

	@PersistenceContext(unitName = "MeveoAdmin", type = PersistenceContextType.TRANSACTION)
	private EntityManager em;

	public StatelessBaseBean() {
		super();
	}

	public StatelessBaseBean(Class<T> clazz) {
		super(clazz);
	}

}
