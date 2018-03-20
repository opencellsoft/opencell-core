package org.meveo.service.crm.impl;

import java.util.Arrays;
import java.util.Date;

import javax.annotation.Resource;
import javax.ejb.Stateless;
import javax.ejb.Timeout;
import javax.ejb.Timer;
import javax.ejb.TimerConfig;
import javax.ejb.TimerService;
import javax.faces.context.FacesContext;
import javax.inject.Inject;
import javax.servlet.http.HttpServletRequest;

import org.apache.commons.lang.time.DateUtils;
import org.keycloak.KeycloakSecurityContext;
import org.meveo.admin.exception.BusinessException;
import org.meveo.admin.listener.ApplicationInitializer;
import org.meveo.api.dto.RoleDto;
import org.meveo.api.dto.UserDto;
import org.meveo.api.exception.EntityDoesNotExistsException;
import org.meveo.commons.utils.EjbUtils;
import org.meveo.commons.utils.StringUtils;
import org.meveo.keycloak.client.KeycloakAdminClientService;
import org.meveo.model.crm.Provider;
import org.meveo.service.base.EntityManagerProvider;
import org.slf4j.Logger;

/**
 * Manages providers connection to DB
 * 
 * @author Andrius Karpavicius
 * @author Wassim Drira
 * @lastModifiedVersion 5.0.1
 * 
 */
@Stateless
public class TenantRegistry {

    @Inject
    private EntityManagerProvider entityManagerProvider;

    @Inject
    private ApplicationInitializer applicationInitializer;

    @Resource
    private TimerService timerService;

    @Inject
    private Logger log;

    /**
     * Unregister a tenant/provider
     * 
     * @param provider Provider to unregister
     */
    public void removeTenant(Provider provider) {
        entityManagerProvider.unregisterEntityManagerFactory(provider.getCode());
    }

    /**
     * Register a new tenant/provider and create a default $providerCode$.superadmin user.
     * 
     * @param provider Provider to register as a new tenant
     * @lastModifiedVersion 5.0.1
     */
    public void addTenant(Provider provider) {

        // Create a timer to be triggered instantly, that way we can force authentication to a new tenant, as timer has no security context and can be overriden
        TimerConfig timerConfig = new TimerConfig();
        timerConfig.setInfo(provider);

        /* -- Add Keycloak superadmin user for this new tenant -- */
        HttpServletRequest request = (HttpServletRequest) (FacesContext.getCurrentInstance().getExternalContext().getRequest());
        KeycloakSecurityContext session = (KeycloakSecurityContext) request.getAttribute(KeycloakSecurityContext.class.getName());
        log.info("> addTenant > getTokenString : " + session.getTokenString());

        // Create user
        UserDto userDto = new UserDto();
        String name = (provider.getCode() + "." + "superadmin").toLowerCase();
        log.info("> addTenant > name " + session.getTokenString());
        userDto.setUsername(name);
        userDto.setPassword(name);
        if (!StringUtils.isBlank(provider.getEmail())) {
            userDto.setEmail(provider.getEmail());
        } else {
            userDto.setEmail(name + "@" + provider.getCode().toLowerCase() + ".com");
        }
        userDto.setRoles(Arrays.asList("CUSTOMER_CARE_USER", "superAdministrateur"));
        userDto.setExternalRoles(Arrays.asList(new RoleDto("CC_ADMIN"), new RoleDto("SUPER_ADMIN")));

        // Get services
        KeycloakAdminClientService kc = (KeycloakAdminClientService) EjbUtils.getServiceInterface(KeycloakAdminClientService.class.getSimpleName());
        try {
            kc.createUser(request, userDto, provider.getCode());
        } catch (EntityDoesNotExistsException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (BusinessException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }

        /* -- END of user creation in keycloak -- */

        Date expireOn = new Date();
        expireOn = DateUtils.addMilliseconds(expireOn, 30);

        timerService.createSingleActionTimer(expireOn, timerConfig);
    }

    /**
     * A trigger when a future custom field end period event expired
     * 
     * @param timer Timer information
     */
    @Timeout
    private void triggerCreateTenant(Timer timer) {
        try {
            Provider tenantInfo = (Provider) timer.getInfo();

            applicationInitializer.initializeTenant(tenantInfo, false, true);

        } catch (Exception e) {
            log.error("Failed to launch create tenant timer", e);
        }
    }
}