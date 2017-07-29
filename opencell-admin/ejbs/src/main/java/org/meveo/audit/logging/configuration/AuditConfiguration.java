package org.meveo.audit.logging.configuration;

import java.util.ArrayList;
import java.util.List;

import org.meveo.audit.logging.dto.ClassAndMethods;
import org.meveo.audit.logging.handler.ConsoleAuditHandler;
import org.meveo.audit.logging.handler.Handler;
import org.meveo.audit.logging.layout.Layout;
import org.meveo.audit.logging.layout.SimpleLayout;

/**
 * @author Edward P. Legaspi
 **/
public class AuditConfiguration {

	private boolean enabled;
	private Layout layout;
	private List<Handler> handlers = new ArrayList<>();
	private List<ClassAndMethods> classes = new ArrayList<>();

	public void init() {
		setEnabled(false);
		getHandlers().add(new ConsoleAuditHandler());
		setLayout(new SimpleLayout());
		setClasses(new ArrayList<>());
	}

	public ClassAndMethods findByClassName(String className) {
		if (classes == null) {
			return null;
		}

		for (ClassAndMethods cm : classes) {
			if (cm.getClassName().equals(className)) {
				return cm;
			}
		}

		return null;
	}

	public boolean isMethodLoggable(String className, String methodName) {
		ClassAndMethods cm = findByClassName(className);
		if (cm == null) {
			return false;
		}

		if (cm.getMethods() != null) {
			for (String m : cm.getMethods()) {
				if (m.equals(methodName)) {
					return true;
				}
			}
		}

		return false;
	}

	public boolean isEnabled() {
		return enabled;
	}

	public void setEnabled(boolean enabled) {
		this.enabled = enabled;
	}

	public Layout getLayout() {
		return layout;
	}

	public void setLayout(Layout layout) {
		this.layout = layout;
	}

	public List<Handler> getHandlers() {
		return handlers;
	}

	public void setHandlers(List<Handler> handlers) {
		this.handlers = handlers;
	}

	public List<ClassAndMethods> getClasses() {
		return classes;
	}

	public void setClasses(List<ClassAndMethods> classes) {
		this.classes = classes;
	}

}
