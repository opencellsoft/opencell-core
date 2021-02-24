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

package org.meveo.admin.job;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

import javax.ejb.Stateless;
import javax.ejb.TransactionAttribute;
import javax.ejb.TransactionAttributeType;
import javax.inject.Inject;
import javax.interceptor.Interceptors;
import javax.persistence.Entity;

import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.meveo.admin.job.logging.JobLoggingInterceptor;
import org.meveo.commons.utils.ReflectionUtils;
import org.meveo.interceptor.PerformanceInterceptor;
import org.meveo.model.jobs.JobExecutionResultImpl;
import org.meveo.model.jobs.JobInstance;
import org.meveo.model.security.Permission;
import org.meveo.model.security.Role;
import org.meveo.service.admin.impl.PermissionService;
import org.meveo.service.admin.impl.RoleService;
import org.meveo.service.job.JobExecutionService;
import org.slf4j.Logger;

/**
 * Job to synchronize and generate permissions all, list, create, update and remove for each
 * {@code Entity} class
 *
 * @author Mounir BOUKAYOUA
 * @since 10.X
 */
@Stateless
public class APIv2PermissionsSyncJobBean extends BaseJobBean {

    /**
     * APIv2 super role name
     */
    private static final String APIV2_FULL_ACCESS = "APIv2_FULL_ACCESS";

    @Inject
    private Logger log;

    @Inject
    private RoleService roleService;

    @Inject
    private PermissionService permissionService;

    @Inject
    protected JobExecutionService jobExecutionService;

    /**
     * APIv2 super role
     */
    private Role apiv2SuperRole;

    /**
     * All newest created permissions
     */
    private List<Permission> allCreatedPermissions;

    /**
     * Job execution method
     * @param result Job result
     * @param jobInstance Job instance
     */
    @Interceptors({JobLoggingInterceptor.class, PerformanceInterceptor.class})
    @TransactionAttribute(TransactionAttributeType.NEVER)
    public void execute(JobExecutionResultImpl result, JobInstance jobInstance) {
        log.debug("Job {}: Start synchronisation between APIv2 permissions " +
                "and existing entities", jobInstance.getCode());

        try {
            int entitiesAffectedCount = 0;
            apiv2SuperRole = getApiv2SuperRole();
            allCreatedPermissions = new ArrayList<>();

            Map<String, Set<String>> allManagedEntities = getAPIv2ManagedClassesByPackages();
            Map<String, Set<String>> permissionsByEntities = getAllAPIv2Permissions();

            for (Map.Entry<String, Set<String>> entry : allManagedEntities.entrySet()) {
                String subPackage = entry.getKey();
                Set<String> subPackageEntities = entry.getValue();

                Role thematicRole = getOrCreateThematicRole(subPackage);
                boolean newPermissionsCreated = false;

                for (String entityFullName : subPackageEntities) {
                    String entityName = entityFullName.substring(entityFullName.lastIndexOf(".") + 1);
                    Set<String> entityOperationList = permissionsByEntities != null ? permissionsByEntities.get(entityName) : null;

                    log.debug("Check existing permissions and create missed ones for entity {}", entityName);
                    boolean entityAffected = false;

                    // if at min one permission is created then mark it to update
                    // thematic role and to increment total affected entities counter
                    if (createPermissionForOperationIfDoseNotExist("all", entityName, thematicRole, entityOperationList)) {
                        newPermissionsCreated = true;
                        entityAffected = true;
                    }
                    if (createPermissionForOperationIfDoseNotExist("list", entityName, thematicRole, entityOperationList)) {
                        newPermissionsCreated = true;
                        entityAffected = true;
                    }
                    if (createPermissionForOperationIfDoseNotExist("create", entityName, thematicRole, entityOperationList)) {
                        newPermissionsCreated = true;
                        entityAffected = true;
                    }
                    if (createPermissionForOperationIfDoseNotExist("update", entityName, thematicRole, entityOperationList)) {
                        newPermissionsCreated = true;
                        entityAffected = true;
                    }
                    if (createPermissionForOperationIfDoseNotExist("remove", entityName, thematicRole, entityOperationList)) {
                        newPermissionsCreated = true;
                        entityAffected = true;
                    }

                    if (entityAffected) entitiesAffectedCount++;
                }
                // Update thematic role to persist its new attached permissions if needed
                if (newPermissionsCreated) {
                    log.debug("Update role {} with newest created permissions", thematicRole);
                    roleService.update(thematicRole);
                }
            }

            // Attach all created permissions with the APIv2 super role
            String resultMsg;
            if (!allCreatedPermissions.isEmpty()) {
                apiv2SuperRole.getPermissions().addAll(allCreatedPermissions);
                roleService.update(apiv2SuperRole);
                resultMsg = allCreatedPermissions.size() + " permissions in total are created for "
                        + entitiesAffectedCount + " affected entities";
            } else {
                resultMsg = "All entities permissions are already synchronized";
            }

            // Set Job report
            result.setReport(resultMsg);
            result.setNbItemsCorrectlyProcessed(allCreatedPermissions.size());

        } catch (Exception e) {
            log.error("Failed to run APIv2 permissions synchronisation job ", e);
            jobExecutionService.registerError(result, e.getMessage());
        }
    }

