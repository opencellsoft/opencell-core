package org.meveo.export;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.meveo.commons.utils.ReflectionUtils;
import org.meveo.model.IEntity;

/**
 * Export/import process template
 * 
 * @author Andrius Karpavicius
 */
public class ExportTemplate {

    private String ref;

    private String name;

    private Class<? extends IEntity> entityToExport;

    private Map<String, String> parameters;

    /**
     * Shall all classes be exported with all attributes
     */
    private boolean exportAllClassesAsFull;

    /**
     * A list of classes that should be exported with all attributes - applies only when exportAllClassesAsFull=false
     */
    private List<Class<? extends IEntity>> classesToExportAsFull = new ArrayList<Class<? extends IEntity>>();

    /**
     * A list of classes that should be exported in a short version - without attributes - applies only when exportAllClassesAsFull=true
     */
    private List<Class<? extends IEntity>> classesToExportAsShort = new ArrayList<Class<? extends IEntity>>();

    /**
     * A list of classes that should be exported in a short version - only ID attribute - applies irrelevant of exportAllClassesAsFull value
     */
    private List<Class<? extends IEntity>> classesToExportAsId = new ArrayList<Class<? extends IEntity>>();

    /**
     * A list of classes that should not raise an exception if foreign key to entity of these classes was not found and import was explicitly requested to validate foreign keys
     */
    private List<Class<? extends IEntity>> classesToIgnoreFKNotFound = new ArrayList<Class<? extends IEntity>>();

    private List<RelatedEntityToExport> relatedEntities;

    /**
     * Other export/import templates grouped under this template
     */
    private List<ExportTemplate> groupedTemplates = new ArrayList<ExportTemplate>();

    private boolean canDeleteAfterExport = false;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getHumanName() {
        return ReflectionUtils.getHumanClassName(name);
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

    public boolean isExportAllClassesAsFull() {
        return exportAllClassesAsFull;
    }

    public void setExportAllClassesAsFull(boolean exportAllClassesAsFull) {
        this.exportAllClassesAsFull = exportAllClassesAsFull;
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

    public List<Class<? extends IEntity>> getClassesToExportAsShort() {
        return classesToExportAsShort;
    }

    public void setClassesToExportAsShort(List<Class<? extends IEntity>> classesToExportAsShort) {
        this.classesToExportAsShort = classesToExportAsShort;
    }

    public List<Class<? extends IEntity>> getClassesToExportAsId() {
        return classesToExportAsId;
    }

    public void setClassesToExportAsId(List<Class<? extends IEntity>> classesToExportAsId) {
        this.classesToExportAsId = classesToExportAsId;
    }

    public List<Class<? extends IEntity>> getClassesToIgnoreFKNotFound() {
        return classesToIgnoreFKNotFound;
    }

    public void setClassesToIgnoreFKNotFound(List<Class<? extends IEntity>> classesToIgnoreFKNotFound) {
        this.classesToIgnoreFKNotFound = classesToIgnoreFKNotFound;
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

    public List<RelatedEntityToExport> getRelatedEntities() {
        return relatedEntities;
    }

    public void setRelatedEntities(List<RelatedEntityToExport> relatedEntities) {
        this.relatedEntities = relatedEntities;
    }

    @SuppressWarnings("rawtypes")
    public void addRelatedEntity(String pathToEntity, String relatedEntityCondition, String selection, Map<String, String> parameters, Class entityClass) {
        if (relatedEntities == null) {
            relatedEntities = new ArrayList<RelatedEntityToExport>();
        }
        relatedEntities.add(new RelatedEntityToExport(pathToEntity, relatedEntityCondition, selection, parameters, entityClass));
    }

    public String getRef() {
        return ref;
    }

    public void setRef(String ref) {
        this.ref = ref;
    }

    public boolean isGroupedTemplate() {
        return groupedTemplates != null && !groupedTemplates.isEmpty();
    }

    @Override
    public String toString() {
        final int maxLen = 10;
        return String
            .format(
                "ExportTemplate [ref=%s, name=%s, entityToExport=%s, parameters=%s, exportAllClassesAsFull=%s, classesToExportAsFull=%s, classesToExportAsShort=%s, classesToExportAsId=%s, classesToIgnoreFKNotFound=%s, relatedEntities=%s, groupedTemplates=%s, canDeleteAfterExport=%s]",
                ref, name, entityToExport, parameters != null ? toString(parameters.entrySet(), maxLen) : null, exportAllClassesAsFull,
                classesToExportAsFull != null ? toString(classesToExportAsFull, maxLen) : null, classesToExportAsShort != null ? toString(classesToExportAsShort, maxLen) : null,
                classesToExportAsId != null ? toString(classesToExportAsId, maxLen) : null, classesToIgnoreFKNotFound != null ? toString(classesToIgnoreFKNotFound, maxLen) : null,
                relatedEntities != null ? toString(relatedEntities, maxLen) : null, groupedTemplates != null ? toString(groupedTemplates, maxLen) : null, canDeleteAfterExport);
    }

    private String toString(Collection<?> collection, int maxLen) {
        StringBuilder builder = new StringBuilder();
        builder.append("[");
        int i = 0;
        for (Iterator<?> iterator = collection.iterator(); iterator.hasNext() && i < maxLen; i++) {
            if (i > 0)
                builder.append(", ");
            builder.append(iterator.next());
        }
        builder.append("]");
        return builder.toString();
    }
}