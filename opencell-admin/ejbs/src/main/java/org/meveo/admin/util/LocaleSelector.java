package org.meveo.admin.util;

import java.io.Serializable;
import java.util.Locale;

import javax.enterprise.context.RequestScoped;
import javax.inject.Inject;
import javax.inject.Named;

import org.meveo.security.CurrentUser;
import org.meveo.security.MeveoUser;

@Named
@RequestScoped
public class LocaleSelector implements Serializable {

    private static final long serialVersionUID = -4072480474117257543L;

    private Locale currentLocale;

    @Inject
    @CurrentUser
    protected MeveoUser currentUser;

    public Locale getCurrentLocale() {

        if (currentLocale == null) {
            if (currentUser.getLocale() != null) {
                currentLocale = new Locale(currentUser.getLocale());
            } else {
                currentLocale = new Locale("en");
            }
        }
        return currentLocale;
    }

}
