package org.meveo.admin.action.filter;

import java.util.HashMap;
import java.util.Map;

import javax.enterprise.context.ConversationScoped;
import javax.inject.Named;

/**
 * @author Edward P. Legaspi
 **/
@Named
@ConversationScoped
public class FilterListBean extends FilterBean {

	private static final long serialVersionUID = -1079492194327180982L;

	public FilterListBean() {
	}

	public Map<String, Object> getFilters() {
		if (filters == null)
			filters = new HashMap<String, Object>();

		return filters;
	}

}
