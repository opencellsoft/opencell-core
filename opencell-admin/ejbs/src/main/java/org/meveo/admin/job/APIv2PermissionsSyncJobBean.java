package org.meveo.admin.job;

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

import javax.ejb.Stateless;
import javax.ejb.TransactionAttribute;
import javax.ejb.TransactionAttributeType;
import javax.inject.Inject;
import javax.interceptor.Interceptors;
import javax.persistence.Entity;
import java.util.*;
import java.util.stream.Collectors;

@Stateless
public class APIv2PermissionsSyncJobBean extends BaseJobBean {

    private static final String APIV2_FULL_ACCESS = "APIv2_FULL_ACCESS";

    @Inject
    private RoleService roleService;

    @Inject
    private PermissionService permissionService;

    @Interceptors({JobLoggingInterceptor.class, PerformanceInterceptor.class})
    @TransactionAttribute(TransactionAttributeType.NEVER)
    public void execute(JobExecutionResultImpl result, JobInstance jobInstance) {

        Map<String, Set<String>> allManagedEntities = getAPIv2ManagedClassesByPackages();
        Map<String, Set<String>> permissionsByEntities = getAllAPIv2Permissions();

        allManagedEntities.forEach((subPackage, subPackageEntities) -> {

            Role thematicRole = getOrCreateThematicRole(subPackage);

            for (String entityFullName : subPackageEntities) {
                String entityName = entityFullName.substring(entityFullName.indexOf(".") + 1);

                Set<String> entityOperationList = null;
                if (permissionsByEntities != null) entityOperationList = permissionsByEntities.get(entityName);

                createPermissionForOperationIfDoseNotExist("all", entityName, thematicRole, entityOperationList);
                createPermissionForOperationIfDoseNotExist("list", entityName, thematicRole, entityOperationList);
                createPermissionForOperationIfDoseNotExist("create", entityName, thematicRole, entityOperationList);
                createPermissionForOperationIfDoseNotExist("update", entityName, thematicRole, entityOperationList);
                createPermissionForOperationIfDoseNotExist("remove", entityName, thematicRole, entityOperationList);
            }
        });
    }

    private void createPermissionForOperationIfDoseNotExist(String operation, String entityName, Role thematicRole, Set<String> entityOperationList) {
        // If the permission for the given operation and entity
        if (CollectionUtils.isEmpty(entityOperationList) || !entityOperationList.contains(operation)) {
            // Create permission
            String permission = entityName + "." + operation;
            Permission permissionEntity = new Permission();
            permissionEntity.setName(permission);
            permissionEntity.setPermission(permission);

            permissionService.create(permissionEntity);

            //Add it to thematic role
            thematicRole.getAllPermissions().add(permissionEntity);
            roleService.update(thematicRole);
        }
    }

    private Role getOrCreateThematicRole(String subPackage) {
        // Create thematicRole if doesn't exist
        Role thematicRole = roleService.findByName(subPackage);

        if (thematicRole == null) {
            thematicRole = new Role();
            thematicRole.setName(subPackage);
            thematicRole.setDescription(subPackage);
            roleService.create(thematicRole);
        }
        return thematicRole;
    }

    private Map<String, Set<String>> getAPIv2ManagedClassesByPackages() {
        Map<String, Set<String>> entitiesByPackages = new HashMap<>();
        Set<Class<?>> classesAnnotatedWith = ReflectionUtils.getClassesAnnotatedWith(Entity.class);
        Set<String> managedEntities = classesAnnotatedWith.stream().map(Class::getName).map(String::toLowerCase)
                .collect(Collectors.toSet());

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
        return entitiesByPackages;
    }

    private Map<String, Set<String>> getAllAPIv2Permissions() {
        Role apiv2SuperRole = roleService.findByName(APIV2_FULL_ACCESS);
        // create role if not exists
        if (apiv2SuperRole == null) {
            apiv2SuperRole = new Role();
            apiv2SuperRole.setName(APIV2_FULL_ACCESS);
            apiv2SuperRole.setDescription("API v2 full access");

            roleService.create(apiv2SuperRole);
        }

        Set<Permission> apiv2AllPermissions = apiv2SuperRole.getPermissions();
        if (CollectionUtils.isEmpty(apiv2AllPermissions)) {
            return null;
        }
        // Split each permission to entity name and operation,
        // then group operations by entity name
        return apiv2AllPermissions.stream()
                .map(Permission::getPermission).map(permission -> permission.split("\\."))
                .collect(Collectors.groupingBy(permissionParts -> permissionParts[0],
                        Collectors.mapping(permissionParts -> permissionParts[1], Collectors.toSet())));


    }
}
