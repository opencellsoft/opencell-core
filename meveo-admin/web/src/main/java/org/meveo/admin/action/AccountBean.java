package org.meveo.admin.action;

import org.meveo.model.AccountEntity;

public abstract class AccountBean<T extends AccountEntity> extends
		BaseBean<T> {

	private static final long serialVersionUID = 3407699633028715707L;


	public AccountBean() {

	}

	public AccountBean(Class<T> clazz) {
		super(clazz);
	}


}
