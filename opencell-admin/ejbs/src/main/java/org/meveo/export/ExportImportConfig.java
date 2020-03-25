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

package org.meveo.export;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
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
     * Shall all classes be exported with all attributes
     */
    private boolean exportAllClassesAsFull;

    /**
     * A list of classes that should be exported with all attributes - applies only when exportAllClassesAsFull=false
     */
    private Set<Class<? extends IEntity>> classesToExportAsFull = new HashSet<Class<? extends IEntity>>();

    /**
     * A list of classes that should be exported in a short version - without attributes - applies only when exportAllClassesAsFull=true
     */
    private Set<Class<? extends IEntity>> classesToExportAsShort = new HashSet<Class<? extends IEntity>>();

    /**
     * A list of classes that should be exported in a short version - only ID attribute
     */
    private Set<Class<? extends IEntity>> classesToExportAsId = new HashSet<Class<? extends IEntity>>();

    /**
     * A list of classes that should not raise an exception if foreign key to entity of these classes was not found and import was explicitly requested to validate foreign keys
     */
    private List<Class<? extends IEntity>> classesToIgnoreFKNotFound = new ArrayList<Class<? extends IEntity>>();

    /**
     * A mapping between a class and attributes for <code>classesToExportAsId</code> parameter
     */
    private Map<Class<? extends IEntity>, String[]> exportIdMapping;

    private Logger log = LoggerFactory.getLogger(this.getClass());

    public ExportImportConfig() {

    }

    public ExportImportConfig(ExportTemplate exportTemplate, Map<Class<? extends IEntity>, String[]> exportIdMapping) {
        exportAllClassesAsFull = exportTemplate.isExportAllClassesAsFull();

        if (exportAllClassesAsFull) {
            if (exportTemplate.getClassesToExportAsShort() != null) {
                classesToExportAsShort.addAll(exportTemplate.getClassesToExportAsShort());
            }
        } else {
            classesToExportAsFull.add(exportTemplate.getEntityToExport());
            if (exportTemplate.getClassesToExportAsFull() != null) {
                classesToExportAsFull.addAll(exportTemplate.getClassesToExportAsFull());
            }
        }
        if (exportTemplate.getClassesToExportAsId() != null) {
            classesToExportAsId.addAll(exportTemplate.getClassesToExportAsId());
        }
        if (exportTemplate.getClassesToIgnoreFKNotFound() != null) {
            classesToIgnoreFKNotFound.addAll(exportTemplate.getClassesToIgnoreFKNotFound());
        }
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

        // Was explicitly configured to export full information except for classes that are excluded or are told to export by id
        if (exportAllClassesAsFull) {

            // Was explicitly told to export a short version
            for (Class<? extends IEntity> clazzAsShort : classesToExportAsShort) {
                // log.trace("clazzAsShort={}", clazzAsShort);
                if (clazzAsShort.isAssignableFrom(clazz)) {
                    log.debug("Exporting/importing entity " + clazz.getName() + " as short. Match classesToExportAsShort rule " + clazzAsShort.getName());
                    return false;
                }
            }

            // Export by id only
            if (isExportIdOnly(clazz)) {
                log.debug("Exporting/importing entity " + clazz.getName() + " as short - by id");
                return false;
            }
            log.debug("Exporting/importing entity " + clazz.getName() + " as full by default as exportAllClassesAsFull=true");
            return true;

        } else {
            // Was explicitly configured to export full information
            for (Class<? extends IEntity> clazzAsFull : classesToExportAsFull) {
                // log.trace("clazzAsFull={}", clazzAsFull);
                if (clazzAsFull.isAssignableFrom(clazz)) {
                    log.debug("Exporting/importing entity " + clazz.getName() + " as full. Match classesToExportAsFull rule " + clazzAsFull.getName());
                    return true;
                }
            }

            // No other export specification (export as id only, or export identifiers) is available about the class
            if (!isExportIdOnly(clazz) && getExportIdsForClass(clazz) == null) {
                log.debug("Exporting/importing entity " + clazz.getName() + " as full as no export identifiers found and not instructed to export by id");
                return true;
            }
            log.debug("Exporting/importing entity " + clazz.getName() + " as short");
            return false;
        }
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
     * Should ignore when FK to an entity of a given class was not found
     * 
     * @param clazz Class in question
     * @return True if was told to ignore FK to this class
     */
    public boolean isIgnoreFKToClass(Class<? extends IEntity> clazz) {
        log.error("classesToIgnoreFKNotFound check " + clazz + " classesToIgnoreFKNotFound contains " + classesToIgnoreFKNotFound);
        if (classesToIgnoreFKNotFound != null) {

            Class<?> classToCheck = clazz;
            while (!Object.class.equals(classToCheck)) {
                if (classesToIgnoreFKNotFound.contains(classToCheck)) {
                    return true;
                }
                classToCheck = classToCheck.getSuperclass();
            }
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
