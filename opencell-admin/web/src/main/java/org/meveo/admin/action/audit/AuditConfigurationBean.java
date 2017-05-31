package org.meveo.admin.action.audit;

import java.io.IOException;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import javax.annotation.PostConstruct;
import javax.faces.view.ViewScoped;
import javax.inject.Inject;
import javax.inject.Named;

import org.meveo.audit.logging.configuration.AuditConfiguration;
import org.meveo.audit.logging.configuration.AuditConfigurationProvider;
import org.meveo.audit.logging.core.AuditContext;
import org.meveo.audit.logging.dto.MethodWithParameter;
import org.meveo.audit.logging.handler.ConsoleAuditHandler;
import org.meveo.audit.logging.handler.DBAuditHandler;
import org.meveo.audit.logging.handler.FileAuditHandler;
import org.meveo.audit.logging.handler.Handler;
import org.meveo.service.base.local.IPersistenceService;
import org.primefaces.model.DualListModel;
import org.slf4j.Logger;

/**
 * @author Edward P. Legaspi
 **/
@Named
@ViewScoped
public class AuditConfigurationBean implements Serializable {

	private static final long serialVersionUID = -2288050777565855090L;

	@Inject
	private Logger log;

	@Inject
	private AuditConfigurationProvider auditConfigurationProvider;

	private List<Class<? extends IPersistenceService>> serviceClasses;
	private Class<? extends IPersistenceService> selectedClass;
	private DualListModel<String> handlers;
	private DualListModel<MethodWithParameter> methods = new DualListModel<>();

	@PostConstruct
	private void init() {
		// load the configuration from auditContext
		final AuditConfiguration auditConfiguration = AuditContext.getInstance().getAuditConfiguration();
		serviceClasses = auditConfigurationProvider.getServiceClasses();

		List<String> handlerSource = new ArrayList<>();
		List<String> handlerTarget = new ArrayList<>();

		handlerSource.add(ConsoleAuditHandler.class.getName());
		handlerSource.add(FileAuditHandler.class.getName());
		handlerSource.add(DBAuditHandler.class.getName());

		if (getAuditConfiguration().getHandlers() != null) {
			for (Handler h : auditConfiguration.getHandlers()) {
				handlerTarget.add(h.getClass().getName());
			}

			handlerSource.removeAll(handlerTarget);
		}

		handlers = new DualListModel<>(handlerSource, handlerTarget);
	}

	public void onClassChange() {
		log.debug(selectedClass.getName());
		List<MethodWithParameter> sourceMethods = auditConfigurationProvider.getMethods(selectedClass);
		List<MethodWithParameter> targetMethods = new ArrayList<>();

		methods = new DualListModel<>(sourceMethods, targetMethods);
	}

	public void saveOrUpdate()
			throws IOException, InstantiationException, IllegalAccessException, ClassNotFoundException {
		AuditContext.getInstance().getAuditConfiguration().getHandlers().clear();
		if (handlers != null && handlers.getTarget() != null) {
			for (String h : handlers.getTarget()) {
				Class<?> clazz = Class.forName(h);
				Handler handler = (Handler) clazz.newInstance();
				AuditContext.getInstance().getAuditConfiguration().getHandlers().add(handler);
			}
		}
		AuditContext.getInstance().saveConfiguration();
	}

	public AuditConfiguration getAuditConfiguration() {
		return AuditContext.getInstance().getAuditConfiguration();
	}

	public DualListModel<String> getHandlers() {
		return handlers;
	}

	public void setHandlers(DualListModel<String> handlers) {
		this.handlers = handlers;
	}

	public List<Class<? extends IPersistenceService>> getServiceClasses() {
		return serviceClasses;
	}

	public void setServiceClasses(List<Class<? extends IPersistenceService>> serviceClasses) {
		this.serviceClasses = serviceClasses;
	}

	public Class<? extends IPersistenceService> getSelectedClass() {
		return selectedClass;
	}

	public void setSelectedClass(Class<? extends IPersistenceService> selectedClass) {
		this.selectedClass = selectedClass;
	}

	public DualListModel<MethodWithParameter> getMethods() {
		return methods;
	}

	public void setMethods(DualListModel<MethodWithParameter> methods) {
		this.methods = methods;
	}

}
