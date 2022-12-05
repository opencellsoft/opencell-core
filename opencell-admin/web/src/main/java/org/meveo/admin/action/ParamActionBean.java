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

package org.meveo.admin.action;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Set;

import jakarta.enterprise.context.Conversation;
import jakarta.enterprise.context.ConversationScoped;
import jakarta.faces.application.FacesMessage;
import jakarta.faces.context.FacesContext;
import jakarta.inject.Inject;
import jakarta.inject.Named;

import org.jboss.seam.international.status.builder.BundleKey;
import org.meveo.admin.util.ResourceBundle;
import org.meveo.commons.utils.ParamBean;
import org.meveo.commons.utils.ParamBeanFactory;
import org.meveo.model.ParamProperty;
import org.meveo.service.base.local.IPersistenceService;
import org.primefaces.component.datatable.DataTable;
import org.primefaces.event.CellEditEvent;

/**
 * @author Wassim Drira
 * @author Khalid HORRI
 * @lastModifiedVersion 5.2
 *
 */
@Named
@ConversationScoped
public class ParamActionBean extends BaseBean<ParamProperty> implements Serializable {

    private static final long serialVersionUID = -4570971790276879220L;

    @Inject
    protected Conversation conversation;

    @Inject
    private org.slf4j.Logger log;

    @Inject
    private transient ResourceBundle bundle;

    /** paramBean Factory allows to get application scope paramBean or provider specific paramBean */
    @Inject
    private ParamBeanFactory paramBeanFactory;

    private List<ParamProperty> properties = null;


    /**
     * Constructor
     */
    public ParamActionBean() {
        super(ParamProperty.class);
    }

    @Override
    public ParamProperty initEntity() {
        ParamProperty paramProperty = super.initEntity();
        return paramProperty;
    }



    public void preRenderView() {
        beginConversation();
    }

    /**
     * Method that returns concrete PersistenceService for an entity class backing bean is bound to. That service is then used for operations on concrete entities (eg. save, delete
     * etc).
     *
     * @return Persistence service
     */
    @Override
    protected IPersistenceService<ParamProperty> getPersistenceService() {
        return null;
    }

    public void reset() {
        log.debug("load properties from paramBean");

        ParamBean paramBean = paramBeanFactory.getInstance();

        properties = new ArrayList<ParamProperty>();
        Set<Object> keys = paramBean.getProperties().keySet();
        if (keys != null) {
            for (Object key : keys) {
                ParamProperty paramProp = new ParamProperty(log);
                String strKey = (String) key;
                paramProp.setKey(strKey);
                paramProp.setValue(paramBean.getProperties().getProperty(strKey));
                if (strKey.lastIndexOf(".") > 0) {
                    paramProp.setCategory(bundle.getString("property." + strKey.substring(0, strKey.lastIndexOf("."))));
                }
                properties.add(paramProp);
            }
        }
        Collections.sort(properties);
    }

    public List<ParamProperty> getProperties() {
        if (properties == null) {
            reset();
        }
        return properties;
    }

    public void setProperties(List<ParamProperty> properties) {
        this.properties = properties;
    }

    public void save() {
        log.info("update and save paramBean properties " + properties.size());
        ParamBean paramBean = paramBeanFactory.getInstance();

        for (ParamProperty property : properties) {
            log.info(property.getKey() + "->" + property.getValue());
            paramBean.setProperty(property.getKey(), property.getValue(), property.getCategory());
        }
        paramBean.saveProperties();
        reset();
        FacesMessage msg = new FacesMessage(FacesMessage.SEVERITY_INFO, bundle.getString("success"), bundle.getString("properties.save.successful"));
        facesContext.addMessage(null, msg);
    }

    /**
     *
     * @param event the edit event
     */
    public void onCellEdit(CellEditEvent event) {
        Object oldValue = event.getOldValue();
        Object newValue = event.getNewValue();
        DataTable o = (DataTable) event.getSource();
        ParamProperty property = (ParamProperty) o.getRowData();
        property.setValue(newValue == null ? null : newValue.toString());
        log.debug("Old: " + oldValue + ", New:" + newValue);
    }

    /**
     * Add new property to properties list
     */
    public void add() {
        log.info("Add new property:" + this.entity.getKey() + " -> " + this.entity.getValue());
        if (!this.isDataValid()) {
            messages.error(new BundleKey("messages", "properties.add.error"));
            facesContext.validationFailed();
            return;
        }
        if (getProperties().stream().anyMatch(property -> property.getKey().equals(this.entity.getKey()))){
            messages.error(new BundleKey("messages", "properties.add.exist"));
            facesContext.validationFailed();
            return;

        }
        getProperties().add(this.entity);
        this.entity = newEntity();
    }

    /**
     * Check if the input key is valid
     * @return
     */
    private boolean isDataValid() {
        return this.entity != null && this.entity.getKey().matches(ParamProperty.PROPERTY_PATTERN);
    }

}
