package org.meveo.admin.action.filter;

import javax.inject.Inject;
import javax.inject.Named;

import org.meveo.admin.action.BaseBean;
import org.meveo.admin.exception.BusinessException;
import org.meveo.model.filter.Filter;
import org.meveo.service.base.local.IPersistenceService;
import org.meveo.service.filter.FilterService;
import org.omnifaces.cdi.ViewScoped;

/**
 * @author Edward P. Legaspi
 **/
@Named
@ViewScoped
public class FilterBean extends BaseBean<Filter> {

	private static final long serialVersionUID = 6689238784280187702L;

	@Inject
	private FilterService filterService;

	public FilterBean() {
		super(Filter.class);
	}

	@Override
	protected IPersistenceService<Filter> getPersistenceService() {
		return filterService;
	}

	@Override
	public String saveOrUpdate(boolean killConversation) throws BusinessException {
		Filter filter = filterService.parse(getEntity().getInputXml());

		if (filter.getOrderCondition() != null) {
			filter.getOrderCondition().setProvider(getCurrentProvider());
			filter.getOrderCondition();
		}

		return super.saveOrUpdate(killConversation);
	}

}
