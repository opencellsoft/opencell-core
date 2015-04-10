package org.meveo.export;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.meveo.model.IEntity;

public class ExportTemplate {

    private String name;

    private Class<? extends IEntity> entityToExport;

    private Map<String, String> parameters;

    private List<Class<? extends IEntity>> classesToExportAsFull = new ArrayList<Class<? extends IEntity>>();

    private List<Class<? extends IEntity>> classesToExportAsId = new ArrayList<Class<? extends IEntity>>();

    private List<ExportTemplate> groupedTemplates = new ArrayList<ExportTemplate>();

    private boolean canDeleteAfterExport = false;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Class<? extends IEntity> getEntityToExport() {
        return entityToExport;
    }

    public void setEntityToExport(Class<? extends IEntity> entityToExport) {
        this.entityToExport = entityToExport;
    }

    public Map<String, String> getParameters() {
        return parameters;
    }

    public void setParameters(Map<String, String> parameters) {
        this.parameters = parameters;
    }

    public List<Class<? extends IEntity>> getClassesToExportAsFull() {
        return classesToExportAsFull;
    }

    public void setClassesToExportAsFull(List<Class<? extends IEntity>> classesToExportAsFull) {
        this.classesToExportAsFull = classesToExportAsFull;
    }

    public String getClassesToExportAsFullTxt() {
        String classes = "";
        if (classesToExportAsFull != null) {
            for (Class<? extends IEntity> clazz : classesToExportAsFull) {
                classes = classes + (classes.length() == 0 ? "" : ", ") + clazz.getName();
            }
        }
        return classes;
    }

    public List<Class<? extends IEntity>> getClassesToExportAsId() {
        return classesToExportAsId;
    }

    public void setClassesToExportAsId(List<Class<? extends IEntity>> classesToExportAsId) {
        this.classesToExportAsId = classesToExportAsId;
    }

    public List<ExportTemplate> getGroupedTemplates() {
        return groupedTemplates;
    }

    public void setGroupedTemplates(List<ExportTemplate> groupedTemplates) {
        this.groupedTemplates = groupedTemplates;
    }

    public boolean isHasParameters() {
        return parameters != null && !parameters.isEmpty();
    }

    public boolean isCanDeleteAfterExport() {
        return canDeleteAfterExport;
    }

    public void setCanDeleteAfterExport(boolean canDeleteAfterExport) {
        this.canDeleteAfterExport = canDeleteAfterExport;
    }
}