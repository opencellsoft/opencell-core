package org.meveo.admin.job;

import org.apache.commons.collections4.CollectionUtils;
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

        Long nbRuns = (Long) this.getParamOrCFValue(jobInstance, "nbRuns", -1L);
        if (nbRuns == -1) {
            nbRuns = (long) Runtime.getRuntime().availableProcessors();
        }
        Long waitingMillis = (Long) this.getParamOrCFValue(jobInstance, "waitingMillis", 0L);

        Set<String> allEntities = getAPIv2ManagedClasses();
        Map<String, List<String>> permissionsByEntities = getAllAPIv2Permissions();

        for (String entityName : allEntities) {
            String entitySimpleName = entityName.substring(entityName.indexOf(".") + 1);
            List<String> entityPermissionList = permissionsByEntities.get(entitySimpleName);

            Role thematicRole = getOrCreateThematicRole(entityName);

            createPermissionIfDosentExist("all", entityName, entityPermissionList);
            createPermissionIfDosentExist("list", entityName, entityPermissionList);
            createPermissionIfDosentExist("create", entityName, entityPermissionList);
            createPermissionIfDosentExist("update", entityName, entityPermissionList);
            createPermissionIfDosentExist("remove", entityName, entityPermissionList);
        }






    }

    private void createPermissionIfDosentExist(String operation, String entityName, List<String> entityPermissionList) {
        // If the permission for the given operation and entity
        if (CollectionUtils.isEmpty(entityPermissionList) || !entityPermissionList.contains(operation)) {
            // Create permission
            String entitySimpleName = entityName.substring(entityName.lastIndexOf(".") + 1);
            String permission = entitySimpleName + "." + operation;

            Permission permissionEntity = new Permission();
            permissionEntity.setName(permission);
            permissionEntity.setPermission(permission);
            permissionService.create(permissionEntity);

        }
    }

    private Role getOrCreateThematicRole(String entityName) {
        String entityPackage = entityName.substring(0, entityName.lastIndexOf("."));
        //String thematicRole =
        return null;
    }

    private Set<String> getAPIv2ManagedClasses() {
        Set<Class<?>> classesAnnotatedWith = ReflectionUtils.getClassesAnnotatedWith(Entity.class);
        return classesAnnotatedWith.stream().map(Class::getName).map(String::toLowerCase)
                .collect(Collectors.toSet());
    }

    private Map<String, List<String>> getAllAPIv2Permissions() {
        Role apiv2SuperRole = roleService.findByName("APIV2_FULL_ACCESS");
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
        // Map permissions by entities names
        Map<String, String> permissionsByEntities = apiv2AllPermissions.stream()
                .map(Permission::getPermission).map(permission -> permission.split("\\."))
                .collect(Collectors.toMap(permissonParts -> permissonParts[0], permissonParts -> permissonParts[1]));

        // Group permissions into lists by entities names
        Map<String, List<String>> permissionsListByEntities = new HashMap<>();
        for (Map.Entry<String, String> permissionEntry : permissionsByEntities.entrySet()) {
            permissionsListByEntities
                    .computeIfAbsent(permissionEntry.getKey(), key -> new ArrayList<>())
                    .add(permissionEntry.getValue());
        }
        return permissionsListByEntities;
    }


}
