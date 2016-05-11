package org.meveo.admin.action.finance;

import javax.inject.Inject;
import javax.inject.Named;

import org.jboss.seam.faces.view.config.ViewConfig;
import org.meveo.admin.action.BaseBean;
import org.meveo.model.scripts.RevenueRecognitionScriptEntity;
import org.meveo.service.base.local.IPersistenceService;
import org.meveo.service.script.revenue.RevenueRecognitionScriptService;

/**
 * @author Edward P. Legaspi
 **/
@Named
@ViewConfig
public class RevenueRecognitionScriptBean extends BaseBean<RevenueRecognitionScriptEntity> {

	private static final long serialVersionUID = -8804531738358416363L;

	@Inject
	private RevenueRecognitionScriptService revenueRecognitionScriptService;

	public RevenueRecognitionScriptBean() {
		super(RevenueRecognitionScriptEntity.class);
	}

	@Override
	protected IPersistenceService<RevenueRecognitionScriptEntity> getPersistenceService() {
		return revenueRecognitionScriptService;
	}

}
