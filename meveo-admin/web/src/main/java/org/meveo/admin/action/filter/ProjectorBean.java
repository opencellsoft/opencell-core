package org.meveo.admin.action.filter;

import javax.faces.view.ViewScoped;
import javax.inject.Inject;
import javax.inject.Named;

import org.meveo.admin.action.BaseBean;
import org.meveo.model.filter.Projector;
import org.meveo.service.base.local.IPersistenceService;
import org.meveo.service.filter.ProjectorService;

/**
 * @author Edward P. Legaspi
 **/
@Named
@ViewScoped
public class ProjectorBean extends BaseBean<Projector> {

	private static final long serialVersionUID = 2713969688721420571L;

	@Inject
	private ProjectorService projectorService;

	public ProjectorBean() {
		super(Projector.class);
	}

	@Override
	protected IPersistenceService<Projector> getPersistenceService() {
		return projectorService;
	}

}
