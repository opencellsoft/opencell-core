package org.meveo.service.crm.impl;

import java.util.Date;

import javax.annotation.Resource;
import javax.ejb.Stateless;
import javax.ejb.Timeout;
import javax.ejb.Timer;
import javax.ejb.TimerConfig;
import javax.ejb.TimerService;
import javax.inject.Inject;

import org.apache.commons.lang.time.DateUtils;
import org.meveo.admin.listener.ApplicationInitializer;
import org.meveo.jpa.EntityManagerProvider;
import org.meveo.model.crm.Provider;
import org.slf4j.Logger;

/**
 * Manages providers connection to DB.
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
     * Unregister a tenant/provider.
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

        Date expireOn = new Date();
        expireOn = DateUtils.addMilliseconds(expireOn, 30);

        timerService.createSingleActionTimer(expireOn, timerConfig);
    }

    /**
     * A trigger when a future custom field end period event expired.
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