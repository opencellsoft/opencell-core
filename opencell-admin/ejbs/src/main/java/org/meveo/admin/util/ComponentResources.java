package org.meveo.admin.util;

import java.io.Serializable;
import java.util.Locale;

import javax.enterprise.context.ApplicationScoped;
import javax.enterprise.inject.Produces;
import javax.faces.context.FacesContext;
import javax.inject.Inject;
import javax.inject.Named;

import org.meveo.commons.utils.ParamBean;
import org.meveo.commons.utils.ParamBeanFactory;
import org.meveo.util.MeveoParamBean;

/**
 * @author Wassim Drira
 * @lastModifiedVersion 5.0
 * 
 */
public class ComponentResources implements Serializable {

    private static final long serialVersionUID = 1L;

    private Locale locale = Locale.ENGLISH;

    @Inject
    private LocaleSelector localeSelector;

    @Inject
    private ParamBeanFactory paramBeanFactory;

    @Produces
    public ResourceBundle getResourceBundle() {
        String bundleName = "messages";
        if (FacesContext.getCurrentInstance() != null) {
            try {
                locale = localeSelector.getCurrentLocale();
            } catch (Exception e) {
            }
            try {
                bundleName = FacesContext.getCurrentInstance().getApplication().getMessageBundle();
            } catch (Exception e) {
            }
        }

        return new ResourceBundle(java.util.ResourceBundle.getBundle(bundleName, locale));
    }

    @Produces
    @ApplicationScoped
    @Named
    @MeveoParamBean
    public ParamBean getParamBean() {
        return paramBeanFactory.getInstance();
    }

    public void setLocale(Locale locale) {
        this.locale = locale;
    }
}