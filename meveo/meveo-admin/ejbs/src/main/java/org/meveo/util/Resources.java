package org.meveo.util;

import javax.enterprise.inject.Produces;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.PersistenceContextType;

public class Resources {

    // @ExtensionManaged
    // @ConversationScoped
    // @Produces
    // @PersistenceUnit(unitName="MeveoAdmin")
    // @MeveoJpa
    // EntityManagerFactory emf;
    @Produces
    @MeveoJpa
    @PersistenceContext(unitName = "MeveoAdmin", type = PersistenceContextType.EXTENDED)
    private EntityManager em;

    // @ExtensionManaged
    // @ConversationScoped
    // @Produces
    // @PersistenceUnit(unitName="MeveoDWH")
    // @MeveoDWHJpa
    // EntityManagerFactory emfDwh;
    @Produces
    @MeveoDWHJpa
    @PersistenceContext(unitName = "MeveoDWH", type = PersistenceContextType.EXTENDED)
    private EntityManager emDwh;

}
