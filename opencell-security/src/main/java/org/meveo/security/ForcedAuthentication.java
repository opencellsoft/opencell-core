package org.meveo.security;

import java.io.Serializable;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import javax.ejb.PostActivate;
import javax.ejb.PrePassivate;
import javax.enterprise.context.RequestScoped;

/**
 * @author Andrius Karpavicius
 **/
@RequestScoped
public class ForcedAuthentication implements Serializable {

    private static final long serialVersionUID = 1548517186484250733L;

    private String forcedUserUsername;

    private String forcedProvider;

    public void forceAuthentication(String currentUserUserName, String providerCode) {

        this.forcedProvider = providerCode;
        this.forcedUserUsername = currentUserUserName;
    }

    public String getForcedProvider() {
        return forcedProvider;
    }

    public String getForcedUserUsername() {
        return forcedUserUsername;
    }

    @Override
    public String toString() {
        return forcedProvider + "/" + forcedUserUsername;
    }

    @PostConstruct
    public void boo() {
        System.out.println("AKK @PostConstruct " + getClass().getSimpleName() + " " + this);
    }

    @PreDestroy
    public void muu() {
        System.out.println("AKK @PreDestroy " + getClass().getSimpleName() + " " + this);
    }

    @PostActivate
    public void aaa() {
        System.out.println("AKK @PostActivate " + getClass().getSimpleName() + " " + this);
    }

    @PrePassivate
    public void bbb() {
        System.out.println("AKK @PrePassivate " + getClass().getSimpleName() + " " + this);
    }

    public void init() {
        System.out.println("AKK @init " + getClass().getSimpleName() + " " + this);
    }
}