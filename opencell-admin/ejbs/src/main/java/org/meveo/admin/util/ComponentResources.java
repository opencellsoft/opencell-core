/*
 * (C) Copyright 2015-2020 Opencell SAS (https://opencellsoft.com/) and contributors.
 *
 * This program is free software: you can redistribute it and/or modify it under the terms of the GNU Affero General
 * Public License as published by the Free Software Foundation, either version 3 of the License, or (at your option)
 * any later version.
 *
 * THERE IS NO WARRANTY FOR THE PROGRAM, TO THE EXTENT PERMITTED BY APPLICABLE LAW. EXCEPT WHEN
 * OTHERWISE STATED IN WRITING THE COPYRIGHT HOLDERS AND/OR OTHER PARTIES PROVIDE THE PROGRAM "AS
 * IS" WITHOUT WARRANTY OF ANY KIND, EITHER EXPRESSED OR IMPLIED, INCLUDING, BUT NOT LIMITED TO, THE
 * IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE. THE ENTIRE RISK AS TO
 * THE QUALITY AND PERFORMANCE OF THE PROGRAM IS WITH YOU. SHOULD THE PROGRAM PROVE DEFECTIVE,
 * YOU ASSUME THE COST OF ALL NECESSARY SERVICING, REPAIR OR CORRECTION.
 *
 * For more information on the GNU Affero General Public License, please consult
 * <https://www.gnu.org/licenses/agpl-3.0.en.html>.
 */

package org.meveo.admin.util;

import java.io.Serializable;
import java.util.Locale;

import javax.enterprise.context.RequestScoped;
import javax.enterprise.event.Event;
import javax.enterprise.inject.Produces;
import javax.faces.context.FacesContext;
import javax.inject.Inject;
import javax.inject.Named;

import org.meveo.commons.utils.ParamBean;
import org.meveo.commons.utils.ParamBeanFactory;
import org.meveo.util.MeveoParamBean;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author Wassim Drira
 * @lastModifiedVersion 5.0
 * 
 */
public class ComponentResources implements Serializable {

	private Logger log = LoggerFactory.getLogger(this.getClass());
    private static final long serialVersionUID = 1L;

    private Locale locale = Locale.ENGLISH;
    
    @Inject
    @Client
    private Event<Locale> currentLocaleEventProducer;

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
                currentLocaleEventProducer.fire(locale);
                bundleName = FacesContext.getCurrentInstance().getApplication().getMessageBundle();
            } catch (Exception e) {
            	log.error(e.getMessage(), e);
            }
        }

        return new ResourceBundle(java.util.ResourceBundle.getBundle(bundleName, locale));
    }

    @Produces
    @RequestScoped
    @Named("paramBean")
    @MeveoParamBean
    public ParamBean getParamBean() {
        return paramBeanFactory.getInstance();
    }

    public void setLocale(Locale locale) {
        this.locale = locale;
    }
}