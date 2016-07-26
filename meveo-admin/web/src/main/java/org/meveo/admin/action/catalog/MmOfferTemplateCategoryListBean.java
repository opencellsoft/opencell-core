package org.meveo.admin.action.catalog;

import javax.enterprise.context.ConversationScoped;
import javax.faces.context.FacesContext;
import javax.inject.Inject;
import javax.inject.Named;

import org.apache.commons.lang3.StringUtils;
import org.meveo.admin.exception.BusinessException;
import org.meveo.admin.web.interceptor.ActionMethod;
import org.meveo.model.catalog.OfferTemplateCategory;
import org.primefaces.event.CellEditEvent;
import org.slf4j.Logger;

@Named
@ConversationScoped
public class MmOfferTemplateCategoryListBean extends OfferTemplateCategoryListBean {

	private static final long serialVersionUID = 1L;

	private static String NAME_COLUMN = "name";
	private static String PARENT_COLUMN = "parent";

	@Inject
	private Logger log;

	@Override
	public void preRenderView() {
		updateParentHierarchy();
		super.preRenderView();
	}

	@ActionMethod
	public void onCellEdit(CellEditEvent event) throws BusinessException {

		FacesContext context = FacesContext.getCurrentInstance();
		entity = context.getApplication().evaluateExpressionGet(context, "#{entity}", OfferTemplateCategory.class);

		if (entity == null) {
			return;
		}

		String column = StringUtils.substringAfterLast(event.getColumn().getColumnKey(), ":");

		Object newValue = event.getNewValue();

		if (NAME_COLUMN.equals(column)) {
			entity.setName((String) newValue);
		} else if (PARENT_COLUMN.equals(column)) {
			entity.setOfferTemplateCategory((OfferTemplateCategory) newValue);
		}

		super.saveOrUpdate(true);

	}

}
