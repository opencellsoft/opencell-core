package org.meveo.service.crm.impl;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import javax.ejb.Stateless;
import javax.enterprise.context.RequestScoped;
import javax.enterprise.inject.Produces;
import javax.inject.Inject;
import javax.inject.Named;
import javax.persistence.EntityManager;

import org.meveo.security.CurrentUser;
import org.meveo.security.MeveoUser;
import org.meveo.security.keycloak.CurrentUserProvider;
import org.meveo.service.base.EntityManagerProvider;
import org.slf4j.Logger;

@Stateless
public class CurrentUserProducer {

    @Inject
    private CurrentUserProvider currentUserProvider;

    @Inject
    EntityManagerProvider entityManagerProvider;
    
    @Inject
    Logger log;

    /**
     * produce a current user
     * 
     * @return
     */
    @Produces
    @RequestScoped
    @Named("currentUser")
    @CurrentUser
    public MeveoUser getCurrentUser() {
        log.error("AKK start to product current user");
        String providerCode = currentUserProvider.getCurrentUserProviderCode();
        EntityManager em = entityManagerProvider.getEntityManager(providerCode);
        MeveoUser meveoUser = currentUserProvider.getCurrentUser(providerCode, em);
        
        log.error("AKK end to product current user");
        return meveoUser;
    }
    
    @PostConstruct
    public void boo(){
        log.error("AKK PostConstruct {}", getClass().getSimpleName());
    }
    
    
    @PreDestroy
    public void muu(){
        log.error("AKK PreDestroy {}", getClass().getSimpleName());
    }
}