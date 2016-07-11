package org.meveo.api.security.filter;

import org.meveo.model.admin.User;

public class NullFilter extends SecureMethodResultFilter {

	@Override
	public Object filterResult(Object result, User user) {
		return result;
	}

}
