package org.meveo.export;

import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import org.meveo.model.IEntity;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Export/import process configuration
 * 
 * @author Andrius Karpavicius
 * 
 */
public class ExportImportConfig {

    /**
     * A list of classes that should be exported with all attributes
     */
    private Set<Class<? extends IEntity>> classesToExportAsFull = new HashSet<Class<? extends IEntity>>();

    /**
     * A list of classes that should be exported in a short version - only attributes that can uniquely identify an entity in db
     */
    private Set<Class<? extends IEntity>> classesToExportAsId = new HashSet<Class<? extends IEntity>>();

    /**
     * A mapping between a class and attributes for <code>classesToExportAsId</code> parameter
     */
    private Map<Class<? extends IEntity>, String[]> exportIdMapping;

    private Logger log = LoggerFactory.getLogger(this.getClass());

    public ExportImportConfig(ExportTemplate exportTemplate, Map<Class<? extends IEntity>, String[]> exportIdMapping) {
        classesToExportAsFull.add(exportTemplate.getEntityToExport());
        if (exportTemplate.getClassesToExportAsFull() != null) {
            classesToExportAsFull.addAll(exportTemplate.getClassesToExportAsFull());
        }
        if (exportTemplate.getClassesToExportAsId() != null) {
            classesToExportAsId.addAll(exportTemplate.getClassesToExportAsId());
        }
        this.exportIdMapping = exportIdMapping;
    }

    public Set<Class<? extends IEntity>> getClassesToExportAsFull() {
        return classesToExportAsFull;
    }

    public void setClassesToExportAsFull(Set<Class<? extends IEntity>> classesToExportAsFull) {
        this.classesToExportAsFull = classesToExportAsFull;
    }

    public Set<Class<? extends IEntity>> getClassesToExportAsId() {
        return classesToExportAsId;
    }

    public void setClassesToExportAsId(Set<Class<? extends IEntity>> classesToExportAsId) {
        this.classesToExportAsId = classesToExportAsId;
    }

    public void setExportIdMapping(Map<Class<? extends IEntity>, String[]> exportIdMapping) {
        this.exportIdMapping = exportIdMapping;
    }

    /**
     * Determine whether to export full information about an entity
     * 
     * @param clazz Class in question
     * @return True if it was explicitly configured to export full information, or no other export specification (export id only, or export identifiers) is available about the
     *         class
     */
    public boolean isExportFull(Class<? extends IEntity> clazz) {

        for (Class<? extends IEntity> clazzAsFull : classesToExportAsFull) {
            if (clazzAsFull.isAssignableFrom(clazz)) {
                log.info("Exporting/importing entity " + clazz.getName() + " as full. Match classesToExportAsFull rule " + clazzAsFull.getName());
                return true;
            }
        }

        if (!isExportIdOnly(clazz) && getExportIdsForClass(clazz) == null) {
            log.info("Exporting/importing entity " + clazz.getName() + " as full by default.clazz No rule matched.");
            return true;
        }
        return false;
    }

    /**
     * Should export only ID of an entity instead of full information
     * 
     * @param clazz Class in question
     * @return True if was explicitly told to export id only
     */
    public boolean isExportIdOnly(Class<? extends IEntity> clazz) {
        if (classesToExportAsId != null) {
            return classesToExportAsId.contains(clazz);
        }
        return false;
    }

    /**
     * Get list of entity attributes that are considered as unique identifiers (e.g. code) for an entity irrelevant of DB. Attribute Id does not count unless db is populated with
     * fixed values (e.g. picklist) tha will be the same in every installation
     * 
     * @param clazz Class in question
     * @return An array of entity atribute names
     */
    public String[] getExportIdsForClass(Class<? extends IEntity> clazz) {
        return exportIdMapping.get(clazz);
    }
}
