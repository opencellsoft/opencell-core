package org.meveo.admin.action.script;

import javax.faces.view.ViewScoped;
import javax.inject.Inject;
import javax.inject.Named;

import org.meveo.admin.action.BaseBean;
import org.meveo.model.scripts.ScriptInstanceCategory;
import org.meveo.service.base.local.IPersistenceService;
import org.meveo.service.script.ScriptInstanceCategoryService;

/**
 * @author Edward P. Legaspi
 * @lastModifiedVersion 5.1
 */
@Named
@ViewScoped
public class ScriptInstanceCategoryBean extends BaseBean<ScriptInstanceCategory> {

	private static final long serialVersionUID = 4315061097389241940L;

	@Inject
	private ScriptInstanceCategoryService scriptInstanceCategoryService;

	public ScriptInstanceCategoryBean() {
		super(ScriptInstanceCategory.class);
	}

	@Override
	protected String getListViewName() {
		return "scriptInstanceCategories";
	}
	
	@Override
	public String getEditViewName() {
		return "scriptInstanceCategoryDetail";
	}

	@Override
	protected IPersistenceService<ScriptInstanceCategory> getPersistenceService() {
		return scriptInstanceCategoryService;
	}

}
