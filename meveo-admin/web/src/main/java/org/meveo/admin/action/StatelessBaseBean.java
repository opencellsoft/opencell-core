package org.meveo.admin.action;

import org.meveo.model.IEntity;

public abstract class StatelessBaseBean<T extends IEntity> extends BaseBean<T> {

	private static final long serialVersionUID = -2931133126310672792L;

	public StatelessBaseBean() {
		super();
	}

	public StatelessBaseBean(Class<T> clazz) {
		super(clazz);
	}

}
