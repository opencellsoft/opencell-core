package org.meveo.admin.util;

import java.io.Serializable;
import java.util.Locale;
import java.util.ResourceBundle;

import javax.enterprise.context.ApplicationScoped;
import javax.enterprise.inject.Produces;
import javax.enterprise.inject.spi.InjectionPoint;
import javax.faces.context.FacesContext;

import org.meveo.commons.utils.ParamBean;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ComponentResources implements Serializable {

	private static final long serialVersionUID = 1L;

	@Produces
	public ResourceBundle getResourceBundle() {
		Locale locale = FacesContext.getCurrentInstance().getViewRoot().getLocale();
		ResourceBundle resourceBundle = ResourceBundle.getBundle("messages", locale);
		return resourceBundle;
	}

	@Produces
	@ApplicationScoped
	public ParamBean getParamBean() {
		return ParamBean.getInstance("meveo-admin.properties");
	}

	@Produces
	public Logger createLogger(InjectionPoint injectionPoint) {
	    System.out.println("AKK injecting a logger for class "+ injectionPoint.getMember().getDeclaringClass().getName());
		return LoggerFactory.getLogger(injectionPoint.getMember().getDeclaringClass().getName());
	}
}