    /**
     * Get the super APIv2 role. Create it if needed
     *
     * @return super APIv2 role
     */
    private Role getApiv2SuperRole() {
        Role apiv2SuperRole = roleService.findByName(APIV2_FULL_ACCESS);

        if (apiv2SuperRole == null) {
            apiv2SuperRole = new Role();
            apiv2SuperRole.setName(APIV2_FULL_ACCESS);
            apiv2SuperRole.setDescription("API v2 full access");

            roleService.create(apiv2SuperRole);
            log.debug("{} super role just created", APIV2_FULL_ACCESS);
        }
        return apiv2SuperRole;
    }

    /**
     * Create missed permission for the given operation on an entity, and then add it to the thematic role
     *
     * @param operation operation for which the permission should be created
     * @param entityName entity name on which the permission should be created
     * @param thematicRole the thematic role to which attach the created permission
     * @param entityOperationList entity's operations list which have already an existing permission
     * @return permission is created or not
     */
    private boolean createPermissionForOperationIfDoseNotExist(String operation, String entityName, Role thematicRole, Set<String> entityOperationList) {
        // If the permission for the given operation and entity
        if (CollectionUtils.isEmpty(entityOperationList) || !entityOperationList.contains(operation)) {
            // Create permission
            String permission = "APIv2_" + entityName + "." + operation;
            Permission permissionEntity = new Permission();
            permissionEntity.setName(permission);
            permissionEntity.setPermission(permission);

            permissionService.create(permissionEntity);

            // associate the created permission with
            // the thematic role and the super role
            thematicRole.getPermissions().add(permissionEntity);
            allCreatedPermissions.add(permissionEntity);
            return true;
        } else {
            return false;
        }
    }

    /**
     * Get or create a thematic role for a given model's sub package
     * @param subPackage model's first sub package
     * @return Thematic role
     */
    private Role getOrCreateThematicRole(String subPackage) {
        // Create thematicRole if doesn't exist
        Role thematicRole = roleService.findByName(subPackage);

        if (thematicRole == null) {
            thematicRole = new Role();
            thematicRole.setName(subPackage);
            thematicRole.setDescription(subPackage);
            roleService.create(thematicRole);
            log.debug("New thematic Role {} is created", subPackage);
        }
        return thematicRole;
    }

    /**
     * Get all entities that should be managed by APIv2, which are basically, classes marked
     * with {@code @Entity} annotation
     *
     * @return All entities manageable by APIv2 mapped with their first model subpackage
     */
    private Map<String, Set<String>> getAPIv2ManagedClassesByPackages() {
        Map<String, Set<String>> entitiesByPackages = new HashMap<>();
        Set<Class<?>> classesAnnotatedWith = ReflectionUtils.getClassesAnnotatedWith(Entity.class);
        Set<String> managedEntities = classesAnnotatedWith.stream().map(Class::getName).collect(Collectors.toSet());

        for (String entityName : managedEntities) {
            // Get first sub package name under model package of the entity.
            // If the entity is directly under model package then return COMMON
            int subPackageStart = StringUtils.ordinalIndexOf(entityName, ".", 3);
            int subPackageEnd = StringUtils.ordinalIndexOf(entityName, ".", 4);
            String subPackageName;
            if (subPackageEnd != -1) {
                subPackageName = "APIv2_" + entityName.substring(subPackageStart + 1, subPackageEnd).toUpperCase();
            } else {
                subPackageName = "APIv2_COMMON";
            }

            entitiesByPackages.computeIfAbsent(subPackageName, key -> new HashSet<>()).add(entityName);
        }
        log.debug("{} Entity classes are found under {} model sub-packages", managedEntities.size(), entitiesByPackages.size());

        return entitiesByPackages;
    }

    /**
     * Get all APIv2 already existing permissions which are basically
     * attached to APIv2 super role. Those permissions are mapped to their related
     * operations and then grouped and mapped by entities name
     *
     * @return All APIv2 existing permissions/operations mapped by entities name
     */
    private Map<String, Set<String>> getAllAPIv2Permissions() {
        Set<Permission> apiv2AllPermissions = apiv2SuperRole.getPermissions();
        if (CollectionUtils.isEmpty(apiv2AllPermissions)) {
            log.debug("No APIv2 permissions have been created yet. They will be in next steps...");
            return null;
        }
        // Split each permission to entity name and operation,
        // then group operations by entity name
        List<String[]> splitedPermissions = apiv2AllPermissions.stream()
                .map(Permission::getPermission)
                .map(permission -> permission.split("\\."))
                .collect(Collectors.toList());

        Map<String, Set<String>> permissionsByEntities = new HashMap<>();
        for (String[] splitedPermission : splitedPermissions) {
            // remove "APIv2_" prefixe from entityName;
            String entityName = splitedPermission[0].substring(splitedPermission[0].lastIndexOf("_") + 1);
            String operation = splitedPermission[1];

            permissionsByEntities.computeIfAbsent(entityName, key -> new HashSet<>()).add(operation);
        }
        log.debug("{} APIv2 permissions are already existing, and which are belonging to {} entities",
                apiv2AllPermissions.size(), permissionsByEntities.size());

        return permissionsByEntities;
    }
}
