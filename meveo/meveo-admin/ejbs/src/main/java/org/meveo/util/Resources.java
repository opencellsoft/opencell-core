package org.meveo.util;

import javax.enterprise.context.ConversationScoped;
import javax.enterprise.inject.Produces;
import javax.persistence.EntityManagerFactory;
import javax.persistence.PersistenceUnit;

import org.jboss.solder.core.ExtensionManaged;

public class Resources {

     @ExtensionManaged
     //@ConversationScoped
     @Produces
     @PersistenceUnit(unitName="MeveoAdmin")
     @MeveoJpa
     private EntityManagerFactory emf;
//    @Produces
//    @MeveoJpa
//    @PersistenceContext(unitName = "MeveoAdmin", type = PersistenceContextType.EXTENDED)
//    private EntityManager em;

     @ExtensionManaged
     //@ConversationScoped
     @Produces
     @PersistenceUnit(unitName="MeveoDWH")
     @MeveoDWHJpa
     private EntityManagerFactory emfDwh;
//    @Produces
//    @MeveoDWHJpa
//    @PersistenceContext(unitName = "MeveoDWH", type = PersistenceContextType.EXTENDED)
//    private EntityManager emDwh;

}
