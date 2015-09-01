package org.meveocrm.admin.action.reporting;

import org.meveo.admin.action.BaseBean;
import org.meveo.model.IEntity;

/**
 * 
 * @author Luis Alfonso L. Mance
 * 
 */

public abstract class ChartEntityBean<T extends IEntity> extends BaseBean<T> {

	private static final long serialVersionUID = 5241132812597358412L;

	public ChartEntityBean() {
		super();
	}

	public ChartEntityBean(Class<T> clazz) {
		super(clazz);
	}

}
