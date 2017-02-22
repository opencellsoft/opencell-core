package org.meveo.admin.action.admin;

import java.lang.annotation.Annotation;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import javax.faces.view.ViewScoped;
import javax.inject.Inject;
import javax.inject.Named;

import org.meveo.admin.action.BaseBean;
import org.meveo.commons.utils.ReflectionUtils;
import org.meveo.model.IEntity;
import org.meveo.service.base.BaseEntityService;
import org.meveo.service.base.local.IPersistenceService;
import org.primefaces.model.LazyDataModel;

@ViewScoped
@Named
public class GenericEntityPickerBean extends BaseBean<IEntity> {

    private static final long serialVersionUID = 115130709397837651L;

    private Class<? extends IEntity> selectedEntityClass;

    @Inject
    private BaseEntityService baseEntityService;

    /**
     * Get a list of classes that contain the given annotation
     * 
     * @param annotation Annotation classname
     * @return A list of classes
     */
    @SuppressWarnings("unchecked")
    public List<Class<?>> getEntityClasses(String annotation) {
        try {

            List<Class<?>> classes = new ArrayList<>(ReflectionUtils.getClassesAnnotatedWith((Class<? extends Annotation>) Class.forName(annotation)));

            Collections.sort(classes, new Comparator<Class<?>>() {
                @Override
                public int compare(Class<?> clazz1, Class<?> clazz2) {
                    return clazz1.getName().compareTo(clazz2.getName());
                }
            });
            return classes;

        } catch (ClassNotFoundException e) {
            return new ArrayList<Class<?>>();
        }
    }

    public Class<? extends IEntity> getSelectedEntityClass() {
        return selectedEntityClass;
    }

    @SuppressWarnings("unchecked")
    public void setSelectedEntityClass(Class<? extends IEntity> selectedEntityClass) {
        this.selectedEntityClass = selectedEntityClass;
        setClazz((Class<IEntity>) selectedEntityClass);
        baseEntityService.setEntityClass((Class<IEntity>) selectedEntityClass);
    }

    @Override
    protected IPersistenceService<IEntity> getPersistenceService() {
        return baseEntityService;
    }

    @Override
    public LazyDataModel<IEntity> getLazyDataModel() {
        if (selectedEntityClass == null) {
            return null;
        } else {
            return super.getLazyDataModel();
        }
    }

    @Override
    protected String getDefaultSort() {
        return "code";
    }
}