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
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

@Stateless
public class GenericApiPermissionsSyncJobBean extends BaseJobBean {

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

        Set<String> allEntities = getGenericApiManagedClasses();
        Map<String, List<String>> allEntitiesPermissions = getAllGenericAPIPermissions();






    }

    private Set<String> getGenericApiManagedClasses() {
        Set<Class<?>> classesAnnotatedWith = ReflectionUtils.getClassesAnnotatedWith(Entity.class);
        return classesAnnotatedWith.stream().map(Class::getSimpleName).map(String::toLowerCase)
                .collect(Collectors.toSet());
    }

    private Map<String, List<String>> getAllGenericAPIPermissions() {
        Role apiv2SuperRole = roleService.findByName("APIV2_FULL_ACCESS");
        // create role if not exists
        if (apiv2SuperRole == null) {
            apiv2SuperRole = new Role();
            apiv2SuperRole.setName("APIV2_FULL_ACCESS");
            apiv2SuperRole.setDescription("API v2 full access");

            roleService.create(apiv2SuperRole);
        }

        Set<Permission> apiv2AllPermissions = apiv2SuperRole.getPermissions();
        if (CollectionUtils.isEmpty(apiv2AllPermissions)) {
            return null;
        }
        Map<String, String> permissionsBy = apiv2AllPermissions.stream()
                .map(Permission::getPermission).map(permission -> permission.split("\\."))
                .collect(Collectors.toMap(permissonParts -> permissonParts[0], permissonParts -> permissonParts[1]));
        return null;
    }


}